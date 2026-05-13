package Exceptions;

/**
 * Thrown when a unit has an invalid configuration or state.
 */
public class InvalidUnitException extends Exception {

    /**
     * Creates the exception with a default message.
     */
    public InvalidUnitException() {
        super("Invalid unit configuration or state.");
    }

    /**
     * Creates the exception with a custom message.
     *
     * @param message description of the invalid state
     */
    public InvalidUnitException(String message) {
        super(message);
    }
}
