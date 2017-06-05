package com.nbakaev.cityguide.auth;

import android.content.Context;
import android.net.Uri;

import com.facebook.Profile;
import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.auth.server.ServerAccountProvider;
import com.nbakaev.cityguide.city.DaoSession;
import com.nbakaev.cityguide.util.StringUtils;
import com.nbakaev.cityguide.util.UiUtils;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Nikita Bakaev on 6/5/2017.
 */
public class CurrentUserService {

    private Context context;
    private DaoSession daoSession;
    private CurrentUser currentUser;
    private ServerAccountProvider serverAccountProvider;

    public CurrentUserService(Context context, ServerAccountProvider serverAccountProvider) {
        this.context = context;
        this.serverAccountProvider = serverAccountProvider;
        this.daoSession = ((App) context).getDaoSession();
    }

    public CurrentUser getCurrentUser() {
        if (currentUser != null) {
            return currentUser;
        }

        CurrentUser first = daoSession.getCurrentUserDao().queryBuilder().unique();
        if (first != null) {
            currentUser = first;
            return currentUser;
        }
        currentUser = new CurrentUser();
        daoSession.getCurrentUserDao().save(currentUser);
        return currentUser;
    }

    public void saveCurrentUser(CurrentUser currentUser) {
        daoSession.getCurrentUserDao().save(currentUser);
    }

    public void updateFromFacebook() {
        Profile facebookProfile = Profile.getCurrentProfile();
        CurrentUser currentUser = getCurrentUser();

        currentUser.setFacebookId(facebookProfile.getId());
        currentUser.setFirstName(facebookProfile.getFirstName());
        currentUser.setLastName(facebookProfile.getLastName());
        currentUser.setMiddleName(facebookProfile.getMiddleName());

        Uri profilePictureUri = facebookProfile.getProfilePictureUri(UiUtils.dpToPixels(context, 70), UiUtils.dpToPixels(context, 70));
        if (profilePictureUri != null && !StringUtils.isEmpty(profilePictureUri.toString())) {
            currentUser.setImageUrl(profilePictureUri.toString());
        }

        saveCurrentUser(currentUser);
    }

    public Observable<ResponseBody> getUserImage() {
        return this.serverAccountProvider.loadData(getCurrentUser().getImageUrl());
    }

    public String getDisplayName() {
        String firstName = this.getCurrentUser().getFirstName();
        String lastName = this.getCurrentUser().getLastName();

        if (!StringUtils.isEmpty(firstName) && !StringUtils.isEmpty(lastName)) {
            return firstName + " " + lastName;
        } else if (!StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName)) {
            return firstName;
        } else if (StringUtils.isEmpty(firstName) && !StringUtils.isEmpty(lastName)) {
            return lastName;
        }
        return "";
    }

}
