package Category;

import java.util.ArrayList;

import Menus.Menu;

public class Category {     // 메뉴 카테고리 ex. 메인메뉴, 서브메뉴, 음료 등
    public String name;

    public ArrayList<Menu> menus;   // 각 카테고리에 해당되는 메뉴들

    public Category(String name, ArrayList<Menu> menus){
        this.name = name;
        this.menus = menus;
    }
}

