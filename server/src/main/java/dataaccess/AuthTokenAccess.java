package dataaccess;
import model.AuthData;

public interface AuthTokenAccess {
    void createAuth(AuthData newData);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData ad);
    void clearAuth();

}
