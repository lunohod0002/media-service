package com.example.media_service.models;


import java.util.Set;

public class Media {
    private MediaType type;

    private String name;
    private String urlRef;

    public Media(MediaType type, String name, String urlRef) {
        this.type = type;
        this.name = name;
        this.urlRef = urlRef;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlRef() {
        return urlRef;
    }

    public void setUrlRef(String urlRef) {
        this.urlRef = urlRef;
    }
}
