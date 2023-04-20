package com.example.hbookdemo.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hbookdemo.R;
import com.example.hbookdemo.activities.GioiThieuTruyenActivity;
import com.example.hbookdemo.adapter.TruyenAdapter;
import com.example.hbookdemo.object.Truyen;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TruyenFullFragment extends Fragment  {

    TruyenAdapter adapter;
    private String url = "https://novelfull.com/completed-novel";
    NestedScrollView nestedScrollView;
    ArrayList mtruyenList;
    private RecyclerView truyenRecyclerView;
    private int page = 1, lastP = 30;
    ProgressBar loading;
    Disposable disposable;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truyenfull, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        truyenRecyclerView = view.findViewById(R.id.rcv_truyenfull);
        nestedScrollView = view.findViewById(R.id.view_truyenfull);
        loading = view.findViewById(R.id.loading_truyenfull);

        mtruyenList = new ArrayList<>();
        loading.setVisibility(View.INVISIBLE);

        truyenRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        loadData();

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@androidx.annotation.NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (page < lastP ) {
                        page++;
                        loadData();
                        loading.setVisibility(View.VISIBLE);

                    }else {
                        loading.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void loadData() {
        disposable = Observable.fromCallable(() -> {
                    String currentPage= url + "?page="+Integer.toString(page);
                    List<Truyen> truyenList = new ArrayList<>();
                    try {
                        Document maindoc = Jsoup.connect(url).get();
                        String lastPage = maindoc.select("li.last").select("a").attr("data-page");
                        lastP = Integer.parseInt(lastPage)+1;
                        Document doc = Jsoup.connect(currentPage).get();
                        Elements data = doc.select("div.col-truyen-main").select("div.row");
                        int size = data.size();
                        for (int i = 0; i < size; i++) {
                            String linkAnh = "https://novelfull.com" + data.select("img").eq(i).attr("src");
                            String tenTruyen = data.select("h3").eq(i).text();
                            String detailURL = "https://novelfull.com" + data.select("h3").eq(i).select("a").attr("href");
                            String tacGia = data.select(".author").eq(i).select("span").text();
                            String soChuong = data.select(".chapter-text").eq(i).text();
                            truyenList.add(new Truyen(tenTruyen, tacGia, soChuong, linkAnh, detailURL));
                            Log.d("items", " img: " + linkAnh + " . title: " + tenTruyen + " . detail url: " + detailURL);

                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    return truyenList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    mtruyenList.addAll(data);
                    truyenRecyclerView.setAdapter(new TruyenAdapter(mtruyenList, new TruyenAdapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(Truyen truyen) {
                            Bundle b = new Bundle();
                            b.putSerializable("truyen", truyen);
                            Intent intent = new Intent(getActivity(), GioiThieuTruyenActivity.class);
                            intent.putExtra("data truyen",b);
                            startActivity(intent);
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
    }

}
