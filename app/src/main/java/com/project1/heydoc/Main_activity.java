package com.project1.heydoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.project1.heydoc.Consult.ConsultList_activity;
import com.project1.heydoc.Login.Login_activity;
import com.project1.heydoc.Login.LoginedUser;
import com.project1.heydoc.Record.Record_activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Main_activity extends AppCompatActivity {

    String newsURL = "https://news.naver.com/main/list.nhn?mode=LS2D&mid=shm&sid1=103&sid2=241";
    ImageView newsImg;
    TextView newsText;
    ProgressBar progressBar;

    JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
    TimerTask changing;

    ArrayList<News_Data> news_data = new ArrayList<>();             //뉴스 데이터를 담을 수 있는 어레이 리스트

//    ArrayList<String> tokens = new ArrayList<>();
    String token;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_logout, menu);                              //로그아웃 기능을 액션바에 표시함(기능은 아님)
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {                                      //액션바 아이템 선택할 때 실행할 기능에 대한 정의
        switch (item.getItemId()){
            case R.id.logout :                                                                          //로그아웃 버튼을 누르면 아래 실행

                //아래 두 쉐어드프리퍼런스는 아이디저장과 자동로그인 기능을 비활성화 하기 위한 작업이다.
                SharedPreferences saveidSP = getSharedPreferences("saveidSP", MODE_PRIVATE);
                SharedPreferences.Editor editor = saveidSP.edit();
                editor.putBoolean("chked", false);
                editor.commit();

                SharedPreferences savepwSP = getSharedPreferences("savepwSP", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = savepwSP.edit();
                editor1.putBoolean("chked", false);
                editor1.commit();


                Intent intent = new Intent(getApplicationContext(), Login_activity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);                                     //새로운 인텐트로 로그인 액티비티를 실행함
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, LoginedUser.name+"님 환영합니다.", Toast.LENGTH_SHORT).show();      //로그인에서 인텐트로 받았던 정보를 토스트로 띄워서 환영

        newsImg = findViewById(R.id.newsImg);               //뉴스를 넣을 이미지와 텍스트 인스턴스화
        newsText = findViewById(R.id.newsText);
        progressBar = findViewById(R.id.progressBar2);

        Button consult = findViewById(R.id.consult);
        consult.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent inconsult = new Intent(getApplicationContext(), ConsultList_activity.class);      //병원 목록 전시되는 액티비티 화면 띄우기
                inconsult.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(inconsult);
            }
        });

        final Button record = findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent inrecord = new Intent(getApplicationContext(), Record_activity.class);            //의료기록 전시되는 액티비티 띄우기
                startActivity(inrecord);
            }
        });

        Button emergency = findViewById(R.id.emergency);                                        //비상콜 보내는 액티비티 띄우기
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();                               //알림다이얼로그 메소드 실행(해당 메소드는 하단에 있음)
            }
        });

        Button location = findViewById(R.id.location);                                      //병원/약국 위치 찾는 액티비티 띄우기
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inlocation = new Intent(getApplicationContext(), Findlocation_activity.class);
                startActivity(inlocation);
            }
        });

//        Button tip = findViewById(R.id.tip);
//        tip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intip = new Intent(getApplicationContext(), Tip_activity.class);                      //의료팁 액티비티 띄우기
//                startActivity(intip);
//            }
//        });

        Button showuserinfo = findViewById(R.id.showuserinfo);
        showuserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inshowinfo = new Intent(getApplicationContext(), Showinfo_activity.class);            //개인정보 액티비티 띄우기
                startActivity(inshowinfo);
            }
        });

        jsoupAsyncTask.execute();               //뉴스를 크롤링하는 어싱크 태스크 작동!!
        Log.i("온 크리에이트", "온 크리에이트의 마지막");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(jsoupAsyncTask.getStatus() == AsyncTask.Status.PENDING){         //어싱크태스크 상태를 확인하기 위한 조건문
            Log.i("태그", "어싱크태스크가 작동 전");
        }

        if(jsoupAsyncTask.getStatus() == AsyncTask.Status.RUNNING){
            Log.i("태그", "어싱크태스크가 작동 중");
        }

        if(jsoupAsyncTask.getStatus() == AsyncTask.Status.FINISHED){
            Log.i("태그", "어싱크태스크가 종료된 상태임");
        }

//        changing.cancel();
//        Log.i("태그", "타이머 태스크 종료");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("태그", "onResume in Main_activity");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sosreceiver/"+LoginedUser.id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("태그", "onDataChange1");
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    String key = item.getValue().toString();
                    Log.i("태그", "데이터스냅샷에서 가져온 벨류"+key);
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users").child(key).child("token");
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.i("태그", "onDataChange2");
                            token = dataSnapshot.getValue().toString();
                            Log.i("태그", "getToken="+token);

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

