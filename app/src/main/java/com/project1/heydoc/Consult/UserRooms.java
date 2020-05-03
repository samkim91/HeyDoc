package com.project1.heydoc.Consult;

import java.util.HashMap;
import java.util.Map;

public class UserRooms {

    String lastMsg;
    String lastMsgTime;

    Map<String, Object> talkerlist;

//    String myId;
//    String myName;
//    String uId;
//    String uName;

    public UserRooms(){

    }

    public UserRooms(String lastMsg, String lastMsgTime, String myId, String myName, String myImgUri, String uId, String uName, String uImgUri){         //유저룸의 생성자
        this.lastMsg = lastMsg;
        this.lastMsgTime = lastMsgTime;
        this.talkerlist = maketalkerlist(myId, myName, myImgUri, uId, uName, uImgUri);
    }

    public Map<String, Object> toMap(){                                     //데이터베이스에 넣을 때 해쉬맵 형태로 넣기 위한 메소드

        HashMap<String, Object> result = new HashMap<>();

        result.put("lastMsg", lastMsg);
        result.put("lastMsgTime", lastMsgTime);
        result.put("talkerlist", talkerlist);

        return result;
    }


    public Map<String, Object> maketalkerlist(String myId, String myName, String myImgUri, String uId, String uName, String uImgUri){            //채팅방 참여 인원들의 리스트를 만들기 위한 메소드
        HashMap<String, Object> result = new HashMap<>();

        result.put("myId", myId);
        result.put("myName", myName);
        result.put("myImgUri", myImgUri);
        result.put("uId", uId);
        result.put("uName", uName);
        result.put("uImgUri", uImgUri);

        return result;
    }


    //이 밑으로는 유저룸 클래스가 가진 속성들의 게터/셋터
    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public Map<String, Object> getTalkerlist() {
        return talkerlist;
    }

    public void setTalkerlist(Map<String, Object> talkerlist) {
        this.talkerlist = talkerlist;
    }
}
