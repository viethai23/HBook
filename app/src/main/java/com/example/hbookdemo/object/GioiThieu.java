package com.example.hbookdemo.object;

import java.io.Serializable;
import java.util.List;

public class GioiThieu implements Serializable {
    String tenTruyen, anhTruyen, tacGia, theLoai, trangThai, moTa;

    public GioiThieu(String tenTruyen, String anhTruyen, String tacGia, String theLoai, String trangThai, String moTa) {
        this.tenTruyen = tenTruyen;
        this.anhTruyen = anhTruyen;
        this.tacGia = tacGia;
        this.theLoai = theLoai;
        this.trangThai = trangThai;
        this.moTa = moTa;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public void setTenTruyen(String tenTruyen) {
        this.tenTruyen = tenTruyen;
    }

    public String getAnhTruyen() {
        return anhTruyen;
    }

    public void setAnhTruyen(String anhTruyen) {
        this.anhTruyen = anhTruyen;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public String getTheLoai() {
        return theLoai;
    }


    public void setTheLoai(String theLoai) {
        this.theLoai = theLoai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
