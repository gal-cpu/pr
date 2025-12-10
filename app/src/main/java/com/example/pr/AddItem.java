package com.example.pr;

import static java.lang.Float.parseFloat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.model.Item;

public class AddItem extends AppCompatActivity implements View.OnClickListener {

    EditText etItemName, etItemType, etItemNote, etItemPrice, etItemImage;
    String itemName, itemType, itemNote, itemPrice1, itemImage;
    Double itemPrice, rate, sumRate, numCount;
    Button btnCreate;



    @SuppressLint("MissingInflatedId")
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

        etItemName=findViewById(R.id.etItemName);
        etItemNote=findViewById(R.id.etItemNote);
        etItemType=findViewById(R.id.etItemType);
        etItemImage=findViewById(R.id.etItemAddImage);
        etItemPrice=findViewById(R.id.etItemPrice);

        btnCreate=findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        itemName = etItemName.getText().toString();
        itemType = etItemType.getText().toString();
        itemNote = etItemNote.getText().toString();
        itemPrice1 = etItemPrice.getText().toString();
        itemImage = etItemImage.getText().toString();

        Item item=new Item("id", "itemImage",0 , itemName, itemNote,itemPrice, 0.0, 0.0, itemType);
    }
}