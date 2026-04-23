package com.example.pr.adapers;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.R;
import com.example.pr.model.Item;
import com.example.pr.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private static final String TAG = "ItemsAdapter";
    private List<Item> originalItemsList = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public ItemsAdapter(@NonNull ItemClickListener itemClickListener) {
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
        try {
            // קריאה לפונקציית הקישור שמעדכנת את כל השדות (כולל הדירוג)
            holder.bindItem(item);

            // הגדרת מאזינים ללחיצות
            holder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onClick(item);
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onLongClick(item, holder.getAdapterPosition());
                }
                return true;
            });

        } catch (Exception e) {
            Log.e(TAG, "Error binding item at position: " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return originalItemsList.size();
    }

    public void setItem(List<Item> items) {
        this.originalItemsList.clear();
        this.originalItemsList.addAll(items);
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onClick(Item item);
        void onLongClick(Item item, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItem;
        private TextView tvName, tvPrice, tvQuantity;
        private RatingBar ratingBar;
        private String itemId;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void bindItem(final Item item) {
            itemId = item.getId();

            // הצגת שם ומחיר
            tvName.setText(item.getpName());
            tvPrice.setText("Price: " + item.getPrice() + "$");

            // טיפול בכמות (מוסתר כברירת מחדל לפי הקוד הקודם שלך)
            tvQuantity.setVisibility(View.INVISIBLE);

            // הגדרת הדירוג ב-RatingBar
            if (ratingBar != null) {
                // המרה ל-float כי זה מה ש-setRating מקבל
                ratingBar.setRating((float) item.getRate());
            }

            // המרה והצגת תמונה
            if (item.getImage() != null && !item.getImage().isEmpty()) {
                Bitmap bitmap = ImageUtil.convertFrom64base(item.getImage());
                if (bitmap != null) {
                    ivItem.setImageBitmap(bitmap);
                }
            }
        }
    }
}
