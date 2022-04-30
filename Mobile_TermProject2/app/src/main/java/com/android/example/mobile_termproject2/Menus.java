package com.android.example.mobile_termproject2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class Menus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_menus);

        // 전화 버튼 누르면 해당 가게 전화번호로 전화 걺. (다이얼에 전화번호 입력)
        Button call = (Button)findViewById(R.id.chooseMenu);

        call.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel: 02-123-4567"));

                startActivity(intent);
            }
        });

    }
}