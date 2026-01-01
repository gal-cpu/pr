package com.example.pr.adapers;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pr.R;
import com.example.pr.model.Item;
import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;
import com.example.pr.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private List<Item> originalItemsList;
    public interface ItemClickListener {
        void onClick(Item item);
    }

    private ItemClickListener itemClickListener;

    public ItemsAdapter(@NonNull ItemClickListener itemClickListener) {
        this.originalItemsList = new ArrayList<>();
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = originalItemsList.get(position);

       holder.ivItem.setImageBitmap(ImageUtil.convertFrom64base(item.getImage()));
        holder.tvName.setText(item.getpName());
        holder.tvRate.setText(item.getRate()+"");
        holder.tvPrice.setText(item.getPrice()+"");


        holder.itemId = item.getId();
        holder.bindItem(item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return originalItemsList.size();
    }

    public void setItem(List<Item> filteredUsers) {
        this.originalItemsList.clear();
        this.originalItemsList.addAll(filteredUsers);
        notifyDataSetChanged();
    }
    public List<Item> getItemList() {
        return originalItemsList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItem;
        private TextView tvName, tvPrice, tvRate;
        private String itemId;
        private TextView dealtag;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRate = itemView.findViewById(R.id.tvRate);
            ivItem = itemView.findViewById(R.id.ivItem);
        }
        public void bindItem(final Item item) {
           ivItem.setImageBitmap(ImageUtil.convertFrom64base(item.getImage()));
            tvName.setText(item.getpName());
            tvRate.setText("Rate: "+item.getRate()+"");
            tvPrice.setText("Price: "+item.getPrice()+"$");
            itemId = item.getId();
        }
    }
}
