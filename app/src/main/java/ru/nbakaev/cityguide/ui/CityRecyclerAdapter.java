package ru.nbakaev.cityguide.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.poi.City;
import ru.nbakaev.cityguide.ui.cityselector.MultiSelector;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.CityHolder>{


    Random randrom = new Random();
    LayoutInflater inflater;
    List<City> cities;
//    List<City> selected = new ArrayList<>();

    ru.nbakaev.cityguide.ui.cityselector.MultiSelector<City> selector;
//    MultiSelector selector = new MultiSelector();
//    MultiSelector multiSelector;

    public CityRecyclerAdapter(Context context, List<City> cities, MultiSelector<City> selector) {
        this.cities = cities;
        inflater = LayoutInflater.from(context);
//        this.multiSelector = multiSelector;
        this.selector = selector;
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

    class CityHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView poi;
        ImageView imgThumb;
        ImageView imgLoad;
        LinearLayout holder;
        int pos;
        City current;

        public CityHolder(View itemView) {
            super(itemView);
            holder = (LinearLayout) itemView.findViewById(R.id.cityHolder);
            title = (TextView) itemView.findViewById(R.id.cityTitle);
            poi = (TextView) itemView.findViewById(R.id.cityPOI);
            imgThumb = (ImageView) itemView.findViewById(R.id.cityImg);
            imgLoad = (ImageView) itemView.findViewById(R.id.loadCity);
        }



        public void setData(City currentCity, final int position)
        {
            this.pos = position;

            this.current = currentCity;
            title.setText(current.getName());
            if (current.getPois()>0)
            {
                poi.setVisibility(View.VISIBLE);
                poi.setText(current.getPois()+ (current.getPois() == 1 ? " POI" : " POIs"));
            }
            else
            {
                poi.setVisibility(View.GONE);
            }
            if (current.getLastUpdate()==null)
            {
                imgLoad.setImageResource(R.drawable.ic_load);
            }
            else {
                imgLoad.setImageResource(R.drawable.ic_update);
            }

            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selector.select(current);
                    notifyItemChanged(position);
                }
            });
            if (selector.isSelected(current)) {
                holder.setBackgroundResource(R.color.grey_300);
            }
            else {
                holder.setBackgroundResource(R.color.white);
            }

        }
    }


}
