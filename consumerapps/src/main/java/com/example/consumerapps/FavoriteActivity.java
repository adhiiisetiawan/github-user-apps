package com.example.consumerapps;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.consumerapps.adapter.UserFavoriteAdapter;
import com.example.consumerapps.database.DatabaseContract;
import com.example.consumerapps.detail_user.DetailUserActivity;
import com.example.consumerapps.helper.MappingHelper;
import com.example.consumerapps.model.User;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity implements LoadUserFavoriteCallback{
    private ProgressBar progressBar;
    private RecyclerView rvUser;
    private UserFavoriteAdapter userFavoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Favorite User");
        }

        progressBar = findViewById(R.id.progressbar_fav);
        rvUser = findViewById(R.id.recyclerview_fav);
        rvUser.setLayoutManager(new LinearLayoutManager(this));
        rvUser.setHasFixedSize(true);
        userFavoriteAdapter = new UserFavoriteAdapter(this);
        userFavoriteAdapter.notifyDataSetChanged();
        rvUser.setAdapter(userFavoriteAdapter);

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        DataObserver observer = new DataObserver(handler, this);
        getContentResolver().registerContentObserver(DatabaseContract.FavoriteColumns.CONTENT_URI, true, observer);

        new LoadUserFavoriteAsync(this, this).execute();

        userFavoriteAdapter.setOnItemFavoriteClickCallback(new UserFavoriteAdapter.onItemFavoriteClickCallback() {
            @Override
            public void onItemClicked(User data) {
                Intent intent = new Intent(FavoriteActivity.this, DetailUserActivity.class);
                intent.putExtra(DetailUserActivity.EXTRA_USERNAME, data);
                startActivity(intent);
            }
        });
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
//            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    private static class LoadUserFavoriteAsync extends AsyncTask<Void, Void, ArrayList<User>>{
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadUserFavoriteCallback> weakReferenceCallback;

        private LoadUserFavoriteAsync(Context context, LoadUserFavoriteCallback callback){
            weakContext = new WeakReference<>(context);
            weakReferenceCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakReferenceCallback.get().preExecute();
        }

        @Override
        protected ArrayList<User> doInBackground(Void... voids) {
            Context context = weakContext.get();
            Cursor dataCursor = context.getContentResolver()
                    .query(DatabaseContract.FavoriteColumns.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
            return MappingHelper.mapCursorToArrayList(dataCursor);
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);
            weakReferenceCallback.get().postExecute(users);
        }
    }

    public static class DataObserver extends ContentObserver{
        final Context context;

        public DataObserver(Handler handler, Context context){
            super(handler);
            this.context = context;
        }

        //rubah onchange dg gambar tanpa toast
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadUserFavoriteAsync(context, (LoadUserFavoriteCallback) context).execute();
        }
    }
}

interface LoadUserFavoriteCallback{
    void preExecute();
    void postExecute(ArrayList<User> users);
}