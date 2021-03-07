package me.clementino.apiproduct.circuitbreaker;

import java.time.LocalDateTime;
import java.util.function.Function;

public class CircuitBreaker <T,R>{

    private final CircuitBreakerStateStore stateStore;

    public CircuitBreaker(CircuitBreakerStateStore stateStore) {
        this.stateStore = stateStore;
    }

    public boolean isClosed() {
        return stateStore.isClosed();
    }

    public boolean isOpen() {
        return !stateStore.isClosed();
    }

    public R executeAction (T t, Function<T,R> action) {

        if (isOpen()) {
            // The circuit breaker is Open. Check if the Open timeout has expired.
            // If it has, set the state to HalfOpen. Another approach might be to
            // check for the HalfOpen state that had be set by some other operation.
            if (stateStore.getLastStateChangedDate().plusSeconds(60).isBefore(LocalDateTime.now())) {
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
            throw new CircuitBreakerOpenException(stateStore.getLastException());
        }

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
}