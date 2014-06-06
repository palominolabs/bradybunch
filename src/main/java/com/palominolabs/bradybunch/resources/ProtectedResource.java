package com.palominolabs.bradybunch.resources;

import com.google.common.base.Charsets;
import com.palominolabs.bradybunch.core.Person;
import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class ProtectedResource {
    @GET
    @Produces("text/html;charset=UTF-8")
    public View chat(@Auth Person person) {
        return new View("/assets/chat.html", Charsets.UTF_8) {
        };
    }
}
