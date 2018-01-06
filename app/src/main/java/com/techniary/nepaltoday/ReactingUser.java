package com.techniary.nepaltoday;

/**
 * Created by sanjiv on 1/6/18.
 */

public class ReactingUser {
    private String UserID;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public ReactingUser() {
    }

    public ReactingUser(String userID) {

        UserID = userID;
    }
}
