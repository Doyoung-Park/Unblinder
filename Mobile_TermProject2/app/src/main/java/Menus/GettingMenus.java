package Menus;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GettingMenus extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.show_stores);

        ArrayList<Menu> menuList = new ArrayList<>();
//
//    Menu menu1 = new Menu("짜장면", 10000);
//    Menu menu2 = new Menu("짬뽕", 5000);

        //menus.add(new Menu("짜장면", 10000));
        menuList.add(new Menu("짜장면", 10000));
        menuList.add(new Menu("짬뽕", 5000));

    }
}
