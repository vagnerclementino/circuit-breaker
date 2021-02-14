package me.clementino.apiproduct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ApiProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiProductApplication.class, args);
    }

}
