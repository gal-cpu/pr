package com.example.pr.adapers;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.example.pr.model.ItemCart;
import com.example.pr.util.ImageUtil;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<ItemCart> cartItems;
    private CartClickListener listener;

    public interface CartClickListener {
        void onClick(ItemCart item);
        void onLongClick(ItemCart item, int position);

        void onQuantityChanged(ItemCart item, int position);
    }

    public CartAdapter( List<ItemCart> cartItems, CartClickListener listener) {

        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ItemCart item = cartItems.get(position);

        // הגנה: אם ה-ItemCart עצמו null או שהמוצר (Item) שבתוכו null - אל תנסה להציג
        if (item == null || item.getItem() == null) {
            holder.tvName.setText("מוצר לא זמין");
            holder.tvPrice.setText("");
            holder.tvQuantity.setVisibility(View.GONE);
            holder.ivItem.setImageBitmap(null);
            holder.ratingBar.setRating(0); // ← כאן, לפני ה-return
            return;
        }

        // מעכשיו בטוח להשתמש ב-item.getItem()
        holder.tvName.setText(item.getItem().getpName());

        // חישוב מחיר שורה
        holder.tvPrice.setText(String.format("%.2f", item.getItem().getPrice()) + "$");

        holder.ratingBar.setRating((float) item.getItem().getRate());

        // הצגת כמות
        holder.tvQuantity.setText("" + item.getAmount());
        holder.tvQuantity.setVisibility(View.VISIBLE);

        holder.btnPlus.setOnClickListener(v -> {
            item.setAmount(item.getAmount() + 1);
            holder.tvQuantity.setText("" + item.getAmount());
            if (listener != null) listener.onQuantityChanged(item, position);
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
                holder.tvQuantity.setText("" + item.getAmount());
                if (listener != null) listener.onQuantityChanged(item, position);
            }
            // אם רוצים להסיר פריט כשכמות מגיעה ל-0:
            // else { cartItems.remove(position); notifyItemRemoved(position); }
        });

        // טעינת תמונה
        holder.ivItem.setImageBitmap(null);
        if (item.getItem().getImage() != null && !item.getItem().getImage().isEmpty()) {
            Bitmap bitmap = ImageUtil.convertFrom64base(item.getItem().getImage());
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
    public void setItem(List<ItemCart> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItem;
        TextView tvName, tvPrice, tvQuantity;
        RatingBar ratingBar;
        MaterialButton btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            //tvRate = itemView.findViewById(R.id.tvRate);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);

        }
    }
}