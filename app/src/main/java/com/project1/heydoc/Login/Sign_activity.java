package com.project1.heydoc.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project1.heydoc.R;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.regex.Pattern;

public class Sign_activity extends AppCompatActivity {


    private static final String TAG = "MA";

    EditText typeid, typepw, retypepw, typename, typephonenum, showdate, email, domain, pastweakness;         //에딭텍스트 내용 필드변수 선언
    RadioGroup selgender;
    String showgender;
    String showbloodtype;

    Button checkId;
    ImageView mypicture;
    Boolean availableId;
    Boolean checkIdDone = false;
    Boolean availablePw = false;
    Boolean availableEmail = false;
    Boolean availableDomain = false;

    int year, month, day;

    String imageFileName = null;
    final int REQUEST_FROM_CAMERA = 100;
    final int REQUEST_FROM_GALLERY = 200;
    final int REQUEST_TO_CROP = 300;
    String mCurrentPhotoPath;
    Uri cameraURI, galleryURI;
    Uri photoURI;
    boolean fromGallery = false;

    Uri uriForDownload;


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign);

        mypicture = findViewById(R.id.mypicture);
        mypicture.setImageResource(R.drawable.photo);

        typeid = findViewById(R.id.typeId);                     //가입화면 에딭텍스트 및 버튼들 매칭 및 인스턴스화
        checkId = findViewById(R.id.checkId);

        typepw = findViewById(R.id.typePw);
        retypepw = findViewById(R.id.retypePw);
        typename = findViewById(R.id.typeName);
        selgender = findViewById(R.id.selgender);
        pastweakness = findViewById(R.id.pastweakness);



        //프로필 사진을 넣을 이미지뷰에 클릭 속성을 넣음. 클릭하면 이미지를 카메라로 찍을지, 앨범에서 가져올지 선택할 수 있는 다이얼로그(메소드)가 실행됨
        mypicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("태그", "이미지뷰 클릭, 포토다이얼로그 실행");
                photoDailog();
            }
        });

        checkId.setEnabled(false);
        //아이디가 정규식에 맞는지 확인함.
        final TextView checkIdFine = findViewById(R.id.checkIdFine);
        typeid.addTextChangedListener(new TextWatcher() {                                   //아이디 입력값이 변화되는지 확인하기 위한 텍스트 와쳐
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkIdDone = false;
                if(!Pattern.matches("^[a-zA-Z0-9]{5,20}$", typeid.getText().toString())){
                    //정규식 조건은 5~20자로 이루어져야하며, 알파벳(대소문자) 또는 숫자여야만 한다.
                    checkIdFine.setVisibility(View.VISIBLE);
                    checkId.setEnabled(false);
                }else {
                    checkIdFine.setVisibility(View.INVISIBLE);
                    checkId.setEnabled(true);
                }
            }
        });


        //아이디 중복검사를 하는 곳

        checkId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                //중복확인 버튼의 클릭 했을 때 실행되는 메소드

                Log.i("태그", "중복확인 버튼 클릭");
                availableId = true;
                valueEventListener();           //파이어베이스에서 아이디 중복검사 하는 것

