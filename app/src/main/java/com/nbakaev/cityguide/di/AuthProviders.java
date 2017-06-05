package com.nbakaev.cityguide.di;

import android.content.Context;

import com.facebook.CallbackManager;
import com.nbakaev.cityguide.auth.CurrentUserService;
import com.nbakaev.cityguide.auth.server.ServerAccountProvider;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Nikita on 10/11/2016.
 */
@Module
public class AuthProviders {

    @ApplicationScope
    @Provides
    public CallbackManager facebookCallbackManager() {
        return CallbackManager.Factory.create();
    }

    @ApplicationScope
    @Provides
    public CurrentUserService currentUserService(Context context, ServerAccountProvider serverAccountProvider) {
        return new CurrentUserService(context, serverAccountProvider);
    }

    @ApplicationScope
    @Provides
    public ServerAccountProvider serverAccountProvider(Context context, Retrofit retrofit) {
        return new ServerAccountProvider(context, retrofit);
    }

}
