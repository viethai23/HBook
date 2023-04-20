package com.example.hbookdemo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hbookdemo.R;
import com.example.hbookdemo.activities.GioiThieuTruyenActivity;
import com.example.hbookdemo.activities.TheLoaiTruyenActivity;
import com.example.hbookdemo.adapter.TheLoaiAdapter;
import com.example.hbookdemo.adapter.TruyenAdapter;
import com.example.hbookdemo.object.TheLoai;
import com.example.hbookdemo.object.Truyen;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TheLoaiFragment extends Fragment {

    private RecyclerView rcvTheLoai;
    private ArrayList mtheLoaiList;
    private String url = "https://novelfull.com/";
    private Disposable disposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theloai, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvTheLoai = view.findViewById(R.id.rcv_theloai);
        mtheLoaiList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
        rcvTheLoai.setLayoutManager(gridLayoutManager);

        loadData();
    }

    private void loadData() {
        disposable = Observable.fromCallable(() -> {
                    List<TheLoai> list = new ArrayList<>();
                    try {
                        Document maindoc = Jsoup.connect(url).get();
                        Elements ullist = maindoc.select("ul.dropdown-menu");
                        int size = ullist.size();
                        for(int i=1; i<size; i++) {
                            Elements liList = ullist.eq(i).select("li");
                            for (Element li : liList) {
                                String tenTheLoai = li.text();
                                String urlTheLoai = "https://novelfull.com" + li.select("a").attr("href");
                                Log.d("TTT", "The loai: " + tenTheLoai + " url: " + urlTheLoai);
                                list.add(new TheLoai(tenTheLoai, urlTheLoai));
                            }
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    mtheLoaiList.addAll(data);
                    rcvTheLoai.setAdapter(new TheLoaiAdapter(mtheLoaiList, new TheLoaiAdapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(TheLoai theloai) {
                            Bundle b = new Bundle();
                            b.putSerializable("the loai", theloai);
                            Intent intent = new Intent(getActivity(), TheLoaiTruyenActivity.class);
                            intent.putExtra("data the loai",b);
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
