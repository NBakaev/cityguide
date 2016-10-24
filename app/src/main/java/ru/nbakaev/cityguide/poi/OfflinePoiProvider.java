package ru.nbakaev.cityguide.poi;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Nikita on 10/14/2016.
 */

public class OfflinePoiProvider implements PoiProvider {

    private final Context context;

    public OfflinePoiProvider(Context context) {
        this.context = context;
    }

    @Override
    public Observable<List<Poi>> getData(double x0, double y0, int radius) {
        return null;
    }

    @Override
    public Call<ResponseBody> getIcon(String fullUrl) {
        return null;
    }
}
