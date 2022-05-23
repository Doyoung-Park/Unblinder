package com.android.example.mobile_termproject2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class Stores extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_stores);

        Button chooseStoreButton = (Button)findViewById(R.id.chooseStore);
        EditText stores;

        Intent intent3 = getIntent();
        String foodTest = intent3.getExtras().getString("foodname");

        System.out.println("Here is Store page & food choice is: "+ foodTest);   // test 용


        // 여기서는 식당 목록 txt 파일로부터 텍스트 가져와야 함.

        chooseStoreButton.setOnClickListener(new View.OnClickListener(){

            protected void onActivityResult(int requestCode, int resultCode, Intent data){

            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menus.class);


                startActivity(intent);


            }
        });

    }
}