package com.jsaul.webflux_apirest.models.services;

import org.springframework.stereotype.Service;

import com.jsaul.webflux_apirest.models.dao.CategoryDao;
import com.jsaul.webflux_apirest.models.dao.ProductDao;
import com.jsaul.webflux_apirest.models.documents.Category;
import com.jsaul.webflux_apirest.models.documents.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service 
public class ProductServiceImpl implements IProductService {

    private final ProductDao productDao;
    private final CategoryDao categoryDao;

    public ProductServiceImpl(ProductDao productDao, CategoryDao categoryDao) {
        this.productDao  = productDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public Flux<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public Mono<Product> findById(String productId) {
        return productDao.findById(productId);
    }

    @Override
    public Mono<Product> save(Product product) {
        return productDao.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        return productDao.delete(product);
    }

    @Override
    public Flux<Product> findAllNameUpperCase() {
        return productDao.findAll()
            .map(product -> {
                product.setName(product.getName().toUpperCase());
                return product;
            });
    }

    @Override
    public Flux<Product> findAllNameUpperCaseRepeat() {
        return this.findAllNameUpperCase().repeat(5000);
    }

    @Override
    public Flux<Category> findAllCategories() {
        return categoryDao.findAll();
    }

    @Override
    public Mono<Category> findCategoryById(String categoryId) {
        return categoryDao.findById(categoryId);
    }

    @Override
    public Mono<Category> saveCategory(Category category) {
        return categoryDao.save(category);
    }

}
