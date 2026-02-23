package dataaccess;

import model.AuthData;

public class MemoryAuthTokenAccess implements AuthTokenAccess{
    public void createAuth() throws DataAccessException {

    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    public void deleteAuth(AuthData ad) throws DataAccessException {

    }
}
