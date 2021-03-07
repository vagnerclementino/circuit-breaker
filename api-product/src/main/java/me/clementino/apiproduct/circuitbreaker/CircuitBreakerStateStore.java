package me.clementino.apiproduct.circuitbreaker;

import java.time.LocalDateTime;

public interface CircuitBreakerStateStore {


    // This indicates the current state of the circuit breaker, and will be either Open, HalfOpen, or Closed
    CircuitBreakerStateEnum getState();

    // The Trip method switches the state of the circuit breaker to the open state and records the exception
    // that caused the change in state, together with the date and time that the exception occurred.
    void trip(Exception ex);

    // The Reset method closes the circuit breaker
    void reset();

    // The HalfOpen method sets the circuit breaker to half open
    void halfOpen();

    // The IsClosed  should return true if the circuit breaker is closed, but false if it's open or half open
    boolean isClosed();

    // This returns the last date which the circuit breaker's status was changed
    LocalDateTime getLastStateChangedDate();

    // This returns the last exception received by the circuit breaker
    Exception getLastException();
}
