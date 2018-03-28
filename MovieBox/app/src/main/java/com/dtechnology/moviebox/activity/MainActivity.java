package com.dtechnology.moviebox.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.dtechnology.moviebox.R;
import com.dtechnology.moviebox.fragment.FavoriteFragment;
import com.dtechnology.moviebox.fragment.MovieFragment;
import com.dtechnology.moviebox.fragment.TvFragment;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class MainActivity extends AppCompatActivity {

    Bundle b;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(b == null) {
                switch (item.getItemId()) {
                    case R.id.navigation_movie:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.layout_untuk_fragment, new MovieFragment())
                                .commit();
                        return true;
                    case R.id.navigation_tv:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.layout_untuk_fragment, new TvFragment())
                                .commit();
                        return true;
                    case R.id.navigation_favorite:
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.layout_untuk_fragment, new FavoriteFragment())
                                .commit();
                        return true;
                }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.layout_untuk_fragment, new MovieFragment())
                    .commit();
        }
    }

}
