package ru.nbakaev.cityguide.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.locaton.LocationProvider;
import ru.nbakaev.cityguide.poi.Poi;

import static ru.nbakaev.cityguide.utils.MapUtils.printDistance;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<Poi> mData;
    private LayoutInflater inflater;
    private final LocationProvider locationProvider;
    private Location lastLocation;


    public RecyclerAdapter(Context context, List<Poi> data, LocationProvider locationProvider) {
        inflater = LayoutInflater.from(context);
        this.mData = data;
        this.locationProvider = locationProvider;

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
        TextView title;
        TextView description;
        TextView distance;
        ImageView imgThumb;
        //        imgDelete, imgAdd;
        int position;
        Poi current;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            distance = (TextView) itemView.findViewById(R.id.rvDistance);
            imgThumb = (ImageView) itemView.findViewById(R.id.img_row);
//            imgDelete = (ImageView) itemView.findViewById(R.id.img_row_delete);
//            imgAdd = (ImageView) itemView.findViewById(R.id.img_row_add);
        }

        public void setData(Poi current, int position) {
            this.title.setText(current.getName());

            if (current.getImage() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(current.getImage(), 0, current.getImage().length);
                this.imgThumb.setImageBitmap(bitmap);
            }
            this.position = position;
            this.current = current;
            this.description.setText(current.getDescription());

            if (lastLocation != null){
                float distanceTo = lastLocation.distanceTo(current.getLocation().toLocation());
                this.distance.setText(printDistance(distanceTo) + " m");
            }else{
                this.distance.setText("");
            }

        }

//        public void setListeners() {
//            imgDelete.setOnClickListener(MyViewHolder.this);
//            imgAdd.setOnClickListener(MyViewHolder.this);
//        }

        @Override
        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.img_row_delete:
//                    removeItem(position);
//                    break;
//
//                case R.id.img_row_add:
//                    addItem(position, current);
//                    break;
//            }
        }
    }
}
