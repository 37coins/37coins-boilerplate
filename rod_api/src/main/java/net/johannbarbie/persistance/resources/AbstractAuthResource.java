package net.johannbarbie.persistance.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.restlet.data.Status;

public abstract class AbstractAuthResource extends AbstractResource {
	public static final String DEFAULT_SESSION = "session";
	public static final String DEFAULT_USER = "user";
	
	protected Map<String,String> authTokens = null;
	
	public AbstractAuthResource(){
		//to provide opt-out instead opt-in authentication paradigm during development process
		authTokens = new HashMap<String,String>(2);
		authTokens.put(DEFAULT_SESSION, null);
		authTokens.put(DEFAULT_USER, null);
	}
	
	@Override
	public void doInit() {
		super.doInit();
		if (null!= authTokens){
			for (Entry<String,String> e : authTokens.entrySet()){
				String val = getCookieValue(e.getKey());
				if (null == val) {
					setStatus(Status.CLIENT_ERROR_UNAUTHORIZED, e.getKey()+"Tocken missing");
					getResponse().getCookieSettings().remove(e.getKey());
				}else{
					authTokens.put(e.getKey(), val);
				}
			}
		}
	}
	
	protected void disableAuthentication() {
		//disable authentication
		authTokens.clear();
	}
}
