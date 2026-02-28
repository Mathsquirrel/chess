package service;
import dataaccess.MemoryAuthTokenAccess;
import dataaccess.MemoryUserAccess;
import model.*;

import java.util.UUID;

public class LoginService {

    public LoginRegisterResult login(LoginRequest loginAttempt, MemoryUserAccess userList, MemoryAuthTokenAccess authList){
        UserData retrievedUser = userList.getUser(loginAttempt.username());
        if(retrievedUser != null){
            String authToken = UUID.randomUUID().toString();
            AuthData newAuth = new AuthData(authToken, retrievedUser.username());
            authList.createAuth(newAuth);
            return new LoginRegisterResult(retrievedUser.username(), authToken);
        }
        return null;
    }
}
