package net.johannbarbie.persistance.stub;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.routing.Filter;
import org.restlet.util.Series;

public class OriginFilter extends Filter {

	public OriginFilter(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected int beforeHandle(Request request, Response response) {
		if (Method.OPTIONS.equals(request.getMethod())) {
			Series<Header> requestHeaders = (Series<Header>) request.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
			String origin = requestHeaders.getFirstValue("Origin", true);
			if(origin == null) {
				origin = "*";
			}

			Series<Header> responseHeaders = (Series<Header>) response.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
			if (responseHeaders == null) {
				responseHeaders = new Series<Header>(Header.class);
				response.getAttributes().put("org.restlet.http.headers",
						responseHeaders);
			}
			responseHeaders.add("Access-Control-Allow-Origin", origin);
			responseHeaders.add("Access-Control-Allow-Methods", "*");
			responseHeaders.add("Access-Control-Allow-Headers", "*");
			responseHeaders.add("Access-Control-Allow-Credentials", "true");
			responseHeaders.add("Access-Control-Max-Age", "60");
			response.setEntity(new EmptyRepresentation());
			return SKIP;
		}

		return super.beforeHandle(request, response);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void afterHandle(Request request, Response response) {
		if (!Method.OPTIONS.equals(request.getMethod())) {
			Series<Header> requestHeaders = (Series<Header>) request.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
			String origin = requestHeaders.getFirstValue("Origin", true);
			if(origin == null) {
				origin = "*";
			}

			Series<Header> responseHeaders = (Series<Header>) response.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
			if (responseHeaders == null) {
				responseHeaders = new Series<Header>(Header.class);
				response.getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS,
						responseHeaders);
			}
			responseHeaders.add("Access-Control-Allow-Origin", origin);
			responseHeaders.add("Access-Control-Allow-Methods", "*");
			responseHeaders.add("Access-Control-Allow-Headers", "*");
			responseHeaders.add("Access-Control-Allow-Credentials", "true");
			responseHeaders.add("Access-Control-Max-Age", "60");
		}
		super.afterHandle(request, response);
	}
}
