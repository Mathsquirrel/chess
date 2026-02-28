package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryAuthTokenAccess implements AuthTokenAccess{
    Collection<AuthData> authtokenList = new ArrayList<>();// List of Authtoken objects or authorizations

    public void createAuth(AuthData newData) {
        // Add new AuthData to authtokenList
        authtokenList.add(newData);
    }

    public AuthData getAuth(String authToken) {
        // If authtoken in authtokenList, return it
        for(AuthData checker : authtokenList){
            if(Objects.equals(checker.authToken(), authToken)){
                return checker;
            }
        }
        // If not, return null
        return null;
    }

    public void deleteAuth(AuthData ad) {
        // Removes "ad" from the authtokenList
        authtokenList.remove(ad);
    }

    public void clearAuth(){
        authtokenList.clear();
    }
}
