package com.dtechnology.moviebox.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.media.Rating;
import android.net.Uri;
import android.nfc.Tag;
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
import com.dtechnology.moviebox.adapter.AdapterMovie;
import com.dtechnology.moviebox.connection.ApiService;
import com.dtechnology.moviebox.connection.RetroConfig;
import com.dtechnology.moviebox.database.MovieContract;
import com.dtechnology.moviebox.database.TvContract;
import com.dtechnology.moviebox.model.DataMovie;
import com.dtechnology.moviebox.model.ModelListMovie;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    ArrayList<DataMovie> dataMovies = new ArrayList<DataMovie>();
    ArrayList<DataMovie> dataSimilar = new ArrayList<>();
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
        rvmovie = (RecyclerView)findViewById(R.id.rvmovie);
        progress_popular = (ProgressBar)findViewById(R.id.progress_popular);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView txtOverview = (TextView)findViewById(R.id.txtOverview);
        ImageView iv_detail_gambar = (ImageView)findViewById(R.id.iv_detail_gambar);
        TextView txtRate = (TextView)findViewById(R.id.txtRate);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        RatingBar rating = (RatingBar)findViewById(R.id.ratingBar);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        dataMovies = getIntent().getParcelableArrayListExtra("MOVIE");
        position = getIntent().getIntExtra("POSITION", 0);

        rating.setRating(Float.valueOf(dataMovies.get(position).getVoteAverage().toString()) / 2);
        txtRate.setText(dataMovies.get(position).getVoteAverage().toString());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(dataMovies.get(position).getTitle());
        txtOverview.setText(dataMovies.get(position).getOverview());
        Glide.with(this).load("https://image.tmdb.org/t/p/w500" +
                dataMovies.get(position).getBackdropPath()).into(iv_detail_gambar);

        isFavorite = preferences.getBoolean("FAVORITE" + dataMovies.get(position).getTitle(), false);
        updateFAB();

        final SharedPreferences.Editor editor = preferences.edit();

        AdapterMovie adapter = new AdapterMovie(this, dataSimilar);
        rvmovie.setAdapter(adapter);
        rvmovie.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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
                    editor.putBoolean("FAVORITE" + dataMovies.get(position).getTitle(), false);
                    editor.commit();
                    isFavorite = false;
                    Snackbar.make(view, "Deleted from favorite", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                    deleteData();
                }else{
                    editor.putBoolean("FAVORITE" + dataMovies.get(position).getTitle(), true);
                    editor.commit();
                    isFavorite = true;
                    Snackbar.make(view, "Added to favorite", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                    saveData();
                }
                isFavorite = preferences.getBoolean("FAVORITE" + dataMovies.get(position).getTitle(), false);
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
                MovieContract.MovieEntry.CONTENT_URI
                        .buildUpon()
                        .appendPath(String.valueOf(dataMovies.get(position).getId()))
                        .build(), null, null);

        if (numRowsDeleted != 0) {
            return;
        } else {
            Toast.makeText(this, "Data gagal dihapus ("+numRowsDeleted+")", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveData(){
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_ID, dataMovies.get(position).getId());
        cv.put(MovieContract.MovieEntry.COLUMN_JUDUL, dataMovies.get(position).getTitle());
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER, dataMovies.get(position).getPosterPath());
        cv.put(MovieContract.MovieEntry.COLUMN_RATING, dataMovies.get(position).getVoteAverage());
        cv.put(MovieContract.MovieEntry.COLUMN_BACK_DROP, dataMovies.get(position).getBackdropPath());
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, dataMovies.get(position).getOverview());
        //3 tambah sini

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
        if (ContentUris.parseId(uri) > 0){
            return;
        } else {
            Toast.makeText(this, "Data gagal disimpan", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSimilarMovie(){
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListMovie> movieCall = api.getMovie("movie/"+dataMovies.get(position).getId()+"/recommendations?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=1");
        movieCall.enqueue(new Callback<ModelListMovie>() {

            @Override
            public void onResponse(Call<ModelListMovie> call, Response<ModelListMovie> response) {
                if (rvmovie == null)
                    return;
                progress_popular.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    dataSimilar = response.body().getResults();
                    AdapterMovie adapter = new AdapterMovie(getBaseContext(), dataSimilar);

                    if (rvmovie == null)
                        return;
                    rvmovie.setAdapter(adapter);
                    rvmovie.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                } else {
                    Toast.makeText(getBaseContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelListMovie> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progress_popular.setVisibility(View.INVISIBLE);
            }

        });
    }
}

