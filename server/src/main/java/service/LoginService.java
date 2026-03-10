package service;
import dataaccess.*;
import exception.BadRequestException;
import exception.DataAccessException;
import exception.ResponseException;
import model.*;

import java.util.Objects;
import java.util.UUID;

public class LoginService {

    public LoginRegisterResult login(LoginRequest loginAttempt, UserAccess userList, AuthTokenAccess authList)
            throws DataAccessException, BadRequestException, ResponseException {
        UserData retrievedUser = userList.getUser(loginAttempt.username());
        if(loginAttempt.username() == null || loginAttempt.password() == null){
            // If empty form throw error
            throw new BadRequestException("{Error: bad request}");
        }
        if(retrievedUser != null){
            // If the user exists in the database
            if(userList.verifyPasswords(loginAttempt.username(), loginAttempt.password())) {
                // If the passwords match
                String authToken = UUID.randomUUID().toString();
                AuthData newAuth = new AuthData(retrievedUser.username(), authToken);
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
