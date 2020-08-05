package com.example.githubuser.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.githubuser.R;
import com.example.githubuser.adapter.UserFavoriteAdapter;
import com.example.githubuser.database.FavoriteUserHelper;
import com.example.githubuser.helper.MappingHelper;
import com.example.githubuser.model.User;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity implements LoadUserFavoriteCallback{
    private ProgressBar progressBar;
    private RecyclerView rvUser;
    private UserFavoriteAdapter userFavoriteAdapter;
    private FavoriteUserHelper favoriteUserHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Favorite User");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressBar = findViewById(R.id.progressbar_fav);
        rvUser = findViewById(R.id.recyclerview_fav);
        rvUser.setLayoutManager(new LinearLayoutManager(this));
        rvUser.setHasFixedSize(true);
        userFavoriteAdapter = new UserFavoriteAdapter(this);
        rvUser.setAdapter(userFavoriteAdapter);

        favoriteUserHelper = FavoriteUserHelper.getInstance(getApplicationContext());
        favoriteUserHelper.open();

        new LoadUserFavoriteAsync(favoriteUserHelper, this).execute();

        userFavoriteAdapter.setOnItemFavoriteClickCallback(new UserFavoriteAdapter.onItemFavoriteClickCallback() {
            @Override
            public void onItemClicked(User data) {
                Intent intent = new Intent(FavoriteActivity.this, DetailUserActivity.class);
                intent.putExtra(DetailUserActivity.EXTRA_USERNAME, data);
                startActivity(intent);
            }
        });
//        userFavoriteAdapter.removeItem();
    }

    @Override
    public void preExecute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<User> users) {
        progressBar.setVisibility(View.INVISIBLE);
        if (users.size() > 0){
            userFavoriteAdapter.setListUser(users);
        } else {
            userFavoriteAdapter.setListUser(new ArrayList<User>());
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    private static class LoadUserFavoriteAsync extends AsyncTask<Void, Void, ArrayList<User>>{
        private final WeakReference<FavoriteUserHelper> weakReferenceUserFavorite;
        private final WeakReference<LoadUserFavoriteCallback> weakReferenceCallback;

        private LoadUserFavoriteAsync(FavoriteUserHelper favoriteUserHelper, LoadUserFavoriteCallback callback){
            weakReferenceUserFavorite = new WeakReference<>(favoriteUserHelper);
            weakReferenceCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakReferenceCallback.get().preExecute();
        }

        @Override
        protected ArrayList<User> doInBackground(Void... voids) {
            Cursor cursor = weakReferenceUserFavorite.get().queryAll();
            return MappingHelper.mapCursorToArrayList(cursor);
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);
            weakReferenceCallback.get().postExecute(users);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favoriteUserHelper.close();
    }
}

interface LoadUserFavoriteCallback{
    void preExecute();
    void postExecute(ArrayList<User> users);
}