package com.example.githubuser.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.example.githubuser.database.DatabaseContract.FavoriteColumns.TABLE_NAME;
import static com.example.githubuser.database.DatabaseContract.FavoriteColumns.USERNAME;

public class FavoriteUserHelper {
    private static final String DATABASE_TABLE = TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static FavoriteUserHelper INSTANCE;

    private static SQLiteDatabase database;

    private FavoriteUserHelper(Context context){
        databaseHelper = new DatabaseHelper(context);
    }

    public static FavoriteUserHelper getInstance(Context context){
        if (INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){
                if (INSTANCE == null){
                    INSTANCE = new FavoriteUserHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException{
        database = databaseHelper.getWritableDatabase();
    }

    public Cursor queryAll(){
        return database.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                _ID + " ASC");
    }

    public Cursor queryByUsername(String username){
        return database.query(
                DATABASE_TABLE,
                null,
                USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null,
                null);
    }

    public long insert(ContentValues contentValues){
        return database.insert(DATABASE_TABLE, null, contentValues);
    }

    public int deleteById(String id){
        return database.delete(DATABASE_TABLE, USERNAME + " = ?", new String[]{id});
    }

    public void close(){
        databaseHelper.close();
        if (database.isOpen()){
            database.close();
        }
    }
}
