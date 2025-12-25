package com.example.pr;

import static java.lang.Float.parseFloat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.model.Item;
import com.example.pr.services.DatabaseService;
import com.example.pr.util.ImageUtil;

public class AddItem extends AppCompatActivity implements View.OnClickListener {
    @Override
    public void onClick(View view) {

    }

    TextView tvPname, tvPtype, tvPnote, tvPprice;
    String mPname="", mPtype="", mPnote="", mPprice="";
    boolean namecheck=true, notecheck=true, typecheck=false, pricecheck=true;
    private EditText etItemName,etItemType, etItemNote, etItemPrice;
    private Button btnGallery, btnTakePic, btnAddItem;
    private  Double rate, sumRate, numCount;
    private ImageView imageView;
    private Button btnBack;

    private DatabaseService databaseService;


    /// Activity result launcher for capturing image from camera
    private ActivityResultLauncher<Intent> captureImageLauncher;

    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitViews();

        /// request permission for the camera and storage
        ImageUtil.requestPermission(this);

        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();


        /// register the activity result launcher for capturing image from camera
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        imageView.setImageBitmap(bitmap);
                    }
                });

        btnBack = findViewById(R.id.btnAddItemBack);

        tvPname=findViewById(R.id.TvPnameMessage);
        tvPtype=findViewById(R.id.TvPtypeMessage);
        tvPnote=findViewById(R.id.TvPnoteMessage);
        tvPprice=findViewById(R.id.TvPpriceMessage);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AddItem.this, AdminPage.class);
            startActivity(intent);
            finish();
        });


        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImageFromCamera();
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = etItemName.getText().toString();
                String itemNote = etItemNote.getText().toString();
                String itemPrice = etItemPrice.getText().toString();
                String itemType = etItemType.getText().toString();

                String imageBase64 = ImageUtil.convertTo64Base(imageView);
                double price = Double.parseDouble(itemPrice);


                if (itemName.length()<1)
                {
                    namecheck=false;
                    mPname="This field cannot be empty";
                }

                tvPname.setText(mPname);

                    if(itemType.contains("book") || itemType.contains("toy") || itemType.contains("tools") || itemType.contains("device"))
                    {
                        typecheck=true;
                    }
                    else
                    {
                        mPtype="book/toy/tools/device";
                    }

                tvPtype.setText(mPtype);


                if (itemNote.isEmpty())
                    {
                        mPnote="the length should be at least 1";
                        notecheck=false;
                    }
                tvPnote.setText(mPnote);


                if (itemPrice.isEmpty())
                {
                    mPnote="the price should be above 0$";
                    pricecheck=false;
                }

                tvPprice.setText(mPnote);


                if (itemName.isEmpty() || itemNote.isEmpty() || itemPrice.isEmpty() || itemType.isEmpty())
                {
                    Toast.makeText(AddItem.this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                }
                if (namecheck == true && typecheck == true && notecheck == true && pricecheck == true)
                {
                    Toast.makeText(AddItem.this, "המוצר נוסף בהצלחה!", Toast.LENGTH_SHORT).show();

                    String id = databaseService.generateItemId();

                    Item newItem = new Item(id, imageBase64, 0, itemName, itemNote, price, 0.0, 0.0, itemType);



                    /// generate a new id for the item

                    /// save the item to the database and get the result in the callback
                    databaseService.createNewItem(newItem, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            Log.d("TAG", "Item added successfully");
                            Toast.makeText(AddItem.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                            /// clear the input fields after adding the item for the next item
                            Log.d("TAG", "Clearing input fields");
                            Intent intent = new Intent(AddItem.this, AdminPage.class);
                           startActivity(intent);
                        }

                        @Override
                        public void onFailed(Exception e)
                        {
                            Log.e("TAG", "Failed to add item", e);
                            Toast.makeText(AddItem.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void InitViews()
    {
        etItemName = findViewById(R.id.etItemName);
        etItemNote = findViewById(R.id.etItemNote);
        etItemPrice = findViewById(R.id.etItemPrice);
        etItemType = findViewById(R.id.etItemType);
        btnGallery = findViewById(R.id.btnGallery);
        btnTakePic = findViewById(R.id.btnTakePic);
        btnAddItem = findViewById(R.id.btnAddItem);
        imageView = findViewById(R.id.imageViewPicture);
    }


    /// select image from gallery
    private void selectImageFromGallery() {
        //   Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //  selectImageLauncher.launch(intent);

        imageChooser();
    }

    /// capture image from camera
    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageLauncher.launch(takePictureIntent);
    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    imageView.setImageURI(selectedImageUri);
                }
            }
        }
    }
}