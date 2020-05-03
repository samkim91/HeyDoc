package com.project1.heydoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project1.heydoc.Login.LoginedUser;
import com.project1.heydoc.Login.Sign_activity;
import com.project1.heydoc.Login.User;
import com.project1.heydoc.SendSOS.SendSOSto_activicy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class Showinfo_activity extends AppCompatActivity {

    EditText typeid, typename, typephonenum, date, showbloodtype, typeemail, typeweakness;
    TextView sendsosto;
    ImageView mypicture;
    RadioGroup gender;
    RadioButton male, female;
    int year, month, day;
    Boolean editbtn = true;
    String showgender;

    User user;

    String imageFileName = null;
    final int REQUEST_FROM_CAMERA = 100;
    final int REQUEST_FROM_GALLERY = 200;
    final int REQUEST_TO_CROP = 300;
    String mCurrentPhotoPath;
    Uri cameraURI, galleryURI;
    Uri photoURI;
    boolean fromGallery = false;

    Uri uriForDownload;

    String setReceiverNames = "";

    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(LoginedUser.id);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showinfo);

        mypicture = findViewById(R.id.mypicture);
        mypicture.setImageResource(R.drawable.photo);

        typeid = findViewById(R.id.typeId);
        typename = findViewById(R.id.typeName);             //각 에딭텍스트 값들 인스턴스화 및 매칭
        typephonenum = findViewById(R.id.typePhoneNum);
        date = findViewById(R.id.date_showinfo);
        typeemail = findViewById(R.id.typeemail);
        typeweakness = findViewById(R.id.typeweakness);
        showbloodtype = findViewById(R.id.showbloodtype);

        gender = findViewById(R.id.gendergroup);
        male = findViewById(R.id.male_info);
        female = findViewById(R.id.female_info);

        sendsosto = findViewById(R.id.sendsosto);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("태그", "온 데이트 체인지 메소드 안 진입");
                user = dataSnapshot.getValue(User.class);

                Log.i("태그", "데이터스냅샷에서 값을 빼서 유저클래스로 넣음"+user);

                typeid.setText(user.getId());                               //이 아래로는 각종 값들을 뷰에 셋팅 해줌..
                typename.setText(user.getName());

                if(user.getGender().equals("남자")){
                    male.setChecked(true);
                }else{
                    female.setChecked(true);
                }

                Glide.with(Showinfo_activity.this).load(user.getImageUri()).into(mypicture);
                showbloodtype.setText(user.getBloodtype());
                typephonenum.setText(user.getPhonenum());
                date.setText(user.getBirthday());
                typeemail.setText(user.getEmail());
                typeweakness.setText(user.getWeakness());

                Log.i("태그", "빼온 값"+user.getId()+user.getName()+user.getGender()+user.getBloodtype()+user.getPhonenum()+user.getBirthday()+
                        user.getEmail()+user.getWeakness());
                Log.i("태그", "빼온 값"+user.getImageUri());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Todo.. 이 밑으론 쉐어드프리퍼런스에서 정보를 얻어오는 것이고 파이어베이스로 변경하면서 주석처리함.
