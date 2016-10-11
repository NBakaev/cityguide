package ru.nbakaev.cityguide.model;

import java.util.ArrayList;
import java.util.List;

import ru.nbakaev.cityguide.R;


public class NavigationDrawerItem {

    private String name;
    private int imageId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public static List<NavigationDrawerItem> getData() {
        List<NavigationDrawerItem> dataList = new ArrayList<>();

        int[] imageIds = getImages();
        String[] titles = getTitles();

        for (int i = 0; i < titles.length; i++) {
            NavigationDrawerItem navItem = new NavigationDrawerItem();
            navItem.setName(titles[i]);
            navItem.setImageId(imageIds[i]);
            dataList.add(navItem);
        }
        return dataList;
    }

    private static int[] getImages() {
        return new int[]{R.drawable.ic_birds, R.drawable.ic_animal};
    }

    private static String[] getTitles() {

        return new String[]{
                "Map", "Poi"
        };
    }
}
