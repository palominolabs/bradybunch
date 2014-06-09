package com.palominolabs.bradybunch.resources;

import com.palominolabs.bradybunch.core.Person;
import com.palominolabs.bradybunch.views.ChatView;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class ProtectedResource {
    @GET
    @Produces(MediaType.TEXT_HTML)
    @UnitOfWork
    public ChatView chat(@Auth Person person) {
        return new ChatView(ChatView.Template.FREEMARKER, person);
    }
}
