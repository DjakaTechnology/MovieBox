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
import com.dtechnology.moviebox.activity.DetailActivity;
import com.dtechnology.moviebox.model.DataMovie;

import java.util.ArrayList;

/**
 * Created by root on 30/01/18.
 */

public class AdapterMovie extends RecyclerView.Adapter<AdapterMovie.MyViewHolder> {
    Context c;
    ArrayList<DataMovie> dataMovies;

    public AdapterMovie(Context context, ArrayList<DataMovie> dataMovies) {
        c = context;
        this.dataMovies = dataMovies;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.movie_item, null, false);
        MyViewHolder holder = new MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterMovie.MyViewHolder holder, final int position) {
        holder.txtJudul.setText(dataMovies.get(position).getTitle());
        Glide.with(c).load("https://image.tmdb.org/t/p/w500/"+dataMovies.get(position)
                .getPosterPath()).into(holder.imgMovie);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c, DetailActivity.class);
                i.putParcelableArrayListExtra("MOVIE", dataMovies);
                i.putExtra("POSITION", position);
                c.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataMovies.size();
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

