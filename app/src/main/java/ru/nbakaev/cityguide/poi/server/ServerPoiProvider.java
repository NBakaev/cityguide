package ru.nbakaev.cityguide.poi.server;

import android.content.Context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.nbakaev.cityguide.poi.City;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiProvider;

import static ru.nbakaev.cityguide.settings.SettingsService.getServerUrl;

/**
 * Created by Nikita on 10/14/2016.
 */

public class ServerPoiProvider implements PoiProvider {

    private final Context context;
    private ServerPOIsProvider poiProvider;

    public ServerPoiProvider(Context context) {
        this.context = context;

        String baseUrl = getServerUrl();
        RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.create();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getDeserializationConfig().without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(rxAdapter)
                .build();

        poiProvider = retrofit.create(ServerPOIsProvider.class);
    }

    @Override
    public Observable<List<Poi>> getData(double x0, double y0, int radius) {
        final SearchRequestDto searchRequest = new SearchRequestDto();
        searchRequest.setLatitude(x0);
        searchRequest.setLongitude(y0);
        searchRequest.setRadius(radius);
        Observable<List<Poi>> result = poiProvider.getPoiInRadius(searchRequest);
        return result;
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
    public Observable<ResponseBody> downloadData(String url) {
        return poiProvider.downloadContent(url);
    }

    public Observable<List<City>> getCities()
    {
        return poiProvider.getCities();
    }
}
