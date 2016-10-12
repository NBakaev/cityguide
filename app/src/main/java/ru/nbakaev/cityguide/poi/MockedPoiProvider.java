package ru.nbakaev.cityguide.poi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.nbakaev.cityguide.R;

/**
 * Created by Nikita on 10/11/2016.
 */

public class MockedPoiProvider implements PoiProvider {

    private ArrayList<Poi> dataList = new ArrayList<>();
    private Poi.PoiLocation dataForLocation;

    /**
     * @param x0
     * @param y0
     * @param radius in meters
     * @return
     */
    private Poi.PoiLocation getRandomLocation(double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;
        return new Poi.PoiLocation(foundLongitude, foundLatitude);
    }

    @Override
    public List<Poi> getData(double x0, double y0, int radius) {
        Poi.PoiLocation poiLocation = new Poi.PoiLocation(x0, y0);
        if (dataForLocation != null && !dataList.isEmpty() & dataForLocation.equals(poiLocation)) {
            return dataList;
        } else {
            setup(x0, y0);
            dataForLocation = poiLocation;
            return dataList;
        }
    }

    private void setup(double x0, double y0) {
        int[] images = getImages();
        dataList.clear();
        for (int i = 0; i < images.length; i++) {

            Poi poi = new Poi();
            poi.setImageID(images[i]);
            poi.setName("Poi " + i);
            poi.setLocation(getRandomLocation(x0, y0, DISTANCE_POI_DOWNLOAD * 4));

            dataList.add(poi);
        }
    }

    private static int[] getImages() {
        return new int[]{
                R.drawable.thumb_1_0, R.drawable.thumb_1_1, R.drawable.thumb_1_2, R.drawable.thumb_1_3,
                R.drawable.thumb_1_4, R.drawable.thumb_1_5, R.drawable.thumb_1_6, R.drawable.thumb_1_7,
                R.drawable.thumb_1_8, R.drawable.thumb_1_9,

                R.drawable.thumb_2_0, R.drawable.thumb_2_1, R.drawable.thumb_2_2, R.drawable.thumb_2_3,
                R.drawable.thumb_2_4, R.drawable.thumb_2_5, R.drawable.thumb_2_6, R.drawable.thumb_2_7,
                R.drawable.thumb_2_8, R.drawable.thumb_2_9,

                R.drawable.thumb_3_0, R.drawable.thumb_3_1, R.drawable.thumb_3_2, R.drawable.thumb_3_3,
                R.drawable.thumb_3_4, R.drawable.thumb_3_5, R.drawable.thumb_3_6, R.drawable.thumb_3_7,
                R.drawable.thumb_3_8, R.drawable.thumb_3_9,

                R.drawable.thumb_4_0, R.drawable.thumb_4_1, R.drawable.thumb_4_2, R.drawable.thumb_4_3,
                R.drawable.thumb_4_4, R.drawable.thumb_4_5, R.drawable.thumb_4_6, R.drawable.thumb_4_7,
                R.drawable.thumb_4_8, R.drawable.thumb_4_9,

                R.drawable.thumb_5_0, R.drawable.thumb_5_1, R.drawable.thumb_5_2, R.drawable.thumb_5_3,
                R.drawable.thumb_5_4, R.drawable.thumb_5_5, R.drawable.thumb_5_6, R.drawable.thumb_5_7,
                R.drawable.thumb_5_8, R.drawable.thumb_5_9,

                R.drawable.thumb_6_0, R.drawable.thumb_6_1, R.drawable.thumb_6_2, R.drawable.thumb_6_3,
                R.drawable.thumb_6_4, R.drawable.thumb_6_5, R.drawable.thumb_6_6, R.drawable.thumb_6_7,
                R.drawable.thumb_6_8, R.drawable.thumb_6_9,

                R.drawable.thumb_7_0, R.drawable.thumb_7_1, R.drawable.thumb_7_2, R.drawable.thumb_7_3,
                R.drawable.thumb_7_4
        };
    }

}
