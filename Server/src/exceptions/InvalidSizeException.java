package src.exceptions;


/**
 * da  sollevare  se  si  prova  a  calcolare  la  distanza  tra  due  esempi  di diversa dimensione. 
 */
public class InvalidSizeException extends Exception{
    public InvalidSizeException() {}

    public InvalidSizeException(String msg) {
        super(msg);
    }
}
