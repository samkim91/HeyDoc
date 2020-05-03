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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

public class Record_detail_activity extends AppCompatActivity {

    EditText subject, section, date, detail;
    Spinner spinner;
    Button setdate, findattach;
    Boolean editbtn = true;
    int posi;
    int year, month, day;
    int REQUEST_CODE = 3;
    ImageView image;

    String attachname;
    Uri adress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_detail);

        Intent intent = getIntent();

        final Record_Data data = (Record_Data) intent.getSerializableExtra("record_data");      //인텐트로 값을 순서대로 가져옴

        subject = findViewById(R.id.thesubject);                //xml의 각 값들 매칭 및 인스턴스화
        section = findViewById(R.id.thespinner);
        date = findViewById(R.id.thedate);
        detail = findViewById(R.id.thedetail);
        spinner = findViewById(R.id.spinner_recorddetail);
        setdate = findViewById(R.id.seldate_recorddetail);
        findattach = findViewById(R.id.findattach);
        image = findViewById(R.id.addimage_detail);


        final String record_ID = intent.getStringExtra("ID");                                     //게시물을 찾을 아이디를 전 액티비티에서 받아옴.
        SharedPreferences records = getSharedPreferences("records", MODE_PRIVATE);          //레코드 쉐어드프리퍼런스를 불러옴
        final SharedPreferences.Editor editor = records.edit();                                         //에디터 선언

        final String thisuserrecord = records.getString(LoginedUser.id, "");                     //유저 아이디로 게시물을 찾음.

        try {
            JSONArray jsonArray = new JSONArray(thisuserrecord);                //찾은 게시물 목록을 제이슨 어레이에 담음

            for(int i = 0 ; i<jsonArray.length() ; i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);                 //제이슨 오브젝트 하나하나 찾음

                if(jsonObject.getString("thistime").equals(record_ID)){         //전에 받아온 키(ID)와 일치하는 게 나오면 안에 값들을 불러오게 함.
                    subject.setText(jsonObject.getString("subject"));
                    section.setText(jsonObject.getString("section"));
                    date.setText(jsonObject.getString("date"));
                    detail.setText(jsonObject.getString("detail"));
                    attachname = jsonObject.getString("attachname");
                    Uri getadress = Uri.parse(jsonObject.getString("attach"));
                    adress = getadress;
                    Log.i("조건문 안으로", "정상진입");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }


//        subject.setText(data.getSubject());                 //인텐트에서 받아온 값을 각 항목에 지정
//        section.setText(data.getSection());
//        date.setText(data.getDate());
//        detail.setText(data.getDetail());
//        adress = Uri.parse(data.getAttach());
        try{
            image.setImageURI(adress);              //불러온 주소로 이미지를 만듦
        }catch (Exception e){

        }

        posi = intent.getIntExtra("posi", 0);

        final Button edit = findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = new GregorianCalendar();            //달력 초기화
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                setdate.setOnClickListener(new View.OnClickListener() {             //달력 만들어서 보여주는 문장
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {       //날짜 리스너 선언
                            @Override
                            public void onDateSet(DatePicker datePicker, int selyear, int selmonth, int selday) {
                                year = selyear;
                                month = selmonth;
                                day = selday;
                                date.setText(String.format("%d - %d - %d", year, month+1, day));            //포맷에 맞춰 날짜란에 텍스트 셋팅
                            }
                        };

                        DatePickerDialog datePickerDialog = new DatePickerDialog(Record_detail_activity.this, onDateSetListener, year, month, day);     //날짜 선택 다이얼로그 선언 및 보이기
                        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                        datePickerDialog.show();
                    }
                });

                Button findattach = findViewById(R.id.findattach_detail);         //이미지 버튼 선언

                findattach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);        //이미지들을 다 불러오는 암시적 인텐트
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });


                if(editbtn==true) {                     //editbtn 불린이 참일 때 각종 입력값 활성화

                    ArrayAdapter adapter = ArrayAdapter.createFromResource(Record_detail_activity.this, R.array.진료과, android.R.layout.simple_spinner_item);       //스피너에 값 넣어주기
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);         //스피너 형식
                    spinner.setAdapter(adapter);            //스피너에 어뎁터 반영

                    if (!section.getText().toString().equals("")){                              //스피너 초기값이 널이 아니면, 이전 값을 보여주도록 함.
                        int locationSpinner = adapter.getPosition(section.getText().toString());
                        spinner.setSelection(locationSpinner);
                    }

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {            //스피너 값 선택하면 반영하기
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            section.setText((String)spinner.getItemAtPosition(i));
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            section.setText("");
                        }
                    });

                    subject.setEnabled(true);
                    detail.setEnabled(true);
                    spinner.setVisibility(View.VISIBLE);
                    setdate.setVisibility(View.VISIBLE);
                    findattach.setVisibility(View.VISIBLE);
                    edit.setText("저장");
                    editbtn = false;            //수정으로 넘어갈 수 있게 함.


                }else {                                     //editbtn 불린이 거짓일 때 각종 입력값 비활성화

                    try {
                        JSONArray jsonArray = new JSONArray(thisuserrecord);        //쉐어드프리퍼런스에서 가져온 유저 정보를 제이슨 어레이로 담음
                        Boolean ischanged = false;

                        for(int i = 0 ; i<jsonArray.length() ; i++){                //제이슨 어레이에 있는 제이슨 오브젝트를 훑는 반복문
                            JSONObject jsonObject = jsonArray.getJSONObject(i);     //인덱스로 제이슨 오브젝트 가져옴

                            if(jsonObject.getString("thistime").equals(record_ID)){         //게시물 아이디와 일치하는 시간을 가진 제이슨 오브젝트를 만나면
                                Log.i("선택된 값", String.valueOf(jsonObject));                //안의 값들을 수정값으로 다시 설정해줌
                                jsonObject.put("subject", subject.getText().toString());
                                jsonObject.put("section", section.getText().toString());
                                jsonObject.put("date", date.getText().toString());
                                jsonObject.put("detail", detail.getText().toString());
                                jsonObject.put("attachname", attachname);
                                jsonObject.put("attach", String.valueOf(adress));

                                ischanged = true;                       //제이슨 오브젝트가 수정되었다면 수정되었냐는 불린을 참으로 만듦
                                Log.i("변경되었나?", String.valueOf(jsonObject));
                            }

                        }

                        if(ischanged==true){                                                //수정이 참이니까 수정된 제이슨 어레이를 쉐어드에 저장하려고 들어옴

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

                            Log.i("어레이에 넣음", String.valueOf(sortedJsonArray));
                            editor.putString(LoginedUser.id, String.valueOf(sortedJsonArray));               //쉐어드프리퍼런스에 제이슨 어레이를 다시 넣는다 + 저장한다.

                            editor.commit();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(Record_detail_activity.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
                    subject.setEnabled(false);
                    detail.setEnabled(false);
                    spinner.setVisibility(View.INVISIBLE);
                    setdate.setVisibility(View.INVISIBLE);
                    findattach.setVisibility(View.INVISIBLE);
                    edit.setText("수정");
                    editbtn = true;             //저장으로 넘어갈 수 있게 함.
                }
            }
        });

        Button back = findViewById(R.id.back);                      //"뒤로" 버튼 누를 때 실행되는 문장.. 수정값을 인텐트로 가져다가 목록으로 가져감.
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editbtn==false){                             //수정이 활성화 되어있는데 뒤로 버튼을 눌렀다면 실행되는 것
                    Toast.makeText(Record_detail_activity.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                }else{                                          //수정이 비활성화(저장된 상태)에서 뒤로 버튼을 누르면 실행되는 것
                    Intent intent = new Intent();

//                    intent.putExtra("subject", subject.getText().toString());
//                    intent.putExtra("section", section.getText().toString());
//                    intent.putExtra("date", date.getText().toString());
//                    intent.putExtra("detail", detail.getText().toString());
//                    intent.putExtra("attach", adress.toString());
//                    intent.putExtra("posi", posi);

                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });
    }

    protected void onActivityResult(int requestcode, int resultcode, Intent data){              //상호작용하는 인텐트에 대한 액티비티 리절트 메소드
        if(requestcode == REQUEST_CODE){
            if(resultcode == RESULT_OK){

                adress = data.getData();                        //주소를 받아서

//                String realadress = getPath(adress);

                attachname = getName(adress);
                Log.i("주소1", adress.toString());
//                Log.i("주소2", realadress);

                try{
                    image.setImageURI(adress);                   //URI를 이미지화 한다.
                }catch (Exception e){
                }

            }else if(resultcode == RESULT_CANCELED){
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_LONG).show();     //취소 되었을 때
            }
        }
    }

    public String getName(Uri uri){                                                             //URI 로부터 이름을 얻어오는 메소드
        String [] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