//        SharedPreferences userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);            //쉐어드프리퍼런스에 저장된 값을 키(아이디)로 찾아와서 각 공간에 넣어주기 위한 선언
//        final SharedPreferences.Editor editor = userinfo.edit();                  //쉐어드프리퍼런스 에디터 셋팅
//
//        String theuserinfo = userinfo.getString(LoginedUser.id, "");              //이 유저정보를 가져와서 스트링에 넣음.
//        final String [] theuserinfoarray = theuserinfo.split("@#@");          //유저 정보를 스트링 배열에 넣어서 활용
//
//        typeid.setText(theuserinfoarray[0]);
//        typename.setText(theuserinfoarray[2]);
//
//        if(theuserinfoarray[3].equals("남자")){               //쉐어드프리퍼런스 성별에 따라 라디오버튼 체크된 상태로 두기
//            male.setChecked(true);
//        }else{
//            female.setChecked(true);
//        }
//
//        showbloodtype.setText(theuserinfoarray[4]);
//        Log.i("태그", theuserinfoarray[4]);
//        typephonenum.setText(theuserinfoarray[5]);
//        date.setText(theuserinfoarray[6]);
//        typeemail.setText(theuserinfoarray[7]+"@"+theuserinfoarray[8]);
//        typeweakness.setText(theuserinfoarray[9]);


        Calendar calendar = new GregorianCalendar();            //달력 초기화
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        final Button setDate = findViewById(R.id.find_date_showinfo);        //달력 선택 버튼

        setDate.setOnClickListener(new View.OnClickListener() {                 //달력 보여주는 문장
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {           //날짜리스너 선언
                    @Override
                    public void onDateSet(DatePicker datePicker, int setyear, int setmonth, int setday) {
                        year = setyear;
                        month = setmonth;
                        day = setday;
                        date.setText(String.format("%d - %d - %d", year, month+1, day));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(Showinfo_activity.this, onDateSetListener, year, month, day);       //날짜 선택 다이얼로그 선언 및 보여주기
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());      //오늘 이후로는 안 보이게 함
                datePickerDialog.show();        //데이트피커다이얼로그 보이게 하기.
            }
        });

        final Spinner selbloodtype = findViewById(R.id.spinner2);       //스피너 xml에 매칭 및 인스턴스화

        typeid.setEnabled(false);
        typename.setEnabled(false);                 //값들 처음에는 수정 못 하게 막아놓음
        showbloodtype.setEnabled(false);
        typephonenum.setEnabled(false);
        date.setEnabled(false);
        typeemail.setEnabled(false);
        typeweakness.setEnabled(false);
        gender.setEnabled(false);
        male.setEnabled(false);
        female.setEnabled(false);
        sendsosto.setEnabled(false);
        setDate.setVisibility(View.INVISIBLE);
        selbloodtype.setVisibility(View.INVISIBLE);

        sendsosto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SendSOSto_activicy.class);
                startActivity(intent);
            }
        });

        RadioGroup.OnCheckedChangeListener changeListener = new RadioGroup.OnCheckedChangeListener() {      //라디오 그룹에서 무엇을 선택했는지 확인하는 리스너
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getId()==R.id.gendergroup){
                    Log.i("태그", "라디오버튼 선택됨");
                    if(i==R.id.male_info){
                        showgender = "남자";
                    }else{
                        showgender = "여자";
                    }
                }
            }
        };
        gender.setOnCheckedChangeListener(changeListener);             //젠더 라디오그룹의 체인지리스너를 위에서 선언한 체인지리스너로 설정

        final Button edit = findViewById(R.id.edit);              //수정 버튼을 누를 때에 정보를 바꿀 수 있게 함
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editbtn==true){                                      //editbtn 이 참이면 각종 값들을 바꾸지 못하게 함.

                    Log.i("태그", "현재는 true 입니다.");
                    mypicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i("태그", "이미지뷰 클릭, 포토다이얼로그 실행");
                            photoDailog();
                        }
                    });

                    mypicture.setClickable(true);
                    typename.setEnabled(true);
                    typephonenum.setEnabled(true);
                    setDate.setVisibility(View.VISIBLE);
                    gender.setEnabled(true);
                    male.setEnabled(true);
                    female.setEnabled(true);
                    typeemail.setEnabled(true);
                    typeweakness.setEnabled(true);
                    selbloodtype.setEnabled(true);
                    sendsosto.setEnabled(true);
                    selbloodtype.setVisibility(View.VISIBLE);

                    edit.setText("저 장");
                    int fontcolor = Color.parseColor("#FFFFFF");        //글자 색상 HTML 을 정수값에 넣기
                    edit.setTextColor(fontcolor);                               //글자 색상을 정수값으로 설정
                    int color = Color.parseColor("#1C8ADB");             //버튼에 입힐 색상 선언
                    edit.setBackgroundColor(color);                        //버튼에 색상 입히기

                    Log.i("태그", "한번 더 누르면 값이 저장되겠지..");
                    editbtn=false;                              //editbtn을 false로 해서 수정가능하게 만들기

                    ArrayAdapter adapter = ArrayAdapter.createFromResource(Showinfo_activity.this, R.array.혈액형, android.R.layout.simple_spinner_item);        //스피너의 어뎁터 만들기
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //어뎁터 타입 지정
                    selbloodtype.setAdapter(adapter);       //스피너 어뎁터 설정

                    if(!showbloodtype.getText().toString().equals("")){
                        int location_spinner = adapter.getPosition(showbloodtype.getText().toString());
                        selbloodtype.setSelection(location_spinner);
                    }

                    selbloodtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {       //스피너 작동에 대한 구문.. 어떤 아이템을 선택했느냐를 보고 정함
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            showbloodtype.setText((String)selbloodtype.getItemAtPosition(i));
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                }else{                                          //editbtn이 거짓이면 각종 값들을 수정할 수 있게함.

                    //빈칸이 있는지 조건문으로 검사함.
                    if(typename.getText().toString().equals("")||showgender.equals("")||showbloodtype.getText().toString().equals("")||typephonenum.getText().toString().equals("")||
                            date.getText().toString().equals("")||typeemail.getText().toString().equals("")||typeweakness.getText().toString().equals("")){
                        Toast.makeText(Showinfo_activity.this, "누락된 값이 없도록 하십시오.", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.i("태그", "누락값이 없으면 수정하는 부분으로 들어옴");

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.i("태그", "온데이트 체인지 안");


                                User editeduser = new User(user.getId(), user.getPw(), typename.getText().toString(), showgender, showbloodtype.getText().toString(),
                                        typephonenum.getText().toString(), date.getText().toString(), typeemail.getText().toString(), typeweakness.getText().toString(), user.getImageUri());
                                Log.i("태그", "수정된 유저정보를 담은 유저클래스를 새로 만듦");

                                Map<String, Object> updateuser = editeduser.toMap();
                                Log.i("태그", "수정된 유저정보를 맵으로 바꿈");

                                databaseReference.updateChildren(updateuser);

                                if(mCurrentPhotoPath!=null||photoURI!=null){
                                    uploadImage();
                                }
                                Log.i("태그", "자식값들을 업데이트 함");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.i("태그", "수정 실패하면 이게 뜬다");
                            }
                        });
                        //Sharedpreferences에 넣기 위해 value값을 string 하나로 합친다. //Todo.. 파이어베이스로 넘어가면서 안씀
