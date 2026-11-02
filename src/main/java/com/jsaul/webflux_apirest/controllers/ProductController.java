package com.jsaul.webflux_apirest.controllers;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.jsaul.webflux_apirest.models.documents.Product;
import com.jsaul.webflux_apirest.models.services.IProductService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final IProductService productService;

    @Value("${config.uploads.path}")
    private String PATH;


    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    /**
     * Guardar un producto con imagen
     */
    @PostMapping("/v2")
    public Mono<ResponseEntity<Product>> saveProductWithImage(Product product, @RequestPart FilePart file) {
        
        if (product.getCreateAt() == null) {
            product.setCreateAt(new Date());
        }

        product.setPhoto(
            UUID.randomUUID().toString() + "-" +
            file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", "")
        );

        return file
            .transferTo(new File(PATH +  product.getPhoto()))
            .then(productService.save(product))
            .map(p -> {
                return ResponseEntity
                    .created(URI.create("/api/product".concat(p.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(p);
        });
    }


    /**
     * Carga de archivo [IMAGEN] para un producto
     */
    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Product>> upload(@PathVariable String id, @RequestPart FilePart file) {

        return productService.findById(id)
            .flatMap(p -> {
                p.setPhoto(
                    UUID.randomUUID().toString() + "-" +
                    file.filename()
                        .replace(" ", "")
                        .replace(":", "")
                        .replace("\\", "")
                );

                return file
                    .transferTo(new File(PATH +  p.getPhoto()))
                    .then(productService.save(p));
            })
            .map(p -> ResponseEntity.ok(p))
            .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @GetMapping 
    public Mono<ResponseEntity<Flux<Product>>> listar() {
        //return Mono.just(ResponseEntity.ok(productService.findAll())); // FORMA 1
        return Mono.just(
            ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.findAll())
        ); 
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> find(@PathVariable(name = "id") String productId) {

        return productService.findById(productId).map(p -> {
            return ResponseEntity.ok(p);
        }).defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> save(@Valid @RequestBody Mono<Product> monoProduct) {

        System.out.println("ProductController.save()");

        Map<String, Object> response = new HashMap<>();

        return monoProduct.flatMap(product -> {

            System.out.println("ProductController.save() - product: " + product);

            if (product.getCreateAt() == null) {
                product.setCreateAt(new Date());
            }

            return productService.save(product).map(p -> {
                response.put("product", p);
                response.put("message", "Product created successfully");
                response.put("timestamp", new Date());

                return ResponseEntity
                    .created(URI.create("/api/product".concat(p.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
            });
        }).onErrorResume(t -> {
                System.out.println("Error occurred while saving product");
                return Mono.just(t).cast(WebExchangeBindException.class)
                    .flatMap(e -> Mono.just(e.getFieldErrors()))
                    .flatMapMany(errors -> Flux.fromIterable(errors))
                    .map(fieldError -> "Field: " + fieldError.getField() + " => " + fieldError.getDefaultMessage())
                    .collectList()
                    .flatMap(list -> {
                        response.put("errors", list);
                        response.put("timestamp", new Date());
                        response.put("status", HttpStatus.BAD_REQUEST.value());

                        return Mono.just(ResponseEntity.badRequest().body(response));
                    });
                    
        });
        

    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> update(@RequestBody Product product, @PathVariable(name = "id") String productId) {
        return productService.findById(productId)
            .flatMap(p -> {
                p.setName(product.getName());
                p.setPrice(product.getPrice());
                p.setCategory(product.getCategory());

                return productService.save(p);
            })
            .map(p -> {
                return ResponseEntity
                    .created(URI.create("/api/product".concat(p.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(p);   
            })
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping ("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return productService.findById(id).flatMap(p -> {
            return productService.delete(p)
                //.then(Mono.just(ResponseEntity.noContent().build()));
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));    
        })
        .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

}
