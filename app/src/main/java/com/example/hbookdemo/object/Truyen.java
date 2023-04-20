package com.example.hbookdemo.object;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Truyen implements Serializable {
    private String tenTruyen, tacGia, soChuong, imgUrl, truyenUrl;

    public Truyen() {
    }

    public Truyen(String tenTruyen, String tacGia, String soChuong, String imgUrl, String truyenUrl) {
        this.tenTruyen = tenTruyen;
        this.tacGia = tacGia;
        this.imgUrl = imgUrl;
        this.truyenUrl = truyenUrl;
        this.soChuong = soChuong;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public void setTenTruyen(String tenTruyen) {
        this.tenTruyen = tenTruyen;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTruyenUrl() {
        return truyenUrl;
    }

    public void setTruyenUrl(String truyenUrl) {
        this.truyenUrl = truyenUrl;
    }

    public String getSoChuong() {
        return soChuong;
    }

    public void setSoChuong(String soChuong) {
        this.soChuong = soChuong;
    }

    public boolean equals(Truyen o) {
        if(this.getTenTruyen().equals(o.getTenTruyen()) && this.getTruyenUrl().equals(o.getTruyenUrl()))
            return true;
        return false;
    }
}
