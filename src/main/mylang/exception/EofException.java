package main.mylang.exception;

public class EofException extends Throwable{
    public EofException(String message) {
        super(message);
    }
}
