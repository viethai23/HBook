package com.example.hbookdemo.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hbookdemo.R;
import com.example.hbookdemo.adapter.ChuongAdapter;
import com.example.hbookdemo.object.Chuong;
import com.example.hbookdemo.object.GioiThieu;
import com.example.hbookdemo.object.Truyen;
import com.example.hbookdemo.object.TruyenLichSu;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GioiThieuTruyenActivity extends AppCompatActivity {

    String fileNameKS = "data_kesach.json";
    String fileName = "data1.json";
    private ImageView back;
    private ImageView anhTruyenIV;
    private TextView tenTruyenTV;
    private TextView tacGiaTV;
    private TextView theLoaiTV;
    private TextView trangThaiTV;
    private TextView moTaTV;
    private Button themBT;
    private boolean checkKS = false;
    private boolean checkLS = false;
    private RecyclerView chuongRecyclerView;
    private ArrayList mchuongList;
    private String url;
    private Disposable disposable1, disposable2;
    private TruyenLichSu truyenLichSu;
    private Truyen truyen;
    int page = 0, lastP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gioi_thieu_truyen);

        mchuongList = new ArrayList<>();

        Bundle c = getIntent().getBundleExtra("data truyen");
        if(c.getSerializable("danh sach chuong") != null) {
            mchuongList.addAll((ArrayList) c.getSerializable("danh sach chuong"));
            checkLS = true;
        }
        truyen = (Truyen) c.getSerializable("truyen");
        url = (String) truyen.getTruyenUrl();

        Log.d("TTT", url);

        back = findViewById(R.id.back);
        anhTruyenIV = findViewById(R.id.anhTruyen);
        tenTruyenTV = findViewById(R.id.tenTruyen);
        tacGiaTV = findViewById(R.id.tacGia);
        theLoaiTV = findViewById(R.id.theLoai);
        trangThaiTV = findViewById(R.id.trangThai);
        moTaTV = findViewById(R.id.moTa);
        chuongRecyclerView = findViewById(R.id.rcv_chuongtruyen);
        themBT = findViewById(R.id.them_ke_sach);

        chuongRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        truyenLichSu = new TruyenLichSu();
        truyenLichSu.setTruyen(truyen);

        loadGioiThieu();
        if(checkLS == false) loadChuong();
        else {
            chuongRecyclerView.setAdapter(new ChuongAdapter(mchuongList, new ChuongAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(int position) {
                    truyenLichSu.setViTriChuong(position);
                    LocalDateTime currentDateTime = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        currentDateTime = LocalDateTime.now();
                    }
                    truyenLichSu.setThoiGianUpdate(String.valueOf(currentDateTime));
                    Log.d("TTT", "Ten truyen: " + truyenLichSu.getTruyen().getTenTruyen() + " Thoi gian: " + truyenLichSu.getThoiGianUpdate());
                    saveIn();
                    Bundle b = new Bundle();
                    b.putSerializable("from", 1);
                    b.putSerializable("danh sach chuong", mchuongList);
                    b.putSerializable("vi tri", position);
                    b.putSerializable("truyen lich su", truyenLichSu);
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
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        themBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveKS();
                if(!checkKS) Toast.makeText(GioiThieuTruyenActivity.this, "Thêm truyện vào kệ sách thành công!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGioiThieu() {
        disposable1 = Observable.fromCallable(() -> {
                    Document maindoc = Jsoup.connect(url).get();
                    String tenTruyen = maindoc.select("h3.title").text();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable1 != null) {
            disposable1.dispose();
        }
        if(disposable2!= null) {
            disposable2.dispose();
        }
    }

    private void loadChuong() {
        disposable2 = Observable.range(0,10).flatMap(ii -> Observable.fromCallable(() -> {
                    Document maindoc = Jsoup.connect(url).get();
                    String lastPage = maindoc.select("li.last").select("a").attr("data-page");
                    lastP = Integer.parseInt(lastPage)+1;
                    List<Chuong> list = new ArrayList<>();
                    while(page<lastP){
                        page+=1;
                        String currentPage= url + "?page="+Integer.toString(page)+"&per-page=50";
                        Document doc = Jsoup.connect(currentPage).get();
                        Elements content = doc.select("ul.list-chapter").select("a");
                        int listChapsize = content.size();
                        for(int i=0;i<listChapsize;i++) {
                            String tenChap = content.select("span.chapter-text")
                                    .eq(i)
                                    .text();
                            String chapUrl = "https://novelfull.com" + content
                                    .eq(i)
                                    .attr("href");
                            list.add(new Chuong(tenChap, chapUrl));
                            Log.d("truyen chap", " .ten chap: " + tenChap + " .chapUrl: " + chapUrl);
                        }
                    }
                    return list;
                })
                        .sorted()
                .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    mchuongList.addAll(data);
                    Collections.sort(mchuongList);
                    truyenLichSu.setListChuong(mchuongList);
                    chuongRecyclerView.setAdapter(new ChuongAdapter(mchuongList, new ChuongAdapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(int position) {
                            truyenLichSu.setViTriChuong(position);
                            LocalDateTime currentDateTime = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                currentDateTime = LocalDateTime.now();
                            }
                            truyenLichSu.setThoiGianUpdate(String.valueOf(currentDateTime));
                            Log.d("TTT", "Ten truyen: " + truyenLichSu.getTruyen().getTenTruyen() + " Thoi gian: " + truyenLichSu.getThoiGianUpdate());
                            saveIn();
                            Bundle b = new Bundle();
                            b.putSerializable("from", 1);
                            b.putSerializable("danh sach chuong", mchuongList);
                            b.putSerializable("vi tri", position);
                            b.putSerializable("truyen lich su", truyenLichSu);
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

    private void saveIn() {
        Gson gson = new Gson();
        String json = readFromFile(this, fileName);
        TruyenLichSu[] lichSu = gson.fromJson(json, TruyenLichSu[].class);
        if(lichSu != null) {
            int index = -1;
            for (int i = 0; i < lichSu.length; i++) {
                if (lichSu[i].getTruyen().equals(truyenLichSu.getTruyen())) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                TruyenLichSu[] new_lichSu = Arrays.copyOf(lichSu, lichSu.length + 1);
                new_lichSu[new_lichSu.length - 1] = truyenLichSu;
                String updatedJson = gson.toJson(new_lichSu);
                writeToFile(this, fileName, updatedJson);
            }
            else {
                lichSu[index].setViTriChuong(truyenLichSu.getViTriChuong());
                String updatedJson = gson.toJson(lichSu);
                writeToFile(this, fileName, updatedJson);
            }
        }
        else {
            TruyenLichSu[] new_lichSu = new TruyenLichSu[1];
            new_lichSu[0] = truyenLichSu;
            String updatedJson = gson.toJson(new_lichSu);
            writeToFile(this, fileName, updatedJson);
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

}