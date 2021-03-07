package me.clementino.apiproduct.circuitbreaker;

import lombok.Getter;

@Getter
public enum CircuitBreakerStateEnum {

    OPEN(1, "Open"),
    HALF_OPEN(2, "HalfOpen"),
    CLOSED(3, "Closed");


    private final int code;
    private final String description;

    private CircuitBreakerStateEnum(int code, String description) {

        this.code = code;
        this.description = description;

    }


    public static CircuitBreakerStateEnum toEnum(Integer code) {

        for (CircuitBreakerStateEnum circuitBreakerStateEnum : CircuitBreakerStateEnum.values()) {

            if (code.equals(circuitBreakerStateEnum.getCode())) {
                return circuitBreakerStateEnum;
            }
        }

        throw new IllegalArgumentException("O código informado " + code + "não é valido");

    }
}