package com.example.pr.adapers;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.R;
import com.example.pr.model.Item;
import com.example.pr.util.ImageUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    public interface ItemClickListener {
        void onClick(Item item);
        void onLongClick(Item item, int position);
    }

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
            Bitmap bitmap = ImageUtil.convertFrom64base(item.getImage());
            if (bitmap != null) {
                holder.ivItem.setImageBitmap(bitmap);
            }
            holder.tvName.setText(item.getpName());
            holder.tvPrice.setText(String.format("%.2f", item.getPrice()) + "$");
            holder.tvQuantity.setVisibility(View.INVISIBLE);
            holder.btnPlus.setVisibility(View.INVISIBLE);
            holder.btnMinus.setVisibility(View.INVISIBLE);

            holder.ratingBar.setRating((float) item.getRate());

            holder.itemId = item.getId();
            holder.bindItem(item);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(item);
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                itemClickListener.onLongClick(item, holder.getAdapterPosition());
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "position :" + holder.getAdapterPosition());
            Log.e(TAG, "item :" + item);
            Log.e(TAG, "item id :" + item.getId());
            throw e;
        }
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
        private TextView tvName, tvPrice, tvQuantity;
        private RatingBar ratingBar;   // ← הוסף
        private String itemId;
        private MaterialButton btnPlus, btnMinus;


        public ItemViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ratingBar = itemView.findViewById(R.id.ratingBar);  // ← הוסף
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }

        public void bindItem(final Item item) {
            ivItem.setImageBitmap(ImageUtil.convertFrom64base(item.getImage()));
            tvName.setText(item.getpName());
            tvPrice.setText(item.getPrice() + "$");
            ratingBar.setRating((float) item.getRate());  // ← הוסף
            itemId = item.getId();
        }
    }
}
