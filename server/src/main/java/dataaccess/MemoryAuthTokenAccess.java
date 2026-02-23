package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryAuthTokenAccess implements AuthTokenAccess{
    Collection<AuthData> authtokenList = new ArrayList<>();// List of Authtoken objects or authorizations

    public void createAuth(AuthData newData) throws DataAccessException {
        // Add new AuthData to authtokenList
        authtokenList.add(newData);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        // If authtoken in authtokenList, return it
        for(AuthData checker : authtokenList){
            if(Objects.equals(checker.authToken(), authToken)){
                return checker;
            }
        }// else
        return null;
    }

    public void deleteAuth(AuthData ad) throws DataAccessException {
        // Removes "ad" from the authtokenList
        authtokenList.remove(ad);
    }
}
