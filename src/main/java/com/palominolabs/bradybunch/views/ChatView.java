package com.palominolabs.bradybunch.views;

import com.palominolabs.bradybunch.core.Person;

import io.dropwizard.views.View;

public class ChatView extends View {
    private final Person person;

    public enum Template{
    	FREEMARKER("freemarker/chat.ftl");

    	private String templateName;

    	private Template(String templateName){
    		this.templateName = templateName;
    	}
    	
    	public String getTemplateName(){
    		return templateName;
    	}
    }

    public ChatView(ChatView.Template template, Person person) {
        super(template.getTemplateName());
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}