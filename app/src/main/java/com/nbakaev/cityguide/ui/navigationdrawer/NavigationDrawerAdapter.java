package com.nbakaev.cityguide.ui.navigationdrawer;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.util.FragmentsWalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Left menu
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {

    private List<NavigationDrawerItem> mDataList = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    private static final String SELECTED_ITEM_COLOR = "#EEEEEE";
    private FragmentManager fragmentManager;
    private DrawerLayout drawerLayout;

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
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        allItems.add(holder);

        if (position == 0) {
            setActiveItem(position);
        }

        final NavigationDrawerItem current = mDataList.get(position);
        holder.imgIcon.setImageResource(current.getImageId());
        holder.title.setText(context.getString(current.getId()));
        holder.itemView.setOnClickListener(v -> {
            setActiveItem(position);
            switch (current.getId()) {
                case R.string.drawer_map:
                    FragmentsWalker.startMapFragment(fragmentManager);
                    drawerLayout.closeDrawer(Gravity.START, false);
                    break;

                case R.string.drawer_cities:
                    FragmentsWalker.startCitiesFragment(fragmentManager);
                    drawerLayout.closeDrawer(Gravity.START, false);
                    break;

                case R.string.drawer_near_me:
                    FragmentsWalker.startNearbyFragment(fragmentManager);
                    drawerLayout.closeDrawer(Gravity.START, false);
                    break;

                case R.string.drawer_about:
                    FragmentsWalker.startAboutFragment(fragmentManager);
                    drawerLayout.closeDrawer(Gravity.START, false);
                    break;

                case R.string.drawer_qr:
                    FragmentsWalker.startQrReaderFragment(fragmentManager);
                    drawerLayout.closeDrawer(Gravity.START, false);
                    break;

                case R.string.drawer_auth:
                    FragmentsWalker.startAuthFragment(fragmentManager);
                    drawerLayout.closeDrawer(Gravity.START, false);
                    break;
            }
        });
    }

    public void setActiveItem(int index) {
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
