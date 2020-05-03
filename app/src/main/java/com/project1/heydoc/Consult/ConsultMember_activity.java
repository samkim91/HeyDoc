package com.project1.heydoc.Consult;

import androidx.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project1.heydoc.Login.LoginedUser;
import com.project1.heydoc.R;
import com.project1.heydoc.Login.User;
import com.project1.heydoc.Showinfo_activity;

import java.util.HashMap;
import java.util.Map;

public class ConsultMember_activity extends AppCompatActivity {

    ConsultMember_Data_Adapter adapter = new ConsultMember_Data_Adapter(this);
    User me, you;
    Boolean nothing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consult_member_activity);

        ImageButton member = findViewById(R.id.member);               //사용할 버튼들 객체화
        ImageButton consultlist = findViewById(R.id.consultlist);
        final Button addmember = findViewById(R.id.add_member);


        RecyclerView recyclerView = findViewById(R.id.consult_member_recyclerview);     //리사이클러뷰 객체화

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);     //리사이클러뷰를 위한 레이아웃 매니저 선언
        recyclerView.setLayoutManager(layoutManager);           //리사이클러뷰에 레이아웃 매니저 설정

        recyclerView.setAdapter(adapter);               //리사이클러뷰 어뎁터 설정

//        final ImageView img = findViewById(R.id.memberimg);
//        adapter.addItem(new ConsultMember_Data(img, "나의사", "testdoc"));


        adapter.setOnItemClickListener(new OnConsultMemberClickListener() {             //아이템 뷰홀더 클릭했을 때 나오는 설정
            @Override
            public void onItemClick(ConsultMember_Data_Adapter.ViewHolder viewHolder, View view, int position) {
                ConsultMember_Data item = adapter.getItem(position);

                dialog(item);
            }

            @Override
            public void onItemLongClick(ConsultMember_Data_Adapter.ViewHolder viewHolder, View view, int position) {            //길게 눌렀을 때 실행되는 메소드
                ConsultMember_Data item = adapter.getItem(position);
                dialoglongclk(item, position);
            }
        });

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    if(item.getKey().equals(LoginedUser.id)){
                        me = item.getValue(User.class);                                             //파이어베이스 내 유저목록이 있는데 여기서 로그인 아이디와 일치하는 객체를 불러와 User 클래스의 me 객체에 넣어준다.
                    }                                                                               //이 작업은 다른 곳에서 사용할 때 매번 파이어베이스에서 불러오지 않고 이 객체에서 get 함수를 사용하기 위함이다.

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        addmember.setOnClickListener(new View.OnClickListener() {               //의사 목록에 아이디 추가하는 버튼
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConsultMember_activity.this);     //다이얼로그를 띄운다
                builder.setMessage("추가할 대상의 아이디를 입력하세요.");

                final EditText typeid = new EditText(ConsultMember_activity.this);          //다이얼로그에 에딭텍스트를 선언한다
                builder.setView(typeid);

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {         //확인버튼을 선언하고 누르면 실행되는 기능을 정의한다.
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String typedid = typeid.getText().toString();                                           //에딭텍스트에 입력된 값을 스트링화

                        //Todo.. 파이어베이스 사용으로 쉐어드프리퍼런스는 주석처리
//                        SharedPreferences userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);        //입력된 아이디를 찾기 위해 쉐어드 프리퍼런스 선언
//
//                        Map<String, ?> total = userinfo.getAll();                   //쉐어드프리퍼런스에 있는 값을 다 Map에 넣어줌. 검색을 위함

                        if(typedid.equals("")){
                            Toast.makeText(ConsultMember_activity.this, "찾으실 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();     //널값 방지
                        }else{

                            if(LoginedUser.id.equals(typedid)){
                                Toast.makeText(ConsultMember_activity.this, "본인은 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }else{

                                Boolean existid = false;

                                for(int j = 0 ; j<adapter.items.size() ; j++) {                 //현재 내 의사목록을 먼저 모두 확인한다.
                                    String id = adapter.items.get(j).getMemberid();
                                    Log.i("아이템의 ", id);
                                    Log.i("몇번째 아이템?", String.valueOf(j));

                                    if (id.equals(typedid)) {                                       //의사목록에 찾는 아이디랑 일치하는 사람이 있으면 불린을 트루로 바꿔줌으로 중복을 방지한다.
                                        existid = true;
                                        Log.i("태그1", "찾는 아이디가 이미 목록에 있네");
                                        Toast.makeText(ConsultMember_activity.this, "이미 등록된 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                if(existid==false){                         //입력된 아이디가 현재 의사목록에 없을 경우 쉐어드프리퍼런스로 접근하여 추가한다.
                                    Log.i("태그", "내 친구목록에 이미 등록된 친구가 아닐 경우 들어오는 조건문");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            nothing = true;                                     //검색에 대한 결과를 나타내기 위한 불린값
                                            Log.i("태그", "온 데이터 체인지 메소드 진입");
                                            for(DataSnapshot item : dataSnapshot.getChildren()){
                                                Log.i("태그", "친구 찾는 반복문!");
                                                if(item.getKey().equals(typedid)){
                                                    Log.i("태그", "찾는 아이디가 파베에 있으면 들어옴");
                                                    nothing = false;
                                                    you = item.getValue(User.class);

                                                    final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("friendlist");
                                                    Log.i("태그", "친구리스트를 참조하는 객체를 하나 만듦");

//                                                    Log.i("태그", "아이템을 추가하기 위한 온 데이터 체인지");

                                                    //추가할 상대에 대한 정보를 담은 유저 객체를 만듦.
                                                    User addyou = new User(you.getId(), you.getName(), you.getPhonenum(), you.getBirthday(), you.getEmail(), you.getImageUri());
                                                    Log.i("태그", "친구이미지"+you.getImageUri());
                                                    //이 객체를 맵 형태로 바꿔줌.
                                                    Map<String, Object> yourvalue = addyou.toMap1();

                                                    //이 아래는 상대 친구리스트에도 나를 추가해야할 때 쓰는 구문...
//                                                            User addme = new User(me.getId(), me.getName(), me.getPhonenum(), me.getBirthday(), me.getEmail(), "myuri");
//                                                            Map<String, Object> myvalue = addme.toMap1();

                                                    //업데이트 할 차일드 맵을 만듦
                                                    Map<String, Object> childupdate = new HashMap<>();
//                                                            childupdate.put(you.getId()+"/"+me.getId(), myvalue);
                                                    childupdate.put(me.getId()+"/"+you.getId(), yourvalue);         //내 아이디를 키로 하는 리스트에 상대를 추가함.

                                                    databaseReference1.updateChildren(childupdate);

                                                    Toast.makeText(ConsultMember_activity.this, you.getId()+" 님이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                                    Log.i("추가된 아이디", you.getId());
                                                    onResume();


                                                    //굳이 리스너를 만들어서 추가할 필요가 없어서 주석처리함.
//                                                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            Log.i("태그", "아이템을 추가하기 위한 온 데이터 체인지");
//                                                            User addyou = new User(you.getId(), you.getName(), you.getPhonenum(), you.getBirthday(), you.getEmail(), "youruri");
//                                                            Map<String, Object> yourvalue = addyou.toMap1();
//
//                                                            //이 아래는 상대 친구리스트에도 나를 추가해야할 때 쓰는 구문...
////                                                            User addme = new User(me.getId(), me.getName(), me.getPhonenum(), me.getBirthday(), me.getEmail(), "myuri");
////                                                            Map<String, Object> myvalue = addme.toMap1();
//
//                                                            Map<String, Object> childupdate = new HashMap<>();
////                                                            childupdate.put(you.getId()+"/"+me.getId(), myvalue);
//                                                            childupdate.put(me.getId()+"/"+you.getId(), yourvalue);
//
//                                                            databaseReference1.updateChildren(childupdate);
//
//                                                            Toast.makeText(ConsultMember_activity.this, you.getId()+" 님이 추가되었습니다.", Toast.LENGTH_SHORT).show();
//                                                            Log.i("추가된 아이디", you.getId());
//                                                            onResume();
//
//
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });
                                                }
                                            }
                                            Log.i("태그", "친구 찾는 반복문 벗어남");
                                            if(nothing==true){
                                                Log.i("태그", "찾는 아이디가 없다는 안내");
                                                Toast.makeText(ConsultMember_activity.this, "찾으시는 아이디가 없습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                    });

                                    //Todo...파이어베이스 사용으로 쉐어드프리퍼런스 부분은 주석처리함

//                                    for(Map.Entry<String, ?> entry : total.entrySet()){          //Map entry를 반복
//                                        String userinfostr = entry.getValue().toString();           //한 엔트리를 스트링화
//                                        String [] userinfoarray = userinfostr.split("@#@");     //스트링 구분
//                                        Log.i("태그1", "중복검사 넘기고 쉐어드프리퍼런스 안");
//
//                                        if(userinfoarray[0].equals(typedid)) {                   //입력된 값과 아이디가 같으면 들어오는 조건문
//                                            Log.i("태그1", "같은 아이디를 찾았으면 오는 조건문");
//
//                                            SharedPreferences friendlist = getSharedPreferences("friendlist", MODE_PRIVATE);        //친구리스트의 쉐어드프리퍼런스를 연다
//                                            SharedPreferences.Editor editor = friendlist.edit();                    //수정하기 위한 에디터 기능을 준다.
//
//                                            String friendinfo = "{"+                                        //추가할 친구에 대한 정보
//                                                    "\"uid\":"+"\""+userinfoarray[0]+"\","+
//                                                    "\"uname\":"+"\""+userinfoarray[2]+"\","+
//                                                    "\"uimageUri\":"+"\"uri\"}";
//
//                                            String getfriendlist = friendlist.getString(LoginedUser.id, "");               //친구리스트를 한번 받아와본다.
//
//                                            if(!getfriendlist.equals("")){                                          //친구리스트가 비어있지 않다면 들어오는 조건문
//                                                Log.i("태그1", "친구리스트가 비어있지 않다면?");
//                                                try {
//                                                    JSONObject jsonObject = new JSONObject(friendinfo);             //추가할 대상에 대한 정보를 오브젝트화 해서
//
//                                                    JSONArray jsonArray = new JSONArray(getfriendlist);             //제이슨 어레이에 넣어준다.
//                                                    jsonArray.put(jsonObject);
//
//                                                    //여기서부터 정렬 기능을 넣을 것이다--------------------------------
//
//                                                    JSONArray sortedjsonArray = new JSONArray();            //정렬된 제이슨 어레이를 담을 곳 선언
//
//                                                    ArrayList<JSONObject> arrayList = new ArrayList<>();                    //정렬할 제이슨 오브젝트를 담을 어레이리스트를 선언
//                                                    Log.i("태그1", "정렬할 어레이리스트를 만들었다.");
//
//
//                                                    for(int j = 0 ; j<jsonArray.length() ; j++){
//                                                        arrayList.add(jsonArray.getJSONObject(j));                      //제이슨 오브젝트를 가져와서 어레이 리스트에 담는다
//                                                        Log.i("태그1", "제이슨 어레이 길이만큼 어레이 리스트에 넣어줬다."+j);
//                                                    }
//
//                                                    Collections.sort(arrayList, new Comparator<JSONObject>() {                      //정렬 기능을 호출
//                                                        @Override
//                                                        public int compare(JSONObject jsonObject, JSONObject jsonObject1) {         //정렬하는 함수.. 비교값에 따라 정수를 반환
//                                                            Log.i("태그1", "비교하는 구문");
//                                                            String name1 = new String();                    //정렬할 기준 값을 받을 스트링(여기선 이름)
//                                                            String name2 = new String();
//
//                                                            try {
//                                                                Log.i("태그1", "트라이");
//                                                                name1 = jsonObject.getString("uname");              //오브젝트에서 이름을 가져옴
//                                                                name2 = jsonObject1.getString("uname");
//
//                                                            } catch (JSONException e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                            Log.i("태그1", "리턴"+name1.compareTo(name2));
//                                                            return name1.compareTo(name2);              //비교하고 우위에 따라 정수를 반환!
//                                                        }
//                                                    });
//
//                                                    for(int k = 0 ; k<arrayList.size() ; k++ ){                                     //어레이 리스트에서 하나씩 빼와서 제이슨 리스트에 다시 담는다.
//                                                        sortedjsonArray.put(arrayList.get(k));
//                                                        Log.i("태그1", "정렬된 제이슨 어레이에 값을 넣어준다"+k);
//                                                    }
//
//                                                    editor.putString(LoginedUser.id, String.valueOf(sortedjsonArray));           //제이슨 어레이를 다시 쉐어드프리퍼런스에 넣어준다.
//                                                    Log.i("태그1", "저장했다"+sortedjsonArray);
//
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//
//                                            }else{
//                                                Log.i("태그1", "친구리스트가 비어있다면 여기로");             //기존 친구리스트가 없다면 추가할 대상을 제이슨 오브젝트형식으로 쉐어드프리퍼런스에 넣어준다.
//                                                editor.putString(LoginedUser.id, "["+friendinfo+"]");
//                                            }
//
//                                            editor.commit();            //쉐어드 프리퍼런스 저장
//
//
//                                            Toast.makeText(ConsultMember_activity.this, userinfoarray[2]+"님이 추가되었습니다.", Toast.LENGTH_SHORT).show();
//                                            Log.i("추가된 아이디", userinfoarray[0]);
////                                            adapter.addItem(new ConsultMember_Data(img, userinfoarray[2], userinfoarray[0]));      //추가
//                                            onResume();
//                                            nothing = false;
////                                            break outter;
//                                        }

                                }else{
                                    Toast.makeText(ConsultMember_activity.this, "이미 추가된 아이디입니다.", Toast.LENGTH_SHORT).show();
                                }
                            }

//                            Log.i("아이템의 ", "포문 들어가기 전");
//                            for(int j = 0 ; j<adapter.items.size() ; j++){
//                                String id = adapter.items.get(j).getMemberid();
//                                Log.i("아이템의 ", id);
//                                Log.i("몇번째 아이템?", String.valueOf(j));
//                                if(id.equals(typedid)){
//                                    Toast.makeText(ConsultMember_activity.this, "이미 추가된 아이디입니다.", Toast.LENGTH_SHORT).show();
//                                    Log.i("태그", "중복검사에서 걸림");
////                                    break;
//                                }else{
//                                    Boolean nothing = true;                                     //검색에 대한 결과를 나타내기 위한 불린값
//                                    for(Map.Entry<String, ?> entry : total.entrySet()){          //Map entry를 반복
//                                        String userinfostr = entry.getValue().toString();           //한 엔트리를 스트링화
//                                        String [] userinfoarray = userinfostr.split("@#@");     //스트링 구분
//                                        Log.i("태그1", "중복검사 넘기고 쉐어드프리퍼런스 안");
//
//                                        if(userinfoarray[0].equals(typedid)) {                   //입력된 값과 아이디가 같으면 들어오는 조건문
//                                            adapter.addItem(new ConsultMember_Data(img, userinfoarray[2], userinfoarray[0]));      //추가
//                                            Log.i("추가된 아이디", userinfoarray[0]);
//                                            adapter.notifyItemRangeChanged(j, adapter.items.size());
//                                            adapter.notifyDataSetChanged();
//                                            nothing = false;
////                                            break outter;
//                                        }
//                                    }
//                                    if(nothing==true){
//                                        Toast.makeText(ConsultMember_activity.this, "찾으시는 아이디가 없습니다.", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                                Log.i("태그", "포문의 끝");
//                            }
                        }
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



        int color = Color.parseColor("#1C8ADB");             //이미지 버튼에 입힐 색상 선언
        member.setBackgroundColor(color);                        //멤버 이미지 버튼에 색상 입히기

        consultlist.setOnClickListener(new View.OnClickListener() {             //의사목록에서 상담목록으로 넘어가기 위한 버튼 이벤트
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConsultList_activity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NO_ANIMATION);
//                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });



    }

    public void dialog(final ConsultMember_Data item){                                    //리사이클러뷰에서 멤버를 클릭했을 때 다이얼로그가 뜨면서 대화를 할지 안할지 정할 수 있게 하는 메소드
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(item.getMembername()+" 과(와) 상담하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Intent intent = new Intent(getApplicationContext(), Consult_activity.class);

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userRooms");     //유저룸의 레퍼런스를 가져옴.

//                SharedPreferences chkuserRooms = getSharedPreferences("userRooms", MODE_PRIVATE);
//                String myrooms = chkuserRooms.getString(LoginedUser.id, "");

                DatabaseReference databaseReference1 = databaseReference.child(LoginedUser.id);
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean existroom = false;

                        for(DataSnapshot itemD : dataSnapshot.getChildren()){
                            String key = itemD.getKey();
                            String myId = itemD.child("talkerlist").child("myId").getValue().toString();
                            String uId = itemD.child("talkerlist").child("uId").getValue().toString();
                            Log.i("태그", "가져온 값"+key+"/"+myId+"/"+uId);

                            if(myId.equals(LoginedUser.id)&&uId.equals(item.getMemberid())){
                                Log.i("태그", "일치하는 방이 있어서 이 방으로 가는 조건문이다."+key+"/"+item.getMemberid());
                                intent.putExtra("roomid", key);
                                intent.putExtra("yourid", item.getMemberid());

                                startActivity(intent);              //룸 아이디, 상대 아이디를 가지고 화면을 채팅방으로 전환한다!
                                existroom = true;
                            }
                        }

                        if(existroom==false){

                            Log.i("태그", "일치하는 방이 없으면 새로운 방을 만든다");
                            String newkey = databaseReference.child(LoginedUser.id).push().getKey();           //새로운 채팅방을 만들기 위한 키 값을 만들고, 가져옴.

                            //추가할 유저룸을 위해 정보를 담을 객체를 만듦 -- 1번은 나에게, 2번은 상대에게
                            UserRooms addRoom = new UserRooms(" ", " ", LoginedUser.id, LoginedUser.name, LoginedUser.imageUri, item.getMemberid(), item.getMembername(), item.getUri());
                            UserRooms addRoom2 = new UserRooms(" ", " ", item.getMemberid(), item.getMembername(), item.getUri(), LoginedUser.id, LoginedUser.name, LoginedUser.imageUri);

                            //위에서 만든 객체를 맵 형식으로 만듦 -- 1번은 나에게, 2번은 상대에게
                            Map<String, Object> addRoomValue = addRoom.toMap();
                            Map<String, Object> addRoom2Value = addRoom2.toMap();

                            //업데이트를 위한 해쉬맵을 만듦 -- 1번은 나에게, 2번은 상대에게
                            Map<String, Object> childUpdate = new HashMap<>();
                            childUpdate.put(LoginedUser.id+"/"+newkey, addRoomValue);          //만든 해쉬맵에 키값과 매칭하여 채팅방 정보를 넣음
                            childUpdate.put(item.memberid+"/"+newkey, addRoom2Value);

                            databaseReference.updateChildren(childUpdate);          //참조한 주소로 데이터를 업데이트함.

                            intent.putExtra("roomid", newkey);
                            intent.putExtra("yourid", item.getMemberid());

                            startActivity(intent);              //룸 아이디, 상대 아이디를 가지고 화면을 채팅방으로 전환한다!
                            //Todo.. 쉐어드프리퍼런스로 한 것은 주석처리(파이어베이스로 넘어가기 위함)
//
//                    SharedPreferences rooms = getSharedPreferences("rooms", MODE_PRIVATE);              //방들의 키값을 저장하는 공간을 하나 만듦
//                    SharedPreferences.Editor editor = rooms.edit();
//
//                    int num = rooms.getInt("key", 0);                       //키값을 불러옴. 키값이 없으면 0
//
//                    editor.putInt("key", num+1);            //키 값이 0이 아니면 이번에 만들어지는 방에 이 번호를 부여해줄 것이고, 이에 따라서 기존 키는 +1을 해줌.
//                    editor.commit();            //저장
//
//                    SharedPreferences userRooms = getSharedPreferences("userRooms", MODE_PRIVATE);          //유저가 가지고 있는 방들에 대한 정보를 저장하는 공간을 만듦
//                    SharedPreferences.Editor editor1 = userRooms.edit();
//
//                    String roominforaw = "{"+                                                                   //방에 대한 정보를 Json형식으로 스트링화 함
//                            "\"roomId\":"+"\"room"+num+"\","+
//                            "\"lastMsg\":"+"\" \","+
//                            "\"lastMsgTime\":"+"\" \","+
//                            "\"talkerlist\":"+"[{"+"\"uId\":"+"\""+LoginedUser.id+"\","+"\"uName\":"+"\""+LoginedUser.name+"\"},"+"{\"uId\":"+"\""+item.getMemberid()+"\","+"\"uName\":"+"\""+item.getMembername()+"\"}]"+
//                            "}";
//
//                    Log.i("태그", roominforaw);
//                    String mytotalroom = userRooms.getString(LoginedUser.id, "");                  //나의 룸현황을 가져옴(현재는 그냥 스트링, not 제이슨어레이)
//                    Log.i("태그", "나의 쉐어드 프리퍼런스를 호출함.");
//
//                    if(!mytotalroom.equals("")){                                //내 룸현황이 비어있지 않다면 들어오는 조건문
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(roominforaw);            //위에서 만든 방 정보 스트링을 제이슨 오브젝트에 넣음
//                            JSONArray jsonArray = new JSONArray(mytotalroom);               //내 룸 현황(스트링형식)을 제이슨 어레이에 넣음
//                            jsonArray.put(jsonObject);                                      //제이슨 어레이에 방을 추가함.
//
//                            editor1.putString(LoginedUser.id, String.valueOf(jsonArray));          //쉐어드프리퍼런스에 변경된 제이슨어레이를 넣음
//                            Log.i("나한테 저장되는 제이슨 어레이", String.valueOf(jsonArray));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }else{
//                        editor1.putString(LoginedUser.id, "["+roominforaw+"]");                    //나의 채팅방 현황이 비어 있다면, 위에서 만든 스트링을 제이슨 어레이 형식으로 바로 넣음
//                        Log.i("기존 데이터가 없으면 여기로 옴", roominforaw);
//                    }
//                    editor1.commit();                   //쉐어드프리퍼런스 에디터 저장!
//                    Log.i("태그", "나의 쉐어드 프리퍼런스 저장");
//
//
//                    SharedPreferences userRooms1 = getSharedPreferences("userRooms", MODE_PRIVATE);
//                    SharedPreferences.Editor editor2 = userRooms1.edit();
//
//                    String yourtotalroom = userRooms1.getString(item.getMemberid(), "");             //상대 룸 현황을 가져옴(현재는 그냥 스트링, not jsonarray)
//                    Log.i("너의 아이디", item.getMemberid());
//                    Log.i("태그", "너의 쉐어드 프리퍼런스를 호출함.");
//                    Log.i("너의 모든 룸", yourtotalroom);
//
//                    if(!yourtotalroom.equals("")){                                              //상대 채팅방 현황이 비어있지 않다면 들어옴.
//
//                        try {
//                            JSONObject jsonObject1 = new JSONObject(roominforaw);            //위에서 만든 스트링 형식의 방 정보를 제이슨 오브젝트에 넣음
//                            JSONArray jsonArray1 = new JSONArray(yourtotalroom);                 //상대 방 현황 스트링을 제이슨 어레이에 넣음
//                            jsonArray1.put(jsonObject1);                              //제이슨 어레이에 제이슨 오브젝트를 추가함
//
//                            editor2.putString(item.getMemberid(), String.valueOf(jsonArray1));           //변경된 제이슨 어레이를 쉐어드프리퍼런스에 다시 넣음
//                            Log.i("너한테 저장되는 제이슨 어레이", String.valueOf(jsonArray1));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }else{
//                        editor2.putString(item.getMemberid(), "["+roominforaw+"]");         //상대 방 현황이 비어 있다면 위에서 만든 스트링형식 방 정보를 제이슨어레이 형식으로 바로 넣음
//                        Log.i("기존 값이 없으면 여기로 옴", roominforaw);
//                    }
//                    editor2.commit();                   //쉐어드프리퍼런스 에디터 저장!
//                    Log.i("태그", "너의 쉐어드 프리퍼런스 저장");
//
//
//                    String roomid = "room"+num;             //해당 룸 아이디 가져감
//                    String yourid = item.getMemberid();
//
//                    intent.putExtra("roomid", roomid);
//                    intent.putExtra("yourid", yourid);
//
//                    Log.i("인텐트로 보냄", roomid+"+"+yourid);
//
//
//                    startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //Todo.. 쉐어드 프리퍼런스로 한 것은 주석처리(파이어베이스로 넘어가기 위함)

//                try {
//                    JSONArray jsonArray = new JSONArray(myrooms);                   //선택한 유저와의 대화방이 이미 있다면 실행되는 문장
//
//                    for(int j = 0 ; j < jsonArray.length() ; j++){
//                        JSONObject jsonObject = jsonArray.getJSONObject(j);                             //제이슨 어레이에서 오브젝트를 뽑아옴
//                        String talkerlist = jsonObject.getString("talkerlist");                     //토커리스트에 접근하기 위한 스트링
//                        JSONArray jsonArray1 = new JSONArray(talkerlist);                                   //토커리스트(제이슨 어레이)에 접근함
//                        for(int k = 0 ; k< jsonArray1.length() ; k++){                          //토커리스트에서 오브젝트를 다 뽑아오기 위한 반복문
//                            JSONObject jsonObject1 = jsonArray1.getJSONObject(k);
//
//                            if(jsonObject1.getString("uId").equals(item.getMemberid())){            //유저아이디를 가져오는데 선택된 유저아이디와 같다면
//
//                                String roomid = jsonObject.getString("roomId");                     //룸 아이디와, 유저아이디를
//                                String yourid = item.getMemberid();
//
//                                intent.putExtra("roomid", roomid);                                  //인텐트에 넣어서 채팅 액티비티를 실행함
//                                intent.putExtra("yourid", yourid);
//                                existroom = true;                                               //방 존재를 참으로 만들어 방이 없을 때 구문이 실행되지 못하게 함
//
//                                startActivity(intent);
//                            }
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });


        builder.create().show();
    }

    public void dialoglongclk(ConsultMember_Data item, final int position){                                 //리사이클러 뷰에서 멤버(아이템)를 롱 클릭 했을 때 실행되는 메소드
        AlertDialog.Builder builder = new AlertDialog.Builder(ConsultMember_activity.this);
        builder.setMessage(item.getMembername()+" 님을 목록에서 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String memberid = adapter.items.get(position).getMemberid();          //선택된 아이템(유저)의 키(id)를 스트링에 넣음


                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("friendlist").child(LoginedUser.id).child(memberid);
                Log.i("태그", "찾는 아이디를 키로 가지는 데이터베이스 레퍼런스를 선언");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.i("태그", "온 데이터 체인지 들어옴");

                        if(dataSnapshot.exists()){
                            Log.i("태그", "만약 이 데이터스냅샷이 존재하면 들어오는 조건문");
                            databaseReference.removeValue();
                            Log.i("태그", "이 레퍼런스 삭제!");

                            adapter.items.remove(position);                                 //아이템 삭제
                            adapter.notifyItemRangeChanged(position, adapter.items.size());     //아이템 범위 변경에 대한 갱신
                            adapter.notifyDataSetChanged();                                 //아이템 뷰들의 변경에 대한 갱신
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //Todo.. 파이어베이스 사용하므로 쉐어드프리퍼런스는 주석처리
//                SharedPreferences friendlist = getSharedPreferences("friendlist", MODE_PRIVATE);
//                SharedPreferences.Editor editor = friendlist.edit();                   //수정을 하기 위해서 에디터 선언
//                String myfriendlist = friendlist.getString(LoginedUser.id, "");                         //쉐어드프리퍼런스에서 이 유저의 친구목록을 스트링에 넣어줌(JSONARRAY형식임)
//
//                try {
//                    JSONArray jsonArray = new JSONArray(myfriendlist);                //이 유저의 친구목록을 제이슨 어레이로 넣음
//
//                    for(int j = 0 ; j<jsonArray.length() ; j++){
//                        JSONObject jsonObject = jsonArray.getJSONObject(j);
//
//                        if(jsonObject.getString("uid").equals(memberid)){            //제이슨 오브젝트의 아이디가 이 포지션의 아이디와 같다면 들어오는 조건문
//                            jsonArray.remove(j);
//                            Log.i("tag", "선택된 오브젝트 삭제됨 / 저장 전");
//                        }
//
//                    }
//
//                    editor.putString(LoginedUser.id, String.valueOf(jsonArray));
//                    editor.commit();                                                        //수정된 제이슨 어레이를 다시 records에 삽입하고 저장(덮어쓰기)
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                adapter.items.remove(position);                                 //아이템 삭제
//                adapter.notifyItemRangeChanged(position, adapter.items.size());     //아이템 범위 변경에 대한 갱신
//                adapter.notifyDataSetChanged();                                 //아이템 뷰들의 변경에 대한 갱신

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.items.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("friendlist/"+LoginedUser.id);
        Log.i("태그", "프렌드리스트 레퍼런스 선언");

        databaseReference.orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("태그", "온 데이터 체인지");
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    Log.i("태그", "반복문 들어옴");

                    String imageUri = item.child("imageUri").getValue().toString();
                    adapter.addItem(new ConsultMember_Data(imageUri, item.child("name").getValue().toString(), item.child("id").getValue().toString()));

//                    Glide.with(ConsultMember_activity.this).load(imageUri).into(img);

                    adapter.notifyDataSetChanged();             //리사이클러뷰 갱신
                    Log.i("태그", "어뎁터에 추가함"+item.child("name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Todo... 파이어베이스 사용함에 따라 쉐어드프리퍼런스는 삭제
//        SharedPreferences friendlist = getSharedPreferences("friendlist", MODE_PRIVATE);            //친구목록 쉐어드프리퍼런스를 선언
//        String myfriendlist = friendlist.getString(LoginedUser.id, "");                            //이 유저에 들어있는 친구목록을 가져옴
//
//
//        try {
//            JSONArray jsonArray = new JSONArray(myfriendlist);                  //친구목록 텍스트를 분해하기 위해 제이슨 어레이에 넣음.
//
//            for(int i = 0 ; i<jsonArray.length() ; i++){
//
//                JSONObject jsonObject = jsonArray.getJSONObject(i);                 // 제이슨 어레이에서 한 제이슨 오브젝트 씩 뽑아서 각각의 아이템(친구) 속성에 넣어줌.
//                String imagaUri = jsonObject.getString("uimageUri");
//                String id = jsonObject.getString("uid");
//                String name = jsonObject.getString("uname");
//
//                Log.i("제이슨 어레이에서 뽑아옴", i+"번째 제이슨 오브젝트" );
//                adapter.addItem(new ConsultMember_Data(img, name, id));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        adapter.notifyDataSetChanged();             //리사이클러뷰 갱신
    }
}
