package com.jsaul.webflux_apirest;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.jsaul.webflux_apirest.models.documents.Category;
import com.jsaul.webflux_apirest.models.documents.Product;
import com.jsaul.webflux_apirest.models.services.IProductService;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class WebfluxApirestApplication implements CommandLineRunner {

	@Autowired 
	private IProductService productService;

	@Autowired 
	private ReactiveMongoTemplate mongoTemplate;

	private static final Logger log = LoggerFactory.getLogger(WebfluxApirestApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WebfluxApirestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		/**
		 * Cargamos datos de prueba en la base de datos, eliminando primero las colecciones existentes.
		 */

		mongoTemplate.dropCollection("products").subscribe();
		mongoTemplate.dropCollection("categories").subscribe();

		Category category1 = new Category("Category 1");
		Category category2 = new Category("Category 2");
		Category category3 = new Category("Category 3");

		Flux.just(category1, category2, category3)
			.flatMap(productService::saveCategory)
			.doOnNext(c -> {
				log.info("Categoria creada : " + c.getName());
			})
			.thenMany(
				Flux.just(
					new Product("Laptop Lenovo IdeaPad", 2499.90, category1),
					new Product("Mouse Logitech inalámbrico", 89.90, category1),
					new Product("Teclado mecánico Redragon", 159.90, category2),
					new Product("Monitor Samsung 27 pulgadas", 899.90, category2),
					new Product("Audífonos Sony Bluetooth", 249.90, category1),
					new Product("Teclado Logitech inalámbrico", 129.90, category3),
					new Product("Disco SSD Kingston 1TB", 299.90, category3),
					new Product("Webcam Logitech Full HD", 199.90, category3)
				)
				.flatMap(p -> {
					p.setCreateAt(new Date());
					return productService.save(p);
				})
			)
			.subscribe(p -> log.info("Insert: " + p.getId() + " " + p.getName()));
	}

}
