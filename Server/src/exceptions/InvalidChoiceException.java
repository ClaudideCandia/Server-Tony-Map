package src.exceptions;

/**
 * da sollevare in caso si scelga un'opzione non valida
 */
public class InvalidChoiceException extends Exception{
    public InvalidChoiceException() {}

    public InvalidChoiceException(String msg) {
        super(msg);
    }
}
