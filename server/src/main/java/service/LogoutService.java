package service;

import dataaccess.*;
import model.*;

public class LogoutService {

    public void logout(String authToken, MemoryAuthTokenAccess authList) throws DataAccessException {
        AuthData usersAuth = authList.getAuth(authToken);
        if(usersAuth == null){
            // If not logged in, throw exception
            throw new DataAccessException("Error: User Not Logged In");
        }
        authList.deleteAuth(usersAuth);
    }
}
