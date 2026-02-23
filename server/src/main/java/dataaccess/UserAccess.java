package dataaccess;
import model.*;

import javax.xml.crypto.Data;

public interface UserAccess {

    // All user related access methods like getting User
    void createUser(UserData u) throws DataAccessException;
    UserData getUser(UserData u) throws DataAccessException;
    void clear() throws DataAccessException; // Only Clears User data?

}
