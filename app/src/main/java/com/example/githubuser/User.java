package com.example.githubuser;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String username;
    private String avatarUrl;
    private String typeUser;

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
}
