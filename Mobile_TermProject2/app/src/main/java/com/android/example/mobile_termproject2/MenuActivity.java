package com.android.example.mobile_termproject2;

import static android.speech.tts.TextToSpeech.ERROR;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class MenuActivity extends AppCompatActivity {
    // 메뉴용 변수들
    String id = "";
    String storeName = "";

    String url_for_menu = null; // 메뉴용 url
    String url_for_phone = null; // 전화번호용 url
    String num = null; // 쓰레드에서 핸들러에 안넘기고 바로 전역변수에 저장 시키기

    TextView textView; // 메뉴를 띄워줄 텍스트뷰
    TextView textViewstoreName;

    int type = -1;

    ArrayList<String> menu_category = new ArrayList<String>(); // 메뉴 카테고리를 담을 어레이 리스트
    ArrayList<String> menu_name = new ArrayList<String>(); // 메뉴 이름을 담을 어레이 리스트
    ArrayList<String> menu_price = new ArrayList<String>(); // 메뉴 가격을 담을 어레이 리스트
    Map<String, String> menuAndPrice = new LinkedHashMap<>(); // 위 두 리스트를 활용해서 채울 해쉬맵. 아래 해쉬맵에 이용
    ArrayList<Map<String, String>> wholeMenu = new ArrayList<>();
    Map<String, Map<String, String>> whole_menu = new LinkedHashMap<>(); //카테고리, <메뉴이름, 가격>을 담을 해쉬맵


    //음성인식 허용(STT)
    SpeechRecognizer mRecognizer;
    final int PERMISSION = 1;
    Intent STT;
    String STT_text = "";


    //TTS
    public TextToSpeech tts;
    String TTS_text="";
    //메뉴 이름, 가격 리스트[][0]=메뉴 이름,[][1]=가격
    private String[][] MenuList= null;

    //additional
    View lay;
    int check = 0;

    Bundle bundle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_menus);



        // initialize 먼저하고
        id = getIntent().getStringExtra("id");
        storeName = getIntent().getStringExtra("storeName");

        System.out.println(id);
        url_for_menu = "https://m.store.naver.com/restaurants/" + id; // 메뉴용 url
        url_for_phone = "https://m.place.naver.com/restaurant/" + id + "/home"; // 전화번호용 url

        textViewstoreName = (TextView) findViewById(R.id.storeName);
        textViewstoreName.setText(storeName);
        textView = (TextView) findViewById(R.id.textView); // TextView textView = ~~ 식으로 하면 앱이 계속 꺼짐 ㄷㄷ
        final Bundle bundle = new Bundle(); // 메뉴 이름 스트링 전달용 번들

        new Thread() {
            @Override

            public void run() {
                Document doc = null;
                try { //

                    // --------------------------------------------------------------------
                    // 링크 타입 구분해보기

                    // 여기서는 배민 or 간편주문 둘중 하나라도 지원하는지 확인
                    doc = Jsoup.connect(url_for_menu + "/tabs/menus/baemin/list").get(); // 우선 배민링크로 가서 확인
                    Elements linkType = doc.getElementsByAttributeValue("class", "btn_wrapper");
                    String text = linkType.text();
                    System.out.println(text);

                    // 케이스 분류 시작
                    if (linkType.size() > 0) { // linkType size가 1이면 배달의 민족이던
                        if (text.contains("배달의민족")) {
                            url_for_menu = url_for_menu + "/tabs/menus/baemin/list";
                            type = 0;
                        } else {
                            url_for_menu = url_for_menu + "/tabs/menus/easyOrder/list";
                            type = 1;
                        }
                    } else {
                        url_for_menu = "https://m.place.naver.com/restaurant/" + id + "/menu/list";
                        url_for_phone = "https://m.place.naver.com/restaurant/" +id + "/home"; // 전화번호용 링크도 바뀌어야 함
                        doc = Jsoup.connect(url_for_menu).get();
                        Elements nullTest = doc.select("._3yfZ1"); // 매뉴 정보를 지원하는지 안하는지 확인하는 용도

                        if (nullTest.size() > 0) {
                            type = 2;
                        } else {
                            type = 3;
                        }
                    }

                    System.out.println(type);

                    // 여기서부터는 url들이 각 유형에 맞는 방식으로 수정되어 있음


                    // --------------------------------------------------------------------
                    // 전화번호 가져와서 'num' 전역변수에 저장
                    doc =Jsoup.connect(url_for_phone).get();
                    Elements phone_num = doc.select("._3HEBM");
                    num = phone_num.attr("href");
                    System.out.println(num);

                    // --------------------------------------------------------------------


                    if (type == 0) {
                        // 배민 지원 가게

                        // '대표메뉴' 카테고리 추가하기
                        doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                        Elements main_menu = doc.select("h2"); // 클래스 네임 "name"(메뉴 이름)인 값을 가져오겠다
                    /*
                        여기에 메뉴판 이미지로 보기가 있는지 없는지 판단해서 메뉴판 이미지가 있는지 없는지 파악할 수 있음
                    */
                        menu_category.add(main_menu.text().substring(0, main_menu.text().indexOf(" "))); // 메뉴카테고리 어레이리스트의 첫번째 키값에 추가 됨

//                    // 이번에는 대표메뉴 제외한 카테고리들 가져오기
//                    doc = Jsoup.connect(url_for_menu).get();
//                    Elements elseCategory = doc.select("..list_tit");
//                    String[] arrayForCategory = elseCategory.text().split(" ");
//                    for (int i = 0; i < elseCategory.size(); i++) {
//                        menu_category.add(arrayForCategory[i]);
//                    }

                        doc = Jsoup.connect(url_for_menu).get();
                        Elements daepyo = doc.select(".list_item.type_d_menu");

                        // 메뉴 이름들 가져오기
                        doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                        Elements menuName_list = doc.getElementsByAttributeValue("class", "name"); // 클래스 네임 "name"(메뉴 이름)인 값을 가져오겠다
                        for (int i = 0; i < daepyo.size(); i++) {
                            menu_name.add(menuName_list.get(i).text());
                        }

                        // 메뉴 가격들 가져오기
                        doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                        Elements menuPrice_list = doc.select(".price");
                        String[] arrayForPrice = menuPrice_list.text().split(" ");
                        for (int i = 0; i < daepyo.size(); i++) {
                            menu_price.add(arrayForPrice[i]);
                        }


                        // 이제 메뉴이름-가격 해쉬맵에 추가하기
                        for (int i = 0; i < menu_price.size(); i++) {
                            menuAndPrice.put(menu_name.get(i), menu_price.get(i));
                        }

                        // 카테고리-(메뉴이름-가격) 해쉬맵에 추가해서 전체 메뉴정보 완성
                        for (int i = 0; i < menu_category.size(); i++) {
                            whole_menu.put(menu_category.get(i), menuAndPrice);
                        }


                        bundle.putSerializable("menu", (Serializable) whole_menu); // (key값, value값) 메뉴 이름
                        // 쓰레드 간의 데이터 전송을 위한 객체
                        Message msg = handler.obtainMessage();
                        msg.setData(bundle);
                        handler.sendMessage(msg);


                    } else if (type == 1) {
                        // 네이버 간편 주문 지원 가게

                        // '대표메뉴' 카테고리 추가하기
                        doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                        Elements main_menu = doc.select("h2"); // 클래스 네임 "name"(메뉴 이름)인 값을 가져오겠다
                    /*
                        여기에 메뉴판 이미지로 보기가 있는지 없는지 판단해서 메뉴판 이미지가 있는지 없는지 파악할 수 있음
                    */
                        menu_category.add(main_menu.text().substring(0, main_menu.text().indexOf(" "))); // 메뉴카테고리 어레이리스트의 첫번째 키값에 추가 됨

//                    // 이번에는 대표메뉴 제외한 카테고리들 가져오기
//                    doc = Jsoup.connect(url_for_menu).get();
//                    Elements elseCategory = doc.select("..list_tit");
//                    String[] arrayForCategory = elseCategory.text().split(" ");
//                    for (int i = 0; i < elseCategory.size(); i++) {
//                        menu_category.add(arrayForCategory[i]);
//                    }

                        doc = Jsoup.connect(url_for_menu).get();
                        Elements daepyo = doc.select(".list_item.type_d_menu");

                        // 메뉴 이름들 가져오기
                        doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                        Elements menuName_list = doc.getElementsByAttributeValue("class", "name"); // 클래스 네임 "name"(메뉴 이름)인 값을 가져오겠다
                        for (int i = 0; i < daepyo.size(); i++) {
                            menu_name.add(menuName_list.get(i).text());
                        }

                        // 메뉴 가격들 가져오기
                        doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                        Elements menuPrice_list = doc.select(".price");
                        String[] arrayForPrice = menuPrice_list.text().split(" ");
                        for (int i = 0; i < daepyo.size(); i++) {
                            menu_price.add(arrayForPrice[i]);
                        }


                        // 이제 메뉴이름-가격 해쉬맵에 추가하기
                        for (int i = 0; i < menu_price.size(); i++) {
                            menuAndPrice.put(menu_name.get(i), menu_price.get(i));
                        }

                        // 카테고리-(메뉴이름-가격) 해쉬맵에 추가해서 전체 메뉴정보 완성
                        for (int i = 0; i < menu_category.size(); i++) {
                            whole_menu.put(menu_category.get(i), menuAndPrice);
                        }


                        bundle.putSerializable("menu", (Serializable) whole_menu); // (key값, value값) 메뉴 이름
                        // 쓰레드 간의 데이터 전송을 위한 객체
                        Message msg = handler.obtainMessage();
                        msg.setData(bundle);
                        handler.sendMessage(msg);

                    } else if (type == 2) {
                        // 배민 네이버 둘 다 지원하지 않지만 메뉴는 제공하는 가게

                        // 메뉴 이름들 가져오기
                        doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                        Elements menuName_list = doc.getElementsByAttributeValue("class", "_3yfZ1"); // 클래스 네임 "_3yfZ1"(메뉴 이름)인 값을 가져오겠다
                        for (int i = 0; i < menuName_list.size(); i++) {
                            menu_name.add(menuName_list.get(i).text());
                        }

                        // 메뉴 가격들 가져오기
                        doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                        Elements menuPrice_list = doc.select("._3qFuX");
                        String[] arrayForPrice = menuPrice_list.text().split(" ");
                        for (int i = 0; i < menuPrice_list.size(); i++) {
                            menu_price.add(arrayForPrice[i]);
                        }


                        // 이제 메뉴이름-가격 해쉬맵에 추가하기
                        for (int i = 0; i < menu_price.size(); i++) {
                            menuAndPrice.put(menu_name.get(i), menu_price.get(i));
                        }

                        bundle.putSerializable("menu", (Serializable) menuAndPrice); // (key값, value값) 메뉴 이름
                        // 쓰레드 간의 데이터 전송을 위한 객체
                        Message msg = handler.obtainMessage();
                        msg.setData(bundle);
                        handler.sendMessage(msg);

                    } else {
                        // 메뉴 정보를 지원하지 않는 가게입니다.

                        bundle.putString("menu", "메뉴 정보를 지원하지 않는 가게입니다."); // (key값, value값) 메뉴 이름
                        // 쓰레드 간의 데이터 전송을 위한 객체
                        Message msg = handler.obtainMessage();
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        // 전화 버튼 누르면 해당 가게 전화번호로 전화 걺. (다이얼에 전화번호 입력)
        Button call = (Button) findViewById(R.id.call);

        call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse(num));

                startActivity(intent);
            }
        });

        // RecognizerIntent 생성
        STT = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        STT.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName()); // 여분의 키
        STT.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR"); // 언어 설정
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(MenuActivity.this); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
        mRecognizer.setRecognitionListener(listener); // 리스너 설정

        //이미지 및 TTS 설정
        lay = findViewById(R.id.activityMenus);
        TTS_text="전화 연결을 원하시면 전화 또는 전화 걸기라고 말씀해 주세요.";
        TTS_text=TTS_text+"선택하신 식당 "+storeName+"에 대한 메뉴 결과 입니다.";
        TTS_text = TTS_text+ "메뉴는 다음과 같습니다.";
        //
        tts();

        //이미지 클릭시 TTS,STT설정
        lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check == 0) {
                    //TTS
                    mRecognizer.cancel();
                    tts.speak(TTS_text, TextToSpeech.QUEUE_FLUSH, null);
                    check = 1;
                } else {
                    //STT
                    tts.stop();
                    STT_text = "";
                    mRecognizer.startListening(STT); // 듣기 시작
                    check = 0;
                }
            }
        });

    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (type == 0 || type == 1) {
                Bundle bundle = msg.getData();
                Map<String, Map<String, String>> recievedMenu = new LinkedHashMap<>(); //카테고리, <메뉴이름, 가격>을 담을 해쉬맵

                recievedMenu = (Map<String, Map<String, String>>) bundle.getSerializable("menu"); // key가 menu 데이터의 value값 가져와라, 이런식으로 View를 메인 쓰레드에 뿌려줘야함

                String category_name = recievedMenu.keySet().toString();
                // 카테고리 이름 획득. 식당별로 대표메뉴인 곳도, 신메뉴인 곳도 있음
                category_name = category_name.substring(1, category_name.indexOf(']'));

                String menuAndPrice = recievedMenu.get(category_name).toString();
                StringBuilder builder = new StringBuilder(menuAndPrice);
                menuAndPrice.replaceAll("\\{", "");
                menuAndPrice.replaceAll("\\}", ""); // 시작과 끝에 붙은 {와 } 제거

                // ", "를 \n으로 바꿔서 한줄당 메뉴하나씩 나오게 출력
                textView.setText(category_name + "\n" + menuAndPrice.replaceAll(", ", "\n"));
                TTS_text=TTS_text+category_name + "\n" + menuAndPrice.replaceAll(", ", "\n");
            } else if (type == 2) {
                Bundle bundle = msg.getData();
                Map<String, String> recievedMenu = new LinkedHashMap<>(); //카테고리, <메뉴이름, 가격>을 담을 해쉬맵

                recievedMenu = (Map<String, String>) bundle.getSerializable("menu"); // key가 menu 데이터의 value값 가져와라, 이런식으로 View를 메인 쓰레드에 뿌려줘야함


                String menuAndPrice = "";
                StringBuilder builder = new StringBuilder(menuAndPrice);
                menuAndPrice.replaceAll("\\{", "");
                menuAndPrice.replaceAll("\\}", ""); // 시작과 끝에 붙은 {와 } 제거
                for (Map.Entry<String, String> entrySet : recievedMenu.entrySet()) {
                    menuAndPrice = menuAndPrice + entrySet.getKey() + "=" + entrySet.getValue() + "\n";
                }

                // ", "를 \n으로 바꿔서 한줄당 메뉴하나씩 나오게 출력
                textView.setText(menuAndPrice);
                TTS_text=TTS_text+menuAndPrice;
            } else {
                Bundle bundle = msg.getData();
                textView.setText(bundle.getString("menu")); // key가 temperature인 데이터의 value값 가져와라, 이런식으로 View를 메인 쓰레드에서 뿌려줘야함
                TTS_text=TTS_text+bundle.getString("menu");
            }



