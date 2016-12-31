package ru.nbakaev.cityguide.util;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import ru.nbakaev.cityguide.SettingsFragment;
import ru.nbakaev.cityguide.city.CitiesFragment;
import ru.nbakaev.cityguide.map.MapsFragment;
import ru.nbakaev.cityguide.nearby.NearbyFragment;
import ru.nbakaev.cityguide.scan.QrScanFragment;
import ru.nbakaev.cityguide.R;

/**
 * Created by ya on 12/27/2016.
 */

public class FragmentsOrganizer {

    // fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);

    public static void startMapFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        MapsFragment mapsFragment = new MapsFragment();
        fragmentTransaction.replace(R.id.main_fragment_content, mapsFragment);
        fragmentTransaction.commit();
    }

    public static void startCitiesFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        CitiesFragment mapsFragment = new CitiesFragment();
        fragmentTransaction.replace(R.id.main_fragment_content, mapsFragment);
        fragmentTransaction.commit();
    }

    public static void startAboutFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        SettingsFragment mapsActivity = new SettingsFragment();
        fragmentTransaction.replace(R.id.main_fragment_content, mapsActivity);
        fragmentTransaction.commit();
    }

    public static void startQrReaderFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        QrScanFragment mapsActivity = new QrScanFragment();
        fragmentTransaction.replace(R.id.main_fragment_content, mapsActivity);
        fragmentTransaction.commit();
    }

    public static void startNearbyFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        NearbyFragment mapsActivity = new NearbyFragment();
        fragmentTransaction.replace(R.id.main_fragment_content, mapsActivity);
        fragmentTransaction.commit();
    }

    public static void startMapFragmentWithPoiOpen(FragmentManager fragmentManager, String id) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        MapsFragment mapsFragment = new MapsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("MOVE_TO_POI_ID", id);
        mapsFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.main_fragment_content, mapsFragment);
        fragmentTransaction.commit();
    }

}
