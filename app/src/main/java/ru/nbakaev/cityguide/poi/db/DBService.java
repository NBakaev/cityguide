package ru.nbakaev.cityguide.poi.db;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ru.nbakaev.cityguide.App;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.settings.SettingsService;

/**
 * Created by ya on 11/24/2016.
 */
public class DBService {

    private Context context;
    private DaoSession daoSession;
    private SettingsService settingsService;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public DBService(Context context, SettingsService settingsService) {
        this.context = context;
        daoSession = ((App) context).getDaoSession();
        this.settingsService = settingsService;
    }

    /**
     * add/update every POI to database
     * @param data POIs to save local
     * @return true when all saved
     */
    public Future<Boolean> cachePoiToDB(final List<Poi> data) {
        final List<PoiDb> of = PoiDb.of(data);

        if (data == null || data.isEmpty()){
            return null;
        }

        if (settingsService.getSettings().isOffline()) {
            return null;
        }

         Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
//                    it's better to save all in one transaction(daoSession.getPoiDbDao().saveInTx(of)), but ORM can't insert object if it have id(is should be null)
//                    but if id is null we have another problems
//                    also ORM think that id must be null see http://greenrobot.org/greendao/documentation/modelling-entities/#Primary_key_restrictions
//                    but on backend we have id as String...
//                    that's why in orm we use string#hasCode. We believe that there will not be collisions...
//                    also, on every record we check if it is exist, and it has huge performance issues
//
                    for (PoiDb poiDb : of) {
                        long count = daoSession.getPoiDbDao().queryBuilder().where(PoiDbDao.Properties.PoiId.eq(poiDb.getPoiId())).count();
                        if (count > 0){
                            // update
                            daoSession.getPoiDbDao().save(poiDb);
                        }else{
                            // insert new record
                            daoSession.getPoiDbDao().insert(poiDb);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }
        };

        return executor.submit(callable);
    }

    /**
     * Get poi by id,
     * @param id id of poi
     * @return POI, or null if no poi with such id in db
     */
    public Poi getPoiById(String id){
        PoiDb poiDb = daoSession.getPoiDbDao().queryBuilder().where(PoiDbDao.Properties.PoiId.eq(id)).unique();
        if (poiDb == null){
            return null;
        }

        return PoiDb.toPoi(poiDb);
    }

}
