package com.project1.heydoc.Consult;

import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

public class Consult_Data {

//    ImageView img;
    String uri;
    String name;              //상담 데이터 변수
    String id;
    String text;
    String time;

    public Consult_Data(){

    }

    public Consult_Data(String uri, String name, String id, String text, String time){  //상담 데이터의 생성자
        this.uri = uri;
        this.name = name;
        this.id = id;
        this.text = text;
        this.time = time;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("uri", uri);
        result.put("name", name);
        result.put("id", id);
        result.put("text", text);
        result.put("time", time);

        return result;
    }

    //여기서 부터는 이 클래스가 가진 속성들의 게터/세터임

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

//    public ImageView getImg() {
//        return img;
//    }
//
//    public void setImg(ImageView img) {
//        this.img = img;
//    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
