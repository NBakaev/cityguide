# CityGuide

 ![](https://github.com/NBakaev/binary/raw/master/cityguide/all.png)

HSE project

 - [Google Drive - all files and APKs](https://drive.google.com/open?id=0BzCXhxh5Y3--WlVnV2JOZDdpVzg)
 - [Trello](https://trello.com/b/CvkJB18L/hse-cityguide)

## Android requirements
 - Android 4.4+. Testing on Android 4.4, 7.0

### Standard telephone with
 - Google Play
 - WebView (such as chrome)

## Technical
 - https://s2.nbakaev.ru/#/main_page - admin UI
 - server https://github.com/NBakaev/cityguide-server
 
### Architecture overview

#### POIs

2 implementations:

 - OfflinePoiProvider.java - saved to SQLite with [greendao ORM](http://greenrobot.org/greendao/documentation/). Images saved to /sdcard/cityguide
 - ServerPoiProvider.java - REST requests with retrofit2

Implementation injects with Dagger2 in sturtup (depend get property offline of AppSettings.java object from SQLite with SettingsService.java helper)

 - results -> Observable (rxjava2)
 

#### code snippets

##### start map with some POI
```java
FragmentsOrganizer.startMapFragmentWithPoiOpen(getSupportFragmentManager(), poiId);
```

##### pull db from device via adb
`adb pull /data/data/com.nbakaev.cityguide/databases/poi-db.db .`

Notes:
 - POIs in DB stored as com.nbakaev.cityguide.poi.db.PoiDb but all methods operate with com.nbakaev.cityguide.poi.Poi. You can transform between tho methods with static methods of PoiDb.class
 - Android icons from https://material.io/icons
 - https://developers.google.com/maps/documentation/android-api/utility/marker-clustering
 - mockups from http://mockuphone.com/nexus6p

### Offline distance
 - http://goodenoughpractices.blogspot.ru/2011/08/query-by-proximity-in-android.html
 - http://stackoverflow.com/questions/15372705/calculating-a-radius-with-longitude-and-lattitude
 - https://github.com/sozialhelden/wheelmap-android/wiki/Sqlite,-Distance-calculations
