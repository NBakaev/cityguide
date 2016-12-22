package ru.nbakaev.cityguide.poi.server;

import android.content.Context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.nbakaev.cityguide.poi.City;

import static ru.nbakaev.cityguide.settings.SettingsService.getServerUrl;

/**
 * Created by Наташа on 22.12.2016.
 */

public class ServerCityProvider {

    private final Context context;
    private ServerCitiesProvider cityProvider;

    public ServerCityProvider(Context context) {
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

        cityProvider = retrofit.create(ServerCitiesProvider.class);
    }

    public List<City> getCities()
    {
        return cityProvider.getCities();
    }
}
