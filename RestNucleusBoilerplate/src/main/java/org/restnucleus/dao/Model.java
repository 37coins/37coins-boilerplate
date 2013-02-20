package org.restnucleus.dao;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This is the mother of all persistable objects. 
 * Here we manage primary key, and keep helper functions.
 * 
 * @author johba
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
@JsonInclude(Include.NON_NULL)
public abstract class Model implements Serializable, IModel {

	private static final long serialVersionUID = 8281384108777554727L;

	// Index, Primary Key, Auto Increment, Not null
	@Persistent(primaryKey = "true", valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	private Long creationTime;

	public Model() {
		id = -1;
		creationTime = System.currentTimeMillis();
	}


	public Long getId() {
		if (id >= 0) {
			return id;
		} else {
			return null;
		}
	}

	public Model setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getCreationTime() {
		return creationTime;
	}

	public Model setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
		return this;
	}

}