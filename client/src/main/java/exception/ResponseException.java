package exception;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
    }

    public ResponseException(String message) {
        super(message);
    }
}