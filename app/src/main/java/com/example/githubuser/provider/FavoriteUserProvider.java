package com.example.githubuser.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.example.githubuser.database.FavoriteUserHelper;

import static com.example.githubuser.database.DatabaseContract.AUTHORITY;
import static com.example.githubuser.database.DatabaseContract.FavoriteColumns.CONTENT_URI;
import static com.example.githubuser.database.DatabaseContract.FavoriteColumns.TABLE_NAME;

public class FavoriteUserProvider extends ContentProvider {
    private static final int FAVORITE_USER = 1;
    private static final int FAVORITE_USER_ID = 2;
    private FavoriteUserHelper favoriteUserHelper;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAVORITE_USER);

        mUriMatcher.addURI(AUTHORITY,
                TABLE_NAME + "/#",
                FAVORITE_USER_ID);

        mUriMatcher.addURI(AUTHORITY,
                TABLE_NAME + "/*",
                FAVORITE_USER_ID);
    }

    public FavoriteUserProvider() {
    }

    @Override
    public boolean onCreate() {
        favoriteUserHelper = FavoriteUserHelper.getInstance(getContext());
        favoriteUserHelper.open();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (mUriMatcher.match(uri)){
            case FAVORITE_USER:
                cursor = favoriteUserHelper.queryAll();
                break;
            case FAVORITE_USER_ID:
                cursor = favoriteUserHelper.queryByUsername(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long added;
        if (mUriMatcher.match(uri) == FAVORITE_USER){
            added = favoriteUserHelper.insert(values);
        } else {
            added = 0;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return Uri.parse(CONTENT_URI + "/" + added);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleted;
        if (mUriMatcher.match(uri) == FAVORITE_USER_ID){
            deleted = favoriteUserHelper.deleteById(uri.getLastPathSegment());
        } else {
            deleted = 0;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return deleted;
    }


}
