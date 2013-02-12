package com.johannbarbie.persistance.dao;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Model implements Serializable, IModel {

	private static final long serialVersionUID = 8281384108777554727L;
	
	// Index, Primary Key, Auto Increment, Not null
	@Persistent(primaryKey="true", valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	public Model(){
		id = -1;
	}

	public Long getId() {
		if (id>=0){
			return id;
		}else{
			return null;
		}
	}

	public Model setId(Long id) {
		this.id = id;
		return this;
	}

}