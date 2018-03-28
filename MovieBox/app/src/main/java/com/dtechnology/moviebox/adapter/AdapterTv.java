package com.dtechnology.moviebox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dtechnology.moviebox.R;
import com.dtechnology.moviebox.activity.DetailTvActivity;
import com.dtechnology.moviebox.model.DataTv;

import java.util.ArrayList;

/**
 * Created by root on 30/01/18.
 */

public class AdapterTv extends RecyclerView.Adapter<AdapterTv.MyViewHolder> {
    Context c;
    ArrayList<DataTv> dataTv = new ArrayList<>();

    public AdapterTv(Context context, ArrayList<DataTv> dataTv) {
        c = context;
        this.dataTv = dataTv;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.movie_item, null, false);
        MyViewHolder holder = new MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterTv.MyViewHolder holder, final int position) {
        holder.txtJudul.setText(dataTv.get(position).getName());
        Glide.with(c).load("https://image.tmdb.org/t/p/w500/"+dataTv.get(position)
                .getPosterPath()).into(holder.imgMovie);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c, DetailTvActivity.class);
                i.putParcelableArrayListExtra("MOVIE", dataTv);
                i.putExtra("POSITION", position);
                c.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataTv.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtJudul;
        ImageView imgMovie;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtJudul = (TextView)itemView.findViewById(R.id.tv_item_title);
            imgMovie = (ImageView) itemView.findViewById(R.id.iv_item_image);
        }
    }
}