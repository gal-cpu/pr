package com.example.pr.adapers;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pr.R;
import com.example.pr.model.Item;
import com.example.pr.services.DatabaseService;
import com.example.pr.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private static final String TAG = "ItemsAdapter";
    private List<Item> originalItemsList;

    private Context context;
    private DatabaseService databaseService;

    public interface ItemClickListener {
        void onClick(Item item);
    }

   // @Nullable
   // private final ItemClickListener itemClickListener;

   // public ItemsAdapter(List<Item> itemsList, Context context, @Nullable ItemClickListener itemClickListener) {
     //   this.originalItemsList = itemsList;
    //    this.filteredItemsList = new ArrayList<>(itemsList);
     //   this.context = context;
     //   this.itemClickListener = itemClickListener;
      //  this.databaseService = DatabaseService.getInstance();
  //  }


    public ItemsAdapter(List<Item> itemsList, Context context) {
        this.originalItemsList = itemsList;
        this.context = context;
      //  this.itemClickListener = itemClickListener;
        this.databaseService = DatabaseService.getInstance();
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.one_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = originalItemsList.get(position);

       holder.ivItem.setImageBitmap(ImageUtil.convertFrom64base(item.getImage()));
        holder.tvName.setText(item.getpName());
        holder.tvRate.setText(item.getRate()+"");
        holder.tvPrice.setText(item.getPrice()+"");


        holder.itemId = item.getId();


        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return originalItemsList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItem;
        private TextView tvName, tvPrice, tvRate;
      //  private RatingBar previewRatingBar;
      //  private ImageButton addToCartButton;
        private String itemId;
        private TextView dealtag;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);

            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRate = itemView.findViewById(R.id.tvRate);
            ivItem = itemView.findViewById(R.id.ivItem);
           // addToCartButton = itemView.findViewById(R.id.addToCartButton);


            itemView.setOnClickListener(v -> {
                Item item = originalItemsList.get(getAdapterPosition());
               // Intent intent = new Intent(context, ItemDetailActivity.class);
               // intent.putExtra("itemId", item.getId());
                //context.startActivity(intent);
            });

            // הצגת כפתור הוספה לעגלה רק אם לא מדובר במנהל
//            if (SharedPreferencesUtil.isAdmin(context)) {
//                addToCartButton.setVisibility(View.GONE);
//            } else {
//                addToCartButton.setVisibility(View.VISIBLE);
//            }
        }

        public void bindItem(final Item item) {
           ivItem.setImageBitmap(ImageUtil.convertFrom64base(item.getImage()));
            tvName.setText(item.getpName());
            tvRate.setText(item.getRate()+"");
            tvPrice.setText(item.getPrice()+"");




            itemId = item.getId();
          //  updateAverageRating(previewRatingBar, itemId);

          //  addToCartButton.setOnClickListener(v -> {
         //       if (itemClickListener != null)
         //           itemClickListener.onClick(item);
         //   });
        }



    }
/*
    public void filter(String query) {
        filteredItemsList.clear();
        if (query.isEmpty()) {
            filteredItemsList.addAll(originalItemsList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            try {
                double queryPrice = Double.parseDouble(query);
                for (Item item : originalItemsList) {
                    if (item.getPrice() == queryPrice) {
                        filteredItemsList.add(item);
                    }
                }
            } catch (NumberFormatException e) {
                for (Item item : originalItemsList) {
                    if (item.getName().toLowerCase().contains(lowerCaseQuery) ||
                            item.getCompany().toLowerCase().contains(lowerCaseQuery) ||
                            item.getType().toLowerCase().contains(lowerCaseQuery) ||
                            item.getColor().toLowerCase().contains(lowerCaseQuery)) {
                        filteredItemsList.add(item);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


 */
}
