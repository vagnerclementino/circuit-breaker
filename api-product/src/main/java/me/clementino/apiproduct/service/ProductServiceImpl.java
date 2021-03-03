package me.clementino.apiproduct.service;

import lombok.extern.slf4j.Slf4j;
import me.clementino.apiproduct.domain.entity.Product;
import me.clementino.apiproduct.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final PriceService priceService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, PriceService priceService) {
        this.productRepository = productRepository;
        this.priceService = priceService;
    }

    @Override
        public Optional<Product> getProductById(Long id) {

        var optProduct = productRepository.findById(id);
        return optProduct
                .map(product -> {
                    var optSuggestedPrice = priceService.getPriceByProduct(product);
                    product.setSuggestedPrice(optSuggestedPrice.orElse(null));
                    log.info("Retrieving product: {}", product);
                    return Optional.of(product);
                })
                .orElse(Optional.empty());
    }
}
