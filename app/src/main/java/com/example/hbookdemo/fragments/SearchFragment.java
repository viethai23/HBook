package com.example.hbookdemo.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class SearchFragment extends Fragment {

    private String url = "";
    private EditText edtTimKiem;
    private ImageView btnTimKiem;
    NestedScrollView nestedScrollView;
    ArrayList mtruyenList;
    private RecyclerView truyenRecyclerView;
    private int page = 1, lastP = 1;
    ProgressBar loading;
    Disposable disposable1, disposable2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        truyenRecyclerView = view.findViewById(R.id.rcv_timkiem);
        nestedScrollView = view.findViewById(R.id.view_timkiem);
        loading = view.findViewById(R.id.loading_timkiem);
        edtTimKiem = view.findViewById(R.id.edt_timkiem);
        btnTimKiem = view.findViewById(R.id.btn_timkiem);

        loading.setVisibility(View.INVISIBLE);

        edtTimKiem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = String.valueOf(edtTimKiem.getText()).replaceAll(" ", "+");
                    page = 1;
                    url = "https://novelfull.com/search?keyword=" + text;
                    Log.d("TTT", url);
                    edtTimKiem.setText("");
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isAcceptingText()) {
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                    mtruyenList = new ArrayList<>();
                    truyenRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    loadLastPage();
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
                    return true;
                }
                return false;
            }
        });

    }


    private void loadLastPage() {
        disposable1 = Observable.fromCallable(() -> {
                    Document maindoc = Jsoup.connect(url).get();
                    String last = maindoc.select("li.last").select("a").attr("data-page");
                    int lastPage = Integer.parseInt(last)+1;
                    return lastPage;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    lastP = data;
                }, error -> {
                    Log.d("TTT", "Loi lay chap cuoi");
                    lastP = 1;
                });
    }

    private void loadData() {
        disposable2 = Observable.fromCallable(() -> {
                    String currentPage= url + "&page="+Integer.toString(page);
                    Log.d("TTT", "Current page: " + currentPage);
                    List<Truyen> truyenList = new ArrayList<>();
                    try {
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
                    Toast.makeText(getContext(), "Không thể tìm thấy truyện", Toast.LENGTH_SHORT).show();
                    Log.d("TTT", "Loi lay truyen");
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable1 != null) {
            disposable1.dispose();
        }
        if(disposable2 != null) {
            disposable2.dispose();
        }
    }
}
