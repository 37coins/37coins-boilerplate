package com.johannbarbie.persistance.dao;


/**
 * defines minimal requirements for a model class.
 * 
 * @author johba
 *
 */
public interface IModel {

	/*
	 * get the Object Id
	 */
	public Long getId();
	/*
	 * don't touch!
	 */
	public IModel setId(Long id);
	/*
	 * this function will be called with the object to be updated.
	 * expect partual updates and be sure to check every property, by don't touch id;
	 */
	public void update(Model newInstance);
}
