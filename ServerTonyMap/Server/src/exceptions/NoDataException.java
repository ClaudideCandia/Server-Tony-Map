package src.exceptions;

/**
 * Eccezione lanciata in caso la tabella passata al costruttore data non possieda esempi.
 */
public class NoDataException extends Exception {
    public NoDataException() {}

    public NoDataException(String msg) {
        super(msg);
    }
}