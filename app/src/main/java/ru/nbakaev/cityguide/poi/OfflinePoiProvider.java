package ru.nbakaev.cityguide.poi;

import android.content.Context;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.orm.SugarRecord;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import ru.nbakaev.cityguide.poi.db.PoiDb;

import static ru.nbakaev.cityguide.utils.CacheUtils.getImageCacheFile;

/**
 * Created by Nikita on 10/14/2016.
 */

public class OfflinePoiProvider implements PoiProvider {

    private final Context context;

    public OfflinePoiProvider(Context context) {
        this.context = context;
        Toast.makeText(context, "Used offline mode. Disable to load all actual data", Toast.LENGTH_LONG).show();
    }

    @Override
    public Observable<List<Poi>> getData(double x0, double y0, int radius) {

        Iterator<PoiDb> all = SugarRecord.findAll(PoiDb.class);
        List<PoiDb> poiDbs = Lists.newArrayList(all);

        return Observable.fromArray(PoiDb.toPoiList(poiDbs));
    }

    /**
     * return icon for POI on map from url
     *
     * @param poi
     * @return
     */
    @Override
    public Observable<ResponseBody> getIcon(Poi poi) {
        File image = getImageCacheFile(poi);
        if (image.exists()) {
            try {
                byte[] bytes = Files.toByteArray(image);
                ResponseBody responseBody = ResponseBody.create(MediaType.parse("image/png"), bytes);
                return Observable.just(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
