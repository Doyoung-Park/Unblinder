package com.android.example.mobile_termproject2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;


public class Address extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_address);

        Button chooseButton = (Button) findViewById(R.id.choose);
        TextInputEditText addressText = (TextInputEditText)findViewById(R.id.inputAddress);

        chooseButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // 사용자가 입력한 주소가 userAddress 로 저장됨.
                String userAddress = addressText.getText().toString();

                System.out.println(userAddress);    // test 용

                // userAddress 라는 이름으로 intent 에 담음.
                Intent intent = new Intent(getApplicationContext(), FoodName.class);
                intent.putExtra("userAddress",userAddress);

                startActivity(intent);  // foodName 액티비티 열어둠 (단방향)
            }
        });


    }
}