package net.johannbarbie.persistance.resources;


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

import net.johannbarbie.persistance.dao.GenericRepository;
import net.johannbarbie.persistance.exceptions.EntityNotFoundException;
import net.johannbarbie.persistance.exceptions.IdConflictException;
import net.johannbarbie.persistance.exceptions.ParameterMissingException;
import net.johannbarbie.persistance.exceptions.PersistanceException;

import org.apache.commons.codec.binary.Hex;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

public abstract class AbstractResource extends ServerResource {

	public static final String DEFAULT_COOKIE_DOMAIN = ".johannbarbie.net";
	public static final int DEFAULT_COOKIE_MAX_AGE = -1; //-1 equals browser session
	public static final int DEFAULT_OFFSET = 0;
	public static final int DEFAULT_LIMIT = 10;

	public static final String ID = "id";
	public static final String ID2 = "2id";
	public static final String ID3 = "3id";
	public static final String OFFSET = "offset";
	public static final String LIMIT = "limit";

	private static final String SECRET = "anAwfullyStaticSecretRightHereInTheCode!";
	private static SecretKey S_KEY = null;
	static {
		SecretKeyFactory factory = null;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(SECRET.toCharArray(),
					SECRET.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			S_KEY = new SecretKeySpec(tmp.getEncoded(), "AES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String ENCRYPT(String message) throws Exception {
		Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
		aes.init(Cipher.ENCRYPT_MODE, S_KEY);
		byte[] ciphertext = aes.doFinal(message.getBytes());
		return Hex.encodeHexString(ciphertext);
	}

	public static String DECRYPT(String cipher) throws Exception {
		Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
		aes.init(Cipher.DECRYPT_MODE, S_KEY);
		byte[] ciphertext = Hex.decodeHex(cipher.toCharArray());
		String cleartext = new String(aes.doFinal(ciphertext));
		return cleartext;
	}

	protected Long id = null;
	protected Long id2 = null;
	protected String id3 = null;
	protected Integer offset = null;
	protected Integer limit = null;
	protected GenericRepository dao = new GenericRepository();
	protected Map<String,String> customParams = new HashMap<String,String>();

	@Override
	public void doInit() {
		super.doInit();
		// disable cache
		@SuppressWarnings("unchecked")
		Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
		if (responseHeaders == null) {
		    responseHeaders = new Series<Header>(Header.class);
		    getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
		}
		responseHeaders.add(new Header("Cache-Control","no-cache, no-store"));
		responseHeaders.add(new Header("Pragma","no-cache"));
		
		// authentication
		try {
			id = Long.parseLong((String) getRequestAttributes().get(ID));
		} catch (Exception e) {
		}
		try {
			id2 = Long.parseLong((String) getRequestAttributes().get(ID2));
		} catch (Exception e) {
		}
		try {
			id3 = URLDecoder.decode((String) getRequestAttributes().get(ID3),"UTF-8");
		} catch (Exception e) {
		}
		try {
			offset = Integer.parseInt((String) getRequestAttributes().get(
					OFFSET));
		} catch (Exception e) {
		}
		try {
			limit = Integer
					.parseInt((String) getRequestAttributes().get(LIMIT));
			if (limit<1){
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
						"limit must be integer > 0");
			}
		} catch (Exception e) {
		}
	}

	protected String getCookieValue(String cookieName) {
		String crypt = null;
		if (null != getCookies()) {
			for (Cookie c : getCookies()) {
				if (c.getName().equals(cookieName)) {
					if (c.getValue().length() > 1)
						crypt = c.getValue();
				}
			}
		}
		// flash has difficulties with cookies, try request params instead
		if (null == crypt && null != getRequest()) {
			Form queryParams = null;
			queryParams = getRequest().getResourceRef().getQueryAsForm();
			crypt = queryParams.getFirstValue(cookieName);
		}
		if (null == crypt) {
			return null;
		}
		try {
			return DECRYPT(crypt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED,
					"could not decrypt session token");
		}
		return null;
	}

	protected void setCookie(String token, String cookieName, String cookieDomain, int cookieMaxAge) {
		CookieSetting cS = new CookieSetting(0, cookieName, token);
		cS.setPath("/");
		cS.setMaxAge(cookieMaxAge);
		cS.setDomain(cookieDomain);
		this.getResponse().getCookieSettings().add(cS);
	}

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
