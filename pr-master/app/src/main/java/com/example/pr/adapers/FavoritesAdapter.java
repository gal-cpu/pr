package com.example.pr.adapers;

import android.content.Context;
import android.graphics.Bitmap;
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

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private Context context;
    private List<Item> favoriteItems;
    private FavoritesClickListener listener;

    // ממשק לטיפול בלחיצות על פריטים במועדפים
    public interface FavoritesClickListener {
        void onClick(Item item);
        void onLongClick(Item item, int position);
    }

    public FavoritesAdapter(Context context, List<Item> favoriteItems, FavoritesClickListener listener) {
        this.context = context;
        this.favoriteItems = favoriteItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // משתמש באותו Layout של פריט בודד
        View view = LayoutInflater.from(context).inflate(R.layout.one_item, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        Item item = favoriteItems.get(position);

        holder.tvName.setText(item.getpName());

        // הצגת מחיר הפריט
        holder.tvPrice.setText(String.format("%.2f", item.getPrice()) + "$");

        // הצגת דירוג
        holder.tvRate.setText(String.format("%.1f", item.getRate()) + "⭐");

        // במועדפים בדרך כלל לא מציגים כמות, אז נסתיר את ה-TextView של הכמות
        holder.tvQuantity.setVisibility(View.GONE);

        // טעינת תמונה
        holder.ivItem.setImageBitmap(null);
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            Bitmap bitmap = ImageUtil.convertFrom64base(item.getImage());
            if (bitmap != null) {
                holder.ivItem.setImageBitmap(bitmap);
            }
        }

        // לחיצה רגילה
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });

        // לחיצה ארוכה (למשל לצורך הסרה מהמועדפים)
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(item, holder.getAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return favoriteItems != null ? favoriteItems.size() : 0;
    }

    // עדכון הרשימה מחדש
    public void setFavoriteItems(List<Item> newFavoriteItems) {
        this.favoriteItems = newFavoriteItems;
        notifyDataSetChanged();
    }

    public class FavoritesViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItem;
        TextView tvName, tvPrice, tvRate, tvQuantity;

        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRate = itemView.findViewById(R.id.tvRate);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
