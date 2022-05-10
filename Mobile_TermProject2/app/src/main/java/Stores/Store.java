package Stores;

import java.util.ArrayList;

import Category.Category;
import Menus.Menu;

public class Store {    // 음식점 class

    public String name;     // 음식점 이름
    public String callNumber;   // 전화번호     ex. 02-6013-4293
    public Double starPoint;    // 식당의 별점
    public Boolean canDelivey;  // 배달이 가능한지

    public ArrayList<Category> category;   // 식당에서 제공하는 메뉴의 카테고리

    public Store(String name, String callNumber, Double starPoint, Boolean canDelivey, ArrayList<Category> category){
        this.name = name;
        this.callNumber = callNumber;
        this.starPoint = starPoint;
        this.canDelivey = canDelivey;
        this.category= category;
    }
}
