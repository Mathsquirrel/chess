package dataaccess;
import model.*;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface UserAccess {

    // All user related access methods like getting User
    void createUser(UserData u) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear();
    Collection<UserData> getUserList();
}
