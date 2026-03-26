package com.example.pr.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pr.R;
import com.example.pr.model.Order;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    Context context;
    List<Order> orders;


    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderrow, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderIdValue.setText(  order.getOrderId());
        holder.tvTotalPriceValue.setText(String.format(Locale.getDefault(),  order.getTotalPrice()+""));
        holder.tvTimestampValue.setText( order.getFormattedDate());

        holder.tvUserValue.setText(order.getUser().getfName()+" " + order.getUser().getlName());



        holder.rcOrderItems.setLayoutManager(new LinearLayoutManager(context));

        holder.tvPhone.setText(order.getUser().getPhone());

        // אתחול סל ריק בתחילה כדי למנוע NullPointer


       //ItemOrderAdapter  itemOrderAdapter = new ItemOrderAdapter(context, order.getItems());
      //  holder.rcOrderItems.setAdapter(itemOrderAdapter);




        // holder.bind(order);


    }


    @Override
    public int getItemCount() {
        return orders.size();
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

            rcOrderItems = itemView.findViewById(R.id.rcOtrdrItem);


        }
    }

    public void setOrders(List<Order> filteredOrders) {
        this.orders = filteredOrders;
    }


}
