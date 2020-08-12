package com.example.githubuser.ui.detail_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.githubuser.R;
import com.example.githubuser.adapter.SectionsPagerAdapter;
import com.example.githubuser.database.DatabaseContract;
import com.example.githubuser.database.FavoriteUserHelper;
import com.example.githubuser.model.User;
import com.example.githubuser.ui.favorite.FavoriteActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.githubuser.database.DatabaseContract.FavoriteColumns.AVATAR_URL;
import static com.example.githubuser.database.DatabaseContract.FavoriteColumns.LOCATION;
import static com.example.githubuser.database.DatabaseContract.FavoriteColumns.NAME;
import static com.example.githubuser.database.DatabaseContract.FavoriteColumns.USERNAME;

public class DetailUserActivity extends AppCompatActivity {
    public static final String EXTRA_USERNAME = "extra_username";

    private ProgressBar progressBarProfile;
    private FloatingActionButton fab;

    private TextView tvName;
    private TextView tvUsernameProfile;
    private TextView tvLocation;
    private CircleImageView imgAvatarProfile;
    Boolean statusFavorite = false;
    private FavoriteUserHelper favoriteUserHelper;

    private DetailUserViewModel detailUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);
        getSupportActionBar().setTitle(R.string.detail_user);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail User");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fab = findViewById(R.id.floating_action);

        User mUser = getIntent().getParcelableExtra(EXTRA_USERNAME);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());
        sectionsPagerAdapter.username = mUser.getUsername();
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tvName = findViewById(R.id.tv_name);
        tvUsernameProfile = findViewById(R.id.tv_username_profile);
        tvLocation = findViewById(R.id.tv_location_profile);
        imgAvatarProfile = findViewById(R.id.img_avatar_profile);
        progressBarProfile = findViewById(R.id.progressbar_profile);


        detailUserViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(DetailUserViewModel.class);
        detailUserViewModel.setDetailUserViewModel(mUser.getUsername());
        detailUserViewModel.getName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String sname) {
                if (sname != null){
                    tvName.setText(sname);
                    showLoading(false);
                }
            }
        });

        detailUserViewModel.getUsernames().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String sUsername) {
                if (sUsername != null){
                    tvUsernameProfile.setText(sUsername);
                    showLoading(false);
                }
            }
        });

        detailUserViewModel.getLocation().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String sLocation) {
                if (sLocation != null){
                    tvLocation.setText(sLocation);
                    showLoading(false);
                }
            }
        });

        detailUserViewModel.getAvatar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String sAvatar) {
                if (sAvatar != null){
                    Glide.with(DetailUserActivity.this)
                            .load(sAvatar)
                            .apply(new RequestOptions().override(125,125))
                            .into(imgAvatarProfile);
                    showLoading(false);
                }
            }
        });

//        favoriteUserHelper = FavoriteUserHelper.getInstance(getApplicationContext());
//        favoriteUserHelper.open();

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        FavoriteActivity.DataObserver observer = new FavoriteActivity.DataObserver(handler, this);
        getContentResolver().registerContentObserver(DatabaseContract.FavoriteColumns.CONTENT_URI, true, observer);

//ini diganti nanti
        Cursor cursor = favoriteUserHelper.queryByUsername(mUser.getUsername());
        if (cursor.getCount() >= 1){
            setStatusFavorite(true);
        } else {
            setStatusFavorite(false);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusFavorite = !statusFavorite;
                Cursor cursor = favoriteUserHelper.queryByUsername(detailUserViewModel.getUsernames().getValue());
                if (cursor.getCount() < 1){
                    insertDatabase(detailUserViewModel.getName().getValue(), detailUserViewModel.getUsernames().getValue(),
                            detailUserViewModel.getAvatar().getValue(), detailUserViewModel.getLocation().getValue());
                    Toast.makeText(DetailUserActivity.this, "Favorite", Toast.LENGTH_SHORT).show();
                    setStatusFavorite(true);
                }else if (cursor.getCount() >=1){
                    favoriteUserHelper.deleteById(detailUserViewModel.getUsernames().getValue());
                    Toast.makeText(DetailUserActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                    setStatusFavorite(false);
                }
            }
        });
    }

    private void showLoading(Boolean state){
        if (state){
            progressBarProfile.setVisibility(View.VISIBLE);
        } else {
            progressBarProfile.setVisibility(View.GONE);
        }
    }

    private void insertDatabase(String name, String username, String avatar, String location){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(USERNAME, username);
        contentValues.put(AVATAR_URL, avatar);
        contentValues.put(LOCATION, location);

        favoriteUserHelper.insert(contentValues);
    }

    private boolean setStatusFavorite(Boolean statusFavorite){
        if (statusFavorite){
            fab.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            fab.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

        return statusFavorite;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

//    public static class DataObserver extends ContentObserver {
//        final Context context;
//
//        public DataObserver(Handler handler, Context context){
//            super(handler);
//            this.context = context;
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            new LoadUserFavoriteAsync(context, (LoadUserFavoriteCallback) context).execute();
//        }
//    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favoriteUserHelper.close();
    }
}