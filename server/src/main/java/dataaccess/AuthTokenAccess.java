package dataaccess;
import model.AuthData;

public interface AuthTokenAccess {
    void createAuth(AuthData newData) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(AuthData ad) throws DataAccessException;

}
