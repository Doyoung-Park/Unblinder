package com.example.crawlingtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    // 크롤링용 변수
    String url = "https://m.store.naver.com/restaurants/35255743/tabs/menus/baemin/list"; // 도원참치 판교점
    String msg_name;                    // 매뉴 이름을 저장할 변수
    String msg_price;                    // 가격을 저장할 변수
    TextView textView;
    String imgUrl; // 이미지 파일의 주소. 크롤링 해서 html 코드 안에 있는 이 주소를 가져오는 것이 목표.
    HashMap<String, String> map1 = new HashMap<String,String>();

    // 이미지 가져오기 파트
    Button button;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView); // TextView textView = ~~ 식으로 하면 앱이 계속 꺼짐 ㄷㄷ
        // 여러 가지 타입의 데이터들을 String 형태의 Key 값과 함께 저장하는 Map 클래스
        final Bundle bundle1 = new Bundle(); // 메뉴 이름 스트링 전달용 번들
        final Bundle bundle2 = new Bundle(); // 가격 스트링 전달용 번들


        new Thread() {
            @Override

            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get(); // 이 주소의 html코드를 싹 가져오겠다
                    Elements elements_name = doc.select(".name"); // 클래스 네임 "name"(메뉴 이름)인 값을 가져오겠다
                    Elements elements_price = doc.select("._3qFuX"); // 클래스 네임 "_3qFuX"(가격)인 값을 가져오겠다
                    msg_name = elements_name.text();
                    msg_price = elements_price.text();
                    bundle1.putString("menu", msg_name); // (key값, value값) 메뉴 이름
                    bundle2.putString("price", msg_price); // (key값, value값) 가격
                    // 쓰레드 간의 데이터 전송을 위한 객체
                    Message msg1 = handler.obtainMessage();
                    msg1.setData(bundle1);
                    handler.sendMessage(msg1); //메뉴 이름 먼저 보내고~

                   /*Message msg2 = handler.obtainMessage();
                    msg2.setData(bundle2);
                    handler.sendMessage(msg2); // 그 다음에 가격 스컬*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView); // ImageLoadTask의 constructor에 넘겨줘야 함.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImageRequest();
            }
        });
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            textView.setText(bundle.getString("menu")); // key가 temperature인 데이터의 value값 가져와라, 이런식으로 View를 메인 쓰레드에서 뿌려줘야함
        }
    };

    // ImageLoadTask에 이미지 가져오라고 요청보내는 메소드
    public void sendImageRequest() {
        // 요 스트링은 이미지 파일의 링크. html 코드 안에 들어가 있음
        imgUrl = "https://ldb-phinf.pstatic.net/20210323_26/16164673062179MdHa_JPEG/9sosYxV3vtCyWOOtUWpzno5E.jpg";
        // ImageLoadTask 클래스를 실행시킬 때는 실행시키는 클래스에서 주소 String 값과 ImageView 객체를 보내주어야 함.
        ImageLoadTask task = new ImageLoadTask(imgUrl, imageView);
        task.execute(); // 실행 ㄱㄱ
    }


}

