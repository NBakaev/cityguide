# CityGuide
HSE project

 - Android 4.4+. Testing on Android 4.4, 7.0
 - [Google Drive - all files and APKs](https://drive.google.com/open?id=0BzCXhxh5Y3--WlVnV2JOZDdpVzg)
 - [Trello](https://trello.com/b/CvkJB18L/hse-cityguide)
 
## Technical
 - https://s2.nbakaev.ru/#/main_page - admin UI
 - server https://github.com/NBakaev/cityguide-server
 
### Architecture overview

#### POIs

2 implementations:

 - OfflinePoiProvider.java - saved to SQLite with [Sugar ORM](http://satyan.github.io/sugar/). Images saved to /sdcard/cityguide
 - ServerPoiProvider.java - REST requests with retrofit2

Implementation injects with Dagger2 in sturtup (depend get property offline of AppSettings.java object from SQLite with SettingsService.java helper)

 - results -> Observable (rxjava2)
 
