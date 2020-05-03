package com.project1.heydoc.Login;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {

    String id;
    String pw;
    String name;
    String gender;
    String bloodtype;
    String phonenum;
    String birthday;
    String email;
    String weakness;

    String imageUri;


    public User(){

    }

    public User(String id, String pw, String name, String gender, String bloodtype, String phonenum, String birthday, String email, String weakness, String imageUri){
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.gender = gender;
        this.bloodtype = bloodtype;
        this.phonenum = phonenum;
        this.birthday = birthday;
        this.email = email;
        this.weakness = weakness;
        this.imageUri = imageUri;
    }

    public User(String id, String name, String phonenum, String birthday, String email, String imageUri){
        this.id = id;
        this.name = name;
        this.phonenum = phonenum;
        this.birthday = birthday;
        this.email = email;
        this.imageUri = imageUri;
    }

    @Exclude
    public Map<String, Object> toMap(){                             // 회원가입을 하고 회원정보 수정할 때  각 값들을 해쉬맵으로 만들기 위한 메소드
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("pw", pw);
        result.put("name", name);
        result.put("gender", gender);
        result.put("bloodtype", bloodtype);
        result.put("phonenum", phonenum );
        result.put("birthday", birthday);
        result.put("email", email);
        result.put("weakness", weakness);
        result.put("imageUri", imageUri);

        return result;
    }

    @Exclude
    public Map<String, Object> toMap1(){                        //친구 추가할 때 각 값들을 해쉬맵으로 만들기 위한 메소드
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("phonenum", phonenum);
        result.put("birthday", birthday);
        result.put("email", email);
        result.put("imageUri", imageUri);

        return result;
    }


    // 이 아래는 유저 클래스 각 속성들의 게터/셋터임..


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodtype() {
        return bloodtype;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeakness() {
        return weakness;
    }

    public void setWeakness(String weakness) {
        this.weakness = weakness;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
