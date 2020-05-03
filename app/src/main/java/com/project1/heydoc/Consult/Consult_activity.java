package com.project1.heydoc.Consult;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project1.heydoc.Login.LoginedUser;
import com.project1.heydoc.R;
import com.project1.heydoc.Showinfo_activity;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Consult_activity extends AppCompatActivity {

    Consult_Data_Adapter adapter = new Consult_Data_Adapter(this);
    EditText typing;
    String roomid, yourid;
    RecyclerView recyclerView;

    DatabaseReference databaseReference;

    String imageFileName = null;
    final int REQUEST_FROM_CAMERA = 100;
    final int REQUEST_FROM_GALLERY = 200;
    final int REQUEST_TO_CROP = 300;
    String mCurrentPhotoPath;
    Uri cameraURI, galleryURI;
    Uri photoURI;
    boolean fromGallery = false;
    Uri uriForDownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consult);

        Intent intent = getIntent();
        roomid = intent.getStringExtra("roomid");
        yourid = intent.getStringExtra("yourid");
        Log.i("받아온 인텐트", roomid+"+"+yourid);


        final ImageView img = findViewById(R.id.imginconsult);


        recyclerView = findViewById(R.id.consult_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

//이젠 필요없는 구문.. 예전에 하드코딩 할 때 시간 가져오기 위해서 만든 것임.
//        long now = System.currentTimeMillis();                              //현재시간 받아오기
//        Date date = new Date(now);              //데이트에 넣어두기
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");      //형식 변환을 위해 사용할 형식 만들기
//        final String showNowTime = simpleDateFormat.format(date);           //형식변환해서 스트링에 넣기
//        adapter.addItem(new Consult_Data(img, "나의사", "yourid", "상담을 원하시나요?", showNowTime));
//        adapter.addItem(new Consult_Data(img, "나의사", "yourid", "어디가 아픔?", showNowTime));
        typing = findViewById(R.id.typing);             //에딭텍스트 연결
        Button insert = findViewById(R.id.insert);      //전송 버튼 연결
        insert.setOnClickListener(new View.OnClickListener() {      //전송버튼 클릭이벤트 작성
            @Override
            public void onClick(View view) {
                pushMessage();
                //쉐어드 프리퍼런스로 구현했던 것은 주석처리함(기능이 파이어베이스로 넘어감)
//                SharedPreferences messages = getSharedPreferences("messages", MODE_PRIVATE);            //메시지들이라는 쉐어드프리퍼런스를 선언 호출
//                SharedPreferences.Editor editor = messages.edit();                                      //이 쉐어드 프리퍼런스 수정을 위한 에디터 선언
//                String totalmsg = messages.getString(roomid, "");                                   //룸 아이디로 된 메시지 모두(제이슨 어레이)를 불러옴
//
//                String setMessage = "{"+                                                            //보낸 메시지를 제이슨 오브젝트 형식으로 바꿔줌
//                        "\"talkerImg\":"+"\"uri\","+
//                        "\"talkerName\":"+"\""+ LoginedUser.name+"\","+
//                        "\"talkerId\":"+"\""+LoginedUser.id+"\","+
//                        "\"sendedText\":"+"\""+typing.getText().toString()+"\","+
//                        "\"sendedTime\":"+"\""+timeforsaving+"\""+
//                        "}";
//
//                if(!totalmsg.equals("")){                                               //메시지가 비어있지 않다면 들어오는 조건문
//
//                    try {
//                        JSONObject jsonObject = new JSONObject(setMessage);                     //위의 메시지를 오브젝트화 함
//                        JSONArray jsonArray = new JSONArray(totalmsg);                              //원래 있던 메시지들을 어레이에 담아줌
//                        jsonArray.put(jsonObject);                                          //오브젝트화 한 보낸 메시지를 제이슨 어레이에 담아줌
//
//                        editor.putString(roomid, String.valueOf(jsonArray));                            //제이슨 어레이를 다시 메시지들이라는 쉐어드프리퍼런스에 저장(룸아이디 이용)
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }else{
//                    editor.putString(roomid, "["+setMessage+"]");                           //메시지들 제이슨 어레이가 없다면 새로 만들어서 넣어줌.
//                }
//                editor.commit();                //저장
//
//
//                //이 아래로는 방 목록의 최근 메시지와 시간을 변경하기 위한 작업들...
//                SharedPreferences userRooms = getSharedPreferences("userRooms", MODE_PRIVATE);          //유저 룸 쉐어드프리퍼런스를 호출
//                SharedPreferences.Editor editor1 = userRooms.edit();
//
//                String myRoom = userRooms.getString(LoginedUser.id, "");           //나의 채팅방들을 불러와서 스트링에 넣음
//
//                try {
//                    JSONArray jsonArray = new JSONArray(myRoom);                //제이슨 어레이로 인식하기 위해 넣어줌.
//
//                    for(int i = 0 ; i <jsonArray.length() ; i++){
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);             //제이슨 어레이를 하나씩 찾아옴.
//
//                        if(jsonObject.getString("roomId").equals(roomid)){
//                            jsonObject.put("lastMsg", typing.getText().toString());             //룸 아이디가 일치하는 것을 찾아서 마지막 메시지와 시간을 넣어줌
//                            jsonObject.put("lastMsgTime", timeforsaving);
//                        }
//                    }
//
//                    editor1.putString(LoginedUser.id, String.valueOf(jsonArray));              //내 목록에 다시 제이슨 어레이를 넣음
//                    editor1.commit();           //저장
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                String yourRoom = userRooms.getString(yourid, "");          //상대 채팅방들을 불러와서 스트링에 넣어줌
//
//                try {
//                    JSONArray jsonArray = new JSONArray(yourRoom);              //제이슨 어레이로 인식하기 위해 넣음
//
//                    for(int i = 0 ; i <jsonArray.length() ; i++){
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);         //제이슨 오브젝트를 하나씩 불러옴.
//
//                        if(jsonObject.getString("roomId").equals(roomid)){
//                            jsonObject.put("lastMsg", typing.getText().toString());         //룸 아이디가 일치하면 들어가서, 마지막 메시지와 시간을 바꿔줌
//                            jsonObject.put("lastMsgTime", timeforsaving);
//                        }
//                    }
//
//                    editor1.putString(yourid, String.valueOf(jsonArray));               //상대 채팅방에 이 오브젝트를 다시 넣음
//                    editor1.commit();           //저장
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                adapter.addItem(new Consult_Data("", LoginedUser.name, LoginedUser.id, typing.getText().toString(), changedDate));      //입력한 값에 대해서 전송되도록
//
//                recyclerView.scrollToPosition(adapter.getItemCount()-1);            //최근 메시지에 포커싱이 맞도록 하는 것
//                adapter.notifyDataSetChanged();     //변화 알리기
//                typing.getText().clear();       //에딭텍스트 값 초기화
            }
        });

