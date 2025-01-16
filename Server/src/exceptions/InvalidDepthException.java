package src.exceptions;

/**
 * da sollevare se la profondità con cui è stato istanziato il dendrogramma 
 * è superiore al numero di esempi memorizzati nel dataset.
 */
public class InvalidDepthException extends Exception{
    public InvalidDepthException() {}

    public InvalidDepthException(String msg) {
        super(msg);
    }

}
