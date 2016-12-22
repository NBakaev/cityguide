package ru.nbakaev.cityguide.ui.navigationdrawer;

public class NavigationDrawerItem {

    private int id;
    private int imageId;

    public NavigationDrawerItem(int id, int imageId) {
        this.id = id;
        this.imageId = imageId;
    }

    public int getId() {
        return id;
    }

    public int getImageId() {
        return imageId;
    }

}
