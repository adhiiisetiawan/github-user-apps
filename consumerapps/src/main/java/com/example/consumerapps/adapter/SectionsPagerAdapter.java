package com.example.consumerapps.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.consumerapps.R;
import com.example.consumerapps.follow.FollowersFragment;
import com.example.consumerapps.follow.FollowingFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final Context mContext;
    public static String username;

    public SectionsPagerAdapter(Context context, FragmentManager fragmentManager){
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new FollowersFragment().newInstance(username);
                break;
            case 1:
                fragment = new FollowingFragment().newInstance(username);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @StringRes
    private final int[] TAB_TITLES = new int[]{
        R.string.tab_followers,
        R.string.tab_following
    };

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }
}
