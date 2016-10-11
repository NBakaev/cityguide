package ru.nbakaev.cityguide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.model.Poi;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<Poi> mData;
    private LayoutInflater inflater;

    public RecyclerAdapter(Context context, List<Poi> data) {
        inflater = LayoutInflater.from(context);
        this.mData = data;
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
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    public void addItem(int position, Poi poi) {
        mData.add(position, poi);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mData.size());
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView imgThumb, imgDelete, imgAdd;
        int position;
        Poi current;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            imgThumb = (ImageView) itemView.findViewById(R.id.img_row);
            imgDelete = (ImageView) itemView.findViewById(R.id.img_row_delete);
            imgAdd = (ImageView) itemView.findViewById(R.id.img_row_add);
        }

        public void setData(Poi current, int position) {
            this.title.setText(current.getName());
            this.imgThumb.setImageResource(current.getImageID());
            this.position = position;
            this.current = current;
        }

        public void setListeners() {
            imgDelete.setOnClickListener(MyViewHolder.this);
            imgAdd.setOnClickListener(MyViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_row_delete:
                    removeItem(position);
                    break;

                case R.id.img_row_add:
                    addItem(position, current);
                    break;
            }
        }
    }
}
