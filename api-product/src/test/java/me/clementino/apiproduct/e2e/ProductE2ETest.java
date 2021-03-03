package me.clementino.apiproduct.e2e;

import com.github.tomakehurst.wiremock.WireMockServer;
import me.clementino.apiproduct.client.PriceServiceClient;
import me.clementino.apiproduct.domain.entity.Product;
import me.clementino.apiproduct.repository.ProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class ProductE2ETest {
    @Autowired
    private ProductRepository productRepository;

    private static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().port(8080));

    @Autowired
    private PriceServiceClient priceServiceClient;

    @LocalServerPort
    private int port;

    @AfterEach
    public void tearDown() {
        productRepository.deleteAll();
        wiremock.resetAll();
    }

    @BeforeAll
    static void setupClass() {
        wiremock.start();
    }


    @AfterAll
    static void clean() {
        wiremock.shutdown();
    }


    @Test
    public void shouldReturnProduct() throws Exception {
        var product = Product.builder()
                .name("Arquitetura de Software Moderna")
                .price(BigDecimal.valueOf(100.00))
                .build();

        var savedProduct = productRepository.save(product);
        var suggestedPrice = savedProduct.getPrice().add(BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(100)));

        wiremock
                .stubFor(get(urlPathEqualTo("/products/" + savedProduct.getId()))
                        .willReturn(
                                okJson("{\"product_id\":"+ savedProduct.getId() + ",\"price\":" + suggestedPrice.toString() + "}")
                        )
                );

        var productResponse =
                when()
                    .get(String.format("http://localhost:%s/api/v1/products/%s", port, savedProduct.getId()))
                    .then()
                    .statusCode(is(HttpStatus.OK.value()))
                    .extract().response().as(Product.class);

        assertThat(productResponse.getId(), is(equalTo(savedProduct.getId())));
        assertThat(productResponse.getName(), is(equalTo(savedProduct.getName())));
        assertThat(productResponse.getPrice().longValue(), is(equalTo(savedProduct.getPrice().longValue())));

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
