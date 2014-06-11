package com.palominolabs.bradybunch.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

public class Message {
    @JsonProperty("type")
    final String type;

    @JsonProperty("email")
    final String email;

    @JsonProperty("snapshot")
    @Nullable
    final String snapshot;

    public Message(@JsonProperty("type") String type, @JsonProperty("email") String email,
                    @JsonProperty("snapshot") String snapshot) {
        this.type = type;
        this.email = email;
        this.snapshot = snapshot;
    }
}