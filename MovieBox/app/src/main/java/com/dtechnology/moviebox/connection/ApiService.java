package com.dtechnology.moviebox.connection;

import com.dtechnology.moviebox.model.ModelListMovie;
import com.dtechnology.moviebox.model.ModelListTv;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by dtechnology on 30/01/18.
 */

public interface ApiService {
    //sort by popular
    @GET("movie/popular?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=1")
    Call<ModelListMovie> getMoviePopular();

    //sort by top rated
    @GET("movie/top_rated?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=1")
    Call<ModelListMovie>getMovieTopRated();

    @GET("movie/now_playing?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=1")
    Call<ModelListMovie>getMovieNowPlaying();

    @GET("movie/upcoming?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=1")
    Call<ModelListMovie>getMovieUpcoming();

    //------TV------//
    @GET("tv/popular?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=1")
    Call<ModelListTv>getTvPopular();

    @GET("tv/top_rated?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=1")
    Call<ModelListTv>getTvTopRated();

    @GET("tv/airing_today?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&page=1")
    Call<ModelListTv>getTvAiring();

    @GET
    Call<ModelListTv>getTv(@Url String url);

    @GET
    Call<ModelListMovie>getMovie(@Url String url);
}

