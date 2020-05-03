package com.project1.heydoc.SendSOS;

import android.widget.ImageView;

public class SendSOSto_Data {

    //SendSOSto라는 리사이클러뷰가 가질 하나의 아이템을 나타낼 변수들을 만듦.
    String uri;
    String receivername;
    String receiverid;
    String token;

    //이 클래스의 생성자.. 추후 리사이클러뷰 addItem에서 인스턴스화되는 양식
    public SendSOSto_Data(String uri, String receivername, String receiverid, String token){
        this.uri = uri;
        this.receivername = receivername;
        this.receiverid = receiverid;
        this.token = token;
    }


    //이 아래로는 게터와 세터
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getReceivername() {
        return receivername;
    }

    public void setReceivername(String receivername) {
        this.receivername = receivername;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
