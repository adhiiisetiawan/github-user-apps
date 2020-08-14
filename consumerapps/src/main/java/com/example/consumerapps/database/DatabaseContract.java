package com.example.consumerapps.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static final String AUTHORITY = "com.example.githubuser";
    private static final String SCHEME = "content";

    public DatabaseContract() {
    }

    public static final class FavoriteColumns implements BaseColumns{
        public static String TABLE_NAME = "favorite_user";
        public static String NAME = "name";
        public static String USERNAME = "username";
        public static String AVATAR_URL = "avatar_url";
        public static String LOCATION = "location";

        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

    }
}
