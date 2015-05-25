package org.restnucleus;

import com.strategicgains.util.date.DateAdapter;
import cz.jirutka.rsql.parser.ParseException;
import cz.jirutka.rsql.parser.RSQLParser;
import org.restnucleus.dao.RNQuery;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

public class RNQueryBean {
    @QueryParam(RNQuery.PAGE)
    public String page;

    @QueryParam(RNQuery.SIZE)
    public String size;

    @QueryParam(RNQuery.SORT)
    public String sort;

    @QueryParam(RNQuery.FILTER)
    public String filter;

    @QueryParam(RNQuery.BEFORE)
    public String createdBefore;

    @QueryParam(RNQuery.AFTER)
    public String createdAfter;


    public RNQuery create() {
        RNQuery q = new RNQuery();
        withPaging(q);
        withSort(q);
        withFilter(q);
        withBeforeAndAfter(q);
        return q;
    }

    public void withBeforeAndAfter(RNQuery q) {
        if (null != createdBefore || null != createdAfter) {
            DateAdapter dateAdapter = new DateAdapter();
            if (null != createdBefore) {
                try {
                    q.setBefore(dateAdapter.parse(createdBefore));
                } catch (java.text.ParseException e) {
                    throw new WebApplicationException(
                            "'before' param is not in ISO 8601 time point format.",
                            javax.ws.rs.core.Response.Status.BAD_REQUEST);
                }
            }
            if (null != createdAfter) {
                try {
                    q.setAfter(dateAdapter.parse(createdAfter));
                } catch (java.text.ParseException e) {
                    throw new WebApplicationException(
                            "'after' param is not in ISO 8601 time point format.",
                            javax.ws.rs.core.Response.Status.BAD_REQUEST);
                }
            }
        }
    }

    public void withFilter(RNQuery q) {
        if (null != filter) {
            try {
                q.addExpression(RSQLParser.parse(filter));
            } catch (ParseException ex) {
                throw new WebApplicationException(
                        "rsql query could not be parsed.",
                        javax.ws.rs.core.Response.Status.BAD_REQUEST);
            }
        }
    }

    /**
     * handle ordering attribute
     * according to Todd Fredrich in "RESTful Best Practices.pdf"
     */
    public void withSort(RNQuery q) {
        if (null != this.sort) {
            StringBuffer sb = new StringBuffer();
            String[] a = this.sort.split("\\|");
            for (String s : a) {
                if (sb.length() > 1)
                    sb.append(", ");
                if (s.charAt(0) == '-') {
                    sb.append(s.substring(1, s.length()));
                    sb.append(" desc");
                } else {
                    sb.append(s);
                    sb.append(" asc");
                }
            }
            q.setOrdering(sb.toString());
        }
    }

    /**
     * handle pagination
     * like proposed here: http://www.baeldung.com/2012/01/18/rest-pagination-in-spring/#httpheaders
     * advantage of following HATEOAS in contrast to range header pagination
     */
    public void withPaging(RNQuery q) {
        Long page = null;
        if (null != this.page) {
            page = Long.parseLong(this.page);
        }
        Long size = null;
        if (null != this.size) {
            size = Long.parseLong(this.size);
        }
        q.setRange(page, size);
    }

}
