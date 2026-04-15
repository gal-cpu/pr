package com.example.pr.adapers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.R;
import com.example.pr.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private static final String TAG = "OrderAdapter";
    private Context context;
    private List<Order> orders = new ArrayList<>();
    private OrderClickListener orderClickListener;

    // קונסטרקטור מאוחד שמקבל גם Context וגם Listener
    public OrderAdapter(Context context, @NonNull OrderClickListener orderClickListener) {
        this.context = context;
        this.orderClickListener = orderClickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_order_row_cart, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        try {
            holder.tvOrderIdValue.setText(order.getOrderId());
            holder.tvTotalPriceValue.setText(String.format(Locale.getDefault(), "%.2f", order.getTotalPrice()));
            holder.tvTimestampValue.setText(order.getFormattedDate());

            if (order.getUser() != null) {
                holder.tvUserValue.setText(order.getUser().getfName() + " " + order.getUser().getlName());
                holder.tvPhone.setText(order.getUser().getPhone());
            }

            // הגדרת ה-RecyclerView הפנימי
            holder.rcOrderItems.setLayoutManager(new LinearLayoutManager(context));

            /* אם יש לך אדפטר לפריטים בתוך הזמנה, בטל את ה-Comment:
            ItemOrderAdapter itemOrderAdapter = new ItemOrderAdapter(context, order.getItems());
            holder.rcOrderItems.setAdapter(itemOrderAdapter);
            */

            // הגדרת הלחיצות
            holder.itemView.setOnClickListener(v -> {
                if (orderClickListener != null) {
                    orderClickListener.onClick(order);
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                if (orderClickListener != null) {
                    orderClickListener.onLongClick(order, holder.getAdapterPosition());
                }
                return true;
            });

        } catch (Exception e) {
            Log.e(TAG, "Error at position: " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // פונקציית עדכון הרשימה שתואמת ל-OrderHistory
    public void setOrders(List<Order> filteredOrders) {
        this.orders.clear();
        if (filteredOrders != null) {
            this.orders.addAll(filteredOrders);
        }
        notifyDataSetChanged();
    }

    public List<Order> getOrderList() {
        return orders;
    }

    // ה-Interface שה-Activity מממש
    public interface OrderClickListener {
        void onClick(Order order);
        void onLongClick(Order order, int position);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserValue, tvTimestampValue, tvOrderIdValue, tvTotalPriceValue, tvPhone;
        private RecyclerView rcOrderItems;

        public OrderViewHolder(View itemView) {
            super(itemView);
            tvUserValue = itemView.findViewById(R.id.tvUserValueO);
            tvTimestampValue = itemView.findViewById(R.id.tvTimestampValueO);
            tvOrderIdValue = itemView.findViewById(R.id.tvOrderIdValueO);
            tvTotalPriceValue = itemView.findViewById(R.id.tvTotalPriceValueO);
            tvPhone = itemView.findViewById(R.id.tvBuyerPhone);
            rcOrderItems = itemView.findViewById(R.id.rcOrderItem);
        }
    }
}
