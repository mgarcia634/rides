package exceptions;

public class InvalidLoginException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidLoginException() {
        super();
    }

    public InvalidLoginException(String message) {
        super(message);
    }
}
