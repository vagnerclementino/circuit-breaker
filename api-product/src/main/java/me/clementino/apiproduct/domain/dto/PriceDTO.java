package me.clementino.apiproduct.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceDTO {
    @JsonProperty("product_id")
    private Long productId;
    private BigDecimal price;
}
