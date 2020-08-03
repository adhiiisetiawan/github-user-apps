package com.example.githubuser.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "favoritedb";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_FAVORITE_USER = String.format("CREATE TABLE %s"
                + " (%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " %s TEXT NOT NULL,"
                + " %s TEXT NOT NULL,"
                + " %s TEXT NOT NULL,"
                + " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_NAME,
            DatabaseContract.FavoriteColumns._ID,
            DatabaseContract.FavoriteColumns.NAME,
            DatabaseContract.FavoriteColumns.USERNAME,
            DatabaseContract.FavoriteColumns.AVATAR_URL,
            DatabaseContract.FavoriteColumns.LOCATION
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        onCreate(db);
    }
}