//                SharedPreferences userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);            //유저 정보 쉐어드프리퍼런스를 선언한다.
//                Map<String, ?> allusers = userinfo.getAll();            //모든 유저의 정보를 가져온다.
//                Log.i("태그", "클릭 리스너");
//                availableId=true;                                  //사용 가능한 아이디인지 알려주는 불린 값
//
//                if(typeid.getText().toString().equals("")){
//                    Toast.makeText(Sign_activity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();          //아무것도 입력하지 않는다면 안내해준다.
//                }else{
//                    for(Map.Entry<String, ?> entry : allusers.entrySet()){                      //모든 유저 정보가 들어있는 맵을 반복하여 기존 아이디인지 확인한다.
//                        Log.i("태그", "반복문 인");
//
//                        String userinforaw = entry.getValue().toString();                   //한 엔트리의 스트링을 가져옴.
//                        String [] userinfoarr = userinforaw.split("@#@");             //이 스트링을 쪼개서 배열에 넣음
//
//                        if(typeid.getText().toString().equals(userinfoarr[0])){             //배열 중 인덱스로 아이디를 뽑아서 입력된 값과 비교함
//                            Log.i("태그", "이미 아이디가 있으면 출력");
//                            Toast.makeText(Sign_activity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
//                            availableId = false;                                    //사용 가능한 아이디가 아님을 알게해줌.
//
//                        }else {
//                            Log.i("태그", "이미 아이디가 없으면 출력");
//                        }
//                    }
//                    if(availableId==true){
//                        Toast.makeText(Sign_activity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
//                        checkIdDone = true;
//                    }
//                }
            }
        });

        //비밀번호가 정규식을 지키는지 확인하는 곳
        final TextView checkPw = findViewById(R.id.checkPw);
        typepw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", typepw.getText().toString())){
                    checkPw.setVisibility(View.VISIBLE);
                    availablePw = false;
                }else {
                    checkPw.setVisibility(View.INVISIBLE);
                    availablePw = true;

                }
                Log.i("태그", "비밀번호 사용가능한지 "+availablePw);
            }
        });


        final TextView matchpw = findViewById(R.id.matchpw);          //비밀번호와 비밀번호확인 텍스트가 일치하는지 보고 일치여부를 나타내줌
        retypepw.addTextChangedListener(new TextWatcher() {           //텍스트왓쳐라는 메소드와, 텍스트체인지리스너를 사용함.
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(typepw.getText().toString().equals(retypepw.getText().toString())){
                    matchpw.setVisibility(View.INVISIBLE);
                    Log.i("태그", "일치하면 실행");
                }
                else{
                    matchpw.setVisibility(View.VISIBLE);
                }
            }
        });

        RadioGroup.OnCheckedChangeListener changeListener = new RadioGroup.OnCheckedChangeListener() {      //라디오 그룹에서 무엇을 선택했는지 확인하는 리스너
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getId()==R.id.selgender){
                    if(i==R.id.male){
                        showgender = "남자";
//                        Toast.makeText(Sign_activity.this, showgender, Toast.LENGTH_SHORT).show();
                    }else{
                        showgender = "여자";
//                        Toast.makeText(Sign_activity.this, showgender, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        selgender.setOnCheckedChangeListener(changeListener);

//        int selgen = selgender.getCheckedRadioButtonId();       //라디오 버튼 선택값 선언
//        RadioButton radioButton = findViewById(selgen);         //라디오 버튼의 선택된 것 지정
//        showgender = radioButton.getText().toString();          //선택된 라디오버튼 값 저장

        typephonenum = findViewById(R.id.typePhoneNum);
//        showbloodtype = findViewById(R.id.showbloodtype);
        showdate = findViewById(R.id.showdate);
        email = findViewById(R.id.email);
        domain = findViewById(R.id.domain);

        showdate.setEnabled(false);     //생년월일 수정 못하게

        final Spinner selbloodtype = findViewById(R.id.selbloodtype);       //스피너 xml과 지정
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.혈액형, android.R.layout.simple_spinner_item);        //스피너의 어뎁터 만들기
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //어뎁터 타입 지정
        selbloodtype.setAdapter(adapter);       //스피너 어뎁터 설정

        selbloodtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {       //스피너 작동에 대한 구문.. 어떤 아이템을 선택했느냐를 보고 정함
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showbloodtype = (String)selbloodtype.getItemAtPosition(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                showbloodtype.setText("");
            }
        });

        Calendar calendar = new GregorianCalendar();            //달력 초기화
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Button setDate = findViewById(R.id.setdate);        //달력 선택 버튼

        setDate.setOnClickListener(new View.OnClickListener() {                 //달력 보여주는 문장
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {           //날짜리스너 선언
                    @Override
                    public void onDateSet(DatePicker datePicker, int setyear, int setmonth, int setday) {
                        year = setyear;
                        month = setmonth;
                        day = setday;
                        upDateNow();
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(Sign_activity.this, onDateSetListener, year, month, day);       //날짜 선택 다이얼로그 선언 및 보여주기
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());      //오늘 이후로는 안 보이게 함
                datePickerDialog.show();        //데이트피커다이얼로그 보이게 하기.
            }
        });


        //이메일의 유효성을 검사하는 곳
        final TextView checkmail = findViewById(R.id.checkmail);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Pattern.matches("^[a-zA-Z0-9]+$", email.getText().toString())){
                    checkmail.setVisibility(View.VISIBLE);
                    checkmail.setText("* 유효한 이메일을 사용해주세요.");
                    availableEmail = false;
                }else {
                    checkmail.setVisibility(View.INVISIBLE);
                    availableEmail = true;
                }
                Log.i("태그", "이메일 사용가능한지 "+availableEmail);
            }
        });
        domain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Pattern.matches("^[0-9a-z]+\\.[a-z]{2,3}$", domain.getText().toString())){
                    checkmail.setText("* 유효한 도메인을 사용해주세요.");
                    checkmail.setVisibility(View.VISIBLE);
                    availableDomain = false;
                }else {
                    checkmail.setVisibility(View.INVISIBLE);
                    availableDomain = true;
                }
                Log.i("태그", "도메인 사용가능한지 "+availableDomain);
            }
        });

