package com.example.consumerapps.follow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.consumerapps.BuildConfig;
import com.example.consumerapps.R;
import com.example.consumerapps.adapter.UserAdapter;
import com.example.consumerapps.detail_user.DetailUserActivity;
import com.example.consumerapps.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowersFragment extends Fragment {
    private static final String ARG_USERNAME = "username";
    private UserAdapter userAdapter;
    private ProgressBar progressBarFollowers;


    public FollowersFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FollowersFragment newInstance(String username) {
        FollowersFragment fragment = new FollowersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBarFollowers = view.findViewById(R.id.progressbar_followers);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_followers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userAdapter = new UserAdapter();
        userAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(userAdapter);

        getDataFollowers();

        userAdapter.setOnItemClickCallback(new UserAdapter.OnItemClickCallback() {
            @Override
            public void onItemCliked(User data) {
                Intent intent = new Intent(getContext(), DetailUserActivity.class);
                intent.putExtra(DetailUserActivity.EXTRA_USERNAME, data);
                startActivity(intent);
            }
        });
    }

    public void getDataFollowers(){
        String API_KEY = BuildConfig.API_KEY;
        progressBarFollowers.setVisibility(View.VISIBLE);
        final ArrayList<User> userArrayList = new ArrayList<>();
        String username = getArguments().getString(ARG_USERNAME);

        String url = "https://api.github.com/users/"+username+"/followers";
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization",API_KEY);
        client.addHeader("User-Agent", "request");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBarFollowers.setVisibility(View.INVISIBLE);
                String result = new String(responseBody);
                Log.d("Success", result);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject list = jsonArray.getJSONObject(i);
                        User mUser = new User();
                        mUser.setUsername(list.getString("login"));
                        mUser.setTypeUser(list.getString("type"));
                        mUser.setAvatarUrl(list.getString("avatar_url"));
                        userArrayList.add(mUser);
                    }
                    userAdapter.setUser(userArrayList);
                }catch (Exception e){
                    Log.d("Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBarFollowers.setVisibility(View.INVISIBLE);
                Log.d("onFailure", error.getMessage());
            }
        });
    }
}