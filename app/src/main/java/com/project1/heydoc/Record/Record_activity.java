package com.project1.heydoc.Record;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.project1.heydoc.Login.LoginedUser;
import com.project1.heydoc.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Record_activity extends AppCompatActivity {

    //AlertDialog show;
    int REQUEST_CODE = 2;
    int REQUEST_CODE_FOR_EDIT = 4;
    Record_Data_Adapter adapter = new Record_Data_Adapter();
    EditText findrecordtext;
    Button findrecordbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);



        RecyclerView recyclerView = findViewById(R.id.record_recyclerview);         //리사이클러뷰 인스턴스화

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);     //레이아웃매니저 만들기
        recyclerView.setLayoutManager(layoutManager);               //리사이클러 뷰에 레이아웃매니저 지정
        //Record_Data_Adapter adapter = new Record_Data_Adapter();        //어뎁터 생성
//        adapter.addItem(new Record_Data("이름", "날짜", "진료과", "내용","첨부물이름"));

        recyclerView.setAdapter(adapter);           //리사이클러 뷰에 어뎁터 지정

        adapter.setOnItemClickListener(new OnRecordItemClickListener() {
            @Override
            public void onItemClick(Record_Data_Adapter.ViewHolder holder, View view, int position) {           //리사이클러뷰 안의 뷰(아이템) 클릭시 실행되는 문장
                Record_Data item = adapter.getItem(position);                   //position의 아이템을 item에 넣음
                String thetime = item.getThistime();

                //Toast.makeText(getApplicationContext(), item.getSubject(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Record_detail_activity.class);      //인텐트로 넘김
                intent.putExtra("ID", thetime);

//                intent.putExtra("record_data", item);           //Record_data가 serializable을 implement해서 넘길 수 있음.
//                intent.putExtra("posi", position);
                startActivityForResult(intent, REQUEST_CODE);
            }

            @Override
            public void onItemLongClick(Record_Data_Adapter.ViewHolder holder, View view, final int position) {           //아이템 뷰홀더를 길게 눌렀을 때 실행되는 문장
                Record_Data item = adapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(Record_activity.this);
                builder.setMessage("이 항목을 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String recordtime = adapter.items.get(position).getThistime();          //해당 기록의 키(ID, 작성날짜)를 가져옴.

                        SharedPreferences records = getSharedPreferences("records", MODE_PRIVATE);
                        SharedPreferences.Editor editor = records.edit();                   //수정을 하기 위해서 에디터 선언
                        String thisuserrecord = records.getString(LoginedUser.id, "");                         //쉐어드프리퍼런스에서 유저가 작성한 게시물을 빼서 스트링에 넣어줌(JSONARRAY형식임)

                        try {
                            JSONArray jsonArray = new JSONArray(thisuserrecord);                //이 유저게시물을 제이슨 어레이로 선언

                            for(int j = 0 ; j<jsonArray.length() ; j++){
                                JSONObject jsonObject = jsonArray.getJSONObject(j);

                                if(jsonObject.getString("thistime").equals(recordtime)){            //제이슨 오브젝트의 기록시간이 이 포지션의 기록시간과 같다면 들어오는 조건문
                                    jsonArray.remove(j);
                                    Log.i("tag", "선택된 오브젝트 삭제됨 / 저장 전");
                                }

                            }

                            editor.putString(LoginedUser.id, String.valueOf(jsonArray));
                            editor.commit();                                                        //수정된 제이슨 어레이를 다시 records에 삽입하고 저장(덮어쓰기)

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        adapter.items.remove(position);                                 //아이템 삭제
                        adapter.notifyItemRangeChanged(position, adapter.items.size());     //아이템 범위 변경에 대한 갱신
                        adapter.notifyDataSetChanged();                                 //아이템 뷰들의 변경에 대한 갱신


                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create().show();

            }
        });

        findrecordtext = findViewById(R.id.findrecordtext);             //필터링 하기 위해 에딭텍스트와 버튼을 선언

        findrecordtext.addTextChangedListener(new TextWatcher() {               //에딭텍스트에 텍스트 와쳐 기능 추가
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);                           //텍스트 값이 들어갈 때마다 입력된 값을 파라미터로 하는 필터 메소드가 실행됨
                Log.i("텍스트체인지", "들어옴");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findrecordbtn = findViewById(R.id.findrecordbtn);
        findrecordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findrecordtext.getText().clear();               //취소 버튼을 누를 때마다 텍스트를 지워준다.
            }
        });


        Button typerecord = findViewById(R.id.add);                     //추가하기 버튼, typerecord 액티비티로 넘어감
        typerecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intyperecord = new Intent(getApplicationContext(), Typerecord_activity.class);
                startActivityForResult(intyperecord, REQUEST_CODE);
                //startActivity(intyperecord);
            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {           //activity for result 에 대한 결과값을 받아올 때 실행되는 메소드
        super.onActivityResult(requestCode, resultCode, data);

//        if(requestCode == REQUEST_CODE){
//            if(resultCode == RESULT_OK){
//                Toast.makeText(Record_activity.this, "등록되었습니다.", Toast.LENGTH_LONG).show();         //아이템을 추가함. 제목. 날짜. 진료과. 내용 순
//
//                adapter.addItem(new Record_Data(data.getStringExtra("subject"), data.getStringExtra("date"), data.getStringExtra("section"),
//                        data.getStringExtra("detail"), data.getStringExtra("attach")));
//
//                Log.i("태그2", data.getStringExtra("attach"));
//
//                adapter.notifyDataSetChanged();             //아이템 변경에 대해 갱신
//
//            }else{
//                Toast.makeText(Record_activity.this, "취소되었습니다", Toast.LENGTH_LONG).show();
//
//            }
//        }
//
//        if(requestCode == REQUEST_CODE_FOR_EDIT){                             //수정 액티비티에서 RESULT_OK 시에 실행되는 구간
//            if(resultCode == RESULT_OK){
//                adapter.setItem(data.getIntExtra("posi", 0), new Record_Data(data.getStringExtra("subject"), data.getStringExtra("date"), data.getStringExtra("section"),
//                        data.getStringExtra("detail"), data.getStringExtra("attach")));
//
//
//                adapter.notifyDataSetChanged();
//            }
//        }

    }

    @Override
    protected void onResume() {                         //pause에서 빈 액티비티 뜬 것을 되돌리기 위해서 선언함.
        super.onResume();

        SharedPreferences records = getSharedPreferences("records", MODE_PRIVATE);
        String thisuserrecord = records.getString(LoginedUser.id, "");

        adapter.items.clear();                          //아이템 리스트 변경에 대해서 갱신하기 위한 클리어 메소드

        Log.i("태그", "아이템 어레이 클리어");

        try {
            JSONArray jsonArray = new JSONArray(thisuserrecord);            //이 유저의 게시물들을 제이슨 어레이에 가져온다.

            for(int i = 0 ; i<jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);             //제이슨 어레이에서 하나의 제이슨 오브젝트를 뽑아서 선언
                String subject = jsonObject.getString("subject");         //오브젝트의 각 속성을 불러와서 스트링에 넣어줌
                String date = jsonObject.getString("date");
                String section = jsonObject.getString("section");
                String detail = jsonObject.getString("detail");
                String attach = jsonObject.getString("attachname");
                String thistime = jsonObject.getString("thistime");

                Log.i("제이슨 어레이에서 뽑아옴", String.valueOf(i)+"번째");
                adapter.addItem(new Record_Data(subject, date, section, detail, "첨부파일 : "+attach, thistime));       //뽑은 스트링으로 리사이클러뷰 한 아이템을 만듦
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();         //데이터셋 바뀐것을 알리기.
//        runprogressbar();
    }

    @Override
    protected void onPause() {                      //액티비티 pause 시에 목록을 가려주기 위해서 빈 액티비티 띄우도록 함.
        super.onPause();
        //setContentView(R.layout.blank);
    }

    @Override
    protected void onStop() {
        super.onStop();
        findrecordtext.getText().clear();               //취소 버튼을 누를 때마다 텍스트를 지워준다.
        //finish();
    }


//    private void runprogressbar(){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(false);
//        builder.setView(R.layout.progressbar);
//        builder.create();
//        show = builder.show();
//        show.dismiss();
//    }

//    private void stopprogressbar(){
//        builder.
//
//    }



}
