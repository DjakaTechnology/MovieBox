package com.dtechnology.moviebox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.dtechnology.moviebox.R;
import com.dtechnology.moviebox.activity.DetailActivity;
import com.dtechnology.moviebox.activity.SearchActivity;
import com.dtechnology.moviebox.activity.SeeAllMovie;
import com.dtechnology.moviebox.adapter.AdapterMovie;
import com.dtechnology.moviebox.connection.ApiService;
import com.dtechnology.moviebox.connection.RetroConfig;
import com.dtechnology.moviebox.model.DataMovie;
import com.dtechnology.moviebox.model.ModelListMovie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment {
    ArrayList<DataMovie> dataMovies = new ArrayList<>();
    ArrayList<DataMovie> dataTopMovies = new ArrayList<>();
    ArrayList<DataMovie> dataNowPlaying = new ArrayList<>();
    ArrayList<DataMovie> dataUpcoming = new ArrayList<>();
    ArrayList<TextSliderView> slider = new ArrayList<>();

    Unbinder unbinder;

    @BindView(R.id.rvmovie)
    RecyclerView rvmovie;
    @BindView(R.id.rv_top_movie)
    RecyclerView rv_top_movie;
    @BindView(R.id.movie_now)
    SliderLayout movie_now;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    @BindView(R.id.progress_popular)
    ProgressBar progress_popular;
    @BindView(R.id.progress_top)
    ProgressBar progress_top;
    @BindView(R.id.seeAllPopular)
    Button seeAllPopular;
    @BindView(R.id.seeAllTop)
    Button seeAllTop;
    @BindView(R.id.seeAllUpcoming)
    Button seeAllUpcoming;
    @BindView(R.id.progress_upcoming)
    ProgressBar progress_upcoming;
    @BindView(R.id.rv_upcoming)
    RecyclerView rv_upcoming;
    @BindView(R.id.search_button)
    FloatingActionButton search_button;

    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            dataMovies = savedInstanceState.getParcelableArrayList("data");
            dataTopMovies = savedInstanceState.getParcelableArrayList("dataTop");
            dataUpcoming = savedInstanceState.getParcelableArrayList("dataUpcoming");
        } else {
            getUpcoming();
            getPopularData();
            getTopData();
            getNowPlaying();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        unbinder = ButterKnife.bind(this, view);
        getActivity().setTitle("Popoular");

        AdapterMovie adapter = new AdapterMovie(getContext(), dataMovies);
        rvmovie.setAdapter(adapter);
        rvmovie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapter = new AdapterMovie(getContext(), dataTopMovies);
        rv_top_movie.setAdapter(adapter);
        rv_top_movie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapter = new AdapterMovie(getContext(), dataUpcoming);
        rv_upcoming.setAdapter(adapter);
        rv_upcoming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        for (BaseSliderView i : slider) {
            movie_now.addSlider(i);
        }

        seeAllPopular.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SeeAllMovie.class);
                i.putExtra("Url", "movie/popular?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=");
                i.putExtra("Title", "All Popular Movie");
                getContext().startActivity(i);
            }
        });

        seeAllTop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SeeAllMovie.class);
                i.putExtra("Url", "movie/top_rated?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=");
                i.putExtra("Title", "All Top Rated Movie");
                getContext().startActivity(i);
            }
        });

        seeAllUpcoming.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SeeAllMovie.class);
                i.putExtra("Url", "movie/upcoming?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=");
                i.putExtra("Title", "Coming Soon in Cinemas");
                getContext().startActivity(i);
            }
        });

        search_button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SearchActivity.class);
                getContext().startActivity(i);
            }
        });
        return view;
    }

    private void getNowPlaying() {
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListMovie> movieCall = api.getMovieNowPlaying();

        movieCall.enqueue(new Callback<ModelListMovie>() {

            @Override
            public void onResponse(Call<ModelListMovie> call, Response<ModelListMovie> response) {
                if (movie_now == null)
                    return;
                if (response.isSuccessful()) {
                    dataNowPlaying = response.body().getResults();
                    //add banner using image url
                    progressBar2.setVisibility(View.INVISIBLE);
                    slider.clear();
                    for (int i = 0; i < dataNowPlaying.size(); i++) {
                        if (movie_now == null)
                            return;
                        final int a = i;
                        movie_now.addSlider(new TextSliderView(getContext())
                                .description(dataNowPlaying.get(i).getTitle())
                                .image("https://image.tmdb.org/t/p/w500/" + dataNowPlaying.get(i)
                                        .getBackdropPath()).setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        Intent i = new Intent(getContext(), DetailActivity.class);
                                        i.putParcelableArrayListExtra("MOVIE", dataNowPlaying);
                                        i.putExtra("POSITION", a);
                                        getContext().startActivity(i);
                                    }
                                }));
                    }
                } else {
                    Toast.makeText(getContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelListMovie> call, Throwable t) {
                progressBar2.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void getUpcoming() {
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListMovie> movieCall = api.getMovieUpcoming();

        movieCall.enqueue(new Callback<ModelListMovie>() {

            @Override
            public void onResponse(Call<ModelListMovie> call, Response<ModelListMovie> response) {
                if (rv_upcoming == null)
                    return;
                progress_upcoming.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    dataUpcoming = response.body().getResults();
                    AdapterMovie adapter = new AdapterMovie(getContext(), dataUpcoming);

                    if (rv_upcoming == null)
                        return;
                    rv_upcoming.setAdapter(adapter);
                    rv_upcoming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                } else {
                    Toast.makeText(getContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelListMovie> call, Throwable t) {
                Toast.makeText(getContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progress_popular.setVisibility(View.INVISIBLE);
            }

        });
    }

    private void getPopularData() {
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListMovie> movieCall = api.getMoviePopular();

        movieCall.enqueue(new Callback<ModelListMovie>() {

            @Override
            public void onResponse(Call<ModelListMovie> call, Response<ModelListMovie> response) {
                if (rvmovie == null)
                    return;
                progress_popular.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    dataMovies = response.body().getResults();
                    AdapterMovie adapter = new AdapterMovie(getContext(), dataMovies);

                    if (rvmovie == null)
                        return;
                    rvmovie.setAdapter(adapter);
                    rvmovie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                } else {
                    Toast.makeText(getContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelListMovie> call, Throwable t) {
                Toast.makeText(getContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progress_popular.setVisibility(View.INVISIBLE);
            }

        });
    }

    public void getTopData() {
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListMovie> movieCall = api.getMovieTopRated();

        movieCall.enqueue(new Callback<ModelListMovie>() {

            @Override
            public void onResponse(Call<ModelListMovie> call, Response<ModelListMovie> response) {
                if (rv_top_movie == null)
                    return;
                progress_top.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    dataTopMovies = response.body().getResults();
                    AdapterMovie adapter = new AdapterMovie(getContext(), dataTopMovies);

                    rv_top_movie.setAdapter(adapter);
                    rv_top_movie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                } else {
                    Toast.makeText(getContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelListMovie> call, Throwable t) {
                if (progress_top == null)
                    return;
                progress_top.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
        //dialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (dataMovies != null)
            outState.putParcelableArrayList("data", dataMovies);
        if (dataTopMovies != null)
            outState.putParcelableArrayList("dataTop", dataTopMovies);
        if (dataUpcoming != null)
            outState.putParcelableArrayList("dataUpcoming", dataUpcoming);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}