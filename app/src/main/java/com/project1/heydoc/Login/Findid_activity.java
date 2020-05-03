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
import android.widget.TextView;
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

public class Findid_activity extends AppCompatActivity {

    int year, month, day;

    TextView typename, find_date, typephonenum;

    Boolean nothing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.findid);

        typename = findViewById(R.id.typeName_findid);             //xml 파일의 텍스트 뷰 매칭/인스턴스화
        find_date = findViewById(R.id.find_date_findid);
        typephonenum = findViewById(R.id.typePhoneNum_findid);

        Calendar calendar = new GregorianCalendar();            //달력(그레고리언) 만들기
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Button find_date_button = findViewById(R.id.find_date_button);      //달력 버튼 만들기

        find_date_button.setOnClickListener(new View.OnClickListener() {        //달력 버튼 누르면 실행
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int setyear, int setmonth, int setday) {       //선택된 날짜를 기준으로 연, 월, 일 설정하기
                        year = setyear;
                        month = setmonth;
                        day = setday;
                        find_date.setText(String.format("%d - %d - %d", year, month+1, day));         //에딛텍스트에 선택된 날짜 보여주기
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(Findid_activity.this, onDateSetListener, year, month, day);            //날짜 선택 다이얼로그 선언
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());          //오늘 이후로는 선택 못하게함
                datePickerDialog.show();        //날짜 선택 다이얼로그 보이게 함
            }
        });

        Button confirm = findViewById(R.id.confirm_findid);                 //찾기 버튼
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String username = typename.getText().toString();            //찾기 버튼 누르면 에딭텍스트에 입력된 값을 string에 저장함
                String birthday = find_date.getText().toString();
                String phonenum = typephonenum.getText().toString();

