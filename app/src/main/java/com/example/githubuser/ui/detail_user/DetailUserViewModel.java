package com.example.githubuser.ui.detail_user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.githubuser.BuildConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class DetailUserViewModel extends ViewModel {
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> usernames = new MutableLiveData<>();
    private MutableLiveData<String> location = new MutableLiveData<>();
    private MutableLiveData<String> avatar = new MutableLiveData<>();

    void setDetailUserViewModel(String username){
        String API_KEY = BuildConfig.API_KEY;
        String url = "https://api.github.com/users/" + username;
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization",API_KEY);
        client.addHeader("User-Agent", "request");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.d("Success", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    name.postValue(jsonObject.getString("name"));
                    location.postValue(jsonObject.getString("location"));
                    usernames.postValue(jsonObject.getString("login"));
                    avatar.postValue(jsonObject.getString("avatar_url"));
                }catch (Exception e){
                    Log.d("Failed", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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
                Log.d("onFailure", errorMessage);
            }
        });

    }

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getUsernames() {
        return usernames;
    }

    public LiveData<String> getLocation() {
        return location;
    }

    public LiveData<String> getAvatar() {
        return avatar;
    }
}
