package src.exceptions;

/**
 * Da sollevare in caso di lettura di valori non numerici da una tabella del DB
 */
public class MissingNumberException extends Exception {
    public MissingNumberException() {}

    public MissingNumberException(String msg) {
        super(msg);
    }
}
