package com.example.pr.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pr.R;
import com.example.pr.model.User;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;


/// Adapter for the items recycler view
///
/// @see RecyclerView
/// @see User
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    /// list of users
    ///
    /// @see User
    ///
    public interface OnUserClickListener {
        void onUserClick(User user);

        void onLongUserClick(User user);
    }

    private final List<User> userList;
    private final OnUserClickListener onUserClickListener;

    public UsersAdapter(@Nullable final OnUserClickListener onUserClickListener) {
        userList = new ArrayList<>();
        this.onUserClickListener = onUserClickListener;
    }

    /// create a view holder for the adapter
    ///
    /// @param parent   the parent view group
    /// @param viewType the type of the view
    /// @return the view holder
    /// @see ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /// inflate the item_selected_item layout
        /// @see R.layout.item_selected_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_one_user, parent, false);
        return new ViewHolder(view);
    }


    /// bind the view holder with the data
    ///
    /// @param holder   the view holder
    /// @param position the position of the item in the list
    /// @see ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        User user = userList.get(position);
        if (user == null) return;

        holder.tvFname.setText("Name: " + user.getfName());

        holder.tvLname.setText("Last Name: " + user.getlName());

        holder.tvEmail.setText("Email: " + user.getEmail());

        holder.tvPhone.setText("Phone: " + user.getPhone() + "");

        holder.tvPassword.setText("Password: " + user.getPassword());
        if (user.gatIsAd()) {
            holder.tvIsAdmin.setText("Admin");
            holder.ivUser.setImageResource(R.drawable.icon_admin_table);
        } else {
            holder.tvIsAdmin.setText("User");
            holder.ivUser.setImageResource(R.drawable.icon_user_table);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onLongUserClick(user);
            }
            return true;
        });

        //holder.bind(user, Listener);
    }

    /// get the number of items in the list
    ///
    /// @return the number of items in the list
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUsers(List<User> filteredUsers) {
        this.userList.clear();
        this.userList.addAll(filteredUsers);
        notifyDataSetChanged();
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> users) {
        userList.clear();
        userList.addAll(users);
        notifyDataSetChanged();
    }

    public void addUser(User user) {
        userList.add(user);
        notifyItemInserted(userList.size() - 1);
    }

    public void updateUser(User user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.set(index, user);
        notifyItemChanged(index);
    }

    public void removeUser(User user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.remove(index);
        notifyItemRemoved(index);
    }

    /// View holder for the items adapter
    ///
    /// @see RecyclerView.ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvFname, tvLname, tvEmail, tvPhone, tvPassword, tvIsAdmin;
        public final ImageView ivUser;

        public ViewHolder(View itemView) {
            super(itemView);

            tvFname = itemView.findViewById(R.id.tvFname);
            tvLname = itemView.findViewById(R.id.tvLname);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvPassword = itemView.findViewById(R.id.tvPassword);
            ivUser = itemView.findViewById(R.id.ivUser);
            tvIsAdmin = itemView.findViewById(R.id.tvISAdmin);
        }
    }
}