package ru.nbakaev.cityguide.poi;

import android.content.Context;
import android.widget.Toast;

import com.google.common.io.Files;

import org.greenrobot.greendao.query.CloseableListIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import ru.nbakaev.cityguide.App;
import ru.nbakaev.cityguide.poi.db.DaoSession;
import ru.nbakaev.cityguide.poi.db.PoiDb;
import ru.nbakaev.cityguide.poi.db.PoiDbDao;

import static ru.nbakaev.cityguide.utils.CacheUtils.getImageCacheFile;

/**
 * Created by Nikita on 10/14/2016.
 */

public class OfflinePoiProvider implements PoiProvider {

    private final Context context;
    public final static int OFFLINE_CHUNK_SIZE = 3_000;
    public final static int MAXIMUM_SQL_QUERY_LIMIT_RETURN = 3_000;

    public OfflinePoiProvider(Context context) {
        this.context = context;
        Toast.makeText(context, "Used offline mode. Disable to load all actual data", Toast.LENGTH_LONG).show();
    }

    @Override
    public Observable<List<Poi>> getData(final double x0, final double y0, int radius) {
        // if we have large collection, it's better for performance to return some iterator, not read whole list from database
        // so, we can chunk some objects to lists and return
        return Observable.create(new ObservableOnSubscribe<List<PoiDb>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PoiDb>> e) throws Exception {
                DaoSession daoSession = ((App) context).getDaoSession();

//                daoSession.getPoiDbDao().getDatabase().rawQuery("SELECT * FROM POI_DB ORDER BY abs(LATITUDE - (?)) + abs( LONGITUDE - (?)) LIMIT 5000", new String[]{Double.toString(x0), Double.toString(y0)})
                CloseableListIterator<PoiDb> all = daoSession.getPoiDbDao().queryRawCreate(
                        "ORDER BY abs(latitude - (?)) + abs( longitude - (?)) LIMIT " + MAXIMUM_SQL_QUERY_LIMIT_RETURN,
                        Double.toString(x0), Double.toString(y0)
                ).listIterator();

                List<PoiDb> buffer = new ArrayList<>();
                while (all.hasNext()) {
                    PoiDb next = all.next();
                    buffer.add(next);
                    if (buffer.size() == OFFLINE_CHUNK_SIZE) {
                        e.onNext(buffer);
                        buffer = new ArrayList<>();
                    }
                }
                if (buffer.size() > 0){
                    e.onNext(buffer);
                }

                all.close();
            }
        }).subscribeOn(Schedulers.computation()).map(new Function<List<PoiDb>, List<Poi>>() {
            @Override
            public List<Poi> apply(List<PoiDb> poiDbs) throws Exception {
                return PoiDb.toPoiList(poiDbs);
            }
        });
    }

    @Override
    public Observable<Poi> getById(String id) {
        DaoSession daoSession = ((App) context).getDaoSession();

        PoiDb poiDb = daoSession.getPoiDbDao().queryBuilder().where(PoiDbDao.Properties.PoiId.eq(id)).unique();
        if (poiDb == null) {
            return Observable.empty();
        }

        return Observable.just(PoiDb.toPoi(poiDb));
    }

    /**
     * return icon for POI on map from filesystem
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

    @Override
    public Observable<ResponseBody> getIcon(City city) {
        return null;
    }


    @Override
    public Observable<ResponseBody> downloadData(String url) {
        Toast.makeText(context, "Disable offline mode to download content", Toast.LENGTH_LONG).show();
        return Observable.empty();
    }

}
