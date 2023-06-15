package com.example.hbookdemo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hbookdemo.R;
import com.example.hbookdemo.adapter.ChuongAdapter;
import com.example.hbookdemo.adapter.TruyenAdapter;
import com.example.hbookdemo.object.Chuong;
import com.example.hbookdemo.object.TheLoai;
import com.example.hbookdemo.object.Truyen;
import com.example.hbookdemo.object.TruyenLichSu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DanhSachChuongActivity extends AppCompatActivity {

    private String url = "";
    private NestedScrollView nestedScrollView;
    private ArrayList<Chuong> mchuongList;
    private RecyclerView chuongRecyclerView;

    private ImageView back, swap;
    private int page = 1, lastP = 30;
    private ProgressBar loading;
    private Disposable disposable, disposable1;
    private TruyenLichSu truyenLichSu;
    private boolean checkSwap = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_chuong);

        Bundle c = getIntent().getBundleExtra("data truyen");
        truyenLichSu = (TruyenLichSu) c.getSerializable("truyenLS");
        url = (String) truyenLichSu.getTruyen().getTruyenUrl();

        chuongRecyclerView = findViewById(R.id.rcv_dschuong);
        nestedScrollView = findViewById(R.id.view_dschuong);
        loading = findViewById(R.id.loading_dschuong);
        back = findViewById(R.id.back_dschuong);
        swap = findViewById(R.id.swap_dschuong);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mchuongList = new ArrayList<>();
        loading.setVisibility(View.INVISIBLE);
        chuongRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setViewNoSwap();

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSwap == false) {
                    Log.d("DDD", "Swap");
                    checkSwap = true;
                    mchuongList.clear();
                    setViewSwap();
                }
                else {
                    checkSwap = false;
                    mchuongList.clear();
                    setViewNoSwap();
                }
            }
        });

    }

    private void setViewNoSwap() {
        page = 1;
        loadDataNoSwap();
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@androidx.annotation.NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (page < lastP ) {
                        page++;
                        loadDataNoSwap();
                        loading.setVisibility(View.VISIBLE);

                    }else {
                        loading.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void setViewSwap() {
        page = lastP;
        loadDataSwap();
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@androidx.annotation.NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (page > 0 ) {
                        page--;
                        loadDataSwap();
                        loading.setVisibility(View.VISIBLE);

                    }else {
                        loading.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void loadDataNoSwap() {
        disposable = Observable.fromCallable(() -> {
                    Document maindoc = Jsoup.connect(url).get();
                    String lastPage = maindoc.select("li.last").select("a").attr("data-page");
                    lastP = Integer.parseInt(lastPage)+1;
                    String currentPage= url + "?page="+Integer.toString(page);
                    List<Chuong> list = new ArrayList<>();
                    try {
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
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
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
                            b.putSerializable("from", 3);
                            b.putSerializable("chuong", chuong);
                            b.putSerializable("truyenLS", truyenLichSu);
                            b.putSerializable("vi tri page", page);
                            Intent intent = new Intent(DanhSachChuongActivity.this,NoiDungTruyenActivity.class);
                            intent.putExtra("data chuong",b);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Fade fade = new Fade();
                                fade.setDuration(500);
                                getWindow().setEnterTransition(fade);
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DanhSachChuongActivity.this);
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

    private void loadDataSwap() {
        disposable1 = Observable.fromCallable(() -> {
                    Document maindoc = Jsoup.connect(url).get();
                    String lastPage = maindoc.select("li.last").select("a").attr("data-page");
                    lastP = Integer.parseInt(lastPage)+1;
                    String currentPage= url + "?page="+Integer.toString(page);
                    List<Chuong> list = new ArrayList<>();
                    try {
                        Document doc = Jsoup.connect(currentPage).get();
                        Elements content = doc.select("ul.list-chapter").select("a");
                        int listChapsize = content.size();
                        for(int i=listChapsize-1;i>=0;i--) {
                            String tenChap = content.select("span.chapter-text")
                                    .eq(i)
                                    .text();
                            String chapUrl = "https://novelfull.com" + content
                                    .eq(i)
                                    .attr("href");
                            list.add(new Chuong(tenChap, chapUrl));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
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
                            b.putSerializable("from", 3);
                            b.putSerializable("chuong", chuong);
                            b.putSerializable("truyenLS", truyenLichSu);
                            b.putSerializable("vi tri page", page);
                            Intent intent = new Intent(DanhSachChuongActivity.this,NoiDungTruyenActivity.class);
                            intent.putExtra("data chuong",b);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Fade fade = new Fade();
                                fade.setDuration(500);
                                getWindow().setEnterTransition(fade);
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DanhSachChuongActivity.this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable != null) {
            disposable.dispose();
        }
        if(disposable1 != null) {
            disposable1.dispose();
        }
    }
}