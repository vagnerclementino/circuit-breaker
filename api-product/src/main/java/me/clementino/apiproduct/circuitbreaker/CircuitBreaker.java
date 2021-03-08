package me.clementino.apiproduct.circuitbreaker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Function;

@Slf4j
@Component
public class CircuitBreaker<T, R> {

    private final CircuitBreakerStateStore stateStore;
    private final long timeout;

    @Autowired
    public CircuitBreaker(CircuitBreakerStateStore stateStore, @Value("${circuitbreaker.timeout:30}") long timeout) {
        this.stateStore = stateStore;
        this.timeout = timeout;
    }

    public boolean isOpen() {
        return !stateStore.isClosed();
    }

    public R executeAction(T t, Function<T, R> action) {

        log.info(String.format("The circuit breaker current status is: %s", stateStore.getState().getDescription()));

        if (isOpen()) {

            log.info("The circuit breaker is OPEN");
            // The circuit breaker is Open. Check if the Open timeout has expired.
            // If it has, set the state to HalfOpen. Another approach might be to
            // check for the HalfOpen state that had be set by some other operation.
            if (hasTimeoutExpired()) {
                log.info("The Open timeout has expired.");
                // The Open timeout has expired. Allow one operation to execute. Note that, in
                // this example, the circuit breaker is set to HalfOpen after being
                // in the Open state for some period of time. An alternative would be to set
                // this using some other approach such as a timer, test method, manually, and
                // so on, and check the state here to determine how to handle execution
                // of the action.

                try {
                    // Set the circuit breaker state to HalfOpen.
                    stateStore.halfOpen();

                    // Attempt the operation.
                    var r = action.apply(t);

                    // If this action succeeds, reset the state and allow other operations.
                    // In reality, instead of immediately returning to the Closed state, a counter
                    // here would record the number of successful operations and return the
                    // circuit breaker to the Closed state only after a specified number succeed.
                    stateStore.reset();
                    return r;

                } catch (Exception ex) {
                    // If there's still an exception, trip the breaker again immediately.
                    stateStore.trip(ex);
                    // Throw the exception so that the caller knows which exception occurred.
                    throw ex;
                }
            }
            // The Open timeout hasn't yet expired. Throw a CircuitBreakerOpen exception to
            // inform the caller that the call was not actually attempted,
            // and return the most recent exception received.
            log.info("The Open timeout hasn't yet expired. ");
            throw new CircuitBreakerOpenException(stateStore
                    .getLastException()
                    .orElse(new RuntimeException()));
        }

        log.info("The circuit breaker is Closed, execute the action.");
        // The circuit breaker is Closed, execute the action.
        try {
            return action.apply(t);
        } catch (Exception ex) {
            // If an exception still occurs here, simply
            // retrip the breaker immediately.
            stateStore.trip(ex);

            // Throw the exception so that the caller can tell
            // the type of exception that was thrown.
            throw ex;
        }
    }

    private boolean hasTimeoutExpired() {
        return stateStore.
                getLastStateChangedDate()
                .orElseThrow(RuntimeException::new)
                .plusSeconds(timeout)
                .isBefore(LocalDateTime.now());
    }
}