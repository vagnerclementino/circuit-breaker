package me.clementino.apiproduct.service;

import me.clementino.apiproduct.domain.entity.Product;

import java.util.Optional;

public interface ProductService {

    Optional<Product> getProductById(Long id);
}