package me.clementino.apiproduct.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.clementino.apiproduct.domain.dto.PriceDTO;
import me.clementino.apiproduct.domain.dto.PriceServiceErrorResponse;
import me.clementino.apiproduct.exception.PriceServiceException;
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
public class PriceServiceClientImpl implements PriceServiceClient {
    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public BigDecimal fetchPriceByProductId(Long productId) {

        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prices/products/1"))
                .GET()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        if (response == null || response.statusCode() != HttpStatus.OK.value()) {
            try {
                var responseError = new ObjectMapper().readValue(response.body(), PriceServiceErrorResponse.class);
                throw new PriceServiceException(response.statusCode(), responseError.getMessage());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        PriceDTO priceDTO = null;
        try {
            priceDTO = new ObjectMapper().readValue(response.body(), PriceDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return priceDTO.getPrice();
    }
}
