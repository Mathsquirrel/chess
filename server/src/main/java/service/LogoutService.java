package service;

import dataaccess.*;
import exception.ResponseException;
import model.*;

public class LogoutService {

    public void logout(String authToken, AuthTokenAccess authList) throws ResponseException {
        AuthData usersAuth = authList.getAuth(authToken);
        authList.deleteAuth(usersAuth);
    }
}