//        Button sendImg = findViewById(R.id.sendimg);
//        sendImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                photoDailog();
//            }
//        });
    }

    public void pushMessage(){

        long now = System.currentTimeMillis();                              //현재시간 받아오기
        Date date = new Date(now);              //데이트에 넣어두기
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");      //형식 변환을 위해 사용할 형식 만들기
        final String timeforsaving = simpleDateFormat.format(date);                      //저장하기 위한 시간

        final String changedDate = formattedDate(timeforsaving, "YYYY-MM-dd HH:mm:ss", "HH:mm");          //보낸시간을 시:분 단위로 보여주기 위해 포맷 바꿈

        databaseReference = FirebaseDatabase.getInstance().getReference("messages").child(roomid);
        Log.i("태그", "데이터 레퍼런스 가져옴");

        String key = databaseReference.push().getKey();
        Log.i("태그", "키를 생성함"+key);
        Consult_Data message = new Consult_Data(LoginedUser.imageUri, LoginedUser.name, LoginedUser.id, typing.getText().toString(), timeforsaving);
        Map<String, Object> messageValue = message.toMap();
        Log.i("태그", "메시지 값을 맵으로 만들어 넣음");

        HashMap<String, Object> updatechild = new HashMap<>();
        Log.i("태그", "업데이트 차일드라는 해쉬맵을 하나 만들어서 메시지 벨류를 넣어줌");
        updatechild.put(key, messageValue);

        databaseReference.updateChildren(updatechild);
        Log.i("태그", "해당 데이터베이스를 업데이트");

        //이 아래는 나와 상대의 최근 메시지 및 시간을 갱신해주기 위한 작업임..

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("userRooms").child(LoginedUser.id).child(roomid);
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("userRooms").child(yourid).child(roomid);

        databaseReference1.child("lastMsg").setValue(typing.getText().toString());
        databaseReference1.child("lastMsgTime").setValue(timeforsaving);

        databaseReference2.child("lastMsg").setValue(typing.getText().toString());
        databaseReference2.child("lastMsgTime").setValue(timeforsaving);

        recyclerView.scrollToPosition(adapter.getItemCount()-1);            //최근 메시지에 포커싱이 맞도록 하는 것
//                adapter.notifyDataSetChanged();     //변화 알리기
        typing.getText().clear();       //에딭텍스트 값 초기화
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.i("태그", "onResume");
        final ImageView img = findViewById(R.id.imginconsult);

        adapter.items.clear();                                              //어뎁터를 클리어 해줌

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("messages").child(roomid);
        Log.i("태그", "데이터베이스 레퍼런스 만듦");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("태그", "온 차일드 어디드 consult_activity");

                Consult_Data msgValue = dataSnapshot.getValue(Consult_Data.class);

                String changedDate = formattedDate(msgValue.getTime(), "YYYY-MM-dd HH:mm:ss", "HH:mm");          //보낸시간을 시:분 단위로 보여주기 위해 포맷 바꿈

                adapter.addItem(new Consult_Data(msgValue.getUri(), msgValue.getName(), msgValue.getId(), msgValue.getText(), changedDate));
                Log.i("태그", "어뎁터에 메시지 추가함");
                adapter.notifyDataSetChanged();                 //어뎁터에 변화 인지시킴


                recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);            //최근 메시지에 포커싱이 맞도록 하는 것
                Log.i("태그", "포지션 맞추기");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
