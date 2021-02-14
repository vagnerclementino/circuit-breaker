package me.clementino.apiproduct.controller;

import me.clementino.apiproduct.api.ProductAPI;
import me.clementino.apiproduct.domain.entity.Product;
import me.clementino.apiproduct.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ProductController implements ProductAPI {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<Product> getProductById(@PathVariable(value = "id") Long id) {
        var optionalProduct = productService.getProductById(id);
        return optionalProduct
                .map(product -> ResponseEntity
                        .ok()
                        .body(product)
                )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
    }
}
