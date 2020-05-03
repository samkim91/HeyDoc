package com.project1.heydoc.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project1.heydoc.Main_activity;
import com.project1.heydoc.R;

public class Login_activity extends AppCompatActivity {

    EditText loginid;
    EditText loginpw;

    CheckBox saveid, savepw;
    boolean saveidchked, savepwchked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

//        Intent intent = new Intent(getApplicationContext(), Loading_activity.class);            //로딩 액티비티 나오게 함
//        startActivity(intent);


        loginid = findViewById(R.id.loginId);               //로그인 아이디, 비밀번호 인스턴스화
        loginpw = findViewById(R.id.loginpw);

        saveid = findViewById(R.id.saveid);
        savepw = findViewById(R.id.savepw);

        Button loginclk = findViewById(R.id.loginclk);          //로그인 버튼 누르면 실행되는 문장들
        loginclk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = loginid.getText().toString();       //에딭텍스트에 입력되어 있는 값들을 가져옴
                String pw = loginpw.getText().toString();

//                SharedPreferences userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);
//                String getuserinfo = userinfo.getString(id, "");                //유저 인포를 빼옴
//                Log.i("태그", getuserinfo);

                if(id.equals("")){              //id 입력칸이 빈 공간인지 확인하는 조건문
                    Toast.makeText(Login_activity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                }else{
                    Login(id, pw);

                    //Todo.. 쉐어드프리퍼런스를 사용한 로그인 기능이었음... 하지만 파이어베이스로 바꾸었기에 주석처리함
//                    if(getuserinfo.contains(id)){              //입력한 id가 유저인포 안에 있으면 들어오는 조건문
//                        String userpw = getuserinfo.split("@#@")[1];          //유저 인포를 #으로 나눔
//                        Log.i("태그", userpw);
//                        if(userpw.equals(pw)){
//                            Intent inloginclk  = new Intent(getApplicationContext(), Main_activity.class);       //메인 화면으로 넘어가도록 함
//                            inloginclk.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            inloginclk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//                            LoginedUser.id = getuserinfo.split("@#@")[0];
////                            User.pw = getuserinfo.split("@#@")[1];
//                            LoginedUser.name = getuserinfo.split("@#@")[2];
////                            User.gender = getuserinfo.split("@#@")[3];
////                            User.bloodtype = getuserinfo.split("@#@")[4];
////                            User.phonenum = getuserinfo.split("@#@")[5];
////                            User.birthday = getuserinfo.split("@#@")[6];
////                            User.email = getuserinfo.split("@#@")[7]+"@"+getuserinfo.split("@#@")[8];
//                            LoginedUser.weakness = getuserinfo.split("@#@")[9];
//
//                            startActivity(inloginclk);
//                        }else{
//                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();     //비밀번호 불일치 할때
//                        }
//                    }else{
//                        Toast.makeText(getApplicationContext(), "존재하지 않는 ID 입니다.", Toast.LENGTH_SHORT).show();      //아이디 없을 때
//                    }
                }
            }
        });


        Button findId = findViewById(R.id.findId);
        findId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infindId = new Intent(getApplicationContext(), Findid_activity.class);        //아이디 찾는 액티비티로 전환
                startActivity(infindId);
            }
        });

        Button findPw = findViewById(R.id.findPw);
        findPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infindPw = new Intent(getApplicationContext(), Findpw_activity.class);        //비밀번호 찾는 액티비티로 전환
                startActivity(infindPw);
            }
        });

        Button sign = findViewById(R.id.signUp);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent insign = new Intent(getApplicationContext(), Agreepolicy_activity.class);     //가입하는 액티비티로 전환