//        SharedPreferences userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);    //유저인포 쉐어드프리퍼런스 선언
//        final SharedPreferences.Editor editor = userinfo.edit();      //에디터 선언

        final Button signup = findViewById(R.id.signClk);                     //회원가입 버튼 누를 때
        signup.setOnClickListener(new View.OnClickListener() {

//            public void SaveUserInfo(){            //유저 인포를 저장하는 함수
//                String usertotalinfo;
//                usertotalinfo = typeid.getText().toString() + "@#@" + typepw.getText().toString() + "@#@" + typename.getText().toString() + "@#@" +
//                        showgender + "@#@" + showbloodtype + "@#@" + typephonenum.getText().toString() + "@#@" + showdate.getText().toString() +
//                        "@#@" + email.getText().toString() + "@#@" + domain.getText().toString() + "@#@" + pastweakness.getText().toString();
//
//                String key = typeid.getText().toString();       //키 값을 아이디로 지정
//
//                editor.putString(key, usertotalinfo);     //키값에 유저 총 인포를 넣음
//
//                editor.commit();        //저장
//
//            }

            @Override
            public void onClick(View view) {

                if(checkIdDone==false||availableId==false){
                    Toast.makeText(Sign_activity.this, "아이디 중복검사를 해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    if(typeid.getText().toString().equals("")||typepw.getText().toString().equals("")||retypepw.getText().toString().equals("")||typename.getText().toString().equals("")||
                            showgender.equals("")||showbloodtype.equals("")||typephonenum.getText().toString().equals("")||showdate.getText().toString().equals("")||email.getText().toString().equals("")||
                            domain.getText().toString().equals("")){                                                                                    //빈칸이 있는지 없는지 검사하는 조건문
                        Toast.makeText(Sign_activity.this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();            //빈칸이 있으면 다시 입력하라는 메시지가 뜬다.
                    }else{
                        Log.i("태그", "비밀번호 가능? "+availablePw);
                        Log.i("태그", "이메일 가능? "+availableEmail);
                        Log.i("태그", "도메인 가능? "+availableDomain);
                        if(!typepw.getText().toString().equals(retypepw.getText().toString())){
                            Toast.makeText(Sign_activity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();          //비밀번호가 비밀번호 확인과 같이 않으면 안내문 띄우기.
                        }else {
                            if(typepw.getText().toString().equals(retypepw.getText().toString())&&availablePw==true&&availableEmail==true&&availableDomain==true){                  //빈칸이 없으면 이 구간으로 들어와서, 비밀번호와 비밀번호 확인란이 같은지 본다.

                                if(mCurrentPhotoPath!=null||photoURI!=null){
                                    uploadImage();
                                }
                                signNewId();                                                             //새로운 아이디를 파이어베이스에 저장 하는 메소드
                                Log.i("태그", "파이어베이스 저장성공");
                                Intent intent = new Intent(getApplicationContext(), Login_activity.class);
                                Toast.makeText(Sign_activity.this, "가입이 되었습니다.", Toast.LENGTH_SHORT).show();
                                Log.i("태그", "가입성공");
                                startActivity(intent);

                            }else{
                                Toast.makeText(Sign_activity.this, "입력하신 내용이 올바른지 다시 확인해주세요.", Toast.LENGTH_SHORT).show();          //뭔가 중간에 변경되면 띄우는 토스트
                            }
                        }
                    }

                }

            }

        });

        Button cancel = findViewById(R.id.signcncl);                    //취소버튼 누를때
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent incancel = new Intent(getApplicationContext(), Login_activity.class);
                startActivity(incancel);
            }
        });
    }

    void upDateNow(){
        showdate.setText(String.format("%d - %d - %d", year, month+1, day));
    }

    @Override
    protected void onPause() {                                      //가입화면 에딭텍스트 내용들을 임시 저장
        super.onPause();
//        Toast.makeText(this, "저장됨", Toast.LENGTH_LONG).show();
//        saveState();
    }

    @Override
    protected void onResume() {                                     //가입화면 에딭텍스트 내용들을 불러오기
        super.onResume();
//        Toast.makeText(this, "복구됨", Toast.LENGTH_LONG).show();
//        restoreState();
    }

    @Override
    protected void onDestroy() {
        //clearState();

        super.onDestroy();
    }

    private void photoDailog(){                             //사진을 어디에서 가져올지 선택하게 되는 다이얼로그를 띄움
        AlertDialog.Builder builder = new AlertDialog.Builder(Sign_activity.this);          //빌더 선언
        builder.setMessage("어디에서 사진을 가져오실래요?");
        builder.setPositiveButton("앨범", new DialogInterface.OnClickListener() {                         //긍정버튼에 앨범에서 가져오기 옵션을 넣음
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Sign_activity.this, "앨범에서 가져오기를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Sign_activity.this, "카메라에서 가져오기를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
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
                    //MediaStore.. 클래스로, media provider와 app간의 계약? URI와 column을 지원하기 위한 정의를 포함함.
                    //EXTRA_OUTPUT.. 요청된 이미지 또는 비디오를 저장하는데 사옹되는 content resolver Uri를 가르키는 intent-extra 이름

                    startActivityForResult(intent, REQUEST_FROM_CAMERA);                //액티비티 시작
                }
            }
        }else{
            Toast.makeText(Sign_activity.this, "외장 메모리 미지원", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        Log.i("태그", "createImageFile");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());          //파일 이름을 유일하게 만들기 위해 시간을 불러옴

        imageFileName = "JPEG"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//        String imageFileName = timeStamp + ".jpg";          //파일 이름을 사진(jpg) 형식으로 정함
//        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/"+imageFileName);  //어떠한 절대 주소에 위의 이름을 가진 파일을 생성함

        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("태그", "실 주소 "+mCurrentPhotoPath);
        return image;          //파일을 반환
    }

