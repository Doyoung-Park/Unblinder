package com.android.example.mobile_termproject2;

import static android.speech.tts.TextToSpeech.ERROR;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;


public class Menus extends AppCompatActivity {

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
    int check=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_menus);

        // 전화 버튼 누르면 해당 가게 전화번호로 전화 걺. (다이얼에 전화번호 입력)
        Button call = (Button)findViewById(R.id.chooseMenu);

//        // GettingMenus class 에서 보낸 intent 받는 부분 시작
//        Intent intent5 = getIntent();
//        String menuIntent = intent5.getExtras().getString("menuList");
//
//        System.out.println("Here is Menu page & MenuList is: "+ menuIntent);   // test 용
//        // 받는 부분 여기까지

        call.setOnClickListener(new View.OnClickListener(){

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







        //mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm

        // RecognizerIntent 생성
        STT = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        STT.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName()); // 여분의 키
        STT.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR"); // 언어 설정
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(Menus.this); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
        mRecognizer.setRecognitionListener(listener); // 리스너 설정

         //이미지 및 TTS 설정
            lay=findViewById(R.id.activityMenus);
            TTS_text = "메뉴는 다음과 같습니다.";
            //
            tts();

            //이미지 클릭시 TTS,STT설정
            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                Toast.makeText(getApplicationContext() , STT_text, Toast.LENGTH_SHORT).show();
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