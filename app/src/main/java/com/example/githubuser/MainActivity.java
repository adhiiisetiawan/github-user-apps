package com.example.githubuser;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private UserAdapter userAdapter;
    private ProgressBar progressBar;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressbar_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter();
        userAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(userAdapter);

        mainViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
        mainViewModel.setUserViewModel();
        mainViewModel.getUserViewModel().observe(this, new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                if (users != null){
                    userAdapter.setUser(users);
                    showLoading(false);
                }
            }
        });

        userAdapter.setOnItemClickCallback(new UserAdapter.OnItemClickCallback() {
            @Override
            public void onItemCliked(User data) {
                Intent intent = new Intent(MainActivity.this, DetailUserActivity.class);
                intent.putExtra(DetailUserActivity.EXTRA_USERNAME, data);
                startActivity(intent);
            }
        });
    }

    private void showLoading(Boolean state){
        if (state){
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchManager != null){
            SearchView searchView = (SearchView) (menu.findItem(R.id.search_view)).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.search_hint));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    mainViewModel = new ViewModelProvider(MainActivity.this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
                    mainViewModel.setSearchUserViewModel(s);
                    mainViewModel.getSearchUserViewModel().observe(MainActivity.this, new Observer<ArrayList<User>>() {
                        @Override
                        public void onChanged(ArrayList<User> users) {
                            if (users != null){
                                userAdapter.setUser(users);
                                showLoading(false);
                            }
                        }
                    });
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    mainViewModel = new ViewModelProvider(MainActivity.this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
                    mainViewModel.setSearchUserViewModel(s);
                    mainViewModel.getSearchUserViewModel().observe(MainActivity.this, new Observer<ArrayList<User>>() {
                        @Override
                        public void onChanged(ArrayList<User> users) {
                            if (users != null){
                                userAdapter.setUser(users);
                                showLoading(false);
                            }
                        }
                    });
                    return true;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings){
            Intent intentSettings = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(intentSettings);
        }
        return super.onOptionsItemSelected(item);
    }
}