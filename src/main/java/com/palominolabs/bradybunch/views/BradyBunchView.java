package com.palominolabs.bradybunch.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.palominolabs.bradybunch.core.Person;
import io.dropwizard.views.View;

import java.util.List;

public class BradyBunchView extends View {
    private String people;
    private Person currentUser;

    public enum Template {
    	FREEMARKER("freemarker/bradybunch.ftl");

    	private String templateName;

    	private Template(String templateName){
    		this.templateName = templateName;
    	}

    	public String getTemplateName(){
    		return templateName;
    	}
    }

    public BradyBunchView(BradyBunchView.Template template, ObjectMapper objectMapper, Person currentUser, List<Person> people) {
        super(template.getTemplateName());

        try {
            this.people = objectMapper.writeValueAsString(people);
        } catch (JsonProcessingException e) {
            Throwables.propagate(e);
        }

        this.currentUser = currentUser;
    }

    public Person getCurrentUser() {
        return currentUser;
    }

    public String getPeople() {
        return people;
    }
}