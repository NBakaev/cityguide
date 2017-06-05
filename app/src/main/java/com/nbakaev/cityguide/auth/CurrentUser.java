package com.nbakaev.cityguide.auth;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Nikita Bakaev on 6/5/2017.
 */
@Entity
public class CurrentUser {

    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String middleName;
    private String facebookId;
    private String googleId;
    private String imageUrl;

    private String username;
    private String password;


    @Generated(hash = 1953988208)
    public CurrentUser(Long id, String firstName, String lastName,
            String middleName, String facebookId, String googleId, String imageUrl,
            String username, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.facebookId = facebookId;
        this.googleId = googleId;
        this.imageUrl = imageUrl;
        this.username = username;
        this.password = password;
    }

    @Generated(hash = 1481753967)
    public CurrentUser() {
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
}
