package src.exceptions;

/**
 * Da sollevare in caso di errore nella connessione con il Database
 */
public class DatabaseConnectionException extends Exception{
    public DatabaseConnectionException() {}

    public DatabaseConnectionException(String msg) {
        super(msg);
    }
}
