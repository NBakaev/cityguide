package com.nbakaev.cityguide.poi.server;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import com.nbakaev.cityguide.city.City;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.poi.PoiProvider;

/**
 * Created by Nikita on 10/14/2016.
 */

public class ServerPoiProvider implements PoiProvider {

    private final Context context;
    private ServerPOIsProvider poiProvider;

    public ServerPoiProvider(Context context, Retrofit retrofit) {
        this.context = context;
        poiProvider = retrofit.create(ServerPOIsProvider.class);
    }

    @Override
    public Observable<List<Poi>> getData(double x0, double y0, int radius) {
        final SearchRequestDto searchRequest = new SearchRequestDto();
        searchRequest.setLatitude(x0);
        searchRequest.setLongitude(y0);
        searchRequest.setRadius(radius);
        return poiProvider.getPoiInRadius(searchRequest);
    }

    @Override
    public Observable<Poi> getById(String id) {
        return poiProvider.getPoiById(id);
    }

    @Override
    public Observable<ResponseBody> getIcon(Poi poi) {
        return poiProvider.downloadContent(poi.getImageUrl());
    }

    @Override
    public Observable<ResponseBody> getIcon(City city) {
        return poiProvider.downloadContent(city.getImageUrl());
    }

    @Override
    public Observable<ResponseBody> downloadData(String url) {
        return poiProvider.downloadContent(url);
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
