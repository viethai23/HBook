package com.example.hbookdemo.object;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chuong implements Comparable<Chuong>, Serializable {

    private String tenChuong, urlChuong;

    public Chuong(String tenChuong, String urlChuong) {
        this.tenChuong = tenChuong;
        this.urlChuong = urlChuong;
    }

    public String getTenChuong() {
        return tenChuong;
    }

    public void setTenChuong(String tenChuong) {
        this.tenChuong = tenChuong;
    }

    public String getUrlChuong() {
        return urlChuong;
    }

    public void setUrlChuong(String urlChuong) {
        this.urlChuong = urlChuong;
    }


    @Override
    public int compareTo(Chuong chuong) {
        String soChuong1 = this.getTenChuong();
        String soChuong2 = chuong.getTenChuong();

        return getSoChuong(soChuong1) - getSoChuong(soChuong2);
    }

    private int getSoChuong(String s) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(s);
        List<Integer> numbers = new ArrayList<>();

        while (matcher.find()) {
            numbers.add(Integer.parseInt(matcher.group()));
        }

        return numbers.get(0);
    }

    @NonNull
    @Override
    public String toString() {
        return tenChuong + " " + urlChuong;
    }
}
