package com.nbakaev.cityguide.di;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.nbakaev.cityguide.city.City;
import com.nbakaev.cityguide.eventbus.EventBus;
import com.nbakaev.cityguide.eventbus.events.ReInjectPoiProvider;
import com.nbakaev.cityguide.poi.OfflinePoiProvider;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.poi.PoiProvider;
import com.nbakaev.cityguide.poi.server.ServerPoiProvider;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.util.CacheUtils;

import java.util.List;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.nbakaev.cityguide.settings.SettingsService.getServerUrl;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class PoiProviderConfiguration {

    private static final String TAG = "PoiProviderConfiguratio";

    @ApplicationScope
    @Provides
    public PoiProvider poiProvider(Context context, SettingsService settingsService, CacheUtils cacheUtils, EventBus eventBus, Retrofit retrofit) {
        boolean offlineMode = settingsService.isOffline();

        Log.d(TAG, Boolean.toString(offlineMode));

        if (offlineMode) {
            WrappedPoiProvider wrappedPoiProvider = new WrappedPoiProvider(new OfflinePoiProvider(context, cacheUtils));
            initReInjectPoiListener(wrappedPoiProvider, eventBus, settingsService, context, cacheUtils);
            return wrappedPoiProvider;
        } else {
            WrappedPoiProvider wrappedPoiProvider = new WrappedPoiProvider(new ServerPoiProvider(context, retrofit));
            initReInjectPoiListener(wrappedPoiProvider, eventBus, settingsService, context, cacheUtils);
            return wrappedPoiProvider;
        }
    }

    private void initReInjectPoiListener(PoiProvider poiProvider, EventBus eventBus, SettingsService settingsService, Context context, CacheUtils cacheUtils) {
        eventBus.observable(ReInjectPoiProvider.class).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<ReInjectPoiProvider>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ReInjectPoiProvider value) {
                if (poiProvider instanceof WrappedPoiProvider) {
                    if (settingsService.isOffline()) {
                        ((WrappedPoiProvider) poiProvider).setPoiProvider(new OfflinePoiProvider(context, cacheUtils));
                    } else {
                        ((WrappedPoiProvider) poiProvider).setPoiProvider(new ServerPoiProvider(context, defaultRetrofit()));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @ApplicationScope
    @Provides
    public Retrofit defaultRetrofit() {

        String baseUrl = getServerUrl();
        RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.create();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(rxAdapter)
                .build();

        return retrofit;
    }

    private static class WrappedPoiProvider implements PoiProvider {

        private PoiProvider poiProvider;

        public PoiProvider getPoiProvider() {
            return poiProvider;
        }

        public void setPoiProvider(PoiProvider poiProvider) {
            this.poiProvider = poiProvider;
        }

        public WrappedPoiProvider(PoiProvider poiProvider) {
            this.poiProvider = poiProvider;
        }

        @Override
        public Observable<List<Poi>> getData(double x0, double y0, int radius) {
            return poiProvider.getData(x0, y0, radius);
        }

        @Override
        public Observable<Poi> getById(String id) {
            return poiProvider.getById(id);
        }

        @Override
        public Observable<ResponseBody> getIcon(Poi poi) {
            return poiProvider.getIcon(poi);
        }

        @Override
        public Observable<ResponseBody> getIcon(City city) {
            return poiProvider.getIcon(city);
        }

        @Override
        public Observable<ResponseBody> downloadData(String url) {
            return poiProvider.downloadData(url);
        }

        @Override
        public Observable<List<City>> getCities() {
            return poiProvider.getCities();
        }

        @Override
        public Observable<List<Poi>> getPoiFromCity(String cityId) {
            return poiProvider.getPoiFromCity(cityId);
        }
    }
}
