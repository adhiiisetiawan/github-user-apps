package com.example.consumerapps.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.consumerapps.R;
import com.example.consumerapps.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> mUser = new ArrayList<>();
    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public void setUser(ArrayList<User> items) {
        mUser.clear();
        mUser.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.UserViewHolder holder, int position) {
        User user = mUser.get(position);

        Glide.with(holder.itemView.getContext())
                .load(user.getAvatarUrl())
                .apply(new RequestOptions().override(55, 55))
                .into(holder.avatarUser);

        holder.tvUsername.setText(user.getUsername());
        holder.tvTypeUser.setText(user.getTypeUser());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemCliked(mUser.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatarUser;
        TextView tvUsername;
        TextView tvTypeUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            avatarUser = itemView.findViewById(R.id.img_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvTypeUser = itemView.findViewById(R.id.tv_type_user);
        }
    }

    public interface OnItemClickCallback{
        void onItemCliked(User data);
    }
}
