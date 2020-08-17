package com.example.consumerapps.detail_user;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.consumerapps.R;
import com.example.consumerapps.adapter.SectionsPagerAdapter;
import com.example.consumerapps.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.consumerapps.database.DatabaseContract.FavoriteColumns.AVATAR_URL;
import static com.example.consumerapps.database.DatabaseContract.FavoriteColumns.CONTENT_URI;
import static com.example.consumerapps.database.DatabaseContract.FavoriteColumns.LOCATION;
import static com.example.consumerapps.database.DatabaseContract.FavoriteColumns.NAME;
import static com.example.consumerapps.database.DatabaseContract.FavoriteColumns.USERNAME;

public class DetailUserActivity extends AppCompatActivity {
    public static final String EXTRA_USERNAME = "extra_username";

    private ProgressBar progressBarProfile;
    private FloatingActionButton fab;

    private TextView tvName;
    private TextView tvUsernameProfile;
    private TextView tvLocation;
    private CircleImageView imgAvatarProfile;
    Boolean statusFavorite = false;

    private DetailUserViewModel detailUserViewModel;
    private Uri uriByUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.detail_user);
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

        Log.d(DetailUserActivity.class.getSimpleName(), "Detail username: "+ mUser.getUsername());
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

        uriByUsername = Uri.parse(CONTENT_URI + "/" + mUser.getUsername());
        Cursor cursorUsername = getContentResolver().query(uriByUsername,
                null,
                null,
                null,
                null);

        if (cursorUsername != null){
            if (cursorUsername.getCount() >= 1){
                statusFavorite = true;
                setStatusFavorite(statusFavorite);
            } else {
                statusFavorite = false;
                setStatusFavorite(statusFavorite);
            }
            cursorUsername.close();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusFavorite = !statusFavorite;
                Cursor cursorClick = getContentResolver().query(uriByUsername, null, null, null, null);
                Log.d(DetailUserActivity.class.getSimpleName(), "Cursor click: "+cursorClick);
                if (cursorClick != null){
                    if (cursorClick.getCount() < 1){
                        insertDatabase(detailUserViewModel.getName().getValue(), detailUserViewModel.getUsernames().getValue(),
                                detailUserViewModel.getAvatar().getValue(), detailUserViewModel.getLocation().getValue());
                        Toast.makeText(DetailUserActivity.this, "Favorite", Toast.LENGTH_SHORT).show();
                        statusFavorite = true;
                        setStatusFavorite(statusFavorite);
                    }else if (cursorClick.getCount() >=1){
                        getContentResolver().delete(uriByUsername, null, null);
                        Toast.makeText(DetailUserActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                        statusFavorite = false;
                        setStatusFavorite(statusFavorite);
                    }
                    cursorClick.close();
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

        getContentResolver().insert(CONTENT_URI, contentValues);
    }

    private void setStatusFavorite(Boolean statusFavorite){
        if (statusFavorite){
            fab.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            fab.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}