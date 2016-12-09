package ru.nbakaev.cityguide.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SelectableHolder;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.city.City;
import ru.nbakaev.cityguide.poi.Poi;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.CityHolder>{


    Random randrom = new Random();
    LayoutInflater inflater;
    List<City> cities;

    MultiSelector selector = new MultiSelector();
//    MultiSelector multiSelector;

    public CityRecyclerAdapter(Context context, List<City> cities) {
        this.cities = cities;
        inflater = LayoutInflater.from(context);
//        this.multiSelector = multiSelector;
    }

    @Override
    public CityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.city_item, parent, false);
        CityHolder holder = new CityHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CityHolder holder, final int position) {
        final City current = cities.get(position);
        holder.setData(current, position);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    class CityHolder extends SwappingHolder implements View.OnLongClickListener
    {
        TextView title;
        TextView poi;
        ImageView imgThumb;
        ImageView imgLoad;
        LinearLayout holder;
        int pos;
        City current;

        public CityHolder(View itemView) {
            super(itemView, selector);
            holder = (LinearLayout) itemView.findViewById(R.id.cityHolder);
            title = (TextView) itemView.findViewById(R.id.cityTitle);
            poi = (TextView) itemView.findViewById(R.id.cityPOI);
            imgThumb = (ImageView) itemView.findViewById(R.id.cityImg);
            imgLoad = (ImageView) itemView.findViewById(R.id.loadCity);
        }

        public void setData(City currentCity, int position)
        {
            this.pos = position;

            this.current = currentCity;
            title.setText(current.name);
            if (current.POINumber>0)
            {
                poi.setVisibility(View.VISIBLE);
                poi.setText(current.POINumber + (current.POINumber == 1 ? " POI" : " POIs"));
            }
            else
            {
                poi.setVisibility(View.GONE);
            }
            if (current.lastUpdated==null)
            {
                imgLoad.setImageResource(R.drawable.ic_load);
            }
            else {
                imgLoad.setImageResource(R.drawable.ic_update);
            }

            holder.setLongClickable(true);
            holder.setOnLongClickListener(this);

            if (isActivated()) {
                holder.setBackgroundResource(R.color.grey_300);
            }
            else {
                holder.setBackgroundResource(R.color.white);
            }

        }






        @Override
        public boolean onLongClick(View v) {
            if (!selector.isSelectable()) {
                selector.setSelectable(true);
                selector.setSelected(CityHolder.this, true);
                return true;
            }
            return false;
        }
    }


}
