package com.palominolabs.bradybunch.resources.atmosphere;

import com.palominolabs.bradybunch.EventsLogger;
import com.palominolabs.bradybunch.core.Message;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.client.TrackMessageSizeFilter;
import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;
import org.atmosphere.jersey.JerseyBroadcaster;
import org.atmosphere.jersey.SuspendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// Root of the Atmosphere controlled portion
@Path("/{topic}")
@AtmosphereService(broadcaster = JerseyBroadcaster.class)
@Produces(MediaType.APPLICATION_JSON)
public class ChatResource {
    private static final Logger logger = LoggerFactory.getLogger(ChatResource.class);

    @PathParam("topic")
    private Broadcaster topic;

    @GET
    public SuspendResponse<Message> subscribe() {
        return new SuspendResponse.SuspendResponseBuilder<Message>()
            .broadcaster(topic)
            .outputComments(true)
            .addListener(new EventsLogger())
            .build();
    }

    @POST
    @Broadcast
    public Broadcastable publish(Message message) {
        return new Broadcastable(message, "", topic);
    }
}