//            String keys = recievedMenu.keySet().toString();
//            keys = keys.substring(1, keys.indexOf(']'));
//            String[] strKeys = keys.split(", "); // strkeys에 번들로 전달받은 해쉬맵의 키값들이 들어가 있음. 이값들이 메뉴 카테고리 값들
//            for (int i = 0; i < strKeys.length; i++) {
//
//            }
//            for (Map.Entry<String, String> entrySet : recievedMenu.get.entrySet()) {
//                textView.setText(entrySet.getKey() + " : " + entrySet.getValue());
//            }

//            Set<String> keySet = recievedMenu.keySet();
//            int count = 0;
//            for (String key : keySet) { // 이 반복문은 번들로 받은 해쉬맵을 키값별로 따로 스트링에 저장하는 반목문
//                menuPrice[count] = key + "\n" + recievedMenu.get(key);
//                count++;
//            }

        }
    };


    public void tts() {
        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

    }

    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }


    //이하STT임
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            // 말하기 시작할 준비가되면 호출
            Toast.makeText(getApplicationContext(), "음성인식 시작", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
            // 말하기 시작했을 때 호출
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // 입력받는 소리의 크기를 알려줌
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // 말을 시작하고 인식이 된 단어를 buffer에 담음
        }

        @Override
        public void onEndOfSpeech() {
            // 말하기를 중지하면 호출
        }

        @Override
        public void onError(int error) {
            // 네트워크 또는 인식 오류가 발생했을 때 호출
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER 가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러 발생 : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < matches.size(); i++) {
                STT_text = STT_text + matches.get(i);
            }
            STT_text = STT_text.replace(" ","");
            Toast.makeText(getApplicationContext() , STT_text, Toast.LENGTH_SHORT).show();
            //select = 식당 번호
            if(STT_text.equals("뒤로")){
                finish();
            }
            else if(STT_text.equals("전화")||STT_text.equals("전화걸기")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(num));
                startActivity(intent);
            }
            else{
                tts.speak("음성인식으로 번호를 다시 입력 해주세요.", TextToSpeech.QUEUE_FLUSH, null);
            }
        }


        @Override
        public void onPartialResults(Bundle partialResults) {
            // 부분 인식 결과를 사용할 수 있을 때 호출
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // 향후 이벤트를 추가하기 위해 예약
        }
    };
}