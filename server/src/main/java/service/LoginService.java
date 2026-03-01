package service;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthTokenAccess;
import dataaccess.MemoryUserAccess;
import model.*;

import java.util.Objects;
import java.util.UUID;

public class LoginService {

    public LoginRegisterResult login(LoginRequest loginAttempt, MemoryUserAccess userList, MemoryAuthTokenAccess authList) throws DataAccessException, BadRequestException {
        UserData retrievedUser = userList.getUser(loginAttempt.username());
        if(loginAttempt.username() == null || loginAttempt.password() == null){
            throw new BadRequestException("{Error: bad request}");
        }
        if(retrievedUser != null){
            if(Objects.equals(retrievedUser.password(), loginAttempt.password())) {
                String authToken = UUID.randomUUID().toString();
                AuthData newAuth = new AuthData(authToken, retrievedUser.username());
                authList.createAuth(newAuth);
                return new LoginRegisterResult(retrievedUser.username(), authToken);
            }
            // If password with username doesn't match
            throw new DataAccessException("{Error: unauthorized}");
        }
        // If Username doesn't exist
        throw new DataAccessException("{Error: unauthorized}");
    }
}
