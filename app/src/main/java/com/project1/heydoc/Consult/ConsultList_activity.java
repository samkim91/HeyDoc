package com.project1.heydoc.Consult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project1.heydoc.Login.LoginedUser;
import com.project1.heydoc.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ConsultList_activity extends AppCompatActivity {
    final ConsultList_Data_Adapter adapter = new ConsultList_Data_Adapter(this);      //어뎁터 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consult_list_activity);

        ImageButton member1 = findViewById(R.id.member1);               //이미지버튼 객체화
        ImageButton consultlist1 = findViewById(R.id.consultlist1);

        int color = Color.parseColor("#1C8ADB");             //이미지 버튼에 입힐 색상 선언
        consultlist1.setBackgroundColor(color);                        //이미지 버튼에 색상 입히기

        member1.setOnClickListener(new View.OnClickListener() {             //상담목록에서  멤버목록으로 넘어가기 위한 버튼 이벤트
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConsultMember_activity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NO_ANIMATION);
//                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.consult_list_recyclerview);       //리사이클러뷰 선언

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);           //리니어레이아웃매니져 선언
        recyclerView.setLayoutManager(linearLayoutManager);                 //리사이클러뷰 레이아웃 매니저에 리니어레이아웃매니저 설정

        recyclerView.setAdapter(adapter);           //리사이클러뷰에 어뎁터 적용

        final ImageView img = findViewById(R.id.personimg);
//        adapter.addItem(new ConsultList_Data(img, "나의사 님과의 대화", "어디가 아프세요?", "22:22"));          //임의값

        adapter.setOnItemClickListener(new OnConsultListClickListener() {               //어뎁터에 아이템 클릭 리스너 설정
            @Override
            public void onItemClick(ConsultList_Data_Adapter.ViewHolder viewHolder, View view, int position) {          //아이템 클릭했을 때 대화방으로 넘어가는 구문
                ConsultList_Data item = adapter.getItem(position);
                String roomid = item.getRoomid();
                String yourid = item.getPersonid();

                Intent intent = new Intent(getApplicationContext(), Consult_activity.class);
                intent.putExtra("roomid", roomid);
                intent.putExtra("yourid", yourid);

                startActivity(intent);
            }

            @Override
            public void onItemLongClick(ConsultList_Data_Adapter.ViewHolder viewHolder, View view, final int position) {      //아이템을 오래 눌렀을 때, 대화방 삭제하는 구문
                ConsultList_Data item = adapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(ConsultList_activity.this);       //알러트다이얼로그 빌더 선언
                builder.setMessage(item.getPersonname()+" 과(와)의 대화방에서 나가시겠습니까?");               //메시지 셋팅
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {             //확인 눌렀을 때 실행되는 구문
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        final String roomid = adapter.items.get(position).getRoomid();                    //선택한 방의 룸 아이디를 받아서 스트링에 넣어둠.(나중에 해당 방을 찾기 위함)
                        String yourid = adapter.items.get(position).getPersonid();                  //대화 상대방의 아이디를 받아서 스트링에 넣어둠
                        String yourname = adapter.items.get(position).getPersonname();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userRooms");         //유저 룸 데이터베이스 레퍼런스를 가져옴

                        final DatabaseReference roomTalkerlistInMyDB = databaseReference.child(LoginedUser.id).child(roomid).child("talkerlist");                       //이 아이디가 가진 유저룸 정보 중에 동일한 아이디를 갖는 방 주소를 가져옴
                        Log.i("태그", "데이터베이스 레퍼런스 가져옴.");

                        roomTalkerlistInMyDB.child("myId").removeValue();                                   //채팅방 참가목록에서 나를 삭제
                        roomTalkerlistInMyDB.child("myName").removeValue();                                   //채팅방 참가목록에서 나를 삭제
                        Log.i("태그", "나의 채팅방 참가 목록에서 나를 삭제함");

                        DatabaseReference roomTalkerlistInyourDB = databaseReference.child(yourid).child(roomid).child("talkerlist");           //상대가 가진 같은 방의 채팅방 목록에서 나를 삭제하는 과정
                        roomTalkerlistInyourDB.child("uId").setValue("");
                        roomTalkerlistInyourDB.child("uName").setValue("");
                        Log.i("태그", "상대방 채팅방 참가 목록에서 나를 삭제함");

//                        roomTalkerlistInMyDB.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if(dataSnapshot.child("uId").getValue().toString().equals("")){
//                                    Log.i("태그", "채팅방 참가 목록이 비어있다면 들어오는 조건문");
//                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("messages").child(roomid);
//                                    databaseReference1.removeValue();
//                                    Log.i("태그", "방의 인원이 모두 나갔으므로, 이 메시지들은 모두 삭제한다.");
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });

                        DatabaseReference roomInMyDB = databaseReference.child(LoginedUser.id).child(roomid);
                        roomInMyDB.removeValue();
                        Log.i("태그", "내 채팅방 목록에서 이 채팅방을 완전히 삭제한다.");

                        adapter.items.remove(position);
                        adapter.notifyDataSetChanged();

                        onResume();

                        //파이어베이스 구현으로 쉐어드프리퍼런스 내용은 모두 주석처리함
//                        SharedPreferences userRooms = getSharedPreferences("userRooms", MODE_PRIVATE);          //쉐어드 프리퍼런스를 선언
//                        SharedPreferences.Editor editor = userRooms.edit();                                         //수정을 위해 에디터 기능 선언
//
//                        String myroominforaw = userRooms.getString(LoginedUser.id, "");                        //이 유저의 jsonArray 형태 채팅방 목록을 가져옴.
//
//                        try {
//                            JSONArray jsonArray = new JSONArray(myroominforaw);                 //제이슨 어레이 형식으로 되어 있기 때문에 어레이에 넣음
//
//                            Log.i("태그", "제이슨 어레이에 담음");
//
//                            for(int j = 0 ; j<jsonArray.length() ; j++){                                        //제이슨 어레이에 있는 제이슨 오브젝트를 하나씩 가져옴
//                                JSONObject jsonObject = jsonArray.getJSONObject(j);
//                                Log.i("태그", "제이슨 어레이에서 오브젝트를 하나씩 빼서 담는 반복문");
//
//                                String getroomid = jsonObject.getString("roomId");                              //하나의 오브젝트에서 룸 아이디를 찾아서 스트링에 담아둠.
//
//                                if(getroomid.equals(roomid)){                                               //만약 불러온 룸 아이디가 클릭한 룸 아이디와 같다면 들어오는 조건문
//
//                                    //Todo 나중에는 룸의 멤버리스트에 아무도 없으면 메시지 쉐어드프리퍼런스를 불러와서 내용을 다 삭제해줘야함.
////                                    String talkerlist = jsonObject.getString("talkerlist");
////                                    JSONArray jsonArray1 = new JSONArray(talkerlist);
////                                    if(jsonArray1.equals("")){
////
////                                    }
//
//                                    jsonArray.remove(j);                                                    //해당 오브젝트를 어레이에서 삭제함.
//                                    Log.i("태그", "룸 아이디가 일치하는 제이슨을 삭제까지 함");
//
//
//                                }
//                            }
//
//                            editor.putString(LoginedUser.id, String.valueOf(jsonArray));               //삭제된 제이슨 어레이를 다시 이 유저의 쉐어드 프리퍼런스에 넣음
//                            Log.i("태그", "변경된 제이슨 어레이를 이 유저 채팅방 목록에 저장");
//                            editor.commit();                                            //저장
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        String yourroominforaw = userRooms.getString(yourid, "");                           //대화 상대방의 채팅방 목록(제이슨 어레이 형태)을 가져와 스트링에 넣음
//
//                        try {
//
//                            JSONArray jsonArray = new JSONArray(yourroominforaw);                           //제이슨 어레이 형태 스트링을 제이슨 어레이에 넣음
//                            Log.i("태그", "제이슨 어레이에 담음");
//
//                            for(int j = 0 ; j < jsonArray.length() ; j++){
//                                JSONObject jsonObject = jsonArray.getJSONObject(j);                             //제이슨 어레이에서 제이슨 오브젝트를 하나씩 빼옴
//                                Log.i("태그", "제이슨 어레이에서 오브젝트를 하나씩 빼서 담는 반복문");
//
//                                String getroomid = jsonObject.getString("roomId");                              //이 오브젝트에서 룸 아이디를 불러서 스트링에 넣음
//
//                                if(getroomid.equals(roomid)){
//                                    Log.i("태그", "선택한 룸 아이디와 제이슨에서 가져온 룸 아이디가 같다면 들어옴");
//                                    String talkerlist = jsonObject.getString("talkerlist");              //가져온 오브젝트의 룸 아이디가 선택된 룸 아이디와 같으면 들어오는 조건문
//                                    JSONArray jsonArray1 = new JSONArray(talkerlist);                       //토커리스트 제이슨 어레이를 불러옴.
//
//                                    for(int k = 0 ; k<jsonArray1.length() ; k++){
//                                        Log.i("태그", "토커리스트에서 해당 아이디를 찾기 위한 포문");
//                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(k);                       //토커리스트 제이슨 어레이 중 하나의 오브젝트를 가져옴.
//                                        String getuserid = jsonObject1.getString("uId");                        //이 오브젝트의 유저아이디를 가져와서 스트링에 넣음
//                                        if(getuserid.equals(LoginedUser.id)){                                      //이 오브젝트의 유저 아이다가 이 유저의 아이디와 같으면 들어오는 조건문
//                                            Log.i("태그", "토커리스트에서 해당 아이디를 찾고 나서 삭제함");
//                                            jsonArray1.remove(k);                                   //이 유저 오브젝트를 토커리스트에서 삭제
//
//                                        }
//                                    }
//                                    jsonObject.put("talkerlist", jsonArray1);                   //토커리스트를 다시 제이슨에 넣음.
//                                    Log.i("태그", "수정된 토커리스트를 다시 제이슨 오브젝트에 키값을 통해 넣어줌");
//                                }
//
//                            }
//
//                            editor.putString(yourid, String.valueOf(jsonArray));                                //상대 아이디로 된 쉐어드프리퍼런스에 수정된 제이슨 어레이를 넣음
//                            editor.commit();                                        //저장
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }

//                        adapter.items.remove(position);                                 //아이템 삭제
//                        adapter.notifyItemRangeChanged(position, adapter.items.size());     //아이템 범위 변경에 대한 갱신
//                        adapter.notifyDataSetChanged();                                 //아이템 뷰들의 변경에 대한 갱신

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {             //취소 눌렀을 때 실행되는 구문
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create().show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        final ArrayList<ConsultList_Data> arrayList = new ArrayList<>();          //정렬을 위해 선언한 어레이 리스트
        final ArrayList<ConsultList_Data> sortedarrayList = new ArrayList<>();        // 정렬된 어레이 리스트

        Log.i("태그", "온 리줌");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userRooms").child(LoginedUser.id);
        Log.i("태그", "데이터베이스 레퍼런스를 가져옴");

        databaseReference.orderByChild("lastMsgTime").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("태그", "온차일드어디드");
                loadChatList(dataSnapshot, arrayList);
                sort(arrayList, sortedarrayList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                String lastMsg = dataSnapshot.child("lastMsg").getValue().toString();
                String lastMsgTime = dataSnapshot.child("lastMsgTime").getValue().toString();
                Log.i("태그", "데이터스냅샷에서 데이터 가져오고"+key+lastMsg+lastMsgTime);

                for(int i = 0 ; i<adapter.items.size() ; i++){
                    Log.i("태그", "같은 아이디 찾기 위한 포문, 어뎁터 사이즈"+adapter.items.size());
                    if(adapter.items.get(i).getRoomid().equals(key)){
                        Log.i("태그", "아이디 찾았으면 어레이 리스트 값 바꿔줌.키는 :"+arrayList.get(i).getRoomid());
                        adapter.items.get(i).setShowmessage(lastMsg);
                        adapter.items.get(i).setLastmsgtime(lastMsgTime);
                        adapter.notifyItemChanged(i);
                        Log.i("태그", "데이터 체인지까지 완료");
                    }
                }

//                arrayList.clear();
//                sortedarrayList.clear();
//                Log.i("태그", "어레이리스트들 클리어했고");
//
//                Log.i("태그", "어뎁터 사이즈"+adapter.items.size());
//                arrayList.addAll(adapter.items);
//                adapter.items.clear();
//                Log.i("태그", "어레이리스트 사이즈"+arrayList.size());
//
//                String key = dataSnapshot.getKey();
//                String lastMsg = dataSnapshot.child("lastMsg").getValue().toString();
//                String lastMsgTime = dataSnapshot.child("lastMsgTime").getValue().toString();
//
//                Log.i("태그", "데이터스냅샷에서 데이터 가져오고"+key+lastMsg+lastMsgTime);
//
//                for(int i = 0 ; i<arrayList.size() ; i++){
//                    Log.i("태그", "같은 아이디 찾기 위한 포문");
//                    if(arrayList.get(i).getRoomid().equals(key)){
//                        Log.i("태그", "아이디 찾았으면 어레이 리스트 값 바꿔줌.키는 :"+arrayList.get(i).getRoomid());
//                        arrayList.get(i).setShowmessage(lastMsg);
//                        arrayList.get(i).setLastmsgtime(lastMsgTime);
//                    }
//                }
//                sort(arrayList, sortedarrayList);

//                loadChatList(dataSnapshot);
//                arrayList.clear();
//                sortedarrayList.clear();
//                Log.i("태그", "어레이리스트들 클리어했고");
//                arrayList.addAll(adapter.items);
//                Log.i("태그", "어뎁터 아이템들을 초기 어레이리스트에 넣음");
//                String key = dataSnapshot.getKey();
//                String lastMsg = dataSnapshot.child("lastMsg").getValue().toString();
//                String lastMsgTime = dataSnapshot.child("lastMsgTime").getValue().toString();
//
//                Log.i("태그", "데이터스냅샷에서 데이터 가져오고"+key+lastMsg+lastMsgTime);
//
//                for(int i = 0 ; i<arrayList.size() ; i++){
//                    Log.i("태그", "같은 아이디 찾기 위한 포문");
//                    if(arrayList.get(i).getRoomid().equals(key)){
//                        Log.i("태그", "아이디 찾았으면 어레이 리스트 값 바꿔줌");
//                        arrayList.get(i).setShowmessage(lastMsg);
//                        arrayList.get(i).setLastmsgtime(lastMsgTime);
//                    }
//                }
//
//                adapter.items.clear();
//                Log.i("태그", "어뎁터 비워주고");
//
//                Collections.sort(arrayList, new Comparator<ConsultList_Data>() {                    //정렬 기능을 호출함
//
//                    @Override
//                    public int compare(ConsultList_Data consultList_data, ConsultList_Data consultList_data1) {         //비교하는 메소드를 실행
//                        Log.i("태그", "정렬하기 위해 비교하는 메소드 진입");
//                        String date1 = consultList_data.getLastmsgtime();           //마지막 메시지 시간 값을 가져옴
//                        String date2 = consultList_data1.getLastmsgtime();
//
//                        return date1.compareTo(date2);                      //1과 2를 비교하여 정수를 반환
//                    }
//                });
//                Collections.reverse(arrayList);                                 //내림차순으로 정렬하기 위한 메소드
//                Log.i("태그", "반대로!!");
//
//                for(int i = 0 ; i<arrayList.size() ; i++){
//                    sortedarrayList.add(arrayList.get(i));                  //정렬된 리스트를 어레이 리스트에 담아줌
//                }
//                Log.i("태그", "재정렬까지 마쳤고 이제 통째로 어뎁터에 넣음");
//                adapter.setItems(sortedarrayList);                              //어뎁터의 아이템 전체 셋팅하는 메소드로 넣음
//                adapter.notifyDataSetChanged();             //리사이클러뷰 갱신

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                adapter.items.clear();          //새로 띄우기 전에 어뎁터 아이템들을 모두 지우기
//                Log.i("태그", "온 데이터 체인지");
//                for(DataSnapshot item : dataSnapshot.getChildren()){
//                    Log.i("태그", "아이템을 뽑기 위한 포문 진입");
//
//                    Log.i("태그", "");
//                    String roomkey = item.getKey();
//                    Log.i("태그", "키 값"+roomkey);
//                    String name = item.child("talkerlist").child("uName").getValue().toString();
//                    String id =  item.child("talkerlist").child("uId").getValue().toString();
//                    String lastMsg =  item.child("lastMsg").getValue().toString();
//                    String lastMsgTime = item.child("lastMsgTime").getValue().toString();
//
//                    Log.i("태그", "뽑아온 값"+name+"/"+id+"/"+lastMsg+"/"+lastMsgTime);
//
//                    arrayList.add(new ConsultList_Data(img, name, id, lastMsg, lastMsgTime, roomkey));
//                    Log.i("태그", "어레이리스트에 추가");
//
//                }

//                Collections.sort(arrayList, new Comparator<ConsultList_Data>() {                    //정렬 기능을 호출함
//
//                    @Override
//                    public int compare(ConsultList_Data consultList_data, ConsultList_Data consultList_data1) {         //비교하는 메소드를 실행
//                        Log.i("태그", "정렬하기 위해 비교하는 메소드 진입");
//                        String date1 = consultList_data.getLastmsgtime();           //마지막 메시지 시간 값을 가져옴
//                        String date2 = consultList_data1.getLastmsgtime();
//
//                        return date1.compareTo(date2);                      //1과 2를 비교하여 정수를 반환
//                    }
//                });
//                Collections.reverse(arrayList);                                 //내림차순으로 정렬하기 위한 메소드
//                Log.i("태그", "반대로!!");
//
//                for(int i = 0 ; i<arrayList.size() ; i++){
//                    sortedarrayList.add(arrayList.get(i));                  //정렬된 리스트를 어레이 리스트에 담아줌
//                }
//                Log.i("태그", "재정렬까지 마쳤고 이제 통째로 어뎁터에 넣음");
//                adapter.setItems(sortedarrayList);                              //어뎁터의 아이템 전체 셋팅하는 메소드로 넣음
//                adapter.notifyDataSetChanged();             //리사이클러뷰 갱신
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.i("태그", "취소되었을 때 나오는 태그");
//            }
//        });

        //파이어베이스 사용으로 이 아래부터 쉐어드프리퍼런스로 구현했던 것은 주석처리함.
//        SharedPreferences userRooms = getSharedPreferences("userRooms", MODE_PRIVATE);          //유저룸 쉐어드에 접근
//        String thisUserRooms = userRooms.getString(LoginedUser.id, "");                            //이 유저가 갖고 있는 채팅방을 불러옴(아직 raw 스트링)
//
//
//
//        try {
//            JSONArray jsonArray = new JSONArray(thisUserRooms);             //이 유저의 채팅방들을 제이슨 어레이에 넣어줌(형 변환을 위함)
//
//            for(int i = 0 ; i<jsonArray.length() ; i++){
//                JSONObject jsonObject = jsonArray.getJSONObject(i);                 //제이슨 어레이에 있는 제이슨 오브젝트를 하나씩 뽑아줌
//
//                String uName = "";                          //아이디와 이름을 담을 스트링 선언
//                String uId = "";
//                JSONArray jsonArray1 = jsonObject.getJSONArray("talkerlist");       //이 제이슨 오브젝트에서 채팅방 안 멤버들을 담고 있는 제이슨 어레이를 뽑는 과정
//                for (int j = 0 ; j<jsonArray1.length() ; j++){
//                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
//
//                    if(!jsonObject1.getString("uId").equals(LoginedUser.id)){              //이 유저가 아닌 조건을 걸러서 uName에 저장함
//                        uId = jsonObject1.getString("uId");
//                        uName = jsonObject1.getString("uName");
//                    }
//                }
//
//
////                String personImg = "";                //Todo 이미지 Uri 받는거 해야함.
//                String personName = uName;                                                      //각각 이름을 가져옴.
//                String personId = uId;
//                String showMsg = jsonObject.getString("lastMsg");
//                String lastMsgTime = jsonObject.getString("lastMsgTime");
//                String roomId = jsonObject.getString("roomId");
//
//                arrayList.add(new ConsultList_Data(img, personName+" 님과의 대화", personId, showMsg, lastMsgTime, roomId));       //리사이클러뷰 어레이리스트에 추가
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        //여기서부터 정렬을 할 것이다------------------------------------------


    }

    public void loadChatList(DataSnapshot dataSnapshot, ArrayList<ConsultList_Data> arrayList){
        final ImageView img = findViewById(R.id.memberimg);                               //더미 이미지를 보이기 위한 작업
        adapter.items.clear();          //새로 띄우기 전에 어뎁터 아이템들을 모두 지우기

        Log.i("태그", "어뎁터 클리어");

        String roomkey = dataSnapshot.getKey();
        Log.i("태그", "키 값"+roomkey);

        String imgUri = dataSnapshot.child("talkerlist").child("uImgUri").getValue().toString();
        String name = dataSnapshot.child("talkerlist").child("uName").getValue().toString();
        String id =  dataSnapshot.child("talkerlist").child("uId").getValue().toString();
        String lastMsg =  dataSnapshot.child("lastMsg").getValue().toString();
        String lastMsgTime = dataSnapshot.child("lastMsgTime").getValue().toString();

        Log.i("태그", "뽑아온 값"+name+"/"+id+"/"+lastMsg+"/"+lastMsgTime);

        arrayList.add(new ConsultList_Data(imgUri, name, id, lastMsg, lastMsgTime, roomkey));
        Log.i("태그", "어레이리스트에 추가");

    }

    public void sort(ArrayList<ConsultList_Data> arrayList, ArrayList<ConsultList_Data> sortedarrayList){
        Log.i("태그", "바꿔줄 어레이리스트 사이즈 :"+arrayList.size());
        Log.i("태그", "바꿀 어레이리스트 사이즈 :"+sortedarrayList.size());

        Collections.sort(arrayList, new Comparator<ConsultList_Data>() {                    //정렬 기능을 호출함

            @Override
            public int compare(ConsultList_Data consultList_data, ConsultList_Data consultList_data1) {         //비교하는 메소드를 실행
                Log.i("태그", "정렬하기 위해 비교하는 메소드 진입");
                String date1 = consultList_data.getLastmsgtime();           //마지막 메시지 시간 값을 가져옴
                String date2 = consultList_data1.getLastmsgtime();

                return date1.compareTo(date2);                      //1과 2를 비교하여 정수를 반환
            }
        });
        Collections.reverse(arrayList);                                 //내림차순으로 정렬하기 위한 메소드
        Log.i("태그", "반대로!!");

        for(int i = 0 ; i<arrayList.size() ; i++){
            sortedarrayList.add(arrayList.get(i));                  //정렬된 리스트를 어레이 리스트에 담아줌
        }
        Log.i("태그", "재정렬까지 마쳤고 이제 통째로 어뎁터에 넣음");
        adapter.setItems(sortedarrayList);                              //어뎁터의 아이템 전체 셋팅하는 메소드로 넣음
        adapter.notifyDataSetChanged();             //리사이클러뷰 갱신
    }
}
