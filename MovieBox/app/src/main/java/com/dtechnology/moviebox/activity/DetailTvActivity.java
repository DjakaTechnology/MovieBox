package com.dtechnology.moviebox.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dtechnology.moviebox.R;
import com.dtechnology.moviebox.adapter.AdapterTv;
import com.dtechnology.moviebox.connection.ApiService;
import com.dtechnology.moviebox.connection.RetroConfig;
import com.dtechnology.moviebox.database.TvContract;
import com.dtechnology.moviebox.model.DataMovie;
import com.dtechnology.moviebox.model.DataTv;
import com.dtechnology.moviebox.model.ModelListTv;
import com.dtechnology.moviebox.model.ModelListTv;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTvActivity extends AppCompatActivity {

    ArrayList<DataTv> dataTv = new ArrayList<>();
    ArrayList<DataTv> dataSimilar = new ArrayList<>();
    int position = 1;
    boolean isFavorite = false;

    SharedPreferences preferences;
    FloatingActionButton fab;
    RecyclerView rvmovie;
    ProgressBar progress_popular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progress_popular = (ProgressBar)findViewById(R.id.progress_popular);
        rvmovie = (RecyclerView)findViewById(R.id.rvmovie);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        TextView txtOverview = (TextView)findViewById(R.id.txtOverview);
        TextView txtRate = (TextView)findViewById(R.id.txtRate);
        ImageView iv_detail_gambar = (ImageView)findViewById(R.id.iv_detail_gambar);
        RatingBar rating = (RatingBar) findViewById(R.id.ratingBar);

        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        dataTv = getIntent().getParcelableArrayListExtra("MOVIE");
        position = getIntent().getIntExtra("POSITION", 0);

        rating.setRating(Float.valueOf(dataTv.get(position).getVoteAverage().toString()) / 2);
        txtRate.setText(dataTv.get(position).getVoteAverage().toString());
        getSupportActionBar().setTitle(dataTv.get(position).getName());
        txtOverview.setText(dataTv.get(position).getOverview());

        Glide.with(this).load("https://image.tmdb.org/t/p/w500" +
                dataTv.get(position).getBackdropPath()).into(iv_detail_gambar);

        isFavorite = preferences.getBoolean("FAVORITE" + dataTv.get(position).getName(), false);
        updateFAB();
        final SharedPreferences.Editor editor = preferences.edit();
        getSimilarMovie();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(isFavorite){
                    editor.putBoolean("FAVORITE" + dataTv.get(position).getName(), false);
                    editor.commit();
                    isFavorite = false;
                    Snackbar.make(view, "Deleted from favorite", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                    deleteData();
                }else{
                    editor.putBoolean("FAVORITE" + dataTv.get(position).getName(), true);
                    editor.commit();
                    isFavorite = true;
                    Snackbar.make(view, "Added to favorite", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                    saveData();
                }
                isFavorite = preferences.getBoolean("FAVORITE" + dataTv.get(position).getName(), false);
                updateFAB();
            }
        });
    }

    private void updateFAB() {
        if(isFavorite)
            fab.setImageResource(R.drawable.ic_is_favorite);
        else
            fab.setImageResource(R.drawable.ic_not_favorite);
    }

    private void deleteData(){
        int numRowsDeleted = getContentResolver().delete(
                TvContract.TvEntry.CONTENT_URI
                        .buildUpon()
                        .appendPath(String.valueOf(dataTv.get(position).getId()))
                        .build(), null, null);

        if (numRowsDeleted != 0) {
            return;
        } else {
            Toast.makeText(this, "Data gagal dihapus", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveData(){
        ContentValues cv = new ContentValues();
        cv.put(TvContract.TvEntry.COLUMN_ID, dataTv.get(position).getId());
        cv.put(TvContract.TvEntry.COLUMN_JUDUL, dataTv.get(position).getName());
        cv.put(TvContract.TvEntry.COLUMN_POSTER, dataTv.get(position).getPosterPath());
        cv.put(TvContract.TvEntry.COLUMN_RATING, dataTv.get(position).getVoteAverage());
        cv.put(TvContract.TvEntry.COLUMN_BACK_DROP, dataTv.get(position).getBackdropPath());
        cv.put(TvContract.TvEntry.COLUMN_OVERVIEW, dataTv.get(position).getOverview());

        Uri uri = getContentResolver().insert(TvContract.TvEntry.CONTENT_URI, cv);
        if (ContentUris.parseId(uri) > 0){
            return;
        } else {
            Toast.makeText(this, "Data gagal disimpan", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSimilarMovie(){
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListTv> movieCall = api.getTv("tv/"+dataTv.get(position).getId()+"/recommendations?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=1");
        movieCall.enqueue(new Callback<ModelListTv>() {

            @Override
            public void onResponse(Call<ModelListTv> call, Response<ModelListTv> response) {
                if (rvmovie == null)
                    return;
                progress_popular.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    dataSimilar = response.body().getResults();
                    AdapterTv adapter = new AdapterTv(getBaseContext(), dataSimilar);

                    if (rvmovie == null)
                        return;
                    rvmovie.setAdapter(adapter);
                    rvmovie.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                } else {
                    Toast.makeText(getBaseContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelListTv> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progress_popular.setVisibility(View.INVISIBLE);
            }

        });
    }
}

