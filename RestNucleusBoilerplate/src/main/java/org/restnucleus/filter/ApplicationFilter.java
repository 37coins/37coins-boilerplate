package org.restnucleus.filter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.routing.Filter;
import org.restnucleus.dao.GenericRepository;

/**
 * 
 * if i got the paragraph in http://flylib.com/books/en/3.381.1.113/1/ right,
 *  then i should get and close the persistance manager here.
 * 
 * @author johba
 *
 */
public class ApplicationFilter extends Filter {
	public static final String DAO_PARAM = "org.restnucleus.DAO";
	
    public ApplicationFilter(Context context) {
        super(context);
    }
    
    @Override
    protected int beforeHandle(Request request, Response response){
    	GenericRepository dao = new GenericRepository();
    	request.getAttributes().put(DAO_PARAM, dao);
    	return Filter.CONTINUE;
    }
    
    @Override
    protected void afterHandle(Request request, Response response) {
    	GenericRepository dao = (GenericRepository)request.getAttributes().get(DAO_PARAM);
    	dao.closePersistenceManager();
    }


}