//                insign.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                insign.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(insign);
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){                                                     //카메라와 외부저장소 쓰기 권한이 있는지 확인하고 없으면 요청함
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Log.i("태그", "카메라와 외부저장소 쓰기 권한 있음");
            }else {
                Log.i("태그", "카메라와 외부저장소 쓰기 권한이 없어서 요청하는 구문");
                ActivityCompat.requestPermissions(Login_activity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {           //권한 요청에 대한 결과를 받는 곳
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("태그", "onRequestPermissionsResult");
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.i("태그", permissions[0]+"is"+grantResults[0]+"and"+permissions[1]+"is"+grantResults[1]);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences saveidSP = getSharedPreferences("saveidSP", MODE_PRIVATE);            //저장된 아이디를 불러오기 위한 쉐어드프리퍼런스를 선언함
        saveidchked = saveidSP.getBoolean("chked", false);                                      //체크박스의 참/거짓을 확인함

        if(saveidchked==true){                                                      //체크박스가 참이면 들어오는 조건문
            saveid.setChecked(true);                                                    //체크박스를 표시하고
            loginid.setText(saveidSP.getString("id", ""));                              //저장된 아이디를 가져온다.
            Log.i("태그",  "저장된 아이디 불러옴 : "+loginid.getText().toString());
        }else{
            Log.i("태그",  "저장된 아이디가 없음");                        //아이디 저장 기능이 비활성화 되었을 때 들어오는 조건문
        }

        SharedPreferences savepwSP = getSharedPreferences("savepwSP", MODE_PRIVATE);
        savepwchked = savepwSP.getBoolean("chked", false);

        if(savepwchked==true){
            savepw.setChecked(true);
            loginid.setText(savepwSP.getString("id",""));
            loginpw.setText(savepwSP.getString("pw", ""));
            Log.i("태그",  "저장된 아이디 비밀번호 불러오고 자동로그인까지 : "+loginid.getText().toString()+"/"+loginpw.getText().toString());
            Login(loginid.getText().toString(), loginpw.getText().toString());
        }else {
            Log.i("태그",  "자동 로그인 기능이 비활성화 상태임");                        //아이디 저장 기능이 비활성화 되었을 때 들어오는 조건문
        }
    }

//    @Override
//    public void onBackPressed() {
//        Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
//        super.onBackPressed();
//    }

    private void Login(final String id, final String pw){

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Log.i("태그", "데이터베이스 레퍼런스 생성");

        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean noId = true;
                Boolean noPw = true;

                for(DataSnapshot item : dataSnapshot.getChildren()){
                    Log.i("태그", "데이터스냅샷을 빼기 위한 반복문 시작");
                    String key = item.getKey();
                    Log.i("태그", "이 아이템 키값 : "+key);

                    User user = item.getValue(User.class);
                    Log.i("태그", "이 아이템 유저벨류 : "+user);

                    if(key.equals(id)){
                        Log.i("태그", "존재하는 아이디가 있을 때 들어오는 조건문, 다음으론 비밀번호 확인");
                        if(user.getPw().equals(pw)){
                            Log.i("태그", "이 유저의 아이디가 같으며, 비밀번호가 같으면 들어오는 조건문");
                            Intent intent  = new Intent(getApplicationContext(), Main_activity.class);       //메인 화면으로 넘어가도록 함
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            LoginedUser.id = user.getId();                              //앱 전역에 쓸 유저 정보를 로그인된 유저클래스에 담아둔다(static 변수로)
                            LoginedUser.name = user.getName();
                            LoginedUser.weakness = user.getWeakness();
                            LoginedUser.imageUri = user.getImageUri();

                            SharedPreferences token = getSharedPreferences("token", MODE_PRIVATE);
                            String gettoken = token.getString("thetoken", "");

                            databaseReference.child(user.getId()).child("token").setValue(gettoken);        //이 로그인한 유저의 token 값을 쉐어드프리퍼런스에서 불러와서 저장해준다.
                            Log.i("태그", "토큰을 부여함 : "+gettoken);

                            SharedPreferences saveidSP = getSharedPreferences("saveidSP", MODE_PRIVATE);                //아이디를 저장할 쉐어드 프리퍼런스 선언
                            SharedPreferences.Editor editor = saveidSP.edit();
                            if(saveid.isChecked()){                                                                 //아이디 저장 체크박스가 체크되어 있다면 들어오는 조건문
                                editor.putString("id", loginid.getText().toString());                       //id 를 키값으로 입력된 아이디를 저장한다
                                editor.putBoolean("chked", true);                                   //체크되어있다는 것을 불린 으로 저장한다.
                                editor.commit();                                    //저장
                                Log.i("태그", "아이디가 저장됨 : "+loginid.getText().toString());
                            }else {
                                editor.putBoolean("chked", false);                              //체크가 안 되어 있으면, 체크 안됨을 불린으로 저장한다.
                                editor.commit();                                    //저장
                                Log.i("태그", "아이디 저장 기능이 풀림");
                            }

                            SharedPreferences savepwSP = getSharedPreferences("savepwSP", MODE_PRIVATE);                //아이디, 비밀번호를 저장할 쉐어드프리퍼런스 선언
                            SharedPreferences.Editor editor1 = savepwSP.edit();
                            if(savepw.isChecked()){                                                                 //자동 로그인이 체크되어 있다면 들어오는 조건문
                                editor1.putString("id", loginid.getText().toString());                                            //입력된 아이디를 가져와서 저장
                                editor1.putString("pw", loginpw.getText().toString());                                              // 입력된 비밀번호를 가져와서 저장
                                editor1.putBoolean("chked", true);                                              //자동 로그인 박스가 체크되어 있다는 것을 저장
                                editor1.commit();
                                Log.i("태그", "자동 로그인 기능이 활성화 됨 : "+loginid.getText().toString()+"/"+loginpw.getText().toString());
                            }else {
                                editor1.putBoolean("chked", false);                                         //자동 로그인 박스가 체크되어 있지 않다는 것을 저장
                                editor1.commit();
                                Log.i("태그", "자동 로그인 기능 비활성화됨");
                            }

                            startActivity(intent);
                            noPw = false;
                        }
                        noId = false;
                    }
                }

                if(noId==true){
                    Toast.makeText(Login_activity.this, "존재하지 않는 ID 입니다.", Toast.LENGTH_SHORT).show();      //아이디 없을 때

                }else {
                    if(noPw==true){
                        Toast.makeText(Login_activity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();     //비밀번호 불일치 할때
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.i("태그", "불러오기 실패");
            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);
        Log.i("태그", "디비레퍼런스 리스너를 싱글 밸류 이벤트로 추가");
    }
}
