package me.clementino.apiproduct.service;

import lombok.extern.slf4j.Slf4j;
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

    @Autowired
    public PriceServiceImpl(PriceServiceClient priceServiceClient) {
        this.priceServiceClient = priceServiceClient;
    }

    @Override
    public Optional<BigDecimal> getPriceByProduct(Product product) {
        return Optional.ofNullable(priceServiceClient.fetchPriceByProductId(product.getId()));
    }
}

