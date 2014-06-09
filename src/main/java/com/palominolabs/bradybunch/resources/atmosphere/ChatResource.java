package com.palominolabs.bradybunch.resources.atmosphere;

import com.palominolabs.bradybunch.core.Message;
import com.palominolabs.bradybunch.core.Response;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// Root of the Atmosphere controlled portion
@Path("/")
public class ChatResource {
    @Suspend(contentType = MediaType.APPLICATION_JSON)
    @GET
    public String suspend() {
        return "";
    }

    @Broadcast(writeEntity = false)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response broadcast(Message message) {
        return new Response(message.author, message.message);
    }
}
