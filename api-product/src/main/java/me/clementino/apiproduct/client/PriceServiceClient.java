package me.clementino.apiproduct.client;

import java.math.BigDecimal;

public interface PriceServiceClient {

    BigDecimal fetchPriceByProductId(final Long productId);
}
