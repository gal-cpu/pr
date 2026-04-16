package com.example.pr.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.R;
import com.example.pr.model.ItemCart;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ItemViewHolder> {

    private List<ItemCart> items;

    public OrderItemAdapter(List<ItemCart> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_order_product_light, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemCart cartItem = items.get(position);

        // שם המוצר
        holder.tvItemName.setText(cartItem.getItem().getpName());

        // כמות
        holder.tvItemAmount.setText("x" + cartItem.getAmount());

        // מחיר שורה = מחיר יחידה × כמות
        double lineTotal = cartItem.getItem().getPrice() * cartItem.getAmount();
        holder.tvItemPrice.setText(String.format("₪%.2f", lineTotal));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // מאפשר עדכון רשימה מבחוץ
    public void setItems(List<ItemCart> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    // ─── ViewHolder ───────────────────────────────────────────────
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemName;
        TextView tvItemAmount;
        TextView tvItemPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName   = itemView.findViewById(R.id.tvItemName);
            tvItemAmount = itemView.findViewById(R.id.tvItemAmount);
            tvItemPrice  = itemView.findViewById(R.id.tvItemPrice);
        }
    }
}