//                        String [] adress = typeemail.getText().toString().split("@");               //텍스트에 있는 이메일을 쪼개서 sharedprefereces에 넣기 위한 작업

//                        String editedinfo = typeid.getText().toString() + "@#@" + theuserinfoarray[1] + "@#@" + typename.getText().toString() + "@#@" + showgender + "@#@" +
//                                showbloodtype.getText().toString() + "@#@" + typephonenum.getText().toString() + "@#@" + date.getText().toString() + "@#@" + adress[0] + "@#@" + adress[1]  + "@#@" +
//                                typeweakness.getText().toString();
//
//                        editor.putString(LoginedUser.id, editedinfo);          //User 객체의 id를 받아와서 위에서 합친 value를 넣는다.
//                        editor.commit();            //저장

                        Log.i("태그", "현재는 false 입니다.");
                        mypicture.setClickable(false);
                        typename.setEnabled(false);
                        typephonenum.setEnabled(false);
                        setDate.setVisibility(View.INVISIBLE);
                        selbloodtype.setVisibility(View.INVISIBLE);
                        gender.setEnabled(false);
                        male.setEnabled(false);
                        female.setEnabled(false);
                        typeemail.setEnabled(false);
                        typeweakness.setEnabled(false);
                        selbloodtype.setEnabled(false);
                        sendsosto.setEnabled(false);
                        edit.setText("수 정");
                        int fontcolor = Color.parseColor("#000000");        //글자 색상 HTML 을 정수값에 넣기
                        edit.setTextColor(fontcolor);                               //글자 색상을 정수값으로 설정
                        int color = Color.parseColor("#BDBDBD");             //버튼에 입힐 색상 선언
                        edit.setBackgroundColor(color);                        //버튼에 색상 입히기
                        editbtn=true;                               //editbtn을 true로 해서 수정불가능하게 만들기

                        Toast.makeText(Showinfo_activity.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();

                        LoginedUser.name = typename.getText().toString();               //로그인 유저 클래스의 값을 바꿔줌.
                        LoginedUser.weakness = typeweakness.getText().toString();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("태그", "onResume in Showinfo_activity");

        setReceiverNames = "";
        Log.i("태그1", "onResume : "+setReceiverNames);
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("sosreceiver/"+LoginedUser.id);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("태그", "onDataChange in Showinfo");
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    String key = item.getKey();

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("users").child(key);
                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.i("태그", "Second onDataChange in Showinfo");
                            String receivername = dataSnapshot.child("name").getValue().toString();
                            Log.i("태그1", "receivername : "+receivername);

                            if(setReceiverNames.equals("")){
                                setReceiverNames = receivername;
                            }else {
                                setReceiverNames += ", "+receivername;
                            }
                            sendsosto.setText(setReceiverNames);
                            Log.i("태그1", "setReceivername : "+setReceiverNames);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if(!dataSnapshot.hasChildren()){
                    Log.i("태그1", "아무것도 없을 때");
                    sendsosto.setText(setReceiverNames);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void photoDailog(){                             //사진을 어디에서 가져올지 선택하게 되는 다이얼로그를 띄움
        AlertDialog.Builder builder = new AlertDialog.Builder(Showinfo_activity.this);          //빌더 선언
        builder.setMessage("어디에서 사진을 가져오실래요?");
        builder.setPositiveButton("앨범", new DialogInterface.OnClickListener() {                         //긍정버튼에 앨범에서 가져오기 옵션을 넣음
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Showinfo_activity.this, "앨범에서 가져오기를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_FROM_GALLERY);                //인텐트에 이미지 타입을 넣고 결과를 받아오는 인텐트를 실행
            }
        });
        builder.setNeutralButton("카메라", new DialogInterface.OnClickListener() {                     //중성버튼에 카메라에서 가져오기 옵션을 넣음
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Showinfo_activity.this, "카메라에서 가져오기를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
                captureCamera();
            }
        });
        builder.create().show();
    }

    private void captureCamera(){
        Log.i("태그", "captureCamera");
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){                        //외장메모리를 지원하는지 확인하는 구문
            Log.i("태그", state+"에 따라 들어오는 조건문");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);                //인텐트를 생성

            if(intent.resolveActivity(getPackageManager()) !=null){             //인텐트를 처리할 수 있는 앱이 있는지 확인! 있다면 들어오는 조건문
                Log.i("태그", "인텐트를 처리할 수 있는지 확인하고 들어오는 조건문");
                File photoFile = null;              //이미지 파일을 담을 파일 변수를 하나 만듦

                try {
                    photoFile = createImageFile();              //이미지 파일을 생성하는 메소드 리턴값으로 선언
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(photoFile !=null){                   //포토 파일이 널이 아니면 들어오는 조건문
                    cameraURI = FileProvider.getUriForFile(this, "com.project1.heydoc.fileprovider", photoFile);
                    Log.i("태그", "포토파일 "+photoFile.toString());
                    Log.i("태그", "카메라이미지URI "+cameraURI.toString());

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraURI);

                    startActivityForResult(intent, REQUEST_FROM_CAMERA);                //액티비티 시작
                }
            }
        }else{
            Toast.makeText(Showinfo_activity.this, "외장 메모리 미지원", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        Log.i("태그", "createImageFile");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());          //파일 이름을 유일하게 만들기 위해 시간을 불러옴

        imageFileName = "JPEG"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("태그", "실 주소 "+mCurrentPhotoPath);
        return image;          //파일을 반환
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){                   //requestcode 에 따라 나눠서 진행
            case REQUEST_FROM_CAMERA :
                Log.i("태그", "카메라로부터 들어옴");
                fromGallery = false;

                Log.i("태그", "현재 사진 위치 uri "+cameraURI);
                Log.i("태그", "현재 사진 위치"+mCurrentPhotoPath);

                mypicture.setImageURI(cameraURI);                               //이미지를 카메라 URI로 셋팅

                break;

            case REQUEST_FROM_GALLERY :
                Log.i("태그", "갤러리로부터 들어옴");
                fromGallery = true;

                File albumfile = null;                          //앨범 파일을 하나 만듦

                try {
                    albumfile = createImageFile();                          //빈 이미지 주소를 넣음

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(albumfile!=null){
                    galleryURI = Uri.fromFile(albumfile);                       //앨범파일 주소를 갤러리 URI로 지정
                }
                //Todo..위의 과정은 크롭을 위한 과정이었으나,, 현재 크롭 기능이 되지 않는 관계로 의미없는 행위임. 앨범에서는 바로 intent data URI로 가져오고 있음..

                if(data!=null){
                    photoURI = data.getData();
                    Log.i("태그", "photoURI "+photoURI);
                    Log.i("태그", "galleryURI "+galleryURI);
                    mypicture.setImageURI(photoURI);            //이게 이미지 띄우는 문장임
                    break;
                }

            default:
                Log.i("태그", "디폴트");
        }

    }

    private void uploadImage (){                                //이미지를 업로드 하기 위한 메소드
        Log.i("태그", "uploadImage");

        Uri fileUri = null;                         //파일 주소를 담을 공간을 하나 만듦!!

        if(fromGallery==true){
            fileUri = photoURI;                                         //갤러리에서 선택했으면, photoURI를 fileURI에 넣음
            Log.i("태그", "앨범에서 "+fileUri);
        }else {
            fileUri = Uri.fromFile(new File(mCurrentPhotoPath));         //사진에서 선택했으면, 현재주소에서 uri를 빼와 fileURI에 넣음
            Log.i("태그", "카메라에서 "+fileUri);
        }

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://heydoc-de775.appspot.com");         //내 파이어베이스 스토리지를 연다.
        final StorageReference storageReference = firebaseStorage.getReference();                               //주소를 가져온다.
        final StorageReference pictures = storageReference.child("Pictures/"+imageFileName);                            //Pictures라는 곳에 파일이름으로 위치를 가져온다.

        final UploadTask uploadTask;            //업로드 작업을 위한 변수

        uploadTask = pictures.putFile(fileUri);         //fileURI에 담겨진 파일을 넣는다.

        uploadTask.addOnFailureListener(new OnFailureListener() {           //실패하면 들어오는 구문
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("태그", "이미지 업로드 실패");
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {          //성공하면 들어오는 구문
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("태그", "이미지 업로드 성공");
                pictures.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {               //다운로드 URI를 받기 위해서 하는 작업
                    @Override
                    public void onSuccess(Uri uri) {
                        uriForDownload = uri;                                                       //다운로드 URI를 따로 유저 정보에 넣어줘야 한다.
                        Log.i("태그", "다운로드 URI는 "+uriForDownload);
                        databaseReference.child("imageUri").setValue(String.valueOf(uriForDownload));               //넣는 작업
                    }
                });
            }
        });
    }



}
