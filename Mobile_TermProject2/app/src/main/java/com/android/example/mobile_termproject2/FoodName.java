package com.android.example.mobile_termproject2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;


public class FoodName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_foodname);


        //  <Address Activity 없어져서 이 부분 필요 없어짐>
//        Intent intent2 = getIntent();
//        String Address2 = intent2.getExtras().getString("userAddress");
//
//        System.out.println("Here is foodname page & address is: "+ Address2);   // test 용

        Button chooseButton = (Button) findViewById(R.id.foodChoose);
        TextInputEditText foodText = (TextInputEditText)findViewById(R.id.foodname);

        chooseButton.setOnClickListener(new View.OnClickListener(){

            protected void onActivityResult(int requestCode, int resultCode, Intent data){

            }

            @Override
            public void onClick(View view) {    // 메뉴 입력 후 선택 누르면 관련 식당 목록 저장한 텍스트 파일 생성
                String foodChoice = foodText.getText().toString();

                System.out.println(foodChoice);    // test 용


                Intent intent = new Intent(getApplicationContext(), Stores.class);
                intent.putExtra("foodname",foodChoice);

                startActivity(intent);
            }
        });
    }


}