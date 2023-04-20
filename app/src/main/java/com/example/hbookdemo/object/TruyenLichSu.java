package com.example.hbookdemo.object;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TruyenLichSu implements Serializable{
    private Truyen truyen;
    private ArrayList<Chuong> listChuong;
    private GioiThieu gioithieu;
    private int viTriChuong;
    private String thoiGianUpdate;

    public TruyenLichSu() {

    }

    public TruyenLichSu(Truyen truyen, ArrayList<Chuong> listChuong, GioiThieu gioithieu, int viTriChuong, String thoiGianUpdate) {
        this.truyen = truyen;
        this.listChuong = listChuong;
        this.gioithieu = gioithieu;
        this.viTriChuong = viTriChuong;
        this.thoiGianUpdate = thoiGianUpdate;
    }

    public Truyen getTruyen() {
        return truyen;
    }

    public void setTruyen(Truyen truyen) {
        this.truyen = truyen;
    }

    public ArrayList<Chuong> getListChuong() {
        return listChuong;
    }

    public void setListChuong(ArrayList<Chuong> listChuong) {
        this.listChuong = listChuong;
    }

    public GioiThieu getGioithieu() {
        return gioithieu;
    }

    public void setGioithieu(GioiThieu gioithieu) {
        this.gioithieu = gioithieu;
    }

    public int getViTriChuong() {
        return viTriChuong;
    }

    public void setViTriChuong(int viTriChuong) {
        this.viTriChuong = viTriChuong;
    }

    public String getThoiGianUpdate() {
        return thoiGianUpdate;
    }

    public void setThoiGianUpdate(String thoiGianUpdate) {
        this.thoiGianUpdate = thoiGianUpdate;
    }
}
