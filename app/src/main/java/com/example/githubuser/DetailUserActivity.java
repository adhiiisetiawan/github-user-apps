package com.example.githubuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class DetailUserActivity extends AppCompatActivity {
    public static final String EXTRA_USERNAME = "extra_username";

    private ProgressBar progressBarProfile;

    private TextView tvName;
    private TextView tvUsernameProfile;
    private TextView tvLocation;
    private CircleImageView imgAvatarProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);
        getSupportActionBar().setTitle(R.string.detail_user);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail User");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        setUserDetail();
    }

    private void setUserDetail(){
        progressBarProfile.setVisibility(View.VISIBLE);
        final User user = getIntent().getParcelableExtra(EXTRA_USERNAME);
        String url = "https://api.github.com/users/"+user.getUsername();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization","token 6d62adc8cecb3a300ff29b1164bffdad4cc46d01");
        client.addHeader("User-Agent", "request");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBarProfile.setVisibility(View.INVISIBLE);
                String result = new String(responseBody);
                Log.d("Success", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String name = jsonObject.getString("name");
                    String location = jsonObject.getString("location");
                    String username = jsonObject.getString("login");
                    String avatar = jsonObject.getString("avatar_url");

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
}