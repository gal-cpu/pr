package com.example.pr.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pr.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
    }

    public void onClickBookPage(View view) {
        Intent go = new Intent(MainActivity.this, Book_page.class);
        go.putExtra("type", "book");
        startActivity(go);
    }

    public void onClickToysPage(View view) {
        Intent go = new Intent(MainActivity.this, Book_page.class);
        go.putExtra("type", "toy");
        startActivity(go);
    }

    public void onClickDevicesPage(View view) {
        Intent go = new Intent(MainActivity.this, Book_page.class);
        go.putExtra("type", "device");
        startActivity(go);
    }

    public void onClickShoesPage(View view) {
        Intent go = new Intent(MainActivity.this, Book_page.class);
        go.putExtra("type", "shoe");
        startActivity(go);
    }

    public void IVuserClickM(View view) {
        Intent go = new Intent(MainActivity.this, UserProfile.class);
        startActivity(go);
    }

    public void IVcartClickM(View view) {
        Intent go = new Intent(MainActivity.this, CartList.class);
        startActivity(go);
    }

    public void IVhouseClickM(View view) {
        Intent go = new Intent(MainActivity.this, MainActivity.class);
        startActivity(go);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


            getMenuInflater().inflate(R.menu.user_menu, menu);

        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



            if(  item.getItemId()==R.id.menu_user) {
                Intent go = new Intent(MainActivity.this, UserProfile.class);
                startActivity(go);
                return true;
            }

        if(  item.getItemId()==R.id.menu_history) {
                Intent go = new Intent(MainActivity.this, OrderHistory.class);
                startActivity(go);
                return true;
            }
        if(  item.getItemId()==R.id.menu_favorites) {
                Intent go = new Intent(MainActivity.this, Favorites.class);
                startActivity(go);
                return true;
            }

        if(  item.getItemId()==R.id.menu_cart) {
                Intent go = new Intent(MainActivity.this, CartList.class);
                startActivity(go);
                return true;
            }

        if(  item.getItemId()==R.id.menu_logout) {
                Intent go = new Intent(MainActivity.this, LogIn.class);
                startActivity(go);
                return true;
            }

        if(  item.getItemId()==R.id.menu_admin) {

            if(LogIn.isAdmin) {
                Intent go = new Intent(MainActivity.this, AdminPage.class);
                startActivity(go);
            }
                return true;
            }


            return super.onOptionsItemSelected(item);
        }




}