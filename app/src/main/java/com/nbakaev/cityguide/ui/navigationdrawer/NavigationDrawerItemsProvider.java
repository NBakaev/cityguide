package com.nbakaev.cityguide.ui.navigationdrawer;

import com.nbakaev.cityguide.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikita Bakaev on 12/14/2016.
 */

public class NavigationDrawerItemsProvider {

    public static List<NavigationDrawerItem> getData() {
        List<NavigationDrawerItem> dataList = new ArrayList<>();

        dataList.add(new NavigationDrawerItem(R.string.drawer_map, R.drawable.ic_place_black_24dp));
        dataList.add(new NavigationDrawerItem(R.string.drawer_near_me, R.drawable.ic_near_me_black_24dp));
        dataList.add(new NavigationDrawerItem(R.string.drawer_cities, R.drawable.ic_business_black_24dp));
        dataList.add(new NavigationDrawerItem(R.string.drawer_trips, R.drawable.ic_local_taxi_black_24dp));
        dataList.add(new NavigationDrawerItem(R.string.drawer_auth, R.drawable.ic_account_circle_black_24dp));
        dataList.add(new NavigationDrawerItem(R.string.drawer_about, R.drawable.ic_build_black_24dp));
        dataList.add(new NavigationDrawerItem(R.string.drawer_qr, R.drawable.ic_camera_black_24dp));
        return dataList;
    }

}
