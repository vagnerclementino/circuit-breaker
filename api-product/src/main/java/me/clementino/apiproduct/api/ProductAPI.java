package me.clementino.apiproduct.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import me.clementino.apiproduct.domain.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Api(value = "products")
@RequestMapping("/products")
public interface ProductAPI {
    @ApiOperation(value = "List all products",
            nickname = "getAllProducts",
            response = ResponseEntity.class,
            tags = {"Products",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List all products", response = ResponseEntity.class),
            @ApiResponse(code = 404, message = "Not Found", response = ResponseEntity.class)})
    @GetMapping(produces = {"application/json"})
    default ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


    @ApiOperation(value = "Get a product by id",
            nickname = "getProductById",
            response = ResponseEntity.class,
            tags = {"Products",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product retrieved with success", response = ResponseEntity.class),
            @ApiResponse(code = 404, message = "Not Found", response = ResponseEntity.class)})
    @GetMapping(value = "/{id}", produces = {"application/json"})
    default ResponseEntity <Product>getProductById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @ApiOperation(value = "Adds a new product",
            nickname = "saveProduct",
            notes = "Adds a new product and return the URI",
            response = ResponseEntity.class,
            tags = {"Products",})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Product created with success", response = ResponseEntity.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Error")})
    @PostMapping(produces = {"application/json"}, consumes = {"application/json"}
    )
    default ResponseEntity<Product> saveProduct(@Valid @RequestBody ProductBody body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @ApiOperation(value = "Update a existent product",
            nickname = "updateProduct",
            notes = "No content response",
            response = ResponseEntity.class,
            tags = {"Products",})
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Product created with success", response = ResponseEntity.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Error")})
    @PutMapping(value = "/{id}", produces = {"application/json"},
            consumes = {"application/json"}
    )
    default ResponseEntity<Product> updateProduct(@PathVariable(value = "id", required = true) Long id,
                                                  @Valid @RequestBody ProductBody body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


    @ApiOperation(value = "Remove a existent Product",
            nickname = "removeProduct",
            notes = "No content response",
            response = ResponseEntity.class,
            tags = {"Products",})
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Product created with success", response = ResponseEntity.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Error")})
    @DeleteMapping(value = "/{id}", produces = {"application/json"})
    default ResponseEntity<Product> removeProduct(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
