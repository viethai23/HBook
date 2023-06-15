package com.example.hbookdemo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hbookdemo.R;
import com.example.hbookdemo.adapter.ChuongAdapter;
import com.example.hbookdemo.adapter.TruyenAdapter;
import com.example.hbookdemo.fragments.LichSuFragment;
import com.example.hbookdemo.manager.FontManager;
import com.example.hbookdemo.object.Chuong;
import com.example.hbookdemo.object.GioiThieu;
import com.example.hbookdemo.object.Truyen;
import com.example.hbookdemo.object.TruyenLichSu;
import com.google.gson.Gson;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GioiThieuTruyenActivity extends AppCompatActivity {

    private String fileNameKS = "data_kesach.json";
    private Button back;
    private ImageView anhTruyenIV;
    private TextView tenTruyenTV;
    private TextView tacGiaTV;
    private TextView theLoaiTV;
    private TextView trangThaiTV;
    private TextView moTaTV;
    private Button themBT;
    private Button docTruyen;
    private TextView xemDS;
    private boolean checkKS = false;
    private RecyclerView chuongRecyclerView;
    public static ArrayList<Chuong> mchuongList;
    private String url;
    private Disposable disposable1, disposable2, disposable3;
    private TruyenLichSu truyenLichSu;
    private Truyen truyen;
    private int lastP = 1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gioi_thieu_truyen);

        mapping();
        init();
        loadGioiThieu();
        loadChuong();
        setOnClick();
    }

    private void setOnClick() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GioiThieuTruyenActivity.this, MainActivity.class);
                finish();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Fade fade = new Fade();
                    fade.setDuration(500);
                    getWindow().setEnterTransition(fade);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(GioiThieuTruyenActivity.this);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

        themBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveKS();
                if(!checkKS) Toast.makeText(GioiThieuTruyenActivity.this, "Thêm truyện vào kệ sách thành công!", Toast.LENGTH_SHORT).show();
            }
        });

        xemDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("truyenLS", truyenLichSu);
                Intent intent = new Intent(GioiThieuTruyenActivity.this, DanhSachChuongActivity.class);
                intent.putExtra("data truyen",b);
                startActivity(intent);
            }
        });

        docTruyen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadChuongDau();
            }
        });
    }

    // Ánh xạ view
    private void mapping() {
        back = findViewById(R.id.back);
        anhTruyenIV = findViewById(R.id.anhTruyen);
        tenTruyenTV = findViewById(R.id.tenTruyen);
        tacGiaTV = findViewById(R.id.tacGia);
        theLoaiTV = findViewById(R.id.theLoai);
        trangThaiTV = findViewById(R.id.trangThai);
        moTaTV = findViewById(R.id.moTa);
        chuongRecyclerView = findViewById(R.id.rcv_chuongtruyen);
        themBT = findViewById(R.id.them_ke_sach);
        docTruyen = findViewById(R.id.doc_truyen);
        xemDS = findViewById(R.id.xem_danh_sach);
    }

    // Khởi tạo
    private void init() {
        mchuongList = new ArrayList<>();
        Bundle c = getIntent().getBundleExtra("data truyen");
        truyen = (Truyen) c.getSerializable("truyen");
        url = (String) truyen.getTruyenUrl();
        chuongRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        truyenLichSu = new TruyenLichSu();
        truyenLichSu.setTruyen(truyen);
    }

    // Load giới thiệu
    private void loadGioiThieu() {
        disposable1 = Observable.fromCallable(() -> {
                    Document maindoc = Jsoup.connect(url).get();
                    String tenTruyen = truyen.getTenTruyen();
                    String anhTruyen = maindoc.select("div.book img").attr("src");
                    String tacGia = maindoc.select("div.info div:nth-child(1) a").text();
                    String theLoai = maindoc.select("div.info div:nth-child(3) a").text();
                    String trangThai = maindoc.select("div.info div:nth-child(5) a").text();
                    String moTa = maindoc.select("div.desc-text").text();
                    Log.d("TTT", "Ten: " + tenTruyen + "\nAnh: " + anhTruyen + "\nTac gia: " + tacGia + "\nThe loai: " + theLoai + "\nTrang thai: " + trangThai + "\nmoTa " + moTa);
                    return new GioiThieu(tenTruyen, anhTruyen, tacGia, theLoai, trangThai, moTa);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    truyenLichSu.setGioithieu(data);
                    tenTruyenTV.setText(data.getTenTruyen());
                    Glide.with(this).load("https://novelfull.com/" + data.getAnhTruyen()).into(anhTruyenIV);
                    tacGiaTV.setText("Tác giả: " + data.getTacGia());
                    theLoaiTV.setText("Thể loại: " + data.getTheLoai());
                    trangThaiTV.setText("Trạng thái: " + data.getTrangThai());
                    moTaTV.setText(data.getMoTa());
                    moTaTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (moTaTV.getMaxLines() == 5) {
                                moTaTV.setMaxLines(Integer.MAX_VALUE);
                                moTaTV.setEllipsize(null);
                            } else {
                                moTaTV.setMaxLines(5);
                                moTaTV.setEllipsize(TextUtils.TruncateAt.END);
                            }
                        }
                    });
                }, error -> {
                    Log.d("TTT", "Loi lay truyen");
                });
    }

    // Load danh sách chương
    private void loadChuong() {
        disposable2 = Observable.fromCallable(() -> {
                    Document maindoc = Jsoup.connect(url).get();
                    String last = maindoc.select("li.last").select("a").attr("data-page");
                    lastP = Integer.parseInt(last)+1;
                    ArrayList<Chuong> list = new ArrayList<>();
                    Document doc = Jsoup.connect(url).get();
                    Elements content = doc.select("ul.l-chapters").select("a");
                    int listChapsize = content.size();
                    for(int i=0;i<listChapsize;i++) {
                        String tenChap = content.select("span.chapter-text")
                                .eq(i)
                                .text();
                        String chapUrl = "https://novelfull.com" + content
                                .eq(i)
                                .attr("href");
                        list.add(new Chuong(tenChap, chapUrl));
                    }

                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    mchuongList.addAll(data);
                    chuongRecyclerView.setAdapter(new ChuongAdapter(mchuongList, new ChuongAdapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(Chuong chuong) {
                            Bundle b = new Bundle();
                            b.putSerializable("from", 1);
                            b.putSerializable("chuong", chuong);
                            b.putSerializable("truyenLS", truyenLichSu);
                            b.putSerializable("vi tri page", lastP);
                            Intent intent = new Intent(GioiThieuTruyenActivity.this,NoiDungTruyenActivity.class);
                            intent.putExtra("data chuong",b);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Fade fade = new Fade();
                                fade.setDuration(500);
                                getWindow().setEnterTransition(fade);
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(GioiThieuTruyenActivity.this);
                                startActivity(intent, options.toBundle());
                            } else {
                                startActivity(intent);
                            }
                        }

                    }));
                }, error -> {
                    Log.d("TTT", "Loi lay truyen");
                });
    }

    private void loadChuongDau() {
        disposable3 = Observable.fromCallable(() -> {
                    Document maindoc = Jsoup.connect(url).get();
                    String last = maindoc.select("li.last").select("a").attr("data-page");
                    lastP = Integer.parseInt(last)+1;
                    Document doc = Jsoup.connect(url).get();
                    Elements content = doc.select("ul.list-chapter").select("a");
                    String tenChap = content.select("span.chapter-text")
                            .eq(0)
                            .text();
                    String chapUrl = "https://novelfull.com" + content.eq(0).attr("href");
                    return new Chuong(tenChap, chapUrl);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    Bundle b = new Bundle();
                    b.putSerializable("from", 1);
                    b.putSerializable("chuong", data);
                    b.putSerializable("truyenLS", truyenLichSu);
                    b.putSerializable("vi tri page", 1);
                    Intent intent = new Intent(GioiThieuTruyenActivity.this,NoiDungTruyenActivity.class);
                    intent.putExtra("data chuong",b);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Fade fade = new Fade();
                        fade.setDuration(500);
                        getWindow().setEnterTransition(fade);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(GioiThieuTruyenActivity.this);
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }
                }, error -> {
                    Log.d("TTT", "Loi lay truyen");
                });
    }

    // Lưu vào truyện vào kệ sách
    private void saveKS() {
        Gson gson = new Gson();
        String json = readFromFile(this, fileNameKS);
        Truyen[] truyenDS = gson.fromJson(json, Truyen[].class);
        if(truyenDS != null) {
            int index = -1;
            for (int i = 0; i < truyenDS.length; i++) {
                if (truyenDS[i].equals(truyen)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                Truyen[] new_truyenDS = Arrays.copyOf(truyenDS, truyenDS.length + 1);
                new_truyenDS[new_truyenDS.length - 1] = truyen;
                String updatedJson = gson.toJson(new_truyenDS);
                writeToFile(this, fileNameKS, updatedJson);
            }
            else {
                checkKS = true;
                Toast.makeText(this, "Truyện đã ở trong kệ sách!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Truyen[] new_truyenDS = new Truyen[1];
            new_truyenDS[0] = truyen;
            String updatedJson = gson.toJson(new_truyenDS);
            writeToFile(this, fileNameKS, updatedJson);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable1 != null) {
            disposable1.dispose();
        }
        if(disposable2!= null) {
            disposable2.dispose();
        }
        if(disposable3!= null) {
            disposable3.dispose();
        }
    }


    private void writeToFile(Context context, String fileName, String data) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(Context context, String fileName) {
        String result = "";
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

}