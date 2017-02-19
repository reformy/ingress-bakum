package org.ybm.bakum;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.kwazylabs.utils.persistence.QueryWithParams;
import com.kwazylabs.utils.webserver.BasicManagerServlet;

public class ManagerServlet extends BasicManagerServlet
{
	public final static Logger logger = Logger.getLogger(ManagerServlet.class);
	
	private DardasonDAO dao;
	
	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		
		WebApplicationContext ctx = WebApplicationContextUtils
		    .getRequiredWebApplicationContext(getServletContext());
		
		dao = (DardasonDAO) ctx.getBean("dardasonDao");
		
		setDefaultAction("viewAll");
	}
	
	public String viewAll(HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	{
		QueryWithParams q = new QueryWithParams(
		    "FROM Dardason ORDER BY birthday DESC");
		q.maxResults = 100;
		@SuppressWarnings("unchecked")
		List<Dardason> list = (List<Dardason>) dao.runMultipleResultsQuery(q);
		
		request.setAttribute("list", list);
		
		q = new QueryWithParams("SELECT COUNT(*) FROM Dardason");
		int n = ((Number) dao.runSingleResultQuery(q)).intValue();
		request.setAttribute("n", n);
		
		return "viewAll.jsp";
	}
	
	public void addAll(HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	{
		String csv = request.getParameter("names");
		String[] namesStr = csv.split("[\\s,@]+");
		String errorMessage = "";
		for (String name : namesStr)
		{
			name = name.trim();
			if (dao.getByUsername(name) != null)
			{
				errorMessage += name + " ";
			}
			else
			{
				Dardason d = new Dardason();
				d.setId(UUID.randomUUID());
				d.setUsername(name);
				d.setBirthday(new Date());
				dao.create(d);
			}
		}
		
		String forwardUrl = "ms";
		if (errorMessage.length() > 0)
		{
			forwardUrl += "?message="
			    + URLEncoder.encode("Existing: " + errorMessage, "utf8");
		}
		
		response.sendRedirect(forwardUrl);
	}
	
	static DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	public void addAllFromComm(HttpServletRequest request,
	    HttpServletResponse response) throws Exception
	{
		String comm = request.getParameter("comm");
		String errorMessage = "";
		if (comm != null)
		{
			BufferedReader reader = new BufferedReader(new StringReader(comm));
			String line;
			List<Dardason> noDates = new ArrayList<Dardason>();
			Set<String> noDatesNames = new HashSet<String>();
			// Keep today's date so we have something.
			String dateStr = dateFormatter.format(new Date());
			dateStr = dateStr.substring(0, dateStr.indexOf(' '));
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("─ "))
				{
					// Check for date.
					int endDate = line.indexOf(' ', 4);
					if (endDate > 0)
					{
						try
						{
							String tmpDateStr = line.substring(2, endDate);
							dateFormatter.parse(tmpDateStr + " 00:00");
							// If succeeded, save it.
							dateStr = tmpDateStr;
							
							if (noDates != null)
							{
								// Correct the noDates.
								for (Dardason d : noDates)
								{
									String hour = dateFormatter.format(d.getBirthday());
									hour = hour.substring(hour.indexOf(' '));
									String ds = dateStr + hour;
									Date bd = dateFormatter.parse(ds);
									bd = new Date(bd.getTime() - TimeUnit.DAYS.toMillis(1));
									d.setBirthday(bd);
									dao.create(d);
								}
								noDates = null;
								noDatesNames = null;
							}
						}
						catch (ParseException pe)
						{
							pe.printStackTrace();
						}
					}
				}
				else if (line.contains("has completed training")
				    || line.contains("created their first Link")
				    || line.contains("created their first Control Field")
				    || line.contains("captured their first Portal"))
				{
					int startName = line.indexOf('<') + 1;
					if (startName > 0)
					{
						int endNAme = line.indexOf('>');
						if (endNAme > -1)
						{
							String username = line.substring(startName, endNAme);
							if (dao.getByUsername(username) != null || noDatesNames.contains(username))
							{
								errorMessage += username + " ";
							}
							else
							{
								Dardason d = new Dardason();
								d.setId(UUID.randomUUID());
								d.setUsername(username);
								// Get hour.
								String hour = line.substring(0,5);
								Date bd = null;
								try
								{
									bd = dateFormatter.parse(dateStr + ' ' + hour);
								}
								catch (ParseException pe)
								{
									bd = dateFormatter.parse(dateStr + " 00:00");
								}
								d.setBirthday(bd);
								if (noDates != null)
								{
									noDates.add(d);
									noDatesNames.add(d.getUsername());
								}
								else
									dao.create(d);
							}
						}
					}
				}
			}
			
			if (noDates != null)
			{
				for (Dardason d : noDates)
					dao.create(d);
			}

			reader.close();
		}
		
		String forwardUrl = "ms";
		if (errorMessage.length() > 0)
		{
			forwardUrl += "?message="
			    + URLEncoder.encode("Existing: " + errorMessage, "utf8");
		}
		
		response.sendRedirect(forwardUrl);
	}
	
	public void prepareWelcomeLines(HttpServletRequest request,
	    HttpServletResponse response) throws Exception
	{
		QueryWithParams q = new QueryWithParams(
		    "FROM Dardason WHERE welcomed=:welcomed ORDER BY birthday ASC");
		q.maxResults = 10;
		q.params.put("welcomed", false);
		@SuppressWarnings("unchecked")
		List<Dardason> list = (List<Dardason>) dao.runMultipleResultsQuery(q);
		
		String message = "";
		if (list.size() > 0)
		{
			String names = "";
			String ids = "";
			for (Dardason d : list)
			{
				names += "@" + d.getUsername() + " ";
				if (ids.length() > 0)
					ids += ",";
				ids += d.getId();
			}
			
			String lines = request.getParameter("lines");
			if (StringUtils.isEmpty(lines))
			{
				lines = "ברוכים הבאים לכוחות ההתנגדות! הצטרפו אלינו כאן bit.ly/joinTheResistance בגוגל פלוס או חפשו ingress resistance israel בפייסבוק\nאתם מוזמנים לפנות אלי בכל שאלה או עזרה, בטלגרם, האנגאאוט או פייסבוק reformy@gmail.com";
			}
			BufferedReader reader = new BufferedReader(new StringReader(
			    lines));
			String line;
			while ((line = reader.readLine()) != null)
			{
				String wl = names + line;
				message += wl + "<br/>\n";
			}
			
			message += "<br/>\n";
			message += "<a href='ms?a=confirmWelcomed&ids=" + ids
			    + "'>Confirm welcomed</a><br/><br/><br/>";
		}
		
		String forwardUrl = "ms";
		if (message.length() > 0)
		{
			forwardUrl += "?message=" + URLEncoder.encode(message, "utf8");
		}
		
		response.sendRedirect(forwardUrl);
	}
	
	public void confirmWelcomed(HttpServletRequest request,
	    HttpServletResponse response) throws Exception
	{
		String ids = request.getParameter("ids");
		for (String id : ids.split(","))
		{
			Dardason d = dao.read(UUID.fromString(id));
			if (d != null)
			{
				d.setWelcomed(true);
				dao.update(d);
			}
		}
		
		String forwardUrl = "ms";
		
		response.sendRedirect(forwardUrl);
	}
}
