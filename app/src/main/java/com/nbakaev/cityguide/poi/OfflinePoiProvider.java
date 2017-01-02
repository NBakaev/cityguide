package com.nbakaev.cityguide.poi;

import android.content.Context;
import android.widget.Toast;

import com.google.common.io.Files;
import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.city.City;
import com.nbakaev.cityguide.city.CityDB;
import com.nbakaev.cityguide.city.DaoSession;
import com.nbakaev.cityguide.poi.db.PoiDb;
import com.nbakaev.cityguide.poi.db.PoiDbDao;

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

import static com.nbakaev.cityguide.util.CacheUtils.getImageCacheFile;

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
    public Observable<List<Poi>> getData(final double x0, final double y0, final int radius) {
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

                    if (meterDistanceBetweenPoints(x0, y0, next.getLatitude(), next.getLongitude()) > radius) {
                        break;
                    }

                    buffer.add(next);
                    if (buffer.size() == OFFLINE_CHUNK_SIZE) {
                        e.onNext(buffer);
                        buffer = new ArrayList<>();
                    }
                }
                if (buffer.size() > 0) {
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

    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        float pk = (float) (180.f / Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
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
        return Observable.empty();
    }

    @Override
    public Observable<ResponseBody> getIcon(City city) {
        File image = getImageCacheFile(city);
        if (image.exists()) {
            try {
                byte[] bytes = Files.toByteArray(image);
                ResponseBody responseBody = ResponseBody.create(MediaType.parse("image/png"), bytes);
                return Observable.just(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Observable.empty();
    }


    @Override
    public Observable<ResponseBody> downloadData(String url) {
        Toast.makeText(context, "Disable offline mode to download content", Toast.LENGTH_LONG).show();
        return Observable.empty();
    }

    @Override
    public Observable<List<City>> getCities() {
        return Observable.create(new ObservableOnSubscribe<List<CityDB>>() {
            @Override
            public void subscribe(ObservableEmitter<List<CityDB>> e) throws Exception {
                DaoSession daoSession = ((App) context).getDaoSession();

                List<CityDB> loaded = daoSession.getCityDBDao().loadAll();
                if (loaded.size() > 0) {
                    e.onNext(loaded);
                }

            }
        }).subscribeOn(Schedulers.computation()).map(new Function<List<CityDB>, List<City>>() {
            @Override
            public List<City> apply(List<CityDB> cityDBs) throws Exception {
                return CityDB.toCityList(cityDBs);
            }
        });
    }

    @Override
    public Observable<List<Poi>> getPoiFromCity(final String cityId) {
        return Observable.create(new ObservableOnSubscribe<List<PoiDb>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PoiDb>> e) throws Exception {
                DaoSession daoSession = ((App) context).getDaoSession();

                List<PoiDb> loaded = daoSession.getPoiDbDao().queryBuilder().where(PoiDbDao.Properties.CityId.eq(cityId)).list();
                if (loaded.size() > 0) {
                    e.onNext(loaded);
                }

            }
        }).subscribeOn(Schedulers.computation()).map(new Function<List<PoiDb>, List<Poi>>() {
            @Override
            public List<Poi> apply(List<PoiDb> poiDbs) throws Exception {
                return PoiDb.toPoiList(poiDbs);
            }
        });
    }

}
