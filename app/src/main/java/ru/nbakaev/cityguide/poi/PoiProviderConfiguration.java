package ru.nbakaev.cityguide.poi;

import android.content.Context;
import android.util.Log;

import dagger.Module;
import dagger.Provides;
import ru.nbakaev.cityguide.di.ApplicationScope;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class PoiProviderConfiguration {

    // TODO: implement settings for offline mode
    // https://trello.com/c/2yKRMygh/3-offline-mode
    private boolean offlineMode = false;
    private final static String TAG = "POI_PROVIDER_CONFIG";

    @ApplicationScope
    @Provides
    public PoiProvider poiProvider(Context context) {
        Log.d(TAG,Boolean.toString(offlineMode));

        if (offlineMode){
            return new OfflinePoiProvider(context);
        }else{
            return new ServerPoiProvider(context);
//            return new MockedPoiProvider(context);
        }
    }

}
