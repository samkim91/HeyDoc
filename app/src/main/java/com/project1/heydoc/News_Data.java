package com.project1.heydoc;

import android.widget.ImageView;
import android.widget.TextView;

public class News_Data {

    String newsImg;
    String newsText;
    String newsURL;

    public News_Data(String newsImg, String newsText, String newsURL) {            //뉴스 데이터를 초기화할 수 있는 생성자
        this.newsImg = newsImg;
        this.newsText = newsText;
        this.newsURL = newsURL;
    }

    //이 아래는 뉴스 이미지와 텍스트에 대한 겟터, 셋터 정의


    public String getNewsURL() {
        return newsURL;
    }

    public void setNewsURL(String newsURL) {
        this.newsURL = newsURL;
    }

    public String getNewsImg() {
        return newsImg;
    }

    public void setNewsImg(String newsImg) {
        this.newsImg = newsImg;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }
}
