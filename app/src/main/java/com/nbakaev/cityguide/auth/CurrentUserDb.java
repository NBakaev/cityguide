package com.nbakaev.cityguide.auth;

/**
 * Created by Nikita Bakaev on 6/5/2017.
 */

public class CurrentUserDb {

    private String firstName;
    private String lastName;
    private String middleName;
    private String facebookId;
    private String googleId;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}
