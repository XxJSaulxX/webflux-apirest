package com.jsaul.webflux_apirest.models.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Document (collection = "products")
public class Product {

    @Id 
    private String id;

    @NotEmpty 
    private String name;

    @NotNull
    private Double price;

    @DateTimeFormat (pattern = "yyyy-MM-dd")
    private Date createAt;

    @Valid // si validamos el Product entonces tambien se validara los atributos controlados del entitie en  Category
    @NotNull 
    private Category category;

    private String photo;

    /**
     * Constructors
     */

    public Product() {

    }

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public Product(String name, Double price, Category category) {
        this(name, price);
        this.category = category;
    }


    /**
     * Getters an Setters
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    

}