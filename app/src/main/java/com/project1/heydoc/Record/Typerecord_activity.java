package com.project1.heydoc.Record;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.project1.heydoc.Login.LoginedUser;
import com.project1.heydoc.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

public class Typerecord_activity extends AppCompatActivity {

    EditText typesubject;  // = findViewById(R.id.typedetail);
    EditText showdate;  //  = findViewById(R.id.year);
    //EditText month;  //  = findViewById(R.id.month);
    //EditText day;  //  = findViewById(R.id.day);
    EditText typedetail;  //  = findViewById(R.id.typedetail);
    EditText showspinner;

    int year, month, day;

    ImageView addimage;
    int REQUEST_CODE = 1;
    Uri adress;
    String attachname;

    int attachnum = 0;
//    String [] spinneritem = { "기타", "호흡기내과", "소화기내과", "순환기내과", "정형외과", "신경외과", "일반외과", "산부인과", "소아청소년과", "피부과", "비뇨기과", "안과", "이비인후과", "정신과", "응급의학과", "재활의학과", "치과"};
    //    String key_subject = "key_subject";
//    String key_year = "key_year";
//    String key_month = "key_month";
//    String key_day = "key_day";
//    String key_detail = "key_detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.typerecord);


        final Spinner spinner = findViewById(R.id.spinner);           //스피너 선언
        showspinner = findViewById(R.id.showspinner);           //스피너 전시 텍스트 선언
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.진료과, android.R.layout.simple_spinner_item);       //스피너에 값 넣어주기
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);         //스피너 형식
        spinner.setAdapter(adapter);            //스피너에 어뎁터 반영

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {            //스피너 값 선택하면 반영하기
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showspinner.setText((String)spinner.getItemAtPosition(i));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                showspinner.setText("");
            }
        });

        addimage = findViewById(R.id.addimage);                 //이미지뷰 선언



        Button findattach = findViewById(R.id.findattach);         //이미지 버튼 선언

        findattach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);        //이미지들을 다 불러오는 암시적 인텐트
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        typesubject = findViewById(R.id.typesubject);            //에팉텍스트들 인스턴스화
        showdate = findViewById(R.id.showdate);                 //날짜 인스턴스화
        Button seldate = findViewById(R.id.seldate);            //날짜 선택 버튼 인스턴스화
        typedetail = findViewById(R.id.typedetail);             //내용 인스턴스화

        Calendar calendar = new GregorianCalendar();            //달력 초기화
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        seldate.setOnClickListener(new View.OnClickListener() {             //달력 만들어서 보여주는 문장
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {       //날짜 리스너 선언
                    @Override
                    public void onDateSet(DatePicker datePicker, int selyear, int selmonth, int selday) {
                        year = selyear;
                        month = selmonth;
                        day = selday;
                        showdate.setText(String.format("%d - %d - %d", year, month+1, day));        //쇼데이트 에딭텍스트에 날짜에서 받아온 스트링값 변환해서 넣기
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(Typerecord_activity.this, onDateSetListener, year, month, day);     //날짜 선택 다이얼로그 선언 및 보이기
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });


        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences records = getSharedPreferences("records", MODE_PRIVATE);
                SharedPreferences.Editor editor = records.edit();
                editor.clear();
                editor.commit();
                finish();
            }
        });

        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long now = System.currentTimeMillis();
                final Date date = new Date(now);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");            //게시글 작성 시간을 추출하기 위한 구문
                String currentTime = simpleDateFormat.format(date);

                SharedPreferences records = getSharedPreferences("records", MODE_PRIVATE);          //레코드라는 쉐어드프리퍼런스에 접근
                SharedPreferences.Editor editor = records.edit();                               //추가하기 위한 에디터 선언

                String insubject = typesubject.getText().toString();                  //Json에 넣을 각 값들 String에 넣기
                String indate = showdate.getText().toString();
                String insection = showspinner.getText().toString();
                String indetail = typedetail.getText().toString();
                String inattachname = attachname;
                String inattach = String.valueOf(adress);

                //Sharedprefereces 에 넣을 값들을 json 형식으로 만듦
                String inrecord = "{"+
                                 "\"subject\":"+"\""+insubject+"\","+
                                 "\"date\":"+"\""+indate+"\","+
                                 "\"section\":"+"\""+insection+"\","+
                                 "\"detail\":"+"\""+indetail+"\","+
                                 "\"attachname\":"+"\""+inattachname+"\","+
                                 "\"attach\":"+"\""+inattach+"\","+
                                 "\"thistime\":"+"\""+currentTime+"\""+"}";

                String lastinfo = records.getString(LoginedUser.id, "");                   //지난 정보를 가져옴.
                if(!lastinfo.equals("")){                                           //지난 정보가 있다면 들어오는 조건문

                    try {
                        JSONObject jsonObject = new JSONObject(inrecord);              //제이슨 오브젝트를 만들어서 스트링을 넣어줌.
                        JSONArray jsonArray = new JSONArray(lastinfo);                  //제이슨 어레이에 지난 정보를 불러옴.
                        jsonArray.put(jsonObject);                                      //제이슨 어레이에 새로운 정보를 넣음

                        JSONArray sortedJsonArray = new JSONArray();                //정렬된 제이슨 어레이를 담을 공간

                        ArrayList<JSONObject> arrayList = new ArrayList<>();            //제이슨 오브젝트 형식의 어레이리스트를 만듦.(정렬 함수 사용을 위함)
                        for(int i = 0 ; i< jsonArray.length() ; i++){
                            arrayList.add(jsonArray.getJSONObject(i));                  //제이슨 오브젝트를 하나씩 다 넣음
                        }

                        Collections.sort(arrayList, new Comparator<JSONObject>() {                  //정렬 기능 실행
                            @Override
                            public int compare(JSONObject jsonObject, JSONObject jsonObject1) {         //비교 메소드
                                String date1 = new String();
                                String date2 = new String();                //날짜를 담을 스트링을 만듦

                                try {
                                    date1 = jsonObject.getString("date");               //제이슨 오브젝트에서 날짜를 빼옴
                                    date2 = jsonObject1.getString("date");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                return date1.compareTo(date2);                              //날짜1과 날짜2를 비교해서 정수값을 반환
                            }
                        });
                        Collections.reverse(arrayList);                         //내림차순으르 정렬하기 위한 메소드

                        for(int i = 0 ; i<arrayList.size() ; i++){
                            sortedJsonArray.put(arrayList.get(i));                  //정렬된 어레이리스트를 제이슨어레이에 다시 담음
                        }

                        editor.putString(LoginedUser.id, String.valueOf(sortedJsonArray));           //다시 저장함.

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    editor.putString(LoginedUser.id, "["+inrecord+"]");         //지난 정보가 없다면,, 유저 아이디를 키값으로 하고 합친 데이터를 넣음
                }
                Log.i("등록되었는지 확인", inrecord);
                editor.commit();                                //쉐어드프리퍼런스 에디터를 저장함.

//                String getrecord = records.getString(User.id,"");
//                try {
//                    JSONArray jsonArray = new JSONArray(getrecord);
//
//                    for(int i = 0 ; i<jsonArray.length() ; i++){
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        Log.i("jsonObject", jsonObject.toString());
//
//                        String getsub = jsonObject.getString("subject");
//                        String getdate = jsonObject.getString("date");
//                        String gettime = jsonObject.getString("thistime");
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


//                Intent indataset = new Intent();                                            //인텐트에 결과값 넣기 위한 선언
//                indataset.putExtra("subject", typesubject.getText().toString());
//                indataset.putExtra("date", showdate.getText().toString());
//                indataset.putExtra("section", showspinner.getText().toString());
//                indataset.putExtra("detail", typedetail.getText().toString());
//                indataset.putExtra("attach", String.valueOf(adress));
//                Log.i("태그", String.valueOf(adress));
                setResult(RESULT_OK);                                           //인텐트 결과코드를 ok로 만듦
                finish();                                                                  //종료
            }
        });

