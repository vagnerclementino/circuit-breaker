package me.clementino.apiproduct.circuitbreaker;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Slf4j
@Component
public class InMemoryCircuitBreakerStateStore implements CircuitBreakerStateStore {

    @NonNull
    private CircuitBreakerStateEnum state;

    public Optional<Exception> getLastException() {
        return Optional.ofNullable(lastException);
    }

    public Optional<LocalDateTime> getLastStateChangedDate() {
        return Optional.ofNullable(lastStateChangedDate);
    }

    private Exception lastException;
    private LocalDateTime lastStateChangedDate;

    public InMemoryCircuitBreakerStateStore() {
        this.state = CircuitBreakerStateEnum.CLOSED;
        this.lastException = null;
        this.lastStateChangedDate = null;
    }


    @Override
    public void trip(Exception ex) {
        changeState(CircuitBreakerStateEnum.OPEN);
        lastException = ex;
    }

    @Override
    public void reset() {
        changeState(CircuitBreakerStateEnum.CLOSED);
    }

    @Override
    public void halfOpen() {
        changeState(CircuitBreakerStateEnum.HALF_OPEN);
    }

    @Override
    public boolean isClosed() {
        return state.equals(CircuitBreakerStateEnum.CLOSED);
    }

    private void changeState(CircuitBreakerStateEnum newState) {
        log.info(String.format("Changing the circuit breaker FROM %s TO %s", state.getDescription(), newState.getDescription()));
        this.state = newState;
        this.lastStateChangedDate = LocalDateTime.now();
    }
}
