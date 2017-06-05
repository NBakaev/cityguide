package com.nbakaev.cityguide.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.nbakaev.cityguide.BaseActivity;
import com.nbakaev.cityguide.MainActivity;
import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.auth.AuthFragment;
import com.nbakaev.cityguide.settings.SettingsFragment;
import com.nbakaev.cityguide.city.CitiesFragment;
import com.nbakaev.cityguide.map.MapsFragment;
import com.nbakaev.cityguide.nearby.NearbyFragment;
import com.nbakaev.cityguide.scan.QrScanFragment;

/**
 * Created by Nikita Bakaev
 */

public class FragmentsWalker {
    public static final String MOVE_TO_POI_ID = "MOVE_TO_POI_ID";
    public static final String FRAGMENT_OPEN = "FRAGMENT_OPEN";
    public static final String NEARBY = "NEARBY";
    public static final String OPEN_FRAGMENT = "OPEN_FRAGMENT";

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

    public static void startAuthFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        AuthFragment authFragment = new AuthFragment();
        fragmentTransaction.replace(R.id.main_fragment_content, authFragment);
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
        bundle.putString(MOVE_TO_POI_ID, id);
        mapsFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.main_fragment_content, mapsFragment);
        fragmentTransaction.commit();
    }

    public static void startMapFragmentFromScratchWithPoiOpen(BaseActivity baseActivity, android.support.v4.app.Fragment fragment, String id){
        Intent intent = new Intent(baseActivity.getApplicationContext(), MainActivity.class);
        intent.putExtra(MOVE_TO_POI_ID, id);
        baseActivity.finish();
        fragment.startActivity(intent);
    }

}
