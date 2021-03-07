package me.clementino.apiproduct.circuitbreaker;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
@Builder
public class InMemoryCircuitBreakerStateStore implements CircuitBreakerStateStore {

    @NonNull
    @Builder.Default
    private CircuitBreakerStateEnum state = CircuitBreakerStateEnum.CLOSED;
    @Builder.Default
    private Exception lastException = null;
    @Builder.Default
    private LocalDateTime lastStateChangedDate = LocalDateTime.now();


    @Override
    public void trip(Exception ex) {
        this.state = CircuitBreakerStateEnum.OPEN;
        this.lastException = ex;
        this.lastStateChangedDate = LocalDateTime.now();
    }

    @Override
    public void reset() {
        this.state = CircuitBreakerStateEnum.CLOSED;
        this.lastStateChangedDate = LocalDateTime.now();
    }

    @Override
    public void halfOpen() {
        this.state = CircuitBreakerStateEnum.HALF_OPEN;
        this.lastStateChangedDate = LocalDateTime.now();
    }

    @Override
    public boolean isClosed() {
        return state.equals(CircuitBreakerStateEnum.CLOSED);
    }
}
