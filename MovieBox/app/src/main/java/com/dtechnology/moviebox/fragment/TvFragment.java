package com.dtechnology.moviebox.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.dtechnology.moviebox.R;
import com.dtechnology.moviebox.adapter.AdapterTv;
import com.dtechnology.moviebox.connection.ApiService;
import com.dtechnology.moviebox.connection.RetroConfig;
import com.dtechnology.moviebox.model.DataTv;
import com.dtechnology.moviebox.model.ModelListTv;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvFragment extends Fragment {
    ArrayList<DataTv> dataMovies = new ArrayList<>();
    ArrayList<DataTv> dataTopMovies = new ArrayList<>();
    ArrayList<DataTv> dataNowPlaying = new ArrayList<>();
    ArrayList<BaseSliderView> slider = new ArrayList<>();

    Unbinder unbinder;
    @BindView(R.id.tv_now)
    SliderLayout tv_now;
    @BindView(R.id.rvmovie)
    RecyclerView rvmovie;
    @BindView(R.id.rv_top_movie)
    RecyclerView rv_top_movie;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    @BindView(R.id.progress_popular)
    ProgressBar progress_popular;
    @BindView(R.id.progress_top)
    ProgressBar progress_top;


    public TvFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            dataMovies = savedInstanceState.getParcelableArrayList("data");
            dataTopMovies = savedInstanceState.getParcelableArrayList("dataTop");
        } else {
            getPopularData();
            getTopData();
            getNowPlaying();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tv, container, false);
        unbinder = ButterKnife.bind(this, view);
        getActivity().setTitle("Popoular");

        AdapterTv adapter = new AdapterTv(getContext(), dataMovies);
        rvmovie.setAdapter(adapter);
        rvmovie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapter = new AdapterTv(getContext(), dataTopMovies);
        rv_top_movie.setAdapter(adapter);
        rv_top_movie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        for (BaseSliderView i : slider) {
            tv_now.addSlider(i);
        }
        return view;
    }

    private void getNowPlaying() {
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListTv> movieCall = api.getTvAiring();

        movieCall.enqueue(new Callback<ModelListTv>() {

            @Override
            public void onResponse(Call<ModelListTv> call, Response<ModelListTv> response) {
                if (tv_now == null)
                    return;
                progressBar2.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    dataNowPlaying = response.body().getResults();
                    //add banner using image url
                    slider.clear();
                    for (int i = 0; i < dataNowPlaying.size(); i++) {
                        if (tv_now == null)
                            return;
                        tv_now.addSlider(new TextSliderView(getContext()).description(dataNowPlaying.get(i).getName())
                                .image("https://image.tmdb.org/t/p/w500/" + dataNowPlaying.get(i)
                                        .getBackdropPath()).setScaleType(BaseSliderView.ScaleType.CenterCrop));
                    }
                } else {
                    Toast.makeText(getContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelListTv> call, Throwable t) {
                progressBar2.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void getPopularData() {
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListTv> movieCall = api.getTvPopular();

        movieCall.enqueue(new Callback<ModelListTv>() {

            @Override
            public void onResponse(Call<ModelListTv> call, Response<ModelListTv> response) {
                if (rvmovie == null)
                    return;
                progress_popular.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    dataMovies = response.body().getResults();
                    AdapterTv adapter = new AdapterTv(getContext(), dataMovies);
                    rvmovie.setAdapter(adapter);
                    rvmovie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                } else {
                    Toast.makeText(getContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelListTv> call, Throwable t) {
                progress_popular.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void getTopData() {
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListTv> movieCall = api.getTvTopRated();

        movieCall.enqueue(new Callback<ModelListTv>() {

            @Override
            public void onResponse(Call<ModelListTv> call, Response<ModelListTv> response) {
                if (rv_top_movie == null)
                    return;

                progress_top.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    dataTopMovies = response.body().getResults();
                    AdapterTv adapter = new AdapterTv(getContext(), dataTopMovies);

                    rv_top_movie.setAdapter(adapter);
                    rv_top_movie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                } else {
                    Toast.makeText(getContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelListTv> call, Throwable t) {
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}