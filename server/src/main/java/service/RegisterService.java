package service;

import dataaccess.*;
import model.*;
import org.eclipse.jetty.util.log.Log;

import java.util.UUID;

public class RegisterService {

    public LoginRegisterResult register(RegisterRequest registration, MemoryUserAccess userList, MemoryAuthTokenAccess authList) throws BadRequestException, AlreadyTakenException {
        LoginRegisterResult registerResult = new LoginRegisterResult(null, null);
        if(registration.username() == null || registration.password() == null || registration.email() == null){
            throw new BadRequestException("{Error: bad request}");
        }
        if(userList.getUser(registration.username()) == null){
            // If the user doesn't exist, create one and add it to the database
            userList.createUser(new UserData(registration.username(), registration.password(), registration.email()));
            String authToken = UUID.randomUUID().toString();
            authList.createAuth(new AuthData(registration.username(), authToken));
            return new LoginRegisterResult(registration.username(), authToken);
        }
        // If username is already taken, throw error
        throw new AlreadyTakenException("{Error: Username Already Taken}");
    }
}
