package org.ybm.bakum;

import java.util.UUID;

import com.kwazylabs.utils.persistence.GenericHibernateDAO;
import com.kwazylabs.utils.persistence.QueryWithParams;

public class DardasonHibernateDAO extends GenericHibernateDAO<Dardason, UUID>
    implements DardasonDAO
{
	@Override
	public Dardason getByUsername(String username) throws Exception
	{
		QueryWithParams q = new QueryWithParams("FROM Dardason WHERE username=:username");
		q.params.put("username", username);
		return (Dardason) runSingleResultQuery(q);
	}
}
