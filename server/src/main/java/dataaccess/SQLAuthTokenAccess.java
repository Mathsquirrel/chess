package dataaccess;

import model.AuthData;

public class SQLAuthTokenAccess implements AuthTokenAccess{
    @Override
    public void createAuth(AuthData newData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData ad) throws DataAccessException {

    }

    @Override
    public void clearAuth() {

    }
}
