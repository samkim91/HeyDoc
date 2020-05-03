package com.project1.heydoc.Record;

import java.io.Serializable;

public class Record_Data implements Serializable {              //기록 데이터를 저장하는 양식을 만듦
    String subject;
    String date;
    String section;
    String detail;
    String attach;
    String thistime;




    public Record_Data(String subject, String date, String section, String detail, String attach, String thistime){     //레코드 데이터의 생성자
        this.subject = subject;
        this.date = date;
        this.section = section;
        this.detail = detail;
        this.attach = attach;
        this.thistime = thistime;
    }

    public String getThistime() {                                   //시간 불러오기
        return thistime;
    }

    public void setThistime(String thistime) {                      //시간 세팅하기
        this.thistime = thistime;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }           //제목 셋터

    public void setDate(String date) {
        this.date = date;
    }                       //날짜 셋터

    public void setSection(String section) {
        this.section = section;
    }           //진료과 셋터

    public void setDetail(String detail) {
        this.detail = detail;
    }               //세부내용 셋터

    public String getAttach() {                                                  //첨부파일 게터
        return attach;
    }

    public void setAttach(String attach) {                                      //첨부파일 세터
        this.attach = attach;
    }

    public String getSubject() {
        return subject;
    }                               //제목 겟터

    public String getDate() {
        return date;
    }                                      //날짜 겟터

    public String getSection() {
        return section;
    }                               //진료과 겟터

    public String getDetail() {
        return detail;
    }                                 //세부내용 겟터
}
