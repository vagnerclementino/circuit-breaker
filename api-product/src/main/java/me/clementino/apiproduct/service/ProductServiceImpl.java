package me.clementino.apiproduct.service;

import lombok.extern.slf4j.Slf4j;
import me.clementino.apiproduct.domain.entity.Product;
import me.clementino.apiproduct.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Cacheable("products")
    public Optional<Product> getProductById(Long id) {
        log.debug("Retrieving product - id: {}", id);
        var optProduct = productRepository.findById(id);
        return optProduct
                .map(product -> {
                    product.setSuggestedPrice(Optional.ofNullable(product.getPrice()));
                    return Optional.of(product);
                })
                .orElse(Optional.empty());
    }
}
