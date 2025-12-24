package com.example.pr.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pr.R;
import com.example.pr.model.Item;
import com.example.pr.util.ImageUtil;

import java.util.List;


/// Adapter for the items recycler view
/// @see RecyclerView
/// @see Item
public class ItemsAdapter2 extends RecyclerView.Adapter<ItemsAdapter2.ViewHolder> {

    /// list of items
    /// @see Item
    private final List<Item> itemList;

    public ItemsAdapter2(List<Item> itemList) {
        this.itemList = itemList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item, parent, false);
        return new ViewHolder(view);
    }

    /// bind the view holder with the data
    /// @param holder the view holder
    /// @param position the position of the item in the list
    /// @see ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        if (item == null) return;

        holder.tvName.setText(item.getpName());

        holder.tvPrice.setText(item.getPrice()+"");


        holder.tvRate.setText(item.getRate()+"");


        holder.ivItem.setImageBitmap(ImageUtil.convertFrom64base(item.getImage()));
    }

    /// get the number of items in the list
    /// @return the number of items in the list
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /// View holder for the items adapter
    /// @see RecyclerView.ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName,tvPrice,tvRate;
        public final ImageView ivItem;

        public ViewHolder(View itemView) {
            super(itemView);



            tvName = itemView.findViewById(R.id.tvItemName);
       ;
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRate = itemView.findViewById(R.id.tvRate);
            ivItem = itemView.findViewById(R.id.ivItem);


        }
    }
}