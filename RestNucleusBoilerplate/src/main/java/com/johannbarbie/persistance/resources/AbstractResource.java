package com.johannbarbie.persistance.resources;


import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


import org.apache.commons.codec.binary.Hex;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import com.johannbarbie.persistance.dao.GenericRepository;
import com.johannbarbie.persistance.exceptions.EntityNotFoundException;
import com.johannbarbie.persistance.exceptions.IdConflictException;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;
import com.johannbarbie.persistance.exceptions.PersistanceException;

public abstract class AbstractResource extends ServerResource {
	
	public static final int DEFAULT_OFFSET = 0;
	public static final int DEFAULT_LIMIT = 10;

	protected Integer offset = null;
	protected Integer limit = null;
	protected GenericRepository dao = new GenericRepository();
	protected Map<String,String> customParams = new HashMap<String,String>();

//	@Override
//	public void doInit() {
//		super.doInit();
//		// disable cache
//		@SuppressWarnings("unchecked")
//		Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
//		if (responseHeaders == null) {
//		    responseHeaders = new Series<Header>(Header.class);
//		    getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
//		}
//		responseHeaders.add(new Header("Cache-Control","no-cache, no-store"));
//		responseHeaders.add(new Header("Pragma","no-cache"));
//		
//		try {
//			offset = Integer.parseInt((String) getRequestAttributes().get(
//					OFFSET));
//		} catch (Exception e) {
//		}
//		try {
//			limit = Integer
//					.parseInt((String) getRequestAttributes().get(LIMIT));
//			if (limit<1){
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
//						"limit must be integer > 0");
//			}
//		} catch (Exception e) {
//		}
//	}

	@Override
	public void doCatch(Throwable throwable) {
		Throwable cause = (null != throwable.getCause()) ? throwable.getCause()
				: throwable;
		if (cause.getClass().equals(EntityNotFoundException.class)) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND, throwable.getMessage());
		} else if (cause.getClass().equals(ParameterMissingException.class)) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, throwable.getMessage());
		} else if (cause.getClass().equals(IdConflictException.class)) {
			setStatus(Status.CLIENT_ERROR_CONFLICT, throwable.getMessage());
		} else if (cause.getClass().equals(PersistanceException.class)) {
			setStatus(Status.SERVER_ERROR_INTERNAL, "pesistance exception");
		} else {
			throwable.printStackTrace();
			setStatus(getStatusService().getStatus(throwable, this));
		}
	}
}
