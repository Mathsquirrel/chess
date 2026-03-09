package dataaccess;
import exception.ResponseException;
import model.*;

import java.util.Collection;

public interface UserAccess {

    // All user related access methods like getting User
    void createUser(UserData u) throws ResponseException;
    UserData getUser(String username)throws ResponseException;
    void clear()throws ResponseException;
    Collection<UserData> getUserList()throws ResponseException;
}
