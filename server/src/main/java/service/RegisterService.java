package service;

import dataaccess.MemoryAuthTokenAccess;
import dataaccess.MemoryUserAccess;
import model.*;
import org.eclipse.jetty.util.log.Log;

import java.util.UUID;

public class RegisterService {

    public LoginRegisterResult register(RegisterRequest registration, MemoryUserAccess userList, MemoryAuthTokenAccess authList){
        LoginRegisterResult registerResult = new LoginRegisterResult(null, null);
        if(userList.getUser(registration.username()) == null){
            // If the user doesn't exist, create one and add it to the database
            userList.createUser(new UserData(registration.username(), registration.password(), registration.email()));
            String authToken = UUID.randomUUID().toString();
            authList.createAuth(new AuthData(registration.username(), authToken));
            return new LoginRegisterResult(registerResult.username(), authToken);
        }
        // THROW ALREADY TAKEN EXCEPTION IF EXISTS
        return null;
    }
}
