package ru.nbakaev.cityguide.ui.navigationdrawer;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.nbakaev.cityguide.AboutFragment;
import ru.nbakaev.cityguide.CitiesFragment;
import ru.nbakaev.cityguide.MapsFragment;
import ru.nbakaev.cityguide.NearbyFragment;
import ru.nbakaev.cityguide.QrScanFragment;
import ru.nbakaev.cityguide.R;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {

    private List<NavigationDrawerItem> mDataList = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    // position of selected item; TODO: refactor - delete / make private
    public static int selectedPos = 0;
    private static final String SELECTED_ITEM_COLOR = "#EEEEEE";
    FragmentManager fragmentManager;
    DrawerLayout drawerLayout;

    private List<MyViewHolder> allItems = new ArrayList<>();

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> data, FragmentManager fragmentManager, DrawerLayout drawerLayout) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mDataList = data;
        this.fragmentManager = fragmentManager;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        allItems.add(holder);

        if (position == 0){
            setActiveItem(position);
        }

        final NavigationDrawerItem current = mDataList.get(position);
        holder.imgIcon.setImageResource(current.getImageId());
        holder.title.setText(context.getString(current.getId()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveItem(position);
                switch (current.getId()) {
                    case R.string.drawer_map: {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.addToBackStack(null);
                        MapsFragment mapsFragment = new MapsFragment();
                        fragmentTransaction.replace(R.id.main_fragment_content, mapsFragment);
                        drawerLayout.closeDrawer(Gravity.LEFT, false);
                        fragmentTransaction.commit();
                        break;
                    }

                    case R.string.drawer_cities: {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.addToBackStack(null);
                        CitiesFragment mapsFragment = new CitiesFragment();
                        fragmentTransaction.replace(R.id.main_fragment_content, mapsFragment);
                        drawerLayout.closeDrawer(Gravity.LEFT, false);
                        fragmentTransaction.commit();
                        break;
                    }

                    case R.string.drawer_near_me: {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                     fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.addToBackStack(null);
                        NearbyFragment mapsActivity = new NearbyFragment();
                        fragmentTransaction.replace(R.id.main_fragment_content, mapsActivity);
                        drawerLayout.closeDrawer(Gravity.LEFT, false);
                        fragmentTransaction.commit();
                        break;
                    }

                    case R.string.drawer_about: {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                     fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.addToBackStack(null);
                        AboutFragment mapsActivity = new AboutFragment();
                        fragmentTransaction.replace(R.id.main_fragment_content, mapsActivity);
                        drawerLayout.closeDrawer(Gravity.LEFT, false);
                        fragmentTransaction.commit();
                        break;
                    }

                    case R.string.drawer_qr: {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                     fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.addToBackStack(null);
                        QrScanFragment mapsActivity = new QrScanFragment();
                        fragmentTransaction.replace(R.id.main_fragment_content, mapsActivity);
                        drawerLayout.closeDrawer(Gravity.LEFT, false);
                        fragmentTransaction.commit();
                        break;
                    }
                }
            }
        });
    }

    private void setActiveItem(int index) {
        MyViewHolder holder = allItems.get(index);
        for (MyViewHolder allItem : allItems) {
            allItem.itemView.setBackgroundColor(Color.parseColor("white"));
        }
        holder.itemView.setBackgroundColor(Color.parseColor(SELECTED_ITEM_COLOR));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imgIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.name);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
        }
    }
}
