package dao.error;

public class ConnectionUnavailableException extends RuntimeException{

    public ConnectionUnavailableException(String message) {
        super(message);
    }
}
