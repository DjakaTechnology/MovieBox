package com.dtechnology.moviebox.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by root on 30/01/18.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "film.db";

    //kalau tiap kali ganti database harus dinaikan +1
    private static final int DATABASE_VERSION = 7;

    private static final String TAG = "MovieDbHelper";
    //logt

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //bertanggung jwb untuk membuat database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_ID + " INTEGER, " +
                MovieContract.MovieEntry.COLUMN_JUDUL + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_POSTER + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_BACK_DROP + " TEXT, "+
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RATING+" TEXT, "+
                "UNIQUE (" + MovieContract.MovieEntry.COLUMN_JUDUL + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TV = "CREATE TABLE " + TvContract.TvEntry.TABLE_NAME + " (" +
                TvContract.TvEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TvContract.TvEntry.COLUMN_ID + " INTEGER, " +
                TvContract.TvEntry.COLUMN_JUDUL + " TEXT, " +
                TvContract.TvEntry.COLUMN_POSTER + " TEXT, " +
                TvContract.TvEntry.COLUMN_BACK_DROP + " TEXT, "+
                TvContract.TvEntry.COLUMN_OVERVIEW + " TEXT, " +
                TvContract.TvEntry.COLUMN_RATING + " TEXT, "+
                "UNIQUE (" + TvContract.TvEntry.COLUMN_JUDUL + ") ON CONFLICT REPLACE);";

        Log.d(TAG, "onCreate: "+SQL_CREATE_TV);

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE);
        sqLiteDatabase.execSQL(SQL_CREATE_TV);
    }
    //bertanggung jwb untuk memastikan skema database selalu deperbaharui
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TvContract.TvEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}