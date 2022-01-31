package com.oxiane.formation.devoxx22.refacto.model;

public class Greeting {
    private final long id;
    private final String text;

    public Greeting(long id, String name) {
        this.text = name;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
