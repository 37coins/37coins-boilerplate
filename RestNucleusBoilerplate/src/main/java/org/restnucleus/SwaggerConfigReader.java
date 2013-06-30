package org.restnucleus;

import javax.servlet.ServletConfig;
import com.wordnik.swagger.jaxrs.ConfigReader;

public class SwaggerConfigReader extends ConfigReader {
	public SwaggerConfigReader(ServletConfig config) {
	}

	// use whatever logic you like to set the base path
	@Override
	public String basePath() {
		return "http://localhost:8080/api";
	}

	@Override
	public String swaggerVersion() {
		return com.wordnik.swagger.core.SwaggerSpec.version();
	}

	// set your api version here
	@Override
	public String apiVersion() {
		return "1.2.3";
	}

	// if you only want to scan certain model packages, you can include them in
	// a CSV-formatted string
	// like com.myapp.models,com.yourapp.stuff
	// otherwise, return null
	@Override
	public String modelPackages() {
		return null;
	}

	// if you have a filter class to handle access to apis, return it as a
	// string
	@Override
	public String apiFilterClassName() {
		return null;
	}
}