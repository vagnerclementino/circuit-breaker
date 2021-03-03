package me.clementino.apiproduct.configuration;

import lombok.extern.slf4j.Slf4j;
import me.clementino.apiproduct.domain.entity.Product;
import me.clementino.apiproduct.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
@Slf4j
@Profile("!test")
public class LoadDatabase {
    @Bean
    public CommandLineRunner initDatabase(final ProductRepository productRepository) {
        return args -> {
            var product = Product.builder()
                    .id(1L)
                    .name("Arquitetura de Software Moderna")
                    .price(BigDecimal.valueOf(100.00))
                    .build();
            log.info("Preloading " +  productRepository.save(product).toString());
        };
    }
}