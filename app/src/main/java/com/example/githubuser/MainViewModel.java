package com.example.githubuser;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainViewModel extends ViewModel {
    private MutableLiveData<ArrayList<User>> listUserMutableLiveData = new MutableLiveData<>();

    void setUserViewModel(){
        final ArrayList<User> userArrayList = new ArrayList<>();

        String url = "https://api.github.com/users";
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization","token 6d62adc8cecb3a300ff29b1164bffdad4cc46d01");
        client.addHeader("User-Agent", "request");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
                    listUserMutableLiveData.postValue(userArrayList);
                }catch (Exception e){
                    Log.d("Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("onFailure", error.getMessage());
            }
        });
    }

    LiveData<ArrayList<User>> getUserViewModel(){
        return listUserMutableLiveData;
    }
}