//                Log.i("태그", "온 데이터 체인지");
//                for(DataSnapshot item : dataSnapshot.getChildren()){
//                    Log.i("태그", "안에 있는 내용 다 뽑는 반복문");
//                    Consult_Data msgValue = item.getValue(Consult_Data.class);
//
//                    String changedDate = formattedDate(msgValue.getTime(), "YYYY-MM-dd HH:mm:ss", "HH:mm");          //보낸시간을 시:분 단위로 보여주기 위해 포맷 바꿈
//
//                    adapter.addItem(new Consult_Data(msgValue.getUri(), msgValue.getName(), msgValue.getId(), msgValue.getText(), changedDate));
//                    Log.i("태그", "어뎁터에 메시지 추가함");
//                    adapter.notifyDataSetChanged();                 //어뎁터에 변화 인지시킴
//
//
//                }
//                recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);            //최근 메시지에 포커싱이 맞도록 하는 것
//                Log.i("태그", "포지션 맞추기");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


        //파이어베이스 사용으로 이 아래 부분인 쉐어드프리퍼런스 부분은 주석 처리 함
//        SharedPreferences messages = getSharedPreferences("messages", MODE_PRIVATE);                            //메시지 쉐어드프리퍼런스를 호출
//        String thisroommessages = messages.getString(roomid, "");                                           //이 룸의 아이디를 지닌 메시지 제이슨 어레이를 스트링에 넣어줌(제이슨화 하기 위해)
////        Log.i("RoomID : ", roomid);
//
//        try {
//            JSONArray jsonArray = new JSONArray(thisroommessages);                              //rawString 을 제이슨 어레이에 넣어줌
//            Log.i("태그", "메시지들 제이슨 어레이에 넣음");
//            for(int i = 0 ; i<jsonArray.length() ; i++){
//                JSONObject jsonObject = jsonArray.getJSONObject(i);                                 //제이슨 어레이에서 제이슨 오브젝트를 하나씩 빼옴
//                Log.i("태그", "메시지(제이슨 오브젝트)들을 뽑기 위한 제이슨 어레이 반복문");
//
//                String talkerImg = jsonObject.getString("talkerImg");               //Todo.. 스트링 형의 uri를 이미지화 시키는 것 필요
//                String talkerName = jsonObject.getString("talkerName");
//                String talkerId = jsonObject.getString("talkerId");
//                String sendedText = jsonObject.getString("sendedText");
//                String sendedTime = jsonObject.getString("sendedTime");
//
//                String changedDate = formattedDate(sendedTime, "YYYY-MM-dd HH:mm:ss", "HH:mm");          //보낸시간을 시:분 단위로 보여주기 위해 포맷 바꿈
//
//                Log.i("제이슨 어레이에서 뽑아옴", i+"번째 제이슨 오브젝트" );
//
//                adapter.addItem(new Consult_Data("", talkerName, talkerId, sendedText, changedDate));             //오브젝트의 값들을 모두 리사이클러뷰(대화창)에 넣어줌
//
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        adapter.notifyDataSetChanged();                 //어뎁터에 변화 인지시킴
    }

