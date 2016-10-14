package ru.nbakaev.cityguide.poi;

import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Nikita on 10/14/2016.
 */

public class ServerPoiProvider implements PoiProvider {

    private final Context context;
    private ServerPOIsProvider poiProvider;

    public ServerPoiProvider(Context context) {
        this.context = context;

        String baseUrl = "https://s2.nbakaev.ru/api/v1/";
        RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        poiProvider = retrofit.create(ServerPOIsProvider.class);
    }

    @Override
    public Observable<List<Poi>> getData(double x0, double y0, int radius) {
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.setLatitude(x0);
        searchRequest.setLongitude(y0);
        searchRequest.setRadius(radius);
       return poiProvider.getPoiInRadius(searchRequest);
    }
}
