package com.example.hbookdemo.object;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TruyenLichSu implements Serializable{
    private Truyen truyen;
    private Chuong chuong;
    private GioiThieu gioithieu;
    private String thoiGianUpdate;

    public TruyenLichSu() {

    }

    public TruyenLichSu(Truyen truyen, Chuong chuong, GioiThieu gioithieu, String thoiGianUpdate) {
        this.truyen = truyen;
        this.chuong = chuong;
        this.gioithieu = gioithieu;
        this.thoiGianUpdate = thoiGianUpdate;
    }

    public Truyen getTruyen() {
        return truyen;
    }

    public void setTruyen(Truyen truyen) {
        this.truyen = truyen;
    }

    public GioiThieu getGioithieu() {
        return gioithieu;
    }

    public void setGioithieu(GioiThieu gioithieu) {
        this.gioithieu = gioithieu;
    }

    public Chuong getChuong() {
        return chuong;
    }

    public void setChuong(Chuong chuong) {
        this.chuong = chuong;
    }

    public String getThoiGianUpdate() {
        return thoiGianUpdate;
    }

    public void setThoiGianUpdate(String thoiGianUpdate) {
        this.thoiGianUpdate = thoiGianUpdate;
    }
}
