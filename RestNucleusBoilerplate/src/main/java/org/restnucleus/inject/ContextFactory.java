package org.restnucleus.inject;

import org.restlet.Context;
import org.restlet.ext.jaxrs.JaxRsApplication;

public interface ContextFactory {
	JaxRsApplication create(Context context);
}
