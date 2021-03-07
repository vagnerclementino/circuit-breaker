package me.clementino.apiproduct.circuitbreaker;

import lombok.Getter;

@Getter
public class CircuitBreakerOpenException extends RuntimeException{

    private final Throwable cause;
    public CircuitBreakerOpenException(Throwable cause) {
        this.cause = cause;
    }
}
