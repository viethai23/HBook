package com.example.hbookdemo.object;

public class NoiDung {
    private String tenTruyen, tenChuong, noiDung;

    public NoiDung(String tenTruyen, String tenChuong, String noiDung) {
        this.tenTruyen = tenTruyen;
        this.tenChuong = tenChuong;
        this.noiDung = noiDung;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public void setTenTruyen(String tenTruyen) {
        this.tenTruyen = tenTruyen;
    }

    public String getTenChuong() {
        return tenChuong;
    }

    public void setTenChuong(String tenChuong) {
        this.tenChuong = tenChuong;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}
