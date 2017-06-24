package com.vikram.studyapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vikram on 6/23/2017.
 */

class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyHolder> {

    private List<Upload> uploads;
    private Context context;

    public DataAdapter(List<Upload> uploads, Context context) {
        this.uploads = uploads;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_image,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder myHolder, int i) {
        Upload upload = uploads.get(i);
        myHolder.title.setText(upload.getImageName());
        Glide.with(context)
                .load(upload.getImageUrl())
                .into(myHolder.image);

    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;

        public MyHolder(View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.tv_text);
            image = (ImageView)itemView.findViewById(R.id.iv_photo);

        }
    }
}
