package com.dtechnology.moviebox.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dtechnology.moviebox.R;
import com.dtechnology.moviebox.adapter.AdapterMovie;
import com.dtechnology.moviebox.adapter.AdapterTv;
import com.dtechnology.moviebox.database.MovieContract;
import com.dtechnology.moviebox.database.TvContract;
import com.dtechnology.moviebox.model.DataMovie;
import com.dtechnology.moviebox.model.DataTv;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public FavoriteFragment() {
        // Required empty public constructor
    }

    RecyclerView recycler, recyclerTv;
    ArrayList<DataMovie> listdata = new ArrayList<>();
    ArrayList<DataTv> listTv= new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_favorite, container, false);

        recycler = fragmentView.findViewById(R.id.rvmovie);
        recyclerTv = fragmentView.findViewById(R.id.rv_tv);

        //data real
        getActivity().getSupportLoaderManager().initLoader(100, null, this);
        getActivity().getSupportLoaderManager().initLoader(150, null, this);

        //  Adapter
        AdapterMovie adapter = new AdapterMovie(getContext(),listdata);
        recycler.setAdapter(adapter);

        AdapterTv adapterTv = new AdapterTv(getContext(), listTv);
        recyclerTv.setAdapter(adapterTv);

        //  Layout Manager
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerTv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        return fragmentView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case 100:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        null, null, null, null);
            case 150:
                return new CursorLoader(
                        getActivity(),
                        TvContract.TvEntry.CONTENT_URI,
                        null,null,null,null);
            default:
                throw new RuntimeException("Loader Not working");
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == 100) {
            listdata.clear();
            if (data.getCount() > 0) {
                if (data.moveToFirst()) {
                    do {
                        DataMovie movie = new DataMovie();
                        movie.setId(data.getInt(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
                        movie.setTitle(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_JUDUL)));
                        movie.setPosterPath(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)));
                        movie.setBackdropPath(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACK_DROP)));
                        movie.setVoteAverage(data.getDouble(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
                        movie.setOverview(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                        //4 tambah sini
                        listdata.add(movie);
                        AdapterMovie adapter = new AdapterMovie(getActivity(), listdata);
                        recycler.setAdapter(adapter);
                    } while (data.moveToNext());
                    return;
                }
            }else{
                AdapterMovie adapter = new AdapterMovie(getActivity(), listdata);
                recycler.setAdapter(adapter);
            }
        }else{
            listTv.clear();
            if (data.getCount() > 0) {
                if (data.moveToFirst()) {
                    do {
                        DataTv movie = new DataTv();
                        movie.setId(data.getInt(data.getColumnIndex(TvContract.TvEntry.COLUMN_ID)));
                        movie.setName(data.getString(data.getColumnIndex(TvContract.TvEntry.COLUMN_JUDUL)));
                        movie.setPosterPath(data.getString(data.getColumnIndex(TvContract.TvEntry.COLUMN_POSTER)));
                        movie.setBackdropPath(data.getString(data.getColumnIndex(TvContract.TvEntry.COLUMN_BACK_DROP)));
                        movie.setVoteAverage(data.getDouble(data.getColumnIndex(TvContract.TvEntry.COLUMN_RATING)));
                        movie.setOverview(data.getString(data.getColumnIndex(TvContract.TvEntry.COLUMN_OVERVIEW)));
                        listTv.add(movie);
                        AdapterTv adapter = new AdapterTv(getActivity(), listTv);
                        recyclerTv.setAdapter(adapter);
                    } while (data.moveToNext());
                }
            }else {
                AdapterTv adapter = new AdapterTv(getActivity(), listTv);
                recyclerTv.setAdapter(adapter);
            }
        }
    }

    // Dipanggil apabila loader yang dibuat sebelumnya selesai dijalankan
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}