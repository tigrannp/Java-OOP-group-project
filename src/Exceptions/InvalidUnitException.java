package Exceptions;

public class InvalidUnitException extends Exception {

    public InvalidUnitException() {
        super("Invalid unit configuration or state.");
    }

    public InvalidUnitException(String message) {
        super(message);
    }
}