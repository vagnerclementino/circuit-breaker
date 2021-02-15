package me.clementino.apiproduct.domain.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Product must have name.")
    @Column(length = 100)
    @Size(min = 10, max = 100)
    private String name;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 5, fraction = 2)
    @NotNull(message = "Product must have price")
    @Column(nullable = false)
    private BigDecimal price;

    @JsonInclude()
    @Transient
    @Builder.Default
    private Optional<BigDecimal> suggestedPrice = Optional.empty();
}