package me.clementino.apiproduct.e2e;

import me.clementino.apiproduct.domain.entity.Product;
import me.clementino.apiproduct.repository.ProductRepository;
import me.clementino.apiproduct.service.PriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProductE2ETest {

    @MockBean
    private ProductRepository productRepository;

    @LocalServerPort
    private int port;

    @MockBean
    private PriceService priceService;


    @Test
    public void shouldReturnProduct() throws Exception {
        var product = Product.builder()
                .id(99999L)
                .name("Arquitetura de Software Moderna")
                .price(BigDecimal.valueOf(100.00))
                .build();

        var suggestedPrice = product
                .getPrice()
                .add(BigDecimal.valueOf(ThreadLocalRandom
                                .current()
                                .nextLong(100)
                        )
                );

        Mockito
                .when(priceService.getPriceByProduct(product))
                .thenReturn(Optional.ofNullable(suggestedPrice));

        Mockito
                .when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));


        var productResponse = when()
                .get(String.format("http://localhost:%s/api/v1/products/%s", port, product.getId()))
                .then()
                .statusCode(is(200))
                .extract()
                .response()
                .as(Product.class);

        assertThat(productResponse.getId(), is(equalTo(product.getId())));
        assertThat(productResponse.getName(), is(equalTo(product.getName())));
        assertThat(productResponse.getPrice().longValue(), is(equalTo(product.getPrice().longValue())));

        assumeTrue(productResponse.getSuggestedPrice().isPresent());

        assertThat(productResponse.getSuggestedPrice().get(), is(equalTo(suggestedPrice)));
    }


    @Test
    public void shouldReturnNotFoundWhenProductDoesExists() throws Exception {

        var invalidProductId = (-1) * new Random().nextInt(100);

        when()
                .get(String.format("http://localhost:%s/api/v1/products/%s", port, invalidProductId))
                .then()
                .statusCode(is(HttpStatus.NOT_FOUND.value()))
                .body("error", is(equalTo("Not Found")))
                .body("path", is(equalTo(String.format("/api/v1/products/%s", invalidProductId))))
                .body("status", is(equalTo(HttpStatus.NOT_FOUND.value())));
    }
}
