package com.dtechnology.moviebox.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dtechnology.moviebox.R;
import com.dtechnology.moviebox.adapter.AdapterMovie;
import com.dtechnology.moviebox.connection.ApiService;
import com.dtechnology.moviebox.connection.RetroConfig;
import com.dtechnology.moviebox.model.DataMovie;
import com.dtechnology.moviebox.model.ModelListMovie;
import com.github.pwittchen.infinitescroll.library.InfiniteScrollListener;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeeAllMovie extends AppCompatActivity {
    ArrayList<DataMovie> dataMovies = new ArrayList<>();
    int i = 1;

    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, 1, false);
    RecyclerView rv_movie;
    String url;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        url = extras.getString("Url");
        dataMovies.clear();

        setContentView(R.layout.activity_see_all_movie);
        rv_movie = (RecyclerView)findViewById(R.id.rv_movie);
        bar = (ProgressBar)findViewById(R.id.progressBar3);

        if (savedInstanceState != null)
            dataMovies = savedInstanceState.getParcelableArrayList("data");
        else
            getData();

        AdapterMovie adapter = new AdapterMovie(this, dataMovies);
        rv_movie.setAdapter(adapter);
        rv_movie.setLayoutManager(gridLayoutManager);
        rv_movie.addOnScrollListener(createInfiniteScrollListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(extras.getString("Title"));
    }

    private InfiniteScrollListener createInfiniteScrollListener() {
        return new InfiniteScrollListener(1, gridLayoutManager) {
            @Override public void onScrolledToEnd(final int firstVisibleItemPosition) {
                // load your items here
                // logic of loading items will be different depending on your specific use case
                bar.setVisibility(View.VISIBLE);
                ApiService api = RetroConfig.getApiServices();
                Call<ModelListMovie> movieCall = api.getMovie(url+i);

                movieCall.enqueue(new Callback<ModelListMovie>() {

                    @Override
                    public void onResponse(Call<ModelListMovie> call, Response<ModelListMovie> response) {
                        if (rv_movie == null)
                            return;
                        if (response.isSuccessful()) {
                            dataMovies.addAll(response.body().getResults());
                        } else {
                            Toast.makeText(getBaseContext(), "Response failed", Toast.LENGTH_SHORT).show();
                        }

                        bar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<ModelListMovie> call, Throwable t) {
                        Toast.makeText(getBaseContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                i++;
                // when new items are loaded, combine old and new items, pass them to your adapter
                // and call refreshView(...) method from InfiniteScrollListener class to refresh RecyclerView
                refreshView(rv_movie, new AdapterMovie(getBaseContext(), dataMovies), firstVisibleItemPosition);
            }
        };
    }

    private void getData(){
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListMovie> movieCall = api.getMovie(url+i);

        movieCall.enqueue(new Callback<ModelListMovie>() {

            @Override
            public void onResponse(Call<ModelListMovie> call, Response<ModelListMovie> response) {
                bar.setVisibility(View.INVISIBLE);
                if (rv_movie == null)
                    return;
                if (response.isSuccessful()) {
                    dataMovies = response.body().getResults();
                    response.body().setPage(i);
                    AdapterMovie adapter = new AdapterMovie(getBaseContext(), dataMovies);

                    if (rv_movie == null)
                        return;
                    rv_movie.setAdapter(adapter);
                    rv_movie.setLayoutManager(gridLayoutManager);
                } else {
                    Toast.makeText(getBaseContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelListMovie> call, Throwable t) {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(getBaseContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        i++;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dataMovies != null)
            outState.putParcelableArrayList("data", dataMovies);
    }
}
