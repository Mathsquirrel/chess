package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.List;

public class SQLUserAccess implements UserAccess{
    @Override
    public void createUser(UserData u) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public Collection<UserData> getUserList() {
        return List.of();
    }
}
