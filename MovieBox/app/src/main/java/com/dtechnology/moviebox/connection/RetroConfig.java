package com.dtechnology.moviebox.connection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by root on 30/01/18.
 */

public class RetroConfig {
    private static Retrofit getRetrofit(){
        Retrofit r = new Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/").
                addConverterFactory(GsonConverterFactory.create()).build();

        return r;
    }

    public static ApiService getApiServices(){
        return getRetrofit().create(ApiService.class);
    }


}
