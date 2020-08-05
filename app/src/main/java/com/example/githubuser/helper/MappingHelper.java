package com.example.githubuser.helper;

import android.database.Cursor;

import com.example.githubuser.database.DatabaseContract;
import com.example.githubuser.model.User;

import java.util.ArrayList;

public class MappingHelper {
    public static ArrayList<User> mapCursorToArrayList(Cursor userCursor){
        ArrayList<User> userArrayList = new ArrayList<>();

        while (userCursor.moveToNext()){
            int id = userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseContract.FavoriteColumns._ID));
            String name = userCursor.getString(userCursor.getColumnIndexOrThrow(DatabaseContract.FavoriteColumns.NAME));
            String username = userCursor.getString(userCursor.getColumnIndexOrThrow(DatabaseContract.FavoriteColumns.USERNAME));
            String avatar = userCursor.getString(userCursor.getColumnIndexOrThrow(DatabaseContract.FavoriteColumns.AVATAR_URL));
            userArrayList.add(new User(id, name, username, avatar));
        }
        return userArrayList;
    }
}
