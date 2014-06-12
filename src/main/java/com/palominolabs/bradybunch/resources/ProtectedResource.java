package com.palominolabs.bradybunch.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palominolabs.bradybunch.core.Person;
import com.palominolabs.bradybunch.db.PersonDAO;
import com.palominolabs.bradybunch.views.BradyBunchView;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class ProtectedResource {
    private final PersonDAO personDao;

    private final ObjectMapper objectMapper;

    public ProtectedResource(PersonDAO personDao, ObjectMapper objectMapper) {
        this.personDao = personDao;
        this.objectMapper = objectMapper;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @UnitOfWork
    public BradyBunchView chat(@Auth Person person) {
        return new BradyBunchView(BradyBunchView.Template.FREEMARKER, objectMapper, person, personDao.findAll());
    }
}
