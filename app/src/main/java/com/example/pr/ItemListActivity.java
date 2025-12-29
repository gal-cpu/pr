//package com.example.pr;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageButton;
//import android.widget.SearchView;
//import android.widget.TextView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//
//import com.example.pr.adapers.ItemsAdapter;
//import com.example.pr.adapers.ItemsAdapter2;
//import com.example.pr.model.Cart;
//import com.example.pr.model.Item;
//import com.example.pr.services.DatabaseService;
//import com.google.firebase.database.DatabaseReference;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ItemListActivity extends AppCompatActivity {
//
//
//    private static final String TAG = "ItemsActivity";
//
//    private RecyclerView recyclerView;
//    private ItemsAdapter2 itemsAdapter2;
//
//    private ArrayList<Item> allItems = new ArrayList<>();
//
//    DatabaseService databaseService;
//
//
//
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_item_list);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//
//        databaseService = DatabaseService.getInstance();
//
//        recyclerView = findViewById(R.id.rcItemes);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//
//
//        itemsAdapter2 = new ItemsAdapter2(allItems);
//
//        recyclerView.setAdapter(itemsAdapter2);
//
//        fetchItemsFromFirebase();
//
//    }
//
//    private void fetchItemsFromFirebase() {
//
//        // טעינת המוצרים
//        databaseService.getItemList(new DatabaseService.DatabaseCallback<List<Item>>() {
//            @Override
//            public void onCompleted(List<Item> items) {
//               // Log.d(TAG, "onCompleted: " + items);
//                allItems.clear();
//                allItems.addAll(items);
//            //    filterItemsByCategory();
//                //קריאה לסינון המוצרים אחרי טעינת המוצרים מחדש
//
//                Log.d(TAG, "onCompleted: " + allItems);
//
//
//               itemsAdapter2.notifyDataSetChanged();
//
//                // עדכון התצוגה על פי חיפוש
//               // String query = ((SearchView) findViewById(R.id.searchView)).getQuery().toString();
//               // itemsAdapter.filter(query);
//            }
//
//            @Override
//            public void onFailed(Exception e) {
//                Log.e(TAG, "Failed to load items: ", e);
//                new android.app.AlertDialog.Builder(ItemListActivity.this)
//                        .setMessage("נראה שקרתה תקלה בטעינת המוצרים, נסה שוב מאוחר יותר")
//                        .setPositiveButton("אוקי", null)
//                        .show();
//            }
//        });
//    }
//
////    private void filterItemsByCategory() {
////        filteredItems.clear();  // מחיקת המוצרים הקודמים ברשימה
////        if (selectedCategory != null && !selectedCategory.isEmpty()) {
////            if (selectedCategory.equals("כל המוצרים")) {
//  //               אם בחרנו בקטגוריה "כל המוצרים", נציג את כל המוצרים
////                filteredItems.addAll(allItems);
////            } else {
//  //               אם נבחרה קטגוריה מסוימת, נבצע סינון
////                for (Item item : allItems) {
////                    if (item.getType().equalsIgnoreCase(selectedCategory)) {
////                        filteredItems.add(item);
////                    }
////                }
////            }
////        } else {
////             אם לא נבחרה קטגוריה, נציג את כל המוצרים
////            filteredItems.addAll(allItems);
////        }
////        itemsAdapter.notifyDataSetChanged();
////    }
//
//
//
////    public void addItemToCart(Item item) {
////        if (!SharedPreferencesUtil.isAdmin(ShopActivity.this)) {
////            this.cart.addItem(item);
////
////            new android.app.AlertDialog.Builder(ShopActivity.this)
////                    .setMessage("המוצר נוסף לעגלה בהצלחה!")
////                    .setPositiveButton("אוקי", null)
////                    .show();
////
////            databaseService.updateCart(this.cart, AuthenticationService.getInstance().getCurrentUserId(), new DatabaseService.DatabaseCallback<Void>() {
////                @Override
////                public void onCompleted(Void object) {
////                    updateTotalPrice();
////                    updateCartItemCount(); // עדכון מספר המוצרים בעגלה אחרי הוספת מוצר
////                }
////
////                @Override
////                public void onFailed(Exception e) {
////                    Log.e(TAG, "Failed to update cart: ", e);
////                    new android.app.AlertDialog.Builder(ShopActivity.this)
////                            .setMessage("נראה שקרתה תקלה בהוספת המוצר לעגלה, נסה שוב")
////                            .setPositiveButton("אוקי", null)
////                            .show();
////                }
////            });
////        }
////    }
////
////    private void updateTotalPrice() {
////        if (SharedPreferencesUtil.isAdmin(ShopActivity.this)) {
////            totalPriceText.setVisibility(View.GONE);
////        } else {
////            השתמשנו ב-AtomicReference לאחסון המחיר הכולל בצורה בטוחה בתוך קריאה אסינכרונית
////            final AtomicReference<Double> totalPriceRef = new AtomicReference<>(0.0);
////
//    //         קריאה למבצעי הנחה מהפיירבייס
////            databaseService.getAllDeals(new DatabaseService.DatabaseCallback<List<Deal>>() {
////                @Override
////                public void onCompleted(List<Deal> deals) {
//    //  חישוב המחיר הכולל
////                    for (Item item : cart.getItems()) {
////                        double itemPrice = item.getPrice();
////                        double finalPrice = itemPrice;
////
////                         חיפוש אחר מבצע תקף לכל פריט
////                        for (Deal deal : deals) {
////                            if (deal.isValid() && deal.getItemType().equals(item.getType())) {
////                                double discount = deal.getDiscountPercentage();
////                                finalPrice = itemPrice * (1 - discount / 100);
////                                break; // מצאנו הנחה עבור הפריט, נצא מהלולאה
////                            }
////                        }
////
////                         עדכון המחיר הכולל
////                        totalPriceRef.set(totalPriceRef.get() + finalPrice);
////                    }
////
////                     עדכון התצוגה של המחיר הכולל
////                    totalPriceText.setText("סך הכל: ₪" + totalPriceRef.get());
////                    totalPriceText.setVisibility(View.VISIBLE);
////                }
////
////                @Override
////                public void onFailed(Exception e) {
////                     טיפול בשגיאה אם קרתה
////                }
////            });
////        }
////    }
////
////
//
//}