//    private void photoDailog(){                             //사진을 어디에서 가져올지 선택하게 되는 다이얼로그를 띄움
//        AlertDialog.Builder builder = new AlertDialog.Builder(Consult_activity.this);          //빌더 선언
//        builder.setMessage("어디에서 사진을 가져오실래요?");
//        builder.setPositiveButton("앨범", new DialogInterface.OnClickListener() {                         //긍정버튼에 앨범에서 가져오기 옵션을 넣음
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(Consult_activity.this, "앨범에서 가져오기를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
////                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, REQUEST_FROM_GALLERY);                //인텐트에 이미지 타입을 넣고 결과를 받아오는 인텐트를 실행
//            }
//        });
//        builder.setNeutralButton("카메라", new DialogInterface.OnClickListener() {                     //중성버튼에 카메라에서 가져오기 옵션을 넣음
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(Consult_activity.this, "카메라에서 가져오기를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
//                captureCamera();
//            }
//        });
//        builder.create().show();
//    }
//
//    private void captureCamera(){
//        Log.i("태그", "captureCamera");
//        String state = Environment.getExternalStorageState();
//        if(Environment.MEDIA_MOUNTED.equals(state)){                        //외장메모리를 지원하는지 확인하는 구문
//            Log.i("태그", state+"에 따라 들어오는 조건문");
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);                //인텐트를 생성
//
//            if(intent.resolveActivity(getPackageManager()) !=null){             //인텐트를 처리할 수 있는 앱이 있는지 확인! 있다면 들어오는 조건문
//                Log.i("태그", "인텐트를 처리할 수 있는지 확인하고 들어오는 조건문");
//                File photoFile = null;              //이미지 파일을 담을 파일 변수를 하나 만듦
//
//                try {
//                    photoFile = createImageFile();              //이미지 파일을 생성하는 메소드 리턴값으로 선언
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if(photoFile !=null){                   //포토 파일이 널이 아니면 들어오는 조건문
//                    cameraURI = FileProvider.getUriForFile(this, "com.project1.heydoc.fileprovider", photoFile);
//                    Log.i("태그", "포토파일 "+photoFile.toString());
//                    Log.i("태그", "카메라이미지URI "+cameraURI.toString());
//
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraURI);
//
//                    startActivityForResult(intent, REQUEST_FROM_CAMERA);                //액티비티 시작
//                }
//            }
//        }else{
//            Toast.makeText(Consult_activity.this, "외장 메모리 미지원", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private File createImageFile() throws IOException {
//        Log.i("태그", "createImageFile");
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());          //파일 이름을 유일하게 만들기 위해 시간을 불러옴
//
//        imageFileName = "JPEG"+timeStamp+"_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//
//        mCurrentPhotoPath = image.getAbsolutePath();
//        Log.i("태그", "실 주소 "+mCurrentPhotoPath);
//        return image;          //파일을 반환
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode){                   //requestcode 에 따라 나눠서 진행
//            case REQUEST_FROM_CAMERA :
//                Log.i("태그", "카메라로부터 들어옴");
//                fromGallery = false;
//
//                Log.i("태그", "현재 사진 위치 uri "+cameraURI);
//                Log.i("태그", "현재 사진 위치"+mCurrentPhotoPath);
//
////                mypicture.setImageURI(cameraURI);                               //이미지를 카메라 URI로 셋팅
//                uploadImage();
//
//                break;
//
//            case REQUEST_FROM_GALLERY :
//                Log.i("태그", "갤러리로부터 들어옴");
//                fromGallery = true;
//
//                File albumfile = null;                          //앨범 파일을 하나 만듦
//
//                try {
//                    albumfile = createImageFile();                          //빈 이미지 주소를 넣음
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if(albumfile!=null){
//                    galleryURI = Uri.fromFile(albumfile);                       //앨범파일 주소를 갤러리 URI로 지정
//                }
//                //Todo..위의 과정은 크롭을 위한 과정이었으나,, 현재 크롭 기능이 되지 않는 관계로 의미없는 행위임. 앨범에서는 바로 intent data URI로 가져오고 있음..
//
//                if(data!=null){
//                    photoURI = data.getData();
//                    Log.i("태그", "photoURI "+photoURI);
//                    Log.i("태그", "galleryURI "+galleryURI);
////                    mypicture.setImageURI(photoURI);            //이게 이미지 띄우는 문장임
//                    uploadImage();
//
//                    break;
//                }
//
//            default:
//                Log.i("태그", "디폴트");
//        }
//
//    }
//
//    private void uploadImage (){                                //이미지를 업로드 하기 위한 메소드
//        Log.i("태그", "uploadImage");
//
//        Uri fileUri = null;                         //파일 주소를 담을 공간을 하나 만듦!!
//
//        if(fromGallery==true){
//            fileUri = photoURI;                                         //갤러리에서 선택했으면, photoURI를 fileURI에 넣음
//            Log.i("태그", "앨범에서 "+fileUri);
//        }else {
//            fileUri = Uri.fromFile(new File(mCurrentPhotoPath));         //사진에서 선택했으면, 현재주소에서 uri를 빼와 fileURI에 넣음
//            Log.i("태그", "카메라에서 "+fileUri);
//        }
//
//        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://heydoc-de775.appspot.com");         //내 파이어베이스 스토리지를 연다.
//        final StorageReference storageReference = firebaseStorage.getReference();                               //주소를 가져온다.
//        final StorageReference pictures = storageReference.child("Pictures/"+imageFileName);                            //Pictures라는 곳에 파일이름으로 위치를 가져온다.
//
//        final UploadTask uploadTask;            //업로드 작업을 위한 변수
//
//        uploadTask = pictures.putFile(fileUri);         //fileURI에 담겨진 파일을 넣는다.
//
//        uploadTask.addOnFailureListener(new OnFailureListener() {           //실패하면 들어오는 구문
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.i("태그", "이미지 업로드 실패");
//            }
//        });
//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {          //성공하면 들어오는 구문
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.i("태그", "이미지 업로드 성공");
//                pictures.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {               //다운로드 URI를 받기 위해서 하는 작업
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        uriForDownload = uri;                                                       //다운로드 URI를 따로 유저 정보에 넣어줘야 한다.
//                        Log.i("태그", "다운로드 URI는 "+uriForDownload);
//                        typing.setText(uriForDownload.toString());
////                        databaseReference.child("imageUri").setValue(String.valueOf(uriForDownload));               //넣는 작업
//                        pushMessage();
//                    }
//                });
//            }
//        });
//    }


    public String formattedDate(String date, String fromString, String toString){               //날짜 형식을 바꿔주는 메소드
        SimpleDateFormat fromFormat = new SimpleDateFormat(fromString);                 //바꿀 날짜를 선언.
        SimpleDateFormat toFormat = new SimpleDateFormat(toString);                     //바뀔 날짜를 선언
        Date fromDate = null;

        try{
            fromDate = fromFormat.parse(date);                              //데이트 형식으로 parse함.
        } catch (ParseException e) {
            fromDate = new Date();
            e.printStackTrace();
        }
        Log.i("태그", "날짜 형식 바꿈");
        return toFormat.format(fromDate);                   //새로운 형식으로 변환하고 반환
    }

}
