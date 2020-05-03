package com.project1.heydoc.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project1.heydoc.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Findpw_activity extends AppCompatActivity {

    int year, month, day;

    Boolean nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.findpw);


        final EditText typeId = findViewById(R.id.typeId);                    //에딭텍스트들 인스턴스화
        final EditText typename = findViewById(R.id.typeName_findpw);
        final EditText birthday = findViewById(R.id.birthday_pw);
        final EditText typephonenum = findViewById(R.id.typePhoneNum_findpw);

        Calendar calendar = new GregorianCalendar();            //달력 선언 및 각 int 값 설정
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Button find_date = findViewById(R.id.find_date);                //달력버튼 생성
        find_date.setOnClickListener(new View.OnClickListener() {       //달력버튼 눌렀을 때 기능
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {       //날짜 선택 리스터 선언
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {           //값을 년,월,일에 넣음
                        year = i;
                        month = i1;
                        day = i2;
                        birthday.setText(String.format("%d - %d - %d", year, month+1, day));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(Findpw_activity.this, onDateSetListener, year, month, day);    //달력 다이얼로그 생성
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());          //오늘 이후로 선택 못하게 하는 기능
                datePickerDialog.show();            //달력 다이얼로그 띄우기
            }
        });


        Button confirm = findViewById(R.id.confirm_findpw);             //찾기 버튼 인스턴스화 및 기능 설정
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = typeId.getText().toString();                //각 입력된 에딭텍스트 값 스트링으로 저장
                String username = typename.getText().toString();
                String userbirth = birthday.getText().toString();
                String userphonenum = typephonenum.getText().toString();

//                SharedPreferences userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);      //
//                String phone = userinfo.getString(userphonenum, "");
//
//                Map<String, ?> total = userinfo.getAll();

                if(userid.equals("")||username.equals("")||userbirth.equals("")||userphonenum.equals("")){              //빈칸이 있는지 확인하는 조건문
                    Toast.makeText(Findpw_activity.this, "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{                                                                                                          //빈칸 없으면 반복문 실행

                    findPw(userid, username, userbirth, userphonenum);


                    //Todo.. 이 아래는 쉐어드프리퍼런스로 값을 불러왔던 구간이다. 이제는 파이어베이스를 사용하기 위해 주석처리 해놓음
//                    nothing=true;
//                    for(Map.Entry<String, ?> entry : total.entrySet()){                 //Map 인터페이스에 모든 값을 불러오는 반복문
//                        String userinfostr = entry.getValue().toString();               //벨류를 스트링에 넣고
//                        String [] userinfoarray = userinfostr.split("@#@");       //미리 지정된 문자로 나눠 배열에 넣는다.
//
//                        Log.i("태그", "반복문");
//                        if(userinfoarray[0].equals(userid)&&userinfoarray[2].equals(username)&&userinfoarray[6].equals(userbirth)&&userinfoarray[5].equals(userphonenum)){          //배열값과 에딭텍스트 입력값을 비교함.
//                            Log.i("태그", "조건문");
//                            AlertDialog.Builder builder = new AlertDialog.Builder(Findpw_activity.this);
//                            builder.setMessage("당신의 비밀번호는 "+userinfoarray[1]+" 입니다.");                      //값이 같으면 출력
//                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                }
//                            });
//                            nothing=false;                                  //없는 경우를 출력하는 토스트를 무효화하기 위한 불린
//                            Log.i("태그", String.valueOf(nothing));
//                            builder.create().show();
//                            break;                  //반복문 이탈
//                        }
//                    }
//                    Log.i("태그", String.valueOf(nothing));
//                    if(nothing==true){                                  //위 반복문을 다 돌았는데도 일치하는 값이 없으면 안내를 토스트함
//                        Toast.makeText(Findpw_activity.this, "일치하는 정보가 없습니다.", Toast.LENGTH_SHORT).show();
//                        Log.i("태그", "몇번?");
//                        Log.i("태그", String.valueOf(nothing));
//                    }
                }

            }
        });



    }

    private void findPw(final String id, final String name, final String birthday, final String phonenum){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Log.i("태그", "데이터베이스 레퍼런스 생성");

        nothing = true;    //찾는 정보가 없을 때 안내 토스트를 띄우기 위한 불린

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot item : dataSnapshot.getChildren()){

                    Log.i("태그", "데이터스냅샷을 빼기 위한 반복문 시작");

                    String key = item.getKey();
                    Log.i("태그", "이 아이템 키값 : "+key);

                    User user = item.getValue(User.class);
                    Log.i("태그", "이 아이템 유저벨류 : "+user);

                    if(user.getId().equals(id)&&user.getName().equals(name)&&user.getBirthday().equals(birthday)&&user.getPhonenum().equals(phonenum)){     //찾는 값들을 비교해보는 조건문
                        Log.i("태그", "찾는 정보가 있을 때 들어오는 조건문");



                        AlertDialog.Builder builder = new AlertDialog.Builder(Findpw_activity.this);
                        builder.setMessage("당신의 비밀번호는 "+user.getPw()+" 입니다.");                      //값이 같으면 출력
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        nothing=false;                                  //없는 경우를 출력하는 토스트를 무효화하기 위한 불린
                        builder.create().show();
                    }
                }

                if(nothing==true){                                  //위 반복문을 다 돌았는데도 일치하는 값이 없으면 안내를 토스트함
                    Toast.makeText(Findpw_activity.this, "일치하는 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    Log.i("태그", "몇번?");
                    Log.i("태그", String.valueOf(nothing));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);
        Log.i("태그", "디비레퍼런스 리스너를 싱글 밸류 이벤트로 추가");
    }
}
