package com.example.pr;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.pr.model.Cart;
import com.example.pr.model.FavoriteList;
import com.example.pr.model.Item;
import com.example.pr.model.ItemCart;
import com.example.pr.services.DatabaseService;
import com.example.pr.util.ImageUtil;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

public class Item_page extends AppCompatActivity {
    DatabaseService databaseService;
    Item current_item;

    ItemCart itemCart;
    TextView tvName, tvNote, tvPrice, tvAverageRating, tvQuantityPage;
    FirebaseAuth mAuth;
    String userId;
    Cart userCart = null;
    FavoriteList userFavorites;
    private RatingBar ratingBarUser;
    private Button btnSubmitRating, btnPlusPage, btnMinusPage;

    private double lastRatingByUser = 0;
    private boolean hasRatedBefore = false;
    private int selectedQuantity = 1; // כמות שנבחרה בדף
    Button AddToFav, CartItemBtn;
    ImageView ivItemField;
    String selectedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }

        selectedItemId = getIntent().getStringExtra("Item_UID");

        if (selectedItemId != null && !selectedItemId.isEmpty()) {
            // טעינת המוצר מהדאטהבייס
            databaseService.getItem(selectedItemId, new DatabaseService.DatabaseCallback<>() {
                @Override
                public void onCompleted(Item item) {
                    current_item = item;

                    itemCart=new ItemCart(current_item,1);
                    populateFields();
                    setupListeners();
                }
                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(Item_page.this, "טעינת מוצר נכשלה", Toast.LENGTH_SHORT).show();
                }
            });

            // טעינת העגלה של המשתמש
            databaseService.getCart(userId, new DatabaseService.DatabaseCallback<>() {
                @Override
                public void onCompleted(Cart cart) {
                    userCart = (cart == null) ? new Cart() : cart;
                }
                @Override
                public void onFailed(Exception e) {
                    userCart = new Cart();
                }
            });

            databaseService.getFavorites(userId, new DatabaseService.DatabaseCallback<FavoriteList>() {
                @Override
                public void onCompleted(FavoriteList favorites) {
                    // עכשיו זה יעבוד חלק כמו ה-Cart!
                    userFavorites = (favorites == null) ? new FavoriteList() : favorites;
                }
                @Override
                public void onFailed(Exception e) {
                    userFavorites = new FavoriteList();
                }
            });
        }
    }

    private void initViews() {
        tvName = findViewById(R.id.tvNameItem);
        tvNote = findViewById(R.id.tvNoteItem);
        tvPrice = findViewById(R.id.tvPriceItem);
        ivItemField = findViewById(R.id.ivItemAddCart);
        AddToFav = findViewById(R.id.AddTOFavBtn);
        CartItemBtn = findViewById(R.id.CartItemBtn);
        ratingBarUser = findViewById(R.id.ratingBarUser);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        btnSubmitRating = findViewById(R.id.btnSubmitRating);

        // רכיבי הכמות החדשים
        tvQuantityPage = findViewById(R.id.tvQuantityPage);
        btnPlusPage = findViewById(R.id.btnPlusPage);
        btnMinusPage = findViewById(R.id.btnMinusPage);
    }

    private void setupListeners() {
        // כפתור הוספה לעגלה
        CartItemBtn.setOnClickListener(v -> addCartItem());
        AddToFav.setOnClickListener(v -> addFavoritesItem());


        // לוגיקה לכפתורי פלוס ומינוס (בחירת כמות)
        btnPlusPage.setOnClickListener(v -> {
            selectedQuantity++;
            itemCart.setAmount(selectedQuantity);
            tvQuantityPage.setText(String.valueOf(selectedQuantity));
        });

        btnMinusPage.setOnClickListener(v -> {
            if (selectedQuantity > 1) {
                selectedQuantity--;
                itemCart.setAmount(selectedQuantity);
                tvQuantityPage.setText(String.valueOf(selectedQuantity));
            }
        });

        // כפתור שליחת דירוג
        btnSubmitRating.setOnClickListener(v -> {
            if (current_item == null) return;
            double newUserRating = ratingBarUser.getRating();
            if (newUserRating > 0) {
                current_item.updateRating(lastRatingByUser, newUserRating, !hasRatedBefore);
                tvAverageRating.setText(String.format("דירוג ממוצע: %.1f", current_item.getRate()));
                databaseService.addItem(current_item, new DatabaseService.DatabaseCallback<>() {
                    @Override
                    public void onCompleted(Void unused) {
                        lastRatingByUser = newUserRating;
                        hasRatedBefore = true;
                        Toast.makeText(Item_page.this, "הדירוג נשמר!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailed(Exception e) {}
                });
            }
        });
    }

    private void addCartItem() {
        if (userCart != null && itemCart != null) {
//            databaseService.updateCart(userId, user -> {
            //       if (user == null) return null;
            //       Cart cart = user.getCart();

            // אתחול הרשימה אם היא null למניעת קריסה
            //     if (cart.getItemArrayList() == null) {
            //          cart.setItemArrayList(new ArrayList<>());
            //      }

            userCart.addItem(itemCart);

            databaseService.updateCart(userId, userCart, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(Item_page.this, "נוספו " + selectedQuantity + " יחידות לעגלה", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailed(Exception e) {

                }
            });

            // מעבר דף לפי סוג המוצר
            Intent go;
            String type = current_item.getType();
            if (type == null) type = "";

            switch (type) {
                case "book":
                    go = new Intent(Item_page.this, Book_page.class);
                    go.putExtra("type", "book");
                    break;
                case "toy":
                    go = new Intent(Item_page.this, Book_page.class);
                    go.putExtra("type", "toy");
                    break;
                case "device":
                    go = new Intent(Item_page.this, Book_page.class);
                    go.putExtra("type", "device");
                    break;
                case "shoe":
                    go = new Intent(Item_page.this, Book_page.class);
                    go.putExtra("type", "shoe");
                    break;
                default:
                    go = new Intent(Item_page.this, MainActivity.class);
                    break;
            }
            startActivity(go);
            finish(); // סגירת הדף הנוכחי
        }
    }


    private void addFavoritesItem() {
        // שימוש ב-userFavorites (מסוג FavoriteList) וב-current_item
        if (userFavorites != null && current_item != null) {

            // אתחול הרשימה בתוך האובייקט אם היא null
            if (userFavorites.getFavoriteItemsList() == null) {
                userFavorites.setFavoriteItemsList(new ArrayList<>());
            }

            ArrayList<Item> list = userFavorites.getFavoriteItemsList();
            boolean isAlreadyInFavorites = false;

            // בדיקה האם המוצר כבר קיים במועדפים לפי ה-ID שלו
            for (Item itemInFav : list) {
                if (itemInFav != null && itemInFav.getId().equals(current_item.getId())) {
                    isAlreadyInFavorites = true;
                    break;
                }
            }

            if (!isAlreadyInFavorites) {
                // מוסיפים את המוצר לרשימה המקומית
                list.add(current_item);

                // עדכון ב-Firebase דרך ה-DatabaseService
                databaseService.updateFavorites(list, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void unused) {
                        Toast.makeText(Item_page.this, current_item.getpName() + " נוסף למועדפים שלך!", Toast.LENGTH_SHORT).show();
                        // במועדפים בדרך כלל לא עוברים דף, אבל אם תרצה לעבור - תוכל להוסיף כאן את ה-Intent
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(Item_page.this, "שגיאה בהוספה למועדפים", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // אם המוצר כבר שם, אפשר להודיע למשתמש
                Toast.makeText(this, "המוצר כבר קיים במועדפים", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void populateFields() {
        if (current_item != null) {
            tvName.setText("Name: " + current_item.getpName());
            tvNote.setText("Information: " + current_item.getpNote());
            tvPrice.setText("Price: " + current_item.getPrice() + "$");
            tvAverageRating.setText(String.format("דירוג ממוצע: %.1f", current_item.getRate()));

            // צביעת הכוכבים בצהוב לפי הדירוג הממוצע הקיים במוצר
            ratingBarUser.setRating((float) current_item.getRate());

            Bitmap bitmap = ImageUtil.convertFrom64base(current_item.getImage());
            if (bitmap != null) ivItemField.setImageBitmap(bitmap);
        }
    }
}