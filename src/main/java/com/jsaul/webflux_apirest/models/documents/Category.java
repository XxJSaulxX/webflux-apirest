package com.jsaul.webflux_apirest.models.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

@Document (collection = "categories")
public class Category {

    @Id 
    @NotBlank  
    private String id;

    private String name;

    /**
     * Constructors
     */
    
    public Category() {

    }

    public Category(String name) {
        this.name = name;    
    }

    /**
     * Getters and Setters
     */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    

}