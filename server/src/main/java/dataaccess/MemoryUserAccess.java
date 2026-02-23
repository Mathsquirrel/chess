package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryUserAccess implements UserAccess{
    Collection<UserData> userList = new ArrayList<>(); // List of UserData objects or users
    public void createUser(UserData u) throws DataAccessException {
        // Add user "u" to userList
        userList.add(u);
    }

    public UserData getUser(String username) throws DataAccessException {
        // If user exists in database, return it
        for(UserData checker : userList) {
            if (Objects.equals(checker.username(), username)) {
                return checker;
            }
        }// else
        return null;
    }

    public void clear() throws DataAccessException {
        // Clears users from database
        userList = new ArrayList<>();
    }
}
