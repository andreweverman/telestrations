package com.example.telestrations.model;

import java.util.List;

public class Notepad {

    private String id;
    private String name;
    private List<String> payloads;

    public Notepad(String id, String name, List<String> payloads) {
        this.id = id;
        this.name = name;
        this.payloads = payloads;
    }

    public String getId() {
        return id;
    }

    public List<String> getPayloads() {
        return payloads;
    }
}
