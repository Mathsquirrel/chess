package dataaccess;
import exception.ResponseException;
import model.AuthData;

public interface AuthTokenAccess {
    void createAuth(AuthData newData) throws ResponseException;
    AuthData getAuth(String authToken) throws ResponseException;
    void deleteAuth(AuthData ad) throws ResponseException;
    void clearAuth() throws ResponseException;

}
