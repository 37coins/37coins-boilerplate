package org.restnucleus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class IndentJsonProvider extends JacksonJsonProvider {
    public IndentJsonProvider() {
        super(
                new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT),
                new Annotations[]{Annotations.JACKSON, Annotations.JAXB}
        );
    }
}
