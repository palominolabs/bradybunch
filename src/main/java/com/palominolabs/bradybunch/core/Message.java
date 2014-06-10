package com.palominolabs.bradybunch.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {
    @JsonProperty("type")
    final String type;

    @JsonProperty("email")
    final String email;

    @JsonProperty("name")
    final String name;

    @JsonProperty("snapshot")
    final String snapshot;

    public Message(@JsonProperty("type") String type, @JsonProperty("email") String email,
        @JsonProperty("name") String name, @JsonProperty("snapshot") String snapshot) {
        this.type = type;
        this.email = email;
        this.name = name;
        this.snapshot = snapshot;
    }
}