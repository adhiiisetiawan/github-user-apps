package com.example.githubuser.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.githubuser.BuildConfig;
import com.example.githubuser.R;
import com.example.githubuser.adapter.SectionsPagerAdapter;
import com.example.githubuser.database.FavoriteUserHelper;
import com.example.githubuser.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
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
    private String name, username = "aaaa", avatar, location;

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

        //ini tempat set detail dari API
        setUserDetail();

        favoriteUserHelper = FavoriteUserHelper.getInstance(getApplicationContext());
        favoriteUserHelper.open();

        //Saya coba Log disini nilai usernamenya null mengikuti varibale global,
        //padahal maksud saya di get API dulu kemudian ambil nilai usernamenya
        Log.d(DetailUserActivity.class.getSimpleName(),"Isi Username: " + username);

        //nilai statusFavorite disini saya jadikan global karena tidak bisa dipasang disini
//        Boolean statusFavorite = false;
        setStatusFavorite(statusFavorite);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                statusFavorite = !statusFavorite;
                //insert code here

                //saya coba kasih seleksi jika usernamenya sudah ada di cursor,
                // tp sepertinya kurang tepat karena ada di dalam onclick,
                //harusnya diluar onclick, tapi ketika diluar onclick....
                // tidak bisa mengambill nilai username, karena null
                Cursor cursor = favoriteUserHelper.queryByUsername(username);
                if (cursor != null){
                    setStatusFavorite(true);
                }
                insertDatabase(name, username, avatar, location);
                setStatusFavorite(statusFavorite);
            }
        });
    }

    private void setUserDetail(){
        String API_KEY = BuildConfig.API_KEY;
        progressBarProfile.setVisibility(View.VISIBLE);
        final User user = getIntent().getParcelableExtra(EXTRA_USERNAME);
        String url = "https://api.github.com/users/"+user.getUsername();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization",API_KEY);
        client.addHeader("User-Agent", "request");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBarProfile.setVisibility(View.INVISIBLE);
                String result = new String(responseBody);
                Log.d("Success", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    name = jsonObject.getString("name");
                    location = jsonObject.getString("location");
                    username = jsonObject.getString("login");
                    avatar = jsonObject.getString("avatar_url");

                    Glide.with(DetailUserActivity.this)
                            .load(avatar)
                            .apply(new RequestOptions().override(125,125))
                            .into(imgAvatarProfile);

                    tvName.setText(name);
                    tvUsernameProfile.setText(username);
                    tvLocation.setText(location);
                }catch (Exception e){
                    Log.d("Failed", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBarProfile.setVisibility(View.INVISIBLE);
                String errorMessage;
                switch (statusCode) {
                    case 401:
                        errorMessage = statusCode + " : Bad Request";
                        break;
                    case 403:
                        errorMessage = statusCode + " : Forbidden";
                        break;
                    case 404:
                        errorMessage = statusCode + " : Not Found";
                        break;
                    default:
                        errorMessage =  statusCode + " : " + error.getMessage();
                        break;
                }
                Toast.makeText(DetailUserActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void insertDatabase(String name, String username, String avatar, String location){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(USERNAME, username);
        contentValues.put(AVATAR_URL, avatar);
        contentValues.put(LOCATION, location);

        favoriteUserHelper.insert(contentValues);

    }

    private void setStatusFavorite(Boolean statusFavorite){
        if (statusFavorite){
            fab.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            fab.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favoriteUserHelper.close();
    }
}