//        if(changing.cancel()){                    //타이머태스크가 종료되었다면, 다시 실행시키려고 하는데 오류가 자꾸 발생함
//            startChangingNews();
//        }
    }


    public void dialog() {
        Log.i("태그", "dialog run");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);            //비상콜을 눌렀을 때 띄우는 메소드.. ToDO 개인정보에서 설정된 과거 병력을 같이 보내는 기능 추가
        builder.setCancelable(false);
        builder.setMessage("SOS를 보내시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("태그", "click yes");
                Toast.makeText(getApplicationContext(), "SOS를 보냈습니다.\n"+"과거 병력 : "+LoginedUser.weakness, Toast.LENGTH_LONG).show();
//                String token1 = "e7QVj9uBbVQ:APA91bE8tys5aUKhcuN8qsyV09Za4YclPqh6fB5j4MkKnRVR_ZC4KHTa9OX4r4nVgF2aALkGlmg6XoSI7yzVJFNZ8owf_x_1JN4ulGziW0zT30XdDcVTencKBS88MCpvRxv8T0Mesxzu";
//                String token2 = "cFsKtgi-v0Q:APA91bE-Qx6oZYoC6elWyNuo4p4RceAIIEVtSqnqxEvMVyxHs2P7Hq1KQQ4vm96KhHQ5wZsFFp05QbJuK9h5Dy6EGXVS-i-kiIzKfnT5tgW9SWxdvEOgK7UCh_X5tf7XsnVSv3kpEJXs";
//
//                tokens.add(token1);
//                tokens.add(token2);

                new Thread() {
                    public void run() {
                        Log.i("태그", "Thread Run~");

                        //푸시 알림을 보내기 위한 데이터 만들기.. 제이슨 오브젝트 형식으로 서버에 전송할 것이다.
                        JsonObject jsonObject = new JsonObject();
                        Gson gson = new Gson();

                        //이것은 받을 사람의 토큰을 만드는 것이다.
//                        Log.i("태그", "입력된 토큰="+token);
                        JsonElement jsonElement = gson.toJsonTree(token);

                        jsonObject.add("to", jsonElement);      //오브젝트에 추가

                        //푸시알림의 제목과 내용을 넣는 부분이다.
                        JsonObject notification = new JsonObject();
                        notification.addProperty("title", "[SOS] "+LoginedUser.id+" 님께서 SOS를 보냈습니다.");
                        notification.addProperty("body", "과거 병력 : "+LoginedUser.weakness);
                        notification.addProperty("priority", "high");

                        jsonObject.add("notification", notification);       //오브젝트에 추가
                        Log.i("태그", "jsonObject : "+jsonObject);

                        //제이슨 형식이라는 것을 인지시키기 위한 미디어타입 선언
                        MediaType mediaType = MediaType.parse("application/json");

                        //OKHttp를 이용해서 서버와 연동하기 위한 클라이언트
                        OkHttpClient httpClient = new OkHttpClient();
//                        Log.i("태그", "forHTTPtoken="+token);
                        try {
                            Log.i("태그", "try 문 진입");
                            //FCM의 주소를 Request 에 넣음. 헤더로 제이슨타입을 넣고, 파이어베이스 클라우드 메시징의 서버키값을 넣는다.(이 키값은 내꺼)
                            Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                                    .addHeader("Content-Type", "application/json; UTF-8")
                                    .addHeader("Authorization", "key=" + "AAAAna0Rh88:APA91bHBYDZPDBxT71aEJp1QT9AafgsRFQ61peZRV1wkrhoHrX0INKe2eLDOpYBO3oabLj-EERo2L9Mu_8qNHkJbLhyXdHeE2Sf3MoARaw6OoeidLv_xqIV3ukzy8g03A6bLAY8nYsvp")
                                    .post(RequestBody.create(mediaType, jsonObject.toString())).build();

                            //대답을 받기 위해 리스폰을 주는 것으로 작업을 만들어서 실행시킨다.
                            Response response = httpClient.newCall(request).execute();

                            //응답을 담기 위한 string 선언
                            String res = response.body().toString();
                            Log.i("태그", "notofication response-" + res);

                        } catch (IOException e) {
                            Log.i("태그", "Error-" + e);
                        }

                    }
                }.start();


                //android admin SDK 를 이용해서 메시징을 보내려고 했는데 각종 오류(duplicate, auto 1.4.jar 등등)가 발생함... 아마 기존에 가져온 dependency와의 충돌로 예상...

//                String token = "dx7PupeYHAw:APA91bEtxCtGHMzMzPUYjaUIrgTyQXmIjzI4dYAjtEr3FGnJA3wGjnIog47E4jUycKSxidwqpDQgoW4LlyY7mRuaKUbFQeF8gomASUbYS-gVGe1MrvFK1lq7sOoaAjDXyA8sOqC3o-Ue";
//                Message message = Message.builder().putData("title", "안녕").putData("body", "하하하").setToken(token).build();
//
//                try {
//                    String response = FirebaseMessaging.getInstance().send(message);
//                    Log.i("태그", "메시지 보냈다!"+message);
//                } catch (FirebaseMessagingException e) {
//                    e.printStackTrace();
//                }

            }

        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_LONG).show();
            }
        });
        builder.create().show();
    }


    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void>{               //뉴스 가져오는 크롤링을 위한 어싱크태스크

        @Override
        protected void onPreExecute() {             //작동 전에 실행되는 메소드
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {                  //백그라운드에서 실행되는 메소드
            try {
                Document doc = Jsoup.connect(newsURL).get();                //뉴스URL을 가져와서 Doc에 넣음(전체를 가져오는 것임)

                Elements article = doc.select("ul[class=type06_headline]").select("li");    //doc에서 필요한 부분만 빼옴. 이 경우 네이버 의료/복지뉴스 헤드라인 테이블만 빼온 것임

                Log.i("태그", article+"가져옴");

                int articleSize = article.size();                           //아티클들의 사이즈를 확인.. 반복문을 위함

                Log.i("아티클 사이즈", String.valueOf(articleSize));


                for(int i = 0 ; i<5 ; i++){                   //아티클 사이즈만큼 반복문을 돌려줌.. 새로운 값들을 계속 빼기 위함

                    Log.i("태그", i+"포문 들어옴");
                    String newsImg = article.select("li dt[class=photo] a img").get(i).attr("src");         //뉴스 이미지 빼옴
                    String newsText = article.select("li dt[class=photo] a img").get(i).attr("alt");        //뉴스 텍스트 빼옴
                    String newsURL = article.select("li dt[class=photo] a").get(i).attr("href");            //뉴스 URL 빼옴

                    Log.i("이미지 url", newsImg);
                    Log.i("뉴스 제목 텍스트", newsText);
                    Log.i("뉴스 URL", newsURL);

                    news_data.add(new News_Data(newsImg, newsText, newsURL));           //쓸 데이터 형식으로 미리 만들어 놓은 어레이 리스트에 담아줌
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {               //작동중일 때 상태 업데이트를 위한 메소드(현재는 미사용)
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Void aVoid) {                  //백그라운드 작업이 끝났을 때 시작하는 메소드
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.GONE);               //프로그레스바를 없앰

            //Todo.. 쓰레드 잡아먹어서 잠시 주석처리해놓음.. 시연 시에는 활성화 필요!!
            startChangingNews();


            //필요없는 문장들... 위의 메소드에서 다 실행함.
//            newsText.setText(news_data.get(0).getNewsText());               //제목을 넣음
//            Log.i("제목 지정", newsText.getText().toString());
//            Glide.with(Main_activity.this).load(news_data.get(0).getNewsImg()).override(150, 100).into(newsImg);
//            Log.i("이미지 뿌림", news_data.get(0).getNewsImg());         //이미지를 넣음(Glide 사용)
//
//            newsText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {                            //뉴스 기사를 눌렀을 때, 해당 웹페이지로 이동하기 위한 기능(이것을 위해 URL을 받아왔던 것)
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    Uri uri = Uri.parse(news_data.get(0).getNewsURL());
//                    intent.setData(uri);
//
//                    startActivity(intent);                                  //해당 화면 보이게함
//                }
//            });
//            newsImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {                            //이미지 클릭해도 위에 텍스트 클릭한 것과 같은 기능을 하도록 함
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    Uri uri = Uri.parse(news_data.get(0).getNewsURL());
//                    intent.setData(uri);
//
//                    startActivity(intent);
//                }
//            });
//
//            Log.i("온포스트이젴큐트", "어싱크태스크 마지막");
        }

    }

    private void startChangingNews(){

        Log.i("태그", "체인징 뉴스 작동!");

        final Handler handler = new Handler();              //핸들러 선언
        Timer timer = new Timer();                          //타이머 태스크를 사용하기 위해 타이머 선언

        changing = new TimerTask() {              //주기적으로 실행할 타이머 태스크에 대해서 정의
            @Override
            public void run() {                                                 //타이머 태스크의 런 메소드
                Log.i("태그", "타이머태스크 런 메소드 안");
                for (int k = 0 ; k<news_data.size() ; k++) {
                    Log.i("태그", "타이머태스크 런 메소드의 포문 안 "+k+"번째 포문");
                    final int i = k;
                    handler.post(new Runnable() {                                   //핸들러 정의. 포스트 러너블을 사용
                        @Override
                        public void run() {
                            Log.i("태그", "핸들러 러너블 안");
                            newsText.setText(news_data.get(i).getNewsText());               //제목을 넣음
                            Log.i("제목 지정", newsText.getText().toString());

                            Activity activity = Main_activity.this;
                            if(activity.isFinishing())
                                return;

                            Glide.with(Main_activity.this).load(news_data.get(i).getNewsImg()).override(150, 100).into(newsImg);
                            Log.i("이미지 뿌림", news_data.get(i).getNewsImg());         //이미지를 넣음(Glide 사용)

                            final int j = i;
                            newsText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {                            //뉴스 기사를 눌렀을 때, 해당 웹페이지로 이동하기 위한 기능(이것을 위해 URL을 받아왔던 것)
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    Uri uri = Uri.parse(news_data.get(j).getNewsURL());
                                    intent.setData(uri);
                                    startActivity(intent);                                  //해당 화면 보이게함
                                }
                            });

                            newsImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {                            //이미지 클릭해도 위에 텍스트 클릭한 것과 같은 기능을 하도록 함
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    Uri uri = Uri.parse(news_data.get(j).getNewsURL());
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });

                        }

                    });
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {                      //포문 안에서 3초의 간격을 주기 위한 스레드 슬립
                        e.printStackTrace();
                    }
                    Log.i("태그", "핸들러 런 메소드 안 3초 경과");
                }
            }
        };
        Log.i("태그", "타이머 스케즐 실행!");
        timer.schedule(changing, 0, 3000);                  //타이머 스케줄을 실행. 0부터 시작해서, 3초 간격


        //Todo.. 러너블을 따로 만들어서 실행시켜봄.. 역시나 arraylist의 마지막 인덱스 데이터만 표시됨.
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                for(int i = 0 ; i < news_data.size() ; i++){
//                    newsText.setText(news_data.get(i).getNewsText());               //제목을 넣음
//                    Log.i("제목 지정", newsText.getText().toString());
//                    Glide.with(Main_activity.this).load(news_data.get(i).getNewsImg()).override(150, 100).into(newsImg);
//                    Log.i("이미지 뿌림", news_data.get(i).getNewsImg());         //이미지를 넣음(Glide 사용)
//
//
//                    final int j = i ;
//                    newsText.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {                            //뉴스 기사를 눌렀을 때, 해당 웹페이지로 이동하기 위한 기능(이것을 위해 URL을 받아왔던 것)
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            Uri uri = Uri.parse(news_data.get(j).getNewsURL());
//                            intent.setData(uri);
//                            startActivity(intent);                                  //해당 화면 보이게함
//                        }
//                    });
//                    newsImg.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {                            //이미지 클릭해도 위에 텍스트 클릭한 것과 같은 기능을 하도록 함
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            Uri uri = Uri.parse(news_data.get(j).getNewsURL());
//                            intent.setData(uri);
//                            startActivity(intent);
//                        }
//                    });
//                }
//                handler.postDelayed(this,2000);
//            }
//        };
//        handler.postDelayed(runnable, 2000);


        //Todo.. 핸들러에 러너블을 만들어서 포스트로 실행시켜봄.. 포스트 딜레이도 써봤으나 이건 최초 딜레이가 생겨서 바꿈.. arraylist의 마지막 인덱스 데이터만 표시되는 문제가 있음.
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//
//                for(int i = 0 ; i < news_data.size() ; i++){
//                    Log.i("태그", "핸들러 런 메소드 안 포문 실행");
//
//                    newsText.setText(news_data.get(i).getNewsText());               //제목을 넣음
//                    Log.i("제목 지정", newsText.getText().toString());
//                    Glide.with(Main_activity.this).load(news_data.get(i).getNewsImg()).override(150, 100).into(newsImg);
//                    Log.i("이미지 뿌림", news_data.get(i).getNewsImg());         //이미지를 넣음(Glide 사용)
//
//
//                    final int j = i ;
//                    newsText.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {                            //뉴스 기사를 눌렀을 때, 해당 웹페이지로 이동하기 위한 기능(이것을 위해 URL을 받아왔던 것)
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            Uri uri = Uri.parse(news_data.get(j).getNewsURL());
//                            intent.setData(uri);
//                            startActivity(intent);                                  //해당 화면 보이게함
//                        }
//                    });
//                    newsImg.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {                            //이미지 클릭해도 위에 텍스트 클릭한 것과 같은 기능을 하도록 함
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            Uri uri = Uri.parse(news_data.get(j).getNewsURL());
//                            intent.setData(uri);
//                            startActivity(intent);
//                        }
//                    });
//
//                    try {
//                        Thread.sleep(3000);                                 //3초간의 시간 지연을 부여함
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Log.i("태그", "핸들러 런 메소드 안 2초 경과");
//                }
//                Log.i("태그", "핸들러 런 메소드 안 포문 벗어나고 런 메소드 끝");
//            }
//        });

        Log.i("태그", "체인징 뉴스 마지막");
    }


}