//        if(savedInstanceState != null){                                 //세이브인스턴스스테이트가 null이 아니면 실행하는 조건문
//
//            Toast.makeText(getApplicationContext(), "인스턴스 불러짐!", Toast.LENGTH_LONG).show();
//
//            String subject = savedInstanceState.getString("key_subject");
//            typesubject.setText(subject);
//
//            String loadyear = savedInstanceState.getString("key_year");
//            year.setText(loadyear);
//
//            String loadmonth = savedInstanceState.getString("key_month");
//            month.setText(loadmonth);
//
//            String loadday = savedInstanceState.getString("key_day");
//            day.setText(loadday);
//
//            String detail = savedInstanceState.getString("key_detail");
//            typedetail.setText(detail);
//        }
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {              //상호작용하는 인텐트에 대한 액티비티 리절트 메소드
        super.onActivityResult(requestcode, resultcode, data);
        if (requestcode == REQUEST_CODE) {
            if (resultcode == RESULT_OK) {
                adress = data.getData();                        //주소를 받아서

//                String realadress = getPath(adress);

                Log.i("주소1", adress.toString());
//                Log.i("주소2", realadress);


                attachname = getName(adress);
                Log.i("이름", attachname);

                try {
                    addimage.setImageURI(adress);                   //URI를 이미지화 한다.
                } catch (Exception e) {
                }

//                try{
//                    InputStream in = getContentResolver().openInputStream(data.getData());          //데이터를 인풋스트림으로 받아오기
//
//                    Bitmap img = BitmapFactory.decodeStream(in);                        //비트맵 이미지에 스트림 넣기
//                    in.close();                                         //스트림 닫기
//
//                    addimage.setImageBitmap(img);                       //이미지뷰에 비트맵 이미지 넣기
////                    attachnum++;                      //첨부파일 수를 늘려줌
//
//                }catch (Exception e){
//
//                }
            } else if (resultcode == RESULT_CANCELED) {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show();     //취소 되었을 때
            }
        }
    }

    public String getName(Uri uri){
        String [] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

//    public String getPath(Uri uri){                 //파일의 주소를 절대 주소로 만들기 위한 함수
//        int column_index = 0;
//        String [] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if(cursor.moveToFirst()){
//            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        }
//        return cursor.getString(column_index);
//    }



//    @Override
//    protected void onSaveInstanceState(Bundle outState) {               //세이브인스턴스스테이트에 값을 넣는 메소드
//        super.onSaveInstanceState(outState);
//
//        typesubject = findViewById(R.id.typedetail);
//        year = findViewById(R.id.year);
//        month = findViewById(R.id.month);
//        day = findViewById(R.id.day);
//        typedetail = findViewById(R.id.typedetail);
//
//        String subject = typesubject.getText().toString();
//        outState.putString("key_subject", subject);
//
//        String saveyear = year.getText().toString();
//        outState.putString("key_year", saveyear);
//
//        String savemonth = month.getText().toString();
//        outState.putString("key_month", savemonth);
//
//        String saveday = day.getText().toString();
//        outState.putString("key_day", saveday);
//
//        String detail = typedetail.getText().toString();
//        outState.putString("key_detail", detail);
//
//    }

}
