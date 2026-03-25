package exception;
import com.google.gson.Gson;

import java.util.HashMap;
public class ResponseException extends Exception {
    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        String message = map.get("message").toString();
        return new ResponseException(message);
    }

    public ResponseException(String message) {
        super(message);
    }
}