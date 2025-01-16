package src.exceptions;

/**
 * Da sollevare in caso di lettura di una tabella vuota dal DB
 */
public class EmptySetException extends Exception {
    public EmptySetException() {}

    public EmptySetException(String msg) {
        super(msg);
    }
}
