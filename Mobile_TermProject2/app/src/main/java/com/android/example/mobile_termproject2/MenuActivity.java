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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public class MenuActivity extends AppCompatActivity {
    // 메뉴용 변수들
    String url_for_menu = "https://m.store.naver.com/restaurants/35255743/tabs/menus/baemin/list"; // 도원참치 판교점 메뉴용
    String msg_name;                    // 매뉴 이름을 저장할 변수

    ArrayList<String> menu_category = new ArrayList<String>(); // 메뉴 카테고리를 담을 어레이 리스트
    ArrayList<String> menu_name = new ArrayList<String>(); // 메뉴 이름을 담을 어레이 리스트
    ArrayList<String> menu_price = new ArrayList<String>(); // 메뉴 가격을 담을 어레이 리스트
    Map<String, String> menuAndPrice = new LinkedHashMap<>(); // 위 두 리스트를 활용해서 채울 해쉬맵. 아래 해쉬맵에 이용
    Map<String, Map<String, String>> whole_menu = new LinkedHashMap<>(); //카테고리, <메뉴이름, 가격>을 담을 해쉬맵

    TextView textView; // layout에 띄워주기 위함 텍스트뷰


    //음성인식 허용(STT)
    SpeechRecognizer mRecognizer;
    final int PERMISSION = 1;
    Intent STT;
    String STT_text = "";


    //TTS
    public TextToSpeech tts;
    String TTS_text;

    //additional
    View lay;
    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_menus);

        // 전화 버튼 누르면 해당 가게 전화번호로 전화 걺. (다이얼에 전화번호 입력)
        Button call = (Button) findViewById(R.id.call);

        call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel: 02-123-4567"));

                startActivity(intent);
            }
        });

        //mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
        //메뉴를 string list로 받아오기

        textView = (TextView) findViewById(R.id.textView); // TextView textView = ~~ 식으로 하면 앱이 계속 꺼짐 ㄷㄷ
        final Bundle bundle = new Bundle(); // 메뉴 이름 스트링 전달용 번들

        new Thread() {
            @Override

            public void run() {
                Document doc = null;
                try {
                    // '대표메뉴' 카테고리 추가하기
                    doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                    Elements main_menu = doc.select("h2"); // 클래스 네임 "name"(메뉴 이름)인 값을 가져오겠다
                    /*
                        여기에 메뉴판 이미지로 보기가 있는지 없는지 판단해서 메뉴판 이미지가 있는지 없는지 파악할 수 있음
                    */
                    menu_category.add(main_menu.text().substring(0, main_menu.text().indexOf(" "))); // 메뉴카테고리 어레이리스트의 첫번째 키값에 추가 됨

                    // 이번에는 대표메뉴 제외한 카테고리들 가져오기
                    doc = Jsoup.connect(url_for_menu).get();
                    Elements elseCategory = doc.select(".list_tit.active");
                    String[] arrayForCategory = elseCategory.text().split(" ");
                    for (int i = 0; i < elseCategory.size(); i++) {
                        menu_category.add(arrayForCategory[i]);
                    }

                    doc = Jsoup.connect(url_for_menu).get();
                    Elements abc = doc.select(".list_item.type_d_menu");

                    // 메뉴 이름들 가져오기
                    doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                    Elements menuName_list = doc.getElementsByAttributeValue("class", "name"); // 클래스 네임 "name"(메뉴 이름)인 값을 가져오겠다
                    for (int i = 0; i < menuName_list.size(); i++) {
                        menu_name.add(menuName_list.get(i).text());
                    }

                    // 메뉴 가격들 가져오기
                    doc = Jsoup.connect(url_for_menu).get(); // 이 주소의 html코드를 싹 가져오겠다
                    Elements menuPrice_list = doc.select(".price");
                    String[] arrayForPrice = menuPrice_list.text().split(" ");
                    for (int i = 0; i < menuPrice_list.size(); i++) {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();



        // RecognizerIntent 생성
        STT = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        STT.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName()); // 여분의 키
        STT.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR"); // 언어 설정
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(MenuActivity.this); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
        mRecognizer.setRecognitionListener(listener); // 리스너 설정

        //이미지 및 TTS 설정
        lay = findViewById(R.id.activityMenus);
        TTS_text = "메뉴는 다음과 같습니다.";
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
            Bundle bundle = msg.getData();
            Map<String, Map<String, String>> recievedMenu = new LinkedHashMap<>(); //카테고리, <메뉴이름, 가격>을 담을 해쉬맵

            recievedMenu = (Map<String, Map<String, String>>) bundle.getSerializable("menu"); // key가 menu 데이터의 value값 가져와라, 이런식으로 View를 메인 쓰레드에 뿌려줘야함

            for (Map.Entry<String,  Map<String, String>> entrySet : recievedMenu.entrySet()) {
                textView.setText(entrySet.getKey() + " : " + entrySet.getValue());
            }
            // textView.setText("가쟈왔습니다");
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
            Toast.makeText(getApplicationContext(), STT_text, Toast.LENGTH_SHORT).show();
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