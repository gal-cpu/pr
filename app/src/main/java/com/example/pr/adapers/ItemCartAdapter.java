package com.example.pr.adapers;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pr.R;
import com.example.pr.model.Item;
import com.example.pr.model.ItemCart;
import com.example.pr.util.ImageUtil;

import java.util.List;

public class ItemCartAdapter extends RecyclerView.Adapter<ItemCartAdapter.ViewHolder> {
    private Context context;
    private List<ItemCart> items;

    public ItemCartAdapter(Context context, List<ItemCart> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_cart_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ItemCart item_cart = items.get(position);
        holder.bind(item_cart);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemCart_image;
        TextView itemCart_name, itemCart_price, itemCart_amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //itemCart_image = itemView.findViewById(R.id.itemCart_imageItemOrder);
            //itemCart_name = itemView.findViewById(R.id.itemCart_nameItemOrder);
            //itemCart_price = itemView.findViewById(R.id.itemCart_priceItemOrder);
            //itemCart_amount = itemView.findViewById(R.id.itemCart_AmountItemOrder);
        }

        public void bind(final ItemCart item_cart) {
            Item item = item_cart.getItem();
            int amount = item_cart.getAmount();

            itemCart_image.setImageBitmap(ImageUtil.convertFrom64base(item.getImage()));
            itemCart_name.setText(item.getpName());
            itemCart_price.setText(item.getPrice() + "₪");
            itemCart_amount.setText(String.valueOf(amount));
        }

    }
}
