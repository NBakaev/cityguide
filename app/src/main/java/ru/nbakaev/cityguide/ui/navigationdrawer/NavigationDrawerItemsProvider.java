package ru.nbakaev.cityguide.ui.navigationdrawer;

import java.util.ArrayList;
import java.util.List;

import ru.nbakaev.cityguide.R;

/**
 * Created by ya on 12/14/2016.
 */

public class NavigationDrawerItemsProvider {

    public static List<NavigationDrawerItem> getData() {
        List<NavigationDrawerItem> dataList = new ArrayList<>();

        dataList.add(new NavigationDrawerItem(R.string.drawer_map, R.drawable.ic_place_black_24dp));
        dataList.add(new NavigationDrawerItem(R.string.drawer_near_me, R.drawable.ic_near_me_black_24dp));
        dataList.add(new NavigationDrawerItem(R.string.drawer_about, R.drawable.ic_build_black_24dp));

        return dataList;
    }

}
