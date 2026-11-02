package com.jsaul.webflux_apirest.models.services;

import com.jsaul.webflux_apirest.models.documents.Category;
import com.jsaul.webflux_apirest.models.documents.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

    Flux<Product> findAll();

    Flux<Product> findAllNameUpperCase();
    Flux<Product> findAllNameUpperCaseRepeat();

    Mono<Product> findById(String productId);

    Mono<Product> save(Product product);

    Mono<Void> delete(Product product);

    Flux<Category> findAllCategories();
    Mono<Category> findCategoryById(String categoryId);
    Mono<Category> saveCategory(Category category);


}