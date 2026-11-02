package com.jsaul.webflux_apirest.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jsaul.webflux_apirest.models.documents.Product;

public interface ProductDao extends ReactiveMongoRepository<Product, String> {

}