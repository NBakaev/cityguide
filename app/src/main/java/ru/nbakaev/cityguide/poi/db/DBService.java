package ru.nbakaev.cityguide.poi.db;

import com.orm.SugarRecord;

import java.util.List;

import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.settings.SettingsService;

/**
 * TODO: optional; make as dagger component, not static methods
 * Created by ya on 11/24/2016.
 */

public class DBService {

    /**
     * add/update every POI to database
     * @param data
     */
    public static void cachePoiToDB(final List<Poi> data) {
        if (SettingsService.getSettings().isOffline()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SugarRecord.saveInTx(PoiDb.of(data));
            }
        }).start();
    }

    /**
     * Get poi by id,
     * @param id id of poi
     * @return POI, or null if no poi with such id in db
     */
    public static Poi getPoiById(String id){
        List<PoiDb> withQuery = SugarRecord.findWithQuery(PoiDb.class, "SELECT * FROM POI_DB where poiId=(?) ", id);
        if (withQuery == null || withQuery.size() == 0){
            return null;
        }

        return PoiDb.toPoi(withQuery.get(0));
    }

}
