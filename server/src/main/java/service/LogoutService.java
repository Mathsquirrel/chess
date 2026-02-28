package service;

import dataaccess.*;
import model.*;

public class LogoutService {

    public void logout(String authToken, MemoryAuthTokenAccess authList) {
        AuthData usersAuth = authList.getAuth(authToken);
        authList.deleteAuth(usersAuth);
    }
}
