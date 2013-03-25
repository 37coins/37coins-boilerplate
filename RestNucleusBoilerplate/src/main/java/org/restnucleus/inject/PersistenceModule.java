package org.restnucleus.inject;

import org.restlet.Request;
import org.restnucleus.dao.GenericRepository;
import org.restnucleus.dao.RNQuery;
import org.restnucleus.filter.ApplicationFilter;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class PersistenceModule extends AbstractModule {

	@Override
	protected void configure() {}
	
	@Provides 
	GenericRepository provideDao() {
		return (GenericRepository) Request.getCurrent().getAttributes()
				.get(ApplicationFilter.DAO_PARAM);
		//we don't capture a null pointer here
		//creating new repo would make closing db connection unmanagebale
	}
	
	@Provides
	RNQuery getQuery(){
		RNQuery rv = (RNQuery) Request.getCurrent().getAttributes().get(RNQuery.QUERY_PARAM);
		if (null==rv)
			rv = new RNQuery();
		return rv;
	}

}
