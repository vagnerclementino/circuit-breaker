package me.clementino.apiproduct.service;

import lombok.extern.slf4j.Slf4j;
import me.clementino.apiproduct.circuitbreaker.CircuitBreaker;
import me.clementino.apiproduct.circuitbreaker.CircuitBreakerOpenException;
import me.clementino.apiproduct.client.PriceServiceClient;
import me.clementino.apiproduct.domain.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class PriceServiceImpl implements PriceService {

    private final PriceServiceClient priceServiceClient;
    private final CircuitBreaker<Long, BigDecimal> circuitBreaker;

    @Autowired
    public PriceServiceImpl(PriceServiceClient priceServiceClient, CircuitBreaker<Long, BigDecimal> circuitBreaker) {
        this.priceServiceClient = priceServiceClient;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public Optional<BigDecimal> getPriceByProduct(Product product) {
        BigDecimal price = null;
        try {
            price = circuitBreaker.executeAction(product.getId(), priceServiceClient::fetchPriceByProductId);
        } catch (CircuitBreakerOpenException ex) {
            price = product.getPrice();
            log.info(String.format("The circuit breaker is OPEN. The service exception is %s. The product's price will be %s", ex.getCause().getMessage(), product.getPrice()));
        }
        return Optional.ofNullable(price);
    }
}

