package com.example.pr.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.R;
import com.example.pr.model.Order;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orderList;

    private OrderClickListener listener;

    // הגדרת הממשק ללחיצות
    public interface OrderClickListener {
        void onOrderClick(Order order);

        // מימוש ה-Interface של האדפטר (לחיצה רגילה)


        // מימוש ה-Interface של האדפטר (לחיצה ארוכה)
        void onOrderLongClick(Order order);
    }

    // ה-Constructor המעודכן שמקבל Context ו-Listener
    public OrdersAdapter( OrderClickListener listener) {
        this.listener = listener;
    }


    public OrdersAdapter( List<Order> orderList,OrderClickListener listener) {
        this.listener = listener;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_order_row_cart, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Order ID
        holder.tvOrderId.setText(order.getOrderId());

        // Buyer details
        if (order.getUser() != null) {
            holder.tvUserName.setText(order.getUser().getfName() + " " +order.getUser().getlName());
            holder.tvBuyerPhone.setText(order.getUser().getPhone());
        }

        // Date
        holder.tvTimestamp.setText(order.getFormattedDate());

        // Total price
        holder.tvTotalPrice.setText(String.format("₪%.2f", order.getTotalPrice()));

        // Status badge
        holder.tvStatus.setText(order.getStatus());

        // הגדרת לחיצה על כל השורה
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onOrderLongClick(order);
            }
            return false;
        });


        // Items RecyclerView
        if (order.getItems() != null) {
            com.example.pr.adapers.OrderItemAdapter itemAdapter = new com.example.pr.adapers.OrderItemAdapter(order.getItems());
            holder.rcOrderItem.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.rcOrderItem.setAdapter(itemAdapter);
            holder.rcOrderItem.setNestedScrollingEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void setOrderList(List<Order> newOrders) {
        this.orderList = newOrders;

    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId;
        TextView tvUserName;
        TextView tvBuyerPhone;
        TextView tvTimestamp;
        TextView tvTotalPrice;
        TextView tvPaymentMethod;
        TextView tvStatus;
        RecyclerView rcOrderItem;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId       = itemView.findViewById(R.id.tvOrderIdValueO);
            tvUserName      = itemView.findViewById(R.id.tvUserValueO);
            tvBuyerPhone    = itemView.findViewById(R.id.tvBuyerPhone);
            tvTimestamp     = itemView.findViewById(R.id.tvTimestampValueO);
            tvTotalPrice    = itemView.findViewById(R.id.tvTotalPriceValueO);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvStatus        = itemView.findViewById(R.id.tvStatusBadge);
            rcOrderItem     = itemView.findViewById(R.id.rcOrderItem);
        }
    }
}
