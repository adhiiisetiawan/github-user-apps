package com.example.githubuser.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String name;
    private String username;
    private String avatarUrl;
    private String typeUser;
    private String location;

    protected User(Parcel in) {
        username = in.readString();
        avatarUrl = in.readString();
        typeUser = in.readString();
    }

    public User() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(avatarUrl);
        dest.writeString(typeUser);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(String name, String username, String avatarUrl, String typeUser, String location) {
        this.name = name;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.typeUser = typeUser;
        this.location = location;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String usernam) {
        this.username = usernam;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
