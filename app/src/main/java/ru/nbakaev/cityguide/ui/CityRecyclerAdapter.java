package ru.nbakaev.cityguide.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.poi.City;
import ru.nbakaev.cityguide.poi.PoiProvider;
import ru.nbakaev.cityguide.ui.cityselector.MultiSelector;
import ru.nbakaev.cityguide.utils.CacheUtils;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.CityHolder>{

    final BitmapFactory.Options options = new BitmapFactory.Options();
    Random randrom = new Random();
    LayoutInflater inflater;
    List<City> cities;
    PoiProvider poiProvider;
    CacheUtils cacheUtils;
//    List<City> selected = new ArrayList<>();

    ru.nbakaev.cityguide.ui.cityselector.MultiSelector<City> selector;
//    MultiSelector selector = new MultiSelector();
//    MultiSelector multiSelector;

    public CityRecyclerAdapter(Context context, List<City> cities, MultiSelector<City> selector, PoiProvider poiProvider, CacheUtils cacheUtils) {
        this.cities = cities;
        inflater = LayoutInflater.from(context);
//        this.multiSelector = multiSelector;
        this.selector = selector;
        this.poiProvider = poiProvider;
        this.cacheUtils = cacheUtils;
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
        LinearLayout holder;
        int pos;
        City current;

        public CityHolder(View itemView) {
            super(itemView);
            holder = (LinearLayout) itemView.findViewById(R.id.cityHolder);
            title = (TextView) itemView.findViewById(R.id.cityTitle);
            poi = (TextView) itemView.findViewById(R.id.cityPOI);
            imgThumb = (ImageView) itemView.findViewById(R.id.cityImg);
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

            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selector.select(current);
                    notifyItemChanged(position);
                }
            });
            if (current.getImageUrl()==null)
            {
                imgThumb.setVisibility(View.GONE);
            }
            else
            {
                imgThumb.setImageResource(R.drawable.ic_placeholder);
                imgThumb.setVisibility(View.VISIBLE);
                setIcon();

            }
            if (selector.isSelected(current)) {
                holder.setBackgroundResource(R.color.grey_300);
            }
            else {
                holder.setBackgroundResource(R.color.white);
            }

        }

        private void setIcon()
        {
            Observable<ResponseBody> icon = poiProvider.getIcon(current);
            Observer<ResponseBody> iconResult = new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ResponseBody value) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(value.byteStream(), null, options);
                        cacheUtils.cacheCityImage(bitmap, current);
                        imgThumb.setImageBitmap(bitmap);
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                }
            };

            icon.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(iconResult);
        }
    }


}
