package com.project1.heydoc.Consult;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ConsultMember_Data {

    ImageView memberimg;
    String uri;
    String membername;
    String memberid;

    public ConsultMember_Data(ImageView memberimg, String membername, String memberid){          //생성자
        this.memberimg = memberimg;
        this.membername = membername;
        this.memberid = memberid;
    }

    public ConsultMember_Data(String uri, String membername, String memberid){          //생성자
        this.uri = uri;
        this.membername = membername;
        this.memberid = memberid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ImageView getMemberimg() {           //멤버이미지 게터
        return memberimg;
    }

    public void setMemberimg(ImageView memberimg) {         //멤버이미지 세터
        this.memberimg = memberimg;
    }

    public String getMembername() {             //멤버네임 게터
        return membername;
    }

    public void setMembername(String membername) {              //멤버네임 세터
        this.membername = membername;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }
}
