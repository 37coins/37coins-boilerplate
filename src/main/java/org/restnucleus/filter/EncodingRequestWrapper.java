package org.restnucleus.filter;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.HttpHeaders;

/**
 * 
 * this class sole purpose is to fix a bug in jersey:
 * 
 * http://stackoverflow.com/questions/17602432/jersey-and-formparam-not-working-when-charset-is-specified-in-the-content-type
 * 
 * @author johann
 *
 */
public class EncodingRequestWrapper extends HttpServletRequestWrapper {

	public EncodingRequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public Enumeration<String> getHeaders(String name) {
		if (name.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)){
			Enumeration<String> headers = super.getHeaders(name);
			Set<String> newHeaders = new HashSet<>();
			while (headers.hasMoreElements()){
				String val = headers.nextElement();
				if (val.indexOf("charset")!=-1){
					val = val.substring(0, val.indexOf(";"));
				}
				newHeaders.add(val);
			}
			return new Vector<String>(newHeaders).elements();
		}else{
			return super.getHeaders(name);
		}
		
	}

}
