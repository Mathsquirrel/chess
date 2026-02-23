package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserAccess implements UserAccess{
    Collection<UserData> userList = new ArrayList<>(); // List of UserData objects or users
    public void createUser(UserData u) throws DataAccessException {
        // Add user "u" to userList
        userList.add(u);
    }

    public UserData getUser(UserData u) throws DataAccessException {
        // If user exists in database, return it
        if(userList.contains(u)){
            return u;
        }else{
            return null;
        }
    }

    public void clear() throws DataAccessException {
        // Clears users from database
        userList = new ArrayList<>();
    }
}
