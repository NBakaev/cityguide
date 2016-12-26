package ru.nbakaev.cityguide.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import ru.nbakaev.cityguide.MapsActivity;
import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.locaton.LocationProvider;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiProvider;
import ru.nbakaev.cityguide.ui.navigationdrawer.NavigationDrawerAdapter;
import ru.nbakaev.cityguide.util.StringUtils;

import static ru.nbakaev.cityguide.util.MapUtils.printDistance;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<Poi> mData;
    private LayoutInflater inflater;
    private final LocationProvider locationProvider;
    private Location lastLocation;
    private Context context;
    private PoiProvider poiProvider;
    private final BitmapFactory.Options options = new BitmapFactory.Options();

    private static final String TAG = RecyclerAdapter.class.getSimpleName();

    public RecyclerAdapter(Context context, List<Poi> data, LocationProvider locationProvider, PoiProvider poiProvider) {
        inflater = LayoutInflater.from(context);
        this.mData = data;
        this.locationProvider = locationProvider;
        this.context = context;
        this.poiProvider = poiProvider;

        Observer<Location> locationObserver = new Observer<Location>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Location value) {
                lastLocation = value;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        locationProvider.getCurrentUserLocation().subscribe(locationObserver);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_main_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Poi current = mData.get(position);
        holder.setData(current, position);
//        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

//    public void removeItem(int position) {
//        mData.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, mData.size());
//    }
//
//    public void addItem(int position, Poi poi) {
//        mData.add(position, poi);
//        notifyItemInserted(position);
//        notifyItemRangeChanged(position, mData.size());
//    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        static final int MAX_DESCRIPTION_LENGTH = 35;
        TextView title;
        TextView description;
        TextView distance;
        ImageView imgThumb;
        int position;
        Poi current;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            distance = (TextView) itemView.findViewById(R.id.rvDistance);

            // default image
            imgThumb = (ImageView) itemView.findViewById(R.id.img_row);
            itemView.setOnClickListener(this);
        }

        public void setData(Poi current, int position) {
            this.title.setText(current.getName());
            this.position = position;
            this.current = current;

            String visibleDescription = current.getDescription() == null ? "" : current.getDescription().substring(0, Math.min(MAX_DESCRIPTION_LENGTH, current.getDescription().length())).concat("...");
            this.description.setText(visibleDescription);

            if (!StringUtils.isEmpty(current.getImageUrl())) {
                Observable<ResponseBody> icon = poiProvider.getIcon(current);
                Observer<ResponseBody> iconResult = new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, d.toString());
                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(value.byteStream(), null, options);
                            imgThumb.setImageBitmap(bitmap);
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
            }

            if (lastLocation != null) {
                float distanceTo = lastLocation.distanceTo(current.getLocation().toLocation());
                this.distance.setText(printDistance(distanceTo));
            } else {
                this.distance.setText("");
            }

        }

//        public void setListeners() {
//            imgDelete.setOnClickListener(MyViewHolder.this);
//            imgAdd.setOnClickListener(MyViewHolder.this);
//        }

        @Override
        public void onClick(View v) {
            RecyclerView recyclerView = (RecyclerView) v.getParent();

            int itemPosition = recyclerView.getChildLayoutPosition(v);
            Poi poi = mData.get(itemPosition);

            // hack to change active tab
            // 0 is index of MapsActivity
            NavigationDrawerAdapter.selectedPos = 0;

            Intent i = new Intent(context, MapsActivity.class);
            i.putExtra("MOVE_TO_POI_ID", poi.getId());
            context.startActivity(i);
        }
    }
}
