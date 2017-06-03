package com.nbakaev.cityguide.city;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.poi.PoiProvider;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.util.StringUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class CityRecyclerViewAdapter extends RecyclerView.Adapter<CityRecyclerViewHolder> {

    private List<City> itemList;
    private Context context;
    private static final String TAG = "CityRecyclerViewAdapter";
    private PoiProvider poiProvider;
    private SettingsService settingsService;


    public CityRecyclerViewAdapter(Context context, List<City> itemList, PoiProvider poiProvider, SettingsService settingsService) {
        this.itemList = itemList;
        this.context = context;
        this.poiProvider = poiProvider;
        this.settingsService = settingsService;
    }

    @Override
    public CityRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cities_list, null);
        CityRecyclerViewHolder rcv = new CityRecyclerViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CityRecyclerViewHolder holder, int position) {
        holder.countryName.setText(itemList.get(position).getName());
        City current = itemList.get(position);

        if (!StringUtils.isEmpty(current.getContent().getImageUrl())) {
            Observable<ResponseBody> icon = poiProvider.getIcon(current);
            Observer<ResponseBody> iconResult = new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(Disposable d) {
                    Log.d(TAG, d.toString());
                }

                @Override
                public void onNext(ResponseBody value) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(value.byteStream(), null, CityRecyclerViewAdapter.this.settingsService.getDefaultBitmapOptions());
                        holder.countryPhoto.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, e.toString());
                }

                @Override
                public void onComplete() {
                }
            };

            icon.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(iconResult);
        }else{
            // default image
            holder.countryPhoto.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}