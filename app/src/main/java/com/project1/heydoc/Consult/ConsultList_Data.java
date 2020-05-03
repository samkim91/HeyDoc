package com.project1.heydoc.Consult;

import android.widget.ImageView;

public class ConsultList_Data {

    String uri;
    String personname;
    String personid;
    String showmessage;
    String lastmsgtime;
    String roomid;

    public ConsultList_Data(){

    }

    public ConsultList_Data(String uri, String personname, String personid, String showmessage, String lastmsgtime, String roomid){          //생성자
        this.uri = uri;
        this.personname = personname;
        this.personid = personid;
        this.showmessage = showmessage;
        this.lastmsgtime = lastmsgtime;
        this.roomid = roomid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPersonid() {                                       //상대 아이디 게터
        return personid;
    }

    public void setPersonid(String personid) {                          //상대 아이디 셋터
        this.personid = personid;
    }

    public String getRoomid(){                          //룸 아이디 겟터
        return roomid;
    }

    public void setRoomid(String roomid){                   //룸 아이디 셋터
        this.roomid = roomid;
    }

    public String getLastmsgtime() {                //마지막 메시지 시간 게터
        return lastmsgtime;
    }

    public void setLastmsgtime(String lastmsgtime) {           //마지막 메시지 시간 셋터
        this.lastmsgtime = lastmsgtime;
    }


    public String getPersonname() {
        return personname;
    }           //퍼슨네임 게터

    public void setPersonname(String personname) {
        this.personname = personname;
    }       //퍼슨네임 세터

    public String getShowmessage() {                //쇼메시지 게터
        return showmessage;
    }

    public void setShowmessage(String showmessage) {            //쇼메시지 세터
        this.showmessage = showmessage;
    }
}
