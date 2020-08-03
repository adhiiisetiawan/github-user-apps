package com.example.githubuser.database;

import android.provider.BaseColumns;

public class DatabaseContract {
    public static String TABLE_NAME = "favorite_user";

    public static final class FavoriteColumns implements BaseColumns{
        public static String NAME = "name";
        public static String USERNAME = "username";
        public static String AVATAR_URL = "avatar_url";
        public static String LOCATION = "location";
    }
}
