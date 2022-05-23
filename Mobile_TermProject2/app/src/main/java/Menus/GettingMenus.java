//package Menus;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.media.Image;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.os.Parcelable;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.example.mobile_termproject2.Menus;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//
//public class GettingMenus extends AppCompatActivity {
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.show_stores);
//
//        ArrayList<String> menu_category = new ArrayList<String>(); // 메뉴 카테고리를 담을 어레이 리스트
//
//        ArrayList<String> menu_name = new ArrayList<String>(); // 메뉴 이름을 담을 어레이 리스트
//        ArrayList<String> menu_price = new ArrayList<String>(); // 메뉴 가격을 담을 어레이 리스트
//
//        Map<String, String> menuAndPrice = new LinkedHashMap<>(); // 위 두 리스트를 활용해서 채울 해쉬맵. 아래 해쉬맵에 이용
//        Map<String, Map<String, String>> whole_menu = new LinkedHashMap<>(); //카테고리, <메뉴이름, 가격>을 담을 해쉬맵
//
//        menu_category.add("메인 메뉴");
//
//        menu_name.add("짜장면");
//        menu_price.add("3000");
//
//        menu_name.add("짱뽕");
//        menu_price.add("5000");
//
//        menu_name.add("탕수육");
//        menu_price.add("8000");
//
//        menu_name.add("팔보채");
//        menu_price.add("9000");
//
//
//
//        int n = menu_name.size();		// menu_name 길이
//
//        for(int i=0 ; i<n; i++) {
//            menuAndPrice.put(menu_name.get(i), menu_price.get(i));		// (메뉴 - 가격) 짝지음
//        }
//
////        for(int i=0 ; i<n; i++) {
////        	System.out.println(menuAndPrice.get(menu_name.get(i)));
////        }
//
//
//        whole_menu.put(menu_category.get(0), menuAndPrice);		// 카테고리 - {메뉴 해시맵 리스트}
//
//        System.out.println(menu_category.get(0) +" " +  whole_menu.get(menu_category.get(0)) );
//        // 메인 메뉴 {짜장면=3000, 짱뽕=5000, 탕수육=8000, 팔보채=9000}
//
//        Intent intent_menu = new Intent(getApplicationContext(), Menus.class);
//        intent_menu.putExtra("menuList", (Parcelable) whole_menu);
//
//        startActivity(intent_menu);
//    }
//}
