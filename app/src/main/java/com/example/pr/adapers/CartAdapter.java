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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Item> cartItems;
    private CartClickListener listener;

    public interface CartClickListener {
        void onClick(Item item);
        void onLongClick(Item item, int position);
    }

    public CartAdapter(Context context, List<Item> cartItems, CartClickListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.one_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Item item = cartItems.get(position);

        holder.tvName.setText(item.getpName());

        // חישוב מחיר שורה: מחיר X כמות
        double totalPriceForItem = item.getPrice() * item.getQuantity();
        holder.tvPrice.setText(String.format("%.2f", totalPriceForItem) + "$");

        holder.tvRate.setText(String.format("%.1f", item.getRate()) + "⭐");

        // הצגת כמות
        holder.tvQuantity.setText("x" + item.getQuantity());
        holder.tvQuantity.setVisibility(View.VISIBLE);

        // טעינת תמונה עם ניקוי זיכרון למניעת כפילויות בגלילה
        holder.ivItem.setImageBitmap(null);
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            Bitmap bitmap = ImageUtil.convertFrom64base(item.getImage());
            if (bitmap != null) {
                holder.ivItem.setImageBitmap(bitmap);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(item, holder.getAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    // הפעולה שביקשת להוסיף
    public void setItem(List<Item> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItem;
        TextView tvName, tvPrice, tvRate, tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRate = itemView.findViewById(R.id.tvRate);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
