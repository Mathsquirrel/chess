package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryUserAccess implements UserAccess{
    Collection<UserData> userList = new ArrayList<>(); // List of UserData objects or users
    public void createUser(UserData user) {
        // Add user "u" to userList
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData newUser = new UserData(user.username(), hashedPassword, user.email());
        userList.add(newUser);
    }

    public boolean verifyPasswords(String username, String providedClearTextPassword) {
        // read the previously hashed password from the database
        String hashedPassword = getUser(username).password();
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    public UserData getUser(String username) {
        // If user exists in database, return it
        for(UserData checker : userList) {
            if (Objects.equals(checker.username(), username)) {
                return checker;
            }
        }// else
        return null;
    }

    public void clear() {
        // Clears users from database
        userList.clear();
    }

    public Collection<UserData> getUserList(){
        return userList;
    }
}
