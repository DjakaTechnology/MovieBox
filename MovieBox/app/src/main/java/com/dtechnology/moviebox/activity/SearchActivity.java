package com.dtechnology.moviebox.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dtechnology.moviebox.R;
import com.dtechnology.moviebox.adapter.AdapterMovie;
import com.dtechnology.moviebox.adapter.AdapterTv;
import com.dtechnology.moviebox.connection.ApiService;
import com.dtechnology.moviebox.connection.RetroConfig;
import com.dtechnology.moviebox.model.DataMovie;
import com.dtechnology.moviebox.model.DataTv;
import com.dtechnology.moviebox.model.ModelListMovie;
import com.dtechnology.moviebox.model.ModelListTv;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    EditText txtSearch;
    ImageButton button2;
    RecyclerView rvmovie, rvtv;
    TextView txtMovie, txtTv;

    ProgressBar progress_popular, progress_tv;

    ArrayList<DataMovie> dataMovies = new ArrayList<>();
    ArrayList<DataTv> dataTv = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progress_popular = (ProgressBar)findViewById(R.id.progress_popular);
        progress_tv = (ProgressBar)findViewById(R.id.progress_tv);
        txtSearch = (EditText) findViewById(R.id.txtSearch);
        button2 = (ImageButton) findViewById(R.id.button2);
        rvmovie = (RecyclerView)findViewById(R.id.rvmovie);
        rvtv = (RecyclerView)findViewById(R.id.rvtv);
        txtMovie = (TextView)findViewById(R.id.txtMovie);
        txtTv = (TextView)findViewById(R.id.txtTv);

        progress_popular.setVisibility(View.INVISIBLE);
        progress_tv.setVisibility(View.INVISIBLE);

        button2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                txtTv.setVisibility(View.INVISIBLE);
                txtMovie.setVisibility(View.INVISIBLE);
                progress_popular.setVisibility(View.VISIBLE);
                progress_tv.setVisibility(View.VISIBLE);

                getMovieData();
                getTvData();
            }
        });

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    txtTv.setVisibility(View.INVISIBLE);
                    txtMovie.setVisibility(View.INVISIBLE);
                    progress_popular.setVisibility(View.VISIBLE);
                    progress_tv.setVisibility(View.VISIBLE);
                    getMovieData();
                    getTvData();
                }
                return false;
            }
        });
    }

    void getMovieData(){
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListMovie> movieCall = api.getMovie("search/movie?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&query="+txtSearch.getText()+"&page=1&include_adult=false");
        movieCall.enqueue(new Callback<ModelListMovie>() {
            @Override
            public void onResponse(Call<ModelListMovie> call, Response<ModelListMovie> response) {
                if (rvmovie == null)
                        return;
                    progress_popular.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        dataMovies = response.body().getResults();
                        AdapterMovie adapter = new AdapterMovie(getBaseContext(), dataMovies);

                        if (rvmovie == null)
                            return;
                        rvmovie.setAdapter(adapter);
                        rvmovie.setLayoutManager(new GridLayoutManager(getBaseContext(), 3, GridLayoutManager.VERTICAL, false));
                    } else {
                        Toast.makeText(getBaseContext(), "Response failed", Toast.LENGTH_SHORT).show();
                    }

                    if(dataMovies.size() <= 0) {
                        txtMovie.setText("Sorry, we cant find " + txtSearch.getText() + " in our movie library :(");
                        txtMovie.setVisibility(View.VISIBLE);
                    }
                    else
                        txtMovie.setVisibility(View.INVISIBLE);
                }

            @Override
            public void onFailure(Call<ModelListMovie> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progress_popular.setVisibility(View.INVISIBLE);
            }
        });
    }

    void getTvData(){
        ApiService api = RetroConfig.getApiServices();
        Call<ModelListTv> movieCall = api.getTv("search/tv?api_key=e4b2e71c4f49a96f4a45715fa770a341&language=en-US&query="+txtSearch.getText()+"&page=1&include_adult=false");
        movieCall.enqueue(new Callback<ModelListTv>() {
            @Override
            public void onResponse(Call<ModelListTv> call, Response<ModelListTv> response) {
                if (rvtv == null)
                    return;
                progress_tv.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    dataTv = response.body().getResults();
                    AdapterTv adapter = new AdapterTv(getBaseContext(), dataTv);

                    if (rvtv == null)
                        return;
                    rvtv.setAdapter(adapter);
                    rvtv.setLayoutManager(new GridLayoutManager(getBaseContext(), 3, GridLayoutManager.VERTICAL, false));
                } else {
                    Toast.makeText(getBaseContext(), "Response failed", Toast.LENGTH_SHORT).show();
                }

                if(dataTv.size() <= 0) {
                    txtTv.setText("Sorry, we cant find " + txtSearch.getText() + " in our tv shows library :(");
                    txtTv.setVisibility(View.VISIBLE);
                }
                else
                    txtTv.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ModelListTv> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Response failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progress_tv.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dataMovies != null)
            outState.putParcelableArrayList("data", dataMovies);
        if (dataTv != null)
            outState.putParcelableArrayList("dataUpcoming", dataTv);
    }
}