package service;

import dataaccess.*;
import model.*;

public class LogoutService {

    public void logout(String authToken, MemoryAuthTokenAccess authList) throws DataAccessException {
        AuthData usersAuth = authList.getAuth(authToken);
        authList.deleteAuth(usersAuth);
    }
}
