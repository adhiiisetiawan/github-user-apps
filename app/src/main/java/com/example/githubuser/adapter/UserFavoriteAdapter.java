package com.example.githubuser.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.githubuser.R;
import com.example.githubuser.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFavoriteAdapter extends RecyclerView.Adapter<UserFavoriteAdapter.UserFavoriteViewHolder> {
    private ArrayList<User> listUser = new ArrayList<>();
    private Activity activity;
    private onItemFavoriteClickCallback onItemFavoriteClickCallback;

    public void setOnItemFavoriteClickCallback(onItemFavoriteClickCallback onItemFavoriteClickCallback){
        this.onItemFavoriteClickCallback = onItemFavoriteClickCallback;
    }

    public UserFavoriteAdapter(Activity activity){
        this.activity = activity;
    }

    public ArrayList<User> getListUser(){
        return listUser;
    }

    public void setListUser(ArrayList<User> listUser){
        if (listUser.size() > 0){
            this.listUser.clear();
        }
        this.listUser.addAll(listUser);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserFavoriteAdapter.UserFavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserFavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserFavoriteAdapter.UserFavoriteViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(listUser.get(position).getAvatarUrl())
                .apply(new RequestOptions().override(55, 55))
                .into(holder.imgCircleAvatar);

        holder.tvName.setText(listUser.get(position).getName());
        holder.tvUsername.setText(listUser.get(position).getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemFavoriteClickCallback.onItemClicked(listUser.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class UserFavoriteViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgCircleAvatar;
        TextView tvName, tvUsername;
        public UserFavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCircleAvatar = itemView.findViewById(R.id.img_avatar);
            tvName = itemView.findViewById(R.id.tv_username);
            tvUsername = itemView.findViewById(R.id.tv_type_user);
        }
    }

    public interface onItemFavoriteClickCallback{
        void onItemClicked(User data);
    }
}
