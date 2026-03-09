package dataaccess;
import java.sql.*;
import model.AuthData;

public class SQLAuthTokenAccess implements AuthTokenAccess{
    @Override
    public void createAuth(AuthData newData) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData ad) {

    }

    @Override
    public void clearAuth() {

    }
}
