package me.clementino.apiproduct.service;

import me.clementino.apiproduct.domain.entity.Product;

import java.math.BigDecimal;
import java.util.Optional;

public interface PriceService {

    Optional<BigDecimal> getPriceByProduct(final Product product);
}
