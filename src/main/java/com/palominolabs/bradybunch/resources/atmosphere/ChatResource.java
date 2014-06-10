package com.palominolabs.bradybunch.resources.atmosphere;

import com.palominolabs.bradybunch.core.Message;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// Root of the Atmosphere controlled portion
@Path("/{roomname}")
public class ChatResource {
    private static final Logger logger = LoggerFactory.getLogger(ChatResource.class);

    @Suspend(contentType = MediaType.APPLICATION_JSON)
    @GET
    public String suspend() {
        return "";
    }

    @Broadcast(writeEntity = false)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Message broadcast(@PathParam("roomname") String roomname, Message message) {
        return message;
    }
}
