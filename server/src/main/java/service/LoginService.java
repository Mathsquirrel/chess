package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthTokenAccess;
import dataaccess.MemoryUserAccess;
import model.*;

import java.util.Objects;
import java.util.UUID;

public class LoginService {

    public LoginRegisterResult login(LoginRequest loginAttempt, MemoryUserAccess userList, MemoryAuthTokenAccess authList) throws DataAccessException {
        UserData retrievedUser = userList.getUser(loginAttempt.username());
        if(retrievedUser != null){
            if(Objects.equals(retrievedUser.password(), loginAttempt.password())) {
                String authToken = UUID.randomUUID().toString();
                AuthData newAuth = new AuthData(authToken, retrievedUser.username());
                authList.createAuth(newAuth);
                return new LoginRegisterResult(retrievedUser.username(), authToken);
            }
            // If password with username doesn't match
            throw new DataAccessException("Error: Invalid Login Credentials");
        }
        // If Username doesn't exist
        throw new DataAccessException("Error: Invalid Login Credentials");
    }
}
