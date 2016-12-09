package ru.nbakaev.cityguide.ui.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import ru.nbakaev.cityguide.CitiesActivity;
import ru.nbakaev.cityguide.MainActivity;
import ru.nbakaev.cityguide.MapsActivity;
import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.about.AboutActivity;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {

    private List<NavigationDrawerItem> mDataList = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private static int selectedPos = 0;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mDataList = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (selectedPos == position){
            holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));
        }

        NavigationDrawerItem current = mDataList.get(position);
        holder.imgIcon.setImageResource(current.getImageId());
        holder.title.setText(current.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPos = holder.getAdapterPosition();

                if (holder.title.getText().toString().equals("Map")) {
                    Intent intent = new Intent(NavigationDrawerAdapter.this.context, MapsActivity.class);
                    NavigationDrawerAdapter.this.context.startActivity(intent);
                }

                if (holder.title.getText().toString().equals("Near me")) {
                    Intent intent = new Intent(NavigationDrawerAdapter.this.context, MainActivity.class);
                    NavigationDrawerAdapter.this.context.startActivity(intent);
                }
                if (holder.title.getText().toString().equals("Cities")) {
                    Intent intent = new Intent(NavigationDrawerAdapter.this.context, CitiesActivity.class);
                    NavigationDrawerAdapter.this.context.startActivity(intent);

                }
                if (holder.title.getText().toString().equals("About")) {
                    Intent intent = new Intent(NavigationDrawerAdapter.this.context, AboutActivity.class);
                    NavigationDrawerAdapter.this.context.startActivity(intent);
                }
            }
        });
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
