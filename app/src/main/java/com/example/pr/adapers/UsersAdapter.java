package com.example.pr.adapers;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pr.OneUser;
import com.example.pr.R;
import com.example.pr.TableUsers;
import com.example.pr.UpdateUser;
import com.example.pr.model.Item;
import com.example.pr.model.User;
import com.example.pr.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;


/// Adapter for the items recycler view
/// @see RecyclerView
/// @see User
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    /// list of users
    /// @see User
    private final List<User> userList;

    public UsersAdapter() {
        this.userList = new ArrayList<>();
    }

    /// create a view holder for the adapter
    /// @param parent the parent view group
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
    /// @param holder the view holder
    /// @param position the position of the item in the list
    /// @see ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;

        holder.tvFname.setText("Name: "+user.getfName());

        holder.tvLname.setText("Last Name: "+user.getlName());

        holder.tvEmail.setText("Email: "+user.getEmail());

        holder.tvPhone.setText("Phone: "+user.getPhone()+"");

        holder.tvPassword.setText("Password: "+user.getPassword());
        if (user.getAdmin()) {
            holder.tvIsAdmin.setText("Admin");
            holder.ivUser.setImageResource(R.drawable.icon_admin_table);
        }
        else {
            holder.tvIsAdmin.setText("User");
        }
    }

    /// get the number of items in the list
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

    /// View holder for the items adapter
    /// @see RecyclerView.ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvFname,tvLname,tvEmail,tvPhone,tvPassword,tvIsAdmin;
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