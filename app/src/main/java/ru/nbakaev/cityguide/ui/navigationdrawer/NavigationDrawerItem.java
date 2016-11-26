package ru.nbakaev.cityguide.ui.navigationdrawer;

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
        return new int[]{R.drawable.ic_place_black_24dp, R.drawable.ic_near_me_black_24dp,  R.drawable.ic_build_black_24dp};
    }

    private static String[] getTitles() {

        return new String[]{
                "Map", "Poi", "About"
        };
    }
}
