//package ru.nbakaev.cityguide.poi;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//
//import java.io.ByteArrayOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import io.reactivex.Observable;
//import okhttp3.ResponseBody;
//import ru.nbakaev.cityguide.R;
//
///**
// * For test and development only.
// * Generate random POI on map
// *
// * Created by Nikita on 10/11/2016.
// */
//
//public class MockedPoiProvider implements PoiProvider {
//
//    private Poi.PoiLocation dataForLocation;
//
//    private final Context context;
//
//    private int mockedItems = 100;
//
//    private byte[] mockedPoiImage;
//    private final Random random = new Random();
//
//    public MockedPoiProvider(Context context) {
//        this.context = context;
//        mockedPoiImage = convertContent();
//
//        setup(0,0);
////        for (int i=0; i < mockedItems; i++){
////            mockedPoiObjectPool.add(new Poi());
////        }
//    }
//
//    private List<Poi> mockedPoiObjectPool = new ArrayList<>(mockedItems);
//
//    /**
//     * @param x0
//     * @param y0
//     * @param radius in meters
//     * @return
//     */
//    private Poi.PoiLocation getRandomLocation(double x0, double y0, int radius) {
//
//        // Convert radius from meters to degrees
//        double radiusInDegrees = radius / 111000f;
//
//        double u = random.nextDouble();
//        double v = random.nextDouble();
//        double w = radiusInDegrees * Math.sqrt(u);
//        double t = 2 * Math.PI * v;
//        double x = w * Math.cos(t);
//        double y = w * Math.sin(t);
//
//        // Adjust the x-coordinate for the shrinking of the east-west distances
//        double new_x = x / Math.cos(y0);
//
//        double foundLongitude = new_x + x0;
//        double foundLatitude = y + y0;
//        return new Poi.PoiLocation(foundLongitude, foundLatitude);
//    }
//
//    @Override
//    public Observable<List<Poi>> getData(double x0, double y0, int radius) {
//        Poi.PoiLocation poiLocation = new Poi.PoiLocation(x0, y0);
//        if (mockedPoiObjectPool != null && !mockedPoiObjectPool.isEmpty() & dataForLocation!=null && dataForLocation.equals(poiLocation)) {
//            return Observable.fromArray(mockedPoiObjectPool);
//        } else {
//            setup(x0, y0);
//            dataForLocation = poiLocation;
//            return Observable.fromArray(mockedPoiObjectPool);
//        }
//    }
//
//    @Override
//    public Observable<ResponseBody> downloadContent(Poi poi) {
//        return null;
//    }
//
//    private void setup(double x0, double y0) {
//        for (int i = 0; i < mockedItems; i++) {
////            Poi poi = mockedPoiObjectPool.get(i);
//            Poi poi = new Poi();
////            poi.setImage(mockedPoiImage);
//            poi.setId(Double.toString(random.nextDouble()));
//            poi.setName("Poi " + i);
//            poi.setDescription("Description for poi " + i);
//            poi.setLocation(getRandomLocation(x0, y0, DISTANCE_POI_DOWNLOAD * 4));
//            mockedPoiObjectPool.add(i, poi);
//        }
//    }
//
//    private byte[] convertContent(){
//        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.thumb_1_0);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        return byteArray;
//    }
//
//}