//                SharedPreferences userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);      //아이디 찾기 위해 쉐어드프리퍼런스 유저인포 객체화
//                Map<String, ?> total = userinfo.getAll();            //맵 이라는 인터페이스에 쉐어드프리퍼런스 값을 모두 넣음

                if(username.equals("")||birthday.equals("")||phonenum.equals("")){              //빈칸이 있으면 채우라는 안내를 표시
                    Toast.makeText(Findid_activity.this, "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{

                    findId(username, birthday, phonenum);           //아이디 찾는 메소드 실행


                    //Todo.. 이 아래는 두번 째로 작성한 부분으로 쉐어드프리퍼런스를 이용했고 Map 인터페이스를 사용함.. 하지만 파이어베이스를 사용하기 위해 주석처리함
//                    for(Map.Entry<String, ?> entry : total.entrySet()){                                     //엔트리의 모든 값을 보는 반복문
//                        String userinfostr = entry.getValue().toString();                                       //벨류를 스트링에 넣음
//                        String [] userinfoarray = userinfostr.split("@#@");                             //배열을 미리 지정된 문자로 나눠줌
//
//                        if(userinfoarray[2].equals(username)&&userinfoarray[5].equals(phonenum)&&userinfoarray[6].equals(birthday)){
//                            AlertDialog.Builder builder = new AlertDialog.Builder(Findid_activity.this);
//                            builder.setMessage("당신의 아이디는 "+userinfoarray[0]+" 입니다.");                           //일치하는 값이 있으면 아이디 출력
//                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                }
//                            });
//                            nothing=false;                              //없는 경우를 출력하는 토스트를 무효화하기 위한 불린
//                            builder.create().show();
//                            break;                          //반복문 이탈
//                        }
//                    }
                }

                //Todo.. 이 아래는 최초로 작성한 부분으로 로직이 안 맞아서 구현 실패함.. 참고자료로 삭제하지 않음.
//                String phone = userinfo.getString(phonenum, "");
//                if(username.equals("")||birthday.equals("")||phonenum.equals("")){
//                    Toast.makeText(Findid_activity.this, "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
//                }else{
//                    if(userinfo!=null&&phone.contains(phonenum)){               //유저인포가 널이 아니고 휴대폰 번호를 가지고 있는지 확인하는 조건문
//                        String namefromSP = phone.split("@#@")[2];          //이름을 쉐어드프리퍼런스에서 가져옴
//                        String birthfromSP = phone.split("@#@")[5];         //생일을 쉐어드프리퍼런스에서 가져옴
//                        Log.i("태그", namefromSP);
//                        Log.i("태그", birthfromSP);
//
//                        if(namefromSP.equals(username)&&birthfromSP.equals(birthday)){          //입력된 값과 쉐어드프리퍼런스 값과 같은지 확인
//                            AlertDialog.Builder builder = new AlertDialog.Builder(Findid_activity.this);
//                            builder.setMessage("당신의 아이디는 "+phone.split("@#@")[0]+" 입니다.");          //같으면 값을 입력..
//                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                }
//                            });
//                            builder.create().show();
//                        }else{
//                            AlertDialog.Builder builder = new AlertDialog.Builder(Findid_activity.this);        //찾는 값이 없을 때
//                            builder.setMessage("일치하는 정보가 없습니다.");
//                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                }
//                            });
//                            builder.create().show();
//                        }
//                    }else{
//                        AlertDialog.Builder builder = new AlertDialog.Builder(Findid_activity.this);        //값이 정확히 입력되지 않았을 때
//                        builder.setMessage("다시 입력해주세요.");
//                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                            }
//                        });
//                        builder.create().show();
//                    }
//                }
            }
        });


    }

    private void findId(final String name, final String birthday, final String phonenum){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");               //users를 가지고 있는 데이터베이스 주소를 가져옴
        Log.i("태그", "데이터베이스 레퍼런스 생성");

        nothing = true;                     //아무것도 없을 때 안내하기 위한 불린 값

        ValueEventListener valueEventListener = new ValueEventListener() {              //값 이벤트 리스너를 만듦
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {              //데이터 체인지 메소드


                Log.i("태그", "온 데이트 체인지 메소드 안 진입");

                for(DataSnapshot item : dataSnapshot.getChildren()){                            //데이터스냅샷의 자식정보들을 데이터스냅샷 형식의 아이템에 넣기 위한 포문

                    Log.i("태그", "데이터스냅샷을 빼기 위한 반복문 시작");

                    String key = item.getKey();                                 //이 아이템의 키를 가져옴
                    Log.i("태그", "이 아이템 키값 : "+key);

                    User user = item.getValue(User.class);                          //이 아이템의 벨류를 유저 클래스로 담음

                    if(user.getName().equals(name)&&user.getBirthday().equals(birthday)&&user.getPhonenum().equals(phonenum)){                  //각 정보들을 비교하는 조건문

                        Log.i("태그", "입력값과 맞는 아이템을 찾으면 들어오는 조건문, nothing 불린이 false로 바뀜");
                        AlertDialog.Builder builder = new AlertDialog.Builder(Findid_activity.this);
                        builder.setMessage("당신의 아이디는 "+user.getId()+" 입니다.");                           //일치하는 값이 있으면 아이디 출력
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        nothing=false;                              //없는 경우를 출력하는 토스트를 무효화하기 위한 불린
                        builder.create().show();
                    }
                }

                if(nothing==true){                                                         //위 반복문을 다 돌았는데도 일치하는 값이 없으면 안내를 토스트함
                    Toast.makeText(Findid_activity.this, "일치하는 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                }


//                for(int i = 0 ; i<arrayList.size() ; i++){
//                    Log.i("태그", "차일드 이터레이터를 돌릴 반복문 안 진입"+arrayList.get(i));
//
//
//                    String childname = (String) arrayList.get(i).child("name").getValue();
//                    String childbirth = (String) arrayList.get(i).child("birthday").getValue();
//                    String childphone = (String) arrayList.get(i).child("phonenum").getValue();
//
//                    Log.i("태그", childname+"/"+childbirth+"/"+childphone);
//
//                    if(name.equals(childname)&&birthday.equals(childbirth)&&phonenum.equals(childphone)){
//                        Log.i("태그", "같은 아이디 찾으면 들어오는 조건문");
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(Findid_activity.this);
//                        builder.setMessage("당신의 아이디는 "+arrayList.get(i).child("id").getValue()+" 입니다.");                           //일치하는 값이 있으면 아이디 출력
//                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                            }
//                        });
//                        nothing=false;                              //없는 경우를 출력하는 토스트를 무효화하기 위한 불린
//                        builder.create().show();
//                        break;                          //반복문 이탈
//                    }
//                }

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
