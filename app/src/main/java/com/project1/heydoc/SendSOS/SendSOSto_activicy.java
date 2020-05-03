package com.project1.heydoc.SendSOS;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project1.heydoc.Login.LoginedUser;
import com.project1.heydoc.R;

public class SendSOSto_activicy extends AppCompatActivity {

    SendSOSto_Data_Adapter adapter = new SendSOSto_Data_Adapter(this);
    Button addReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendsosto_activity);

        RecyclerView recyclerView = findViewById(R.id.sendsosto_recyclerview);          //리사이클러뷰를 미리 만들어놓은 xml의 요소로 선언
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);     //레이아웃 매니저를 선언
        recyclerView.setLayoutManager(layoutManager);           //리사이클러뷰 레이아웃 매니저로 지정
        recyclerView.setAdapter(adapter);           //리사이클러뷰에 어뎁터 지정

        //어뎁터에 아이템 클릭 리스너를 설정함. 파라미터로는 이전에 만들어 놓은 sosItemClickListener를 넣고
        adapter.setOnItemClickListener(new OnSendSOSItemClickListener() {
            @Override
            public void onItemClick(SendSOSto_Data_Adapter.ViewHolder holder, View view, int position) {
                //아이템을 클릭했을 때 실행될 수 있는 다이얼로그를 메소드르 만들어 놓았으며, 이를 실행함.
                clickdialog(adapter.getItem(position));
            }
        });

        addReceiver = findViewById(R.id.add_receiver);
        addReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReceiver();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("태그", "onResume in SendSOSto_activity");
        Log.i("태그", "어뎁터 아이템 :"+adapter.items.size());
        adapter.items.clear();          //어뎁터 아이템들을 싹 비워줌

        Log.i("태그", "어뎁터 아이템1 :"+adapter.items.size());
        loadReceiver();             //sos 수신목록을 불러오는 메소드

    }

    public void clickdialog(final SendSOSto_Data item){
        Log.i("태그", "clickdialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(item.getReceivername()+" 님을 삭제하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //이 유저의 sos 받는사람 목록에 접근하는 데이터베이스 레퍼런스
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sosreceiver/"+LoginedUser.id).child(item.getReceiverid());
                databaseReference.removeValue();
                adapter.notifyDataSetChanged();
                onResume();
            }
        });
        //'아니오'를 클릭하면 아무일도 일어나지 않고 다이얼로그만 없어진다.
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    public void loadReceiver(){
        Log.i("태그", "loadReceiver");
        //이 유저의 sos 받는사람 목록에 접근하는 데이터베이스 레퍼런스
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sosreceiver/"+ LoginedUser.id);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("태그", "onDataChange");

                for(DataSnapshot item : dataSnapshot.getChildren()){
                    Log.i("태그", "dataSnapshot의 반복문 들어옴");

                    //리시버 아이디 목록에 있는 아이디를 하나씩 가져와서 이를 기반으로 레퍼런스를 만들어.. 아래보면 유저스라는 레퍼런스에 다시 접근할 것임.
                    final String key = item.getKey();
                    Log.i("태그", "가지고올 아이디 키 : "+key);

                    final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users");

                    databaseReference1.orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.i("태그", "onDataChange1");

                            //이 유저스라는 레퍼런스로 접근하여 이 아이디의 정보를 불러올 것이다.
                            for(DataSnapshot item1 : dataSnapshot.getChildren()){
                                Log.i("태그", "dataSnapshot1의 반복문 들어옴");

                                if(item1.getKey().equals(key)){
                                    String imageUri = item1.child("imageUri").getValue().toString();
                                    String receivername = item1.child("name").getValue().toString();
                                    String receiverid = item1.child("id").getValue().toString();
                                    String token = item1.child("token").getValue().toString();

                                    //불러온 정보를 토대로 어뎁터에 추가한다.
                                    adapter.addItem(new SendSOSto_Data(imageUri, receivername, receiverid, token));
                                    Log.i("태그", "추가되는 아이디 : "+receiverid+"/"+receivername);
                                    Log.i("태그", "그리고 토큰 : "+token);
                                    Log.i("태그", "어뎁터 사이즈 : "+adapter.items.size());
                                    adapter.notifyDataSetChanged();
                                }
                            }

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
    }

    public void addReceiver(){                                                      //SOS 받는 사람 목록을 추가하는 버튼에 선언된 메소드(이게 작동함)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("추가할 대상의 아이디를 입력하세요.");

        //알러트빌더에 에딛텍스트를 하나 넣음
        final EditText typeid = new EditText(this);
        builder.setView(typeid);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String typedid = typeid.getText().toString();

                //기본적인 예외처리.. 빈칸이거나, 내 아이디를 넣는다거나, 없는 아이디를 타이핑했을 때를 잡아줌.
                if(typedid.equals("")){
                    Toast.makeText(SendSOSto_activicy.this, "찾으실 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    if(LoginedUser.id.equals(typedid)){
                        Toast.makeText(SendSOSto_activicy.this, "본인은 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }else {
                        boolean exitid = false;

                        //리사이클러뷰에서 미리 검색을 통해서 이미 추가된 아이디인지 확인하는 구간
                        for(int j = 0 ; j<adapter.items.size(); j++){
                            String id = adapter.items.get(j).getReceiverid();
                            if(id.equals(typedid)){
                                exitid = true;
                                Log.i("태그", "기존에 등록된 아이디다.");
                                Toast.makeText(SendSOSto_activicy.this, "이미 등록된 아이디입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        //이미 추가된 아이디가 아니라면 실행되는 조건문으로, 실제로 아이디를 추가하는 부분이다.
                        if(exitid==false){
                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean nothing = true;
                                    //유저 목록 데이터베이스에 접근하여 반복문을 통해 해당 아이디가 있는지 확인
                                    for(DataSnapshot item : dataSnapshot.getChildren()){
                                        if(item.getKey().equals(typedid)){
                                            //아이디가 유저목록에 있다면 들어오는 조건문..
                                            nothing = false;
                                            //sosReceiver 목록에 이 내 아이디를 키값으로 하는 레퍼런스를 만들고, 여기에 검색한 아이디를 추가함.
                                            //아이디에 대한 정보는 나중에 이 키로 접근하고 이 키를 가지고 users 구간에 접근하여 불러올 예정임.
                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("sosreceiver/"+LoginedUser.id);
                                            databaseReference1.child(typedid).setValue(typedid);
                                            onResume();
                                        }
                                    }
                                    if(nothing==true){
                                        Toast.makeText(SendSOSto_activicy.this, "찾으시는 아이디가 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
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


}
