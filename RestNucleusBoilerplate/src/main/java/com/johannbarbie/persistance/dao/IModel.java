package com.johannbarbie.persistance.dao;



public interface IModel {

	public Long getId();
	public IModel setId(Long id);
	public void update(Model newInstance);
}
