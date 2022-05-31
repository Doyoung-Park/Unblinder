package com.android.example.mobile_termproject2;

import static android.speech.tts.TextToSpeech.ERROR;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class StoreActivity extends AppCompatActivity {

    // 크롤링용 변수
    String foodName = "null";
    String id = "";
    private String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=";
    private Document doc = null;
    private String storeClass = "OXiLu";
    private String totalStores = "";
    private int storeCount = 0;

    //식당
    private String[] storeList= null;

    //test용
    String resultTest;
    Bundle bundle = new Bundle();
    Bundle bundle1 = new Bundle();

    private StoreView storeView;


    //음성인식 허용(STT)
    SpeechRecognizer mRecognizer;
    final int PERMISSION = 1;
    Intent STT;
    String STT_text="";

    //TTS
    public TextToSpeech tts;
    String TTS_text;

    //additional
    View lay;
    int check =0;

    TextView test;
    EditText editText;
    String storeNameKeyword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_stores);

        test = findViewById(R.id.test);

        //메뉴 받기
        foodName= getIntent().getStringExtra("food");

        editText = findViewById(R.id.inputStoreName);


        //keyword = editText.getText().toString();

        new Thread() {
            @Override

            public void run() {
                Document doc = null;


                try {
                    totalStores = "";
                    String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=" + foodName + " 배달";
                    doc = Jsoup.connect(url).get(); // 이 주소의 html코드를 싹 가져오겠다
                    Elements elements_name = doc.getElementsByAttributeValue("class", storeClass);

                    storeCount = elements_name.size();
                    for (int i = 0; i < elements_name.size(); i++) {
                        totalStores = totalStores.concat(elements_name.get(i).toString());
                    }
                    bundle1.putString("stores", totalStores); // (key값, value값) 메뉴 이름
                    // 쓰레드 간의 데이터 전송을 위한 객체
                    Message msg1 = handler.obtainMessage();
                    msg1.setData(bundle1);

                    handler.sendMessage(msg1); //메뉴 이름 먼저 보내고~
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();



        Button btnPrev= (Button)findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });


            Button btnToMenu = (Button)findViewById(R.id.btnToMenu);
            btnToMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storeNameKeyword = editText.getText().toString();

                    updateID(storeNameKeyword);
                    id = idcutter(bundle1.getString("id"));

                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });


        //mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
        //이미지 및 TTS 설정
        lay=findViewById(R.id.activityStore);
        tts();
        // RecognizerIntent 생성
        STT = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        STT.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName()); // 여분의 키
        STT.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR"); // 언어 설정
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(StoreActivity.this); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
        mRecognizer.setRecognitionListener(listener); // 리스너 설정

        //이미지 클릭시 TTS,STT설정
        lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TTS_text = "선택하신 메뉴 "+foodName+"에 대한 검색 결과 입니다.\n"+resultTest;
                if(check ==0) {
                    //TTS
                    mRecognizer.cancel();
                    tts.speak(TTS_text, TextToSpeech.QUEUE_FLUSH, null);
                    check=1;
                }
                else {
                    //STT
                    tts.stop();
                    STT_text="";
                    mRecognizer.startListening(STT); // 듣기 시작
                    check=0;
                }
            }
        });
    }

    Handler handler = new Handler (Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            bundle = msg.getData();
            String parsed_total_restaurant = bundle.getString("stores");
            storeList = stringcutter(parsed_total_restaurant);

            //scroll view update
            resultTest = "";
            for (int i = 1; i < storeCount; i++) {
                resultTest = resultTest.concat((i+". "+storeList[i])+ "\n");
            }
            test.setText(resultTest);


        }
    };

    public String[] stringcutter(String message) {
        String[] list = message.split("<span class=\"" + storeClass + "\">", 0);


        String new_list[] = new String[storeCount];

        for (int i = 0; i < storeCount; i++) {
            String[] each_list = list[i].split("</span>", 0);
            new_list[i] = each_list[0];
        }

        return new_list;
    }

    public String idcutter(String message) {
        if (message != null)
            if (message.length() > 0) {
                String information = bundle1.getString("id");

                String[] information_split = information.split("/", 0);

                id = information_split[4];
            }
        return id;
    }

    public void updateID(String storeName) {
        final Document[] doc2 = {null};
        new Thread() {
            @Override
            public void run() {
                String url_for_id = "https://m.search.naver.com/search.naver?sm=mtp_sly.hst&where=m&query=" + storeName;
                try {
                    doc2[0] = Jsoup.connect(url_for_id).get();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Elements elements = doc2[0].select(".place_thumb._2SYdz");
                String text = elements.attr("href");
                System.out.println(text);
                // 쓰레드 간의 데이터 전송을 위한 객체
                bundle1.putString("id", text);
                Message msg1 = handler.obtainMessage();
                msg1.setData(bundle1);
                handler.sendMessage(msg1);
            }
        }.start();
    }





    //tts
    public void tts (){
        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this , new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
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
            Toast.makeText(getApplicationContext(),"음성인식 시작",Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getApplicationContext(), "에러 발생 : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < matches.size() ; i++){
                STT_text=STT_text+matches.get(i);
            }
            STT_text = STT_text.replace(" ","");
            Toast.makeText(getApplicationContext() , STT_text, Toast.LENGTH_SHORT).show();
            //select = 식당 번호
            int select = checkStore(STT_text);
            if(STT_text.equals("뒤로")){
                finish();
            }
            else if(select == 0) {
                tts.speak("음성인식으로 번호를 다시 입력 해주세요.", TextToSpeech.QUEUE_FLUSH, null);
            }
            else{
                updateID(storeList[select]);
                id = idcutter(bundle1.getString("id"));
                Toast.makeText(getApplicationContext() , id, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra("store", storeList[select]);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        }

        public int checkStore (String text){
            int sizeofList = storeList.length;
            int checkSize=9+1;
            String [][] checkNum = new String[checkSize][sizeofList];
            for(int i=1;i<sizeofList;i++){
                String KoreaNum=transNum(i);
                checkNum[1][i]=KoreaNum+"번";
                checkNum[2][i]=KoreaNum+"본";
                checkNum[3][i]= String.valueOf(i);
                checkNum[4][i]=checkNum[1][i]+storeList[i];
                checkNum[5][i]=checkNum[2][i]+storeList[i];
                checkNum[6][i]=checkNum[3][i]+storeList[i];
                checkNum[7][i]=storeList[i];
                checkNum[8][i]= String.valueOf(i)+"번";
                checkNum[9][i]= String.valueOf(i)+"본";
            }
            //select는 식당 번호
            int select=0;
            //CheckSize는 check의 가지 수
            for(int i=1;i<sizeofList;i++) {
                for (int j = 1; j < checkSize; j++) {
                    if (text.equals(checkNum[j][i])){
                        select=i;
                    }
                }
            }
            return select;
        }

        
        public String transNum(int num){
            String[] number = {"", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구"};
            String[] Unit = {"","십"};
            String result="";  //변환된 값을 저장할 배열

            if(num == 0)
                return "영";
            else if(num>=20){
                result=result+number[(int)(num/10)]+ Unit[1]+number[num%10];
            }
            else if(num>=10){
                result=result+Unit[1]+number[num%10];
            }
            else {
                result = result + number[num % 10];
            }

            return result;
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
