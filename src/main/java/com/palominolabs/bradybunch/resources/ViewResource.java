package com.palominolabs.bradybunch.resources;

import com.google.common.base.Charsets;
import io.dropwizard.views.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/views")
public class ViewResource {
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/utf8.ftl")
    public View freemarkerUTF8() {
        return new View("/views/ftl/utf8.ftl", Charsets.UTF_8) {
        };
    }

    @GET
    @Produces("text/html;charset=ISO-8859-1")
    @Path("/iso88591.ftl")
    public View freemarkerISO88591() {
        return new View("/views/ftl/iso88591.ftl", Charsets.ISO_8859_1) {
        };
    }
}