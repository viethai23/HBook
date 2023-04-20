package com.example.hbookdemo.object;

import java.io.Serializable;

public class TheLoai implements Serializable {
    private String tenTheLoai, url;

    public TheLoai(String tenTheLoai, String url) {
        this.tenTheLoai = tenTheLoai;
        this.url = url;
    }

    public String getTenTheLoai() {
        return tenTheLoai;
    }

    public void setTenTheLoai(String tenTheLoai) {
        this.tenTheLoai = tenTheLoai;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
