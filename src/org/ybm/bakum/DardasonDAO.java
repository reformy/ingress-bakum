package org.ybm.bakum;

import java.util.UUID;

import com.kwazylabs.utils.persistence.GenericDAO;

public interface DardasonDAO extends GenericDAO<Dardason, UUID>
{
	public Dardason getByUsername(String username) throws Exception;
}
