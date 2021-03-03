package me.clementino.apiproduct.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.clementino.apiproduct.domain.dto.PriceDTO;
import me.clementino.apiproduct.domain.dto.PriceServiceErrorResponse;
import me.clementino.apiproduct.exception.PriceServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@Slf4j
public class PriceServiceClientImpl implements PriceServiceClient {


    private final String priceServiceHost;

    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Autowired
    private final ObjectMapper mapper;

    public PriceServiceClientImpl( @Value("${service.price.host}") String priceServiceHost, ObjectMapper mapper) {
        this.priceServiceHost = priceServiceHost;
        this.mapper = mapper;
    }

    @Override
    public BigDecimal fetchPriceByProductId(Long productId) {

        var request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/prices/products/%d", priceServiceHost, productId)))
                .GET()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response != null) {
                if (response.statusCode() == HttpStatus.OK.value()) {
                    var priceDTO = mapper.readValue(response.body(), PriceDTO.class);
                    return priceDTO.getPrice();
                } else {
                    var responseError = mapper.readValue(response.body(), PriceServiceErrorResponse.class);
                    throw new PriceServiceException(response.statusCode(), responseError.getMessage());
                }
            } else {
                throw new PriceServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "The response cannot be null");
            }
        } catch (IOException | InterruptedException e) {
            throw new PriceServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), e.getCause());
        }
    }

}
