package com.palominolabs.bradybunch.resources;

import com.google.common.base.Charsets;
import com.yammer.dropwizard.views.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class RootResource {
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/chat.html")
    public View chat() {
        return new View("/assets/chat.html", Charsets.UTF_8) {
        };
    }
}