//안드로이드 업그레이드로 인해 전혀 필요 없어진 기능
//    private void galleryAddPic(){                       //갤러리 새로고침을 위해 사용함.
//        Log.i("태그", "galleryAddPic");
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File file = new File(mCurrentPhotoPath);
//
//        Uri contentUri = Uri.fromFile(file);
//        intent.setData(contentUri);
//        this.sendBroadcast(intent);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_FROM_CAMERA :
                Log.i("태그", "카메라로부터 들어옴");
                fromGallery = false;

                Log.i("태그", "현재 사진 위치 uri "+cameraURI);
                Log.i("태그", "현재 사진 위치"+mCurrentPhotoPath);
//                File file = new File(mCurrentPhotoPath);
//                Uri uri = Uri.fromFile(file);
//                mypicture.setImageURI(uri);

                mypicture.setImageURI(cameraURI);

                break;

            case REQUEST_FROM_GALLERY :
                Log.i("태그", "갤러리로부터 들어옴");
                fromGallery = true;

                File albumfile = null;

                try {
                    albumfile = createImageFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(albumfile!=null){
                    galleryURI = Uri.fromFile(albumfile);
                }
                photoURI = data.getData();
                Log.i("태그", "photoURI "+photoURI);
                Log.i("태그", "galleryURI "+galleryURI);
                mypicture.setImageURI(photoURI);            //이게 이미지 띄우는 문장임
//                cropImage();
//                Uri uri = data.getData();
//                mypicture.setImageURI(uri);
                break;
//            case REQUEST_TO_CROP :
//                Log.i("태그", "크랍한 뒤에 들어옴");
//                Log.i("태그", "photoURI "+photoURI);
//                Log.i("태그", "galleryURI "+galleryURI);
//
//
//                mypicture.setImageURI(galleryURI);
//                Uri uri = data.getData();
//                break;

            default:
                Log.i("태그", "디폴트");
//                mypicture.setImageResource(R.drawable.photo);
        }

    }

    private void uploadImage (){
        Log.i("태그", "uploadImage");

        Uri fileUri = null;

        if(fromGallery==true){
            fileUri = photoURI;
            Log.i("태그", "앨범에서 "+fileUri);
        }else {
            fileUri = Uri.fromFile(new File(mCurrentPhotoPath));
            Log.i("태그", "카메라에서 "+fileUri);
        }

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://heydoc-de775.appspot.com");
        final StorageReference storageReference = firebaseStorage.getReference();
        final StorageReference pictures = storageReference.child("Pictures/"+imageFileName);

        final UploadTask uploadTask;

        uploadTask = pictures.putFile(fileUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("태그", "이미지 업로드 실패");
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("태그", "이미지 업로드 성공");
                pictures.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uriForDownload = uri;
                        Log.i("태그", "다운로드 URI는 "+uriForDownload);
                        databaseReference.child(typeid.getText().toString()).child("imageUri").setValue(String.valueOf(uriForDownload));

                    }
                });
            }
        });
    }

    private void cropImage(){                               //애뮬레이터에서 사진이 뜨지 않는 기이한 현상이 일어남...
        Log.i("태그", "cropImage");
        try{
            Log.i("태그", "photoURI "+photoURI);
            Log.i("태그", "galleryURI "+galleryURI);

            Intent intent = new Intent("com.android.camera.action.CROP");           //크랍 기능 사용할 수 있는 인텐트를 생성함

            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);                //이 인텐트에 uri를 읽고 쓰는 권한을 추가함
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.putExtra("outputX", 128);
//            intent.putExtra("outputY", 128);
            intent.putExtra("aspectX", 1);              //가로 세로 비율이 1임.
            intent.putExtra("aspectY", 1);
            intent.setDataAndType(photoURI, "image/*");         //앨범에서 빼왔던 phothURI를 데이터로 가짐
            intent.putExtra("scale", true);         //크롭 스케일을 조정할 수 있는지 정함.
            intent.putExtra("output", galleryURI);                          //결과(output)는 galleryURI에 저장함.

            startActivityForResult(intent, REQUEST_TO_CROP);            //실행!
        }catch (ActivityNotFoundException e){
            Toast.makeText(Sign_activity.this, "사진 편집이 지원되지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
        }


    }

    //    @Override         //카메라로 찍은 이미지 처리하는데 제대로 작동하지 않아서 다시 함.
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Log.i("태그", "onActivityResult");
//        Log.i("태그", requestCode+"/"+resultCode+"/"+data);
//
//        if(requestCode == REQUEST_FROM_GALLERY && resultCode == RESULT_OK){
//
//            imageUri = data.getData();
//            Log.i("태그", "앨범에서 가져왔을 때 들어오는 조건문 : "+galleryURI.toString());
//
//            mypicture.setImageURI(galleryURI);
//
//        }else if(requestCode == REQUEST_FROM_CAMERA && resultCode == RESULT_OK && data.hasExtra("data")){
//            Log.i("태그", "카메라");
//            cameraURI = data.getData();
//            Log.i("태그", "카메라에서 가져왔을 때 들어오는 조건문 : "+cameraURI.toString());
//            mypicture.setImageURI(cameraURI);
//        }
//    }

    private void valueEventListener(){
        ValueEventListener valueEventListener = new ValueEventListener() {                          //데이터베이스 안에 있는 값을 읽기 위한 벨류 이벤트 리스너를 선언
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("태그", "온 데이터 체인지 메소드 들어옴");
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();               //차일드 값들을 반복하기 위해 이터레이터에 넣음

                if(typeid.getText().toString().equals("")){
                    Toast.makeText(Sign_activity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();          //아무것도 입력하지 않는다면 안내해준다.
                }else{
                    while (child.hasNext()){                                //끝날때까지 반복함
                        Log.i("태그", "반복문 들어옴");
                        if (typeid.getText().toString().equals(child.next().getKey())){             //차일드 중에 키 값이 같은 게 있다면 들어오는 조건문
                            Log.i("태그", "이미 존재하는 아이디로 available는 false가 됨");
                            databaseReference.removeEventListener(this);                                //이 이벤트 리스터를 삭제
                            availableId = false;                                            //availableId 를 false값으로 정함
                            break;
                        }
                    }
                    if(availableId==true){                                                              //availableId boolean 값에 의해 토스트를 보내주는 조건문
                        Toast.makeText(Sign_activity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                        checkIdDone = true;
                    }else{
                        Toast.makeText(Sign_activity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {             //읽기 실패하면 오는 곳
                Log.i("태그", "온 캔슬드.. 읽기 실패");
            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);               //데이터베이스 리퍼런스를 한번만 불러서 사용하기 때문에 싱글 밸류 이벤트 리스너를 추가함.
        Log.i("태그", "밸류이벤트리스너를 데이터리퍼런스에 추가함");
    }

    private void signNewId(){
        String emailadr = email.getText().toString() + "@" + domain.getText().toString();               //이메일을 합치는 스트링
        Log.i("태그", "signNewId"+uriForDownload);

        databaseReference.child(typeid.getText().toString()).child("id").setValue(typeid.getText().toString());                     //데이터베이스 리퍼런스를 통해서 이 아이디를 키로하고, 차일드로 각 키와 밸류를 넣는 과정
        databaseReference.child(typeid.getText().toString()).child("pw").setValue(typepw.getText().toString());
        databaseReference.child(typeid.getText().toString()).child("name").setValue(typename.getText().toString());
        databaseReference.child(typeid.getText().toString()).child("gender").setValue(showgender);
        databaseReference.child(typeid.getText().toString()).child("bloodtype").setValue(showbloodtype);
        databaseReference.child(typeid.getText().toString()).child("phonenum").setValue(typephonenum.getText().toString());
        databaseReference.child(typeid.getText().toString()).child("birthday").setValue(showdate.getText().toString());
        databaseReference.child(typeid.getText().toString()).child("email").setValue(emailadr);
        databaseReference.child(typeid.getText().toString()).child("weakness").setValue(pastweakness.getText().toString());
    }

//    protected void saveState(){                                     //쉐어드프리퍼런스를 이용해서 가입화면 에딭텍스트 내용들을 저장하는 메소드
//        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("id", typeid.getText().toString());
//        editor.putString("pw", typepw.getText().toString());
//        editor.putString("repw", retypepw.getText().toString());
//        editor.putString("name", typename.getText().toString());
//        editor.putString("phonenum", typephonenum.getText().toString());
//        editor.putString("showdate", showdate.getText().toString());
//        editor.putString("email", email.getText().toString());
//        editor.putString("domain", domain.getText().toString());
//        editor.commit();
//    }
//
//    protected void restoreState(){                                  //쉐어드 프리퍼런스를 이용해서 저장되었던 에딭텍스트들을 복구하는 메소드
//        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
//        if((pref!=null) && (pref.contains("id")) ) {
//            String id = pref.getString("id", "");
//            typeid.setText(id);
//
//        }
//        if(pref!=null && pref.contains("pw")){
//            String pw = pref.getString("pw", "");
//            typepw.setText(pw);
//        }
//
//        if(pref!=null && pref.contains("repw")){
//            String repw = pref.getString("repw", "");
//            retypepw.setText(repw);
//        }
//
//        if(pref!=null && pref.contains("name")){
//            String name = pref.getString("name", "");
//            typename.setText(name);
//        }
//
//        if(pref!=null && pref.contains("phonenum")){
//            String phonenum = pref.getString("phonenum", "");
//            typephonenum.setText(phonenum);
//        }
//
//        if(pref!=null && pref.contains("showdate")){
//            String loaddate = pref.getString("showdate", "");
//            showdate.setText(loaddate);
//        }
//
//        if(pref!=null && pref.contains("email")){
//            String loademail = pref.getString("email", "");
//            Log.v("태그", "이메일 불러오기");
//            email.setText(loademail);
//        }
//
//        if(pref!=null && pref.contains("domain")){
//            String loaddomain = pref.getString("domain", "");
//            Log.v("태그", "도메인 불러오기");
//            domain.setText(loaddomain);
//        }
//    }
//
//    protected void clearState(){                                                    //쉐어드 프리퍼런스 내부공간 비우기
//        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.clear();
//        editor.commit();
//    }


}
