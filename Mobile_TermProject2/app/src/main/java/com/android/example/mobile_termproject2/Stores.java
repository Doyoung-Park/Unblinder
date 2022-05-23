package com.android.example.mobile_termproject2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class Stores extends AppCompatActivity {

    String foodOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_stores);

        foodOption = getIntent().getStringExtra("food");
        Toast.makeText(getApplicationContext(), foodOption, Toast.LENGTH_SHORT).show();

        Button chooseStoreButton = (Button)findViewById(R.id.chooseStore);

        chooseStoreButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menus.class);
                startActivity(intent);
            }
        });

    }
}