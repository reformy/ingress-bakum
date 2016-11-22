<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.ybm.bakum.Dardason"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dardasim list</title>
</head>
<body>

<%
	String message = request.getParameter("message");
	if (StringUtils.isNotEmpty(message))
	{
%>
	Message:<br/>
	<%=message %>
	<br/>
	<br/>
<%
	}
%>

<form action="ms" method="post">
	<input type="hidden" name="a" value="addAll"/>
	Add all (comma separated): <input type="text" name="names"/>
	<input type="submit" value="add all"/>
</form>
<br/>
<br/>
<form action="ms" method="post">
	<input type="hidden" name="a" value="addAllFromComm"/>
	Add all from Comm: <br/><textarea name="comm" rows="10" cols="50"></textarea><br/>
	<input type="submit" value="add all from comm"/>
</form>
<br/>
<br/>

<form action="ms" method="post">
	<input type="hidden" name="a" value="prepareWelcomeLines"/>
	Lines: <br/><textarea name="lines" rows="5" cols="80"></textarea><br/>
	<input type="submit" value="prepare"/>
</form>
<br/>
<br/>


<%
	List<Dardason> list = (List<Dardason>) request.getAttribute("list");
	int n = (Integer) request.getAttribute("n");
%>

Number of dardasonim in DB: <%= n %>
<br/><br/>
<table border='1'>
	<tr bgcolor="#c0c0c0">
		<td>username</td>
		<td>real name</td>
		<td>locations</td>
		<td>birthday</td>
	</tr>
<% for (Dardason d : list) {%>
	<tr>
		<td><%=d.getUsername() %></td>
		<td><%=StringUtils.defaultString(d.getRealName()) %></td>
		<td><%=StringUtils.defaultString(d.getLocations()) %></td>
		<td><%=d.getBirthday() %></td>
	</tr>
<% } %>
</table>
</body>
</html>
