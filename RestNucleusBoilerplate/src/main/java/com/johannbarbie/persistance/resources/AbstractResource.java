package com.johannbarbie.persistance.resources;

import java.io.InputStream;

import org.restlet.Request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johannbarbie.persistance.dao.GenericRepository;
import com.johannbarbie.persistance.dao.Model;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;

public abstract class AbstractResource {

	protected <E extends Model> E parse(InputStream requestBodyStream,
			Class<E> clazz) {
		ObjectMapper om = new ObjectMapper();
		E e;
		try {
			e = om.readValue(requestBodyStream, clazz);
		} catch (Exception e1) {
			throw new ParameterMissingException("can not be parsed.");
		}
		return e;
	}

	protected GenericRepository getDao() {
		return (GenericRepository) Request.getCurrent().getAttributes()
				.get("entityRepository");
	}
}
