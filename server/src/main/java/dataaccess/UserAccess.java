package dataaccess;
import model.*;
import java.util.Collection;

public interface UserAccess {

    // All user related access methods like getting User
    void createUser(UserData u);
    UserData getUser(String username);
    void clear();
    Collection<UserData> getUserList();
}
