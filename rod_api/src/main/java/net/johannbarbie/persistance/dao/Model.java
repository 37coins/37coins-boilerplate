package net.johannbarbie.persistance.dao;

import java.io.Serializable;

public abstract class Model implements Serializable, IModel {
	
	public final static String DEFAULT_PRIMARY_KEY_DB_FIELD = "id";
	/**
	 * serial
	 */
	private static final long serialVersionUID = 1L;
	

}