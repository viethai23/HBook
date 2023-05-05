package com.example.hbookdemo.fragments;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
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
import com.example.hbookdemo.activities.NoiDungTruyenActivity;
import com.example.hbookdemo.adapter.ChuongAdapter;
import com.example.hbookdemo.adapter.TruyenAdapter;
import com.example.hbookdemo.adapter.TruyenLishSuAdapter;
import com.example.hbookdemo.object.Chuong;
import com.example.hbookdemo.object.Truyen;
import com.example.hbookdemo.object.TruyenLichSu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LichSuFragment extends Fragment {

    String fileNameKS = "data_kesach.json";
    String fileName = "data1.json";
    ArrayList<TruyenLichSu> mtruyenList;
    public static ArrayList mChuongList;
    private RecyclerView truyenRecyclerView;
    private AlertDialog alertDialog;
    private boolean checkKS = false;
    private boolean luachon = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lichsu, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        truyenRecyclerView = view.findViewById(R.id.rcv_lichsu);

        createData();

    }

    @Override
    public void onResume() {
        super.onResume();

        createData();
    }

    private void createData() {
        Gson gson = new Gson();
        String json = readFromFile(getContext(), fileName);
        TruyenLichSu[] lichSu = gson.fromJson(json, TruyenLichSu[].class);

        if(lichSu != null) {
            mtruyenList = new ArrayList<>(Arrays.asList(lichSu));
            truyenRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            mtruyenList.sort(new Comparator<TruyenLichSu>() {

                @Override
                public int compare(TruyenLichSu t1, TruyenLichSu t2) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if(t1.getThoiGianUpdate().compareTo(t2.getThoiGianUpdate()) > 0)
                            return -1;
                    }
                    return 1;
                }
            });

            truyenRecyclerView.setAdapter(new TruyenLishSuAdapter(mtruyenList, new TruyenLishSuAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(TruyenLichSu truyenLichSu) {
                    Bundle b = new Bundle();
                    mChuongList = truyenLichSu.getListChuong();
                    b.putSerializable("from", 2);
                    b.putSerializable("danh sach chuong", 1);
                    b.putSerializable("vi tri", truyenLichSu.getViTriChuong());
                    b.putSerializable("truyen lich su", truyenLichSu);
                    Intent intent = new Intent(getActivity(), NoiDungTruyenActivity.class);
                    intent.putExtra("data chuong",b);
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(TruyenLichSu truyenLichSu) {
                    createDialog(truyenLichSu);
                    alertDialog.show();
                    Window window = alertDialog.getWindow();
                    if (window != null) {
                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        layoutParams.copyFrom(window.getAttributes());
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        layoutParams.gravity = Gravity.BOTTOM;
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                window.getDecorView().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                            }
                        });
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                window.getDecorView().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
                            }
                        });

                        window.setAttributes(layoutParams);
                    }
                }
            }));

        }

        if(luachon)
            alertDialog.dismiss();
    }

    private void createDialog(TruyenLichSu truyenLichSu) {
        int themeResId = R.style.MyDialogTheme;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), themeResId);

        View customLayout = getLayoutInflater().inflate(R.layout.custom_lichsu_dialog_layout, null);
        builder.setView(customLayout);

        TextView ten_truyen = customLayout.findViewById(R.id.ten_truyen);
        TextView xem = customLayout.findViewById(R.id.xem_gioi_thieu_truyen);
        TextView them = customLayout.findViewById(R.id.them_vao_ke_sach);
        TextView xoa = customLayout.findViewById(R.id.xoa_khoi_lich_su);

        ten_truyen.setText(truyenLichSu.getTruyen().getTenTruyen());
        xem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                luachon = true;
                Bundle b = new Bundle();
                b.putSerializable("truyen", truyenLichSu.getTruyen());
                b.putSerializable("danh sach chuong", truyenLichSu.getListChuong());
                Intent intent = new Intent(getActivity(), GioiThieuTruyenActivity.class);
                intent.putExtra("data truyen",b);
                startActivity(intent);
                createData();
            }
        });

        them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                luachon = true;
                saveKS(truyenLichSu);
                if(!checkKS) Toast.makeText(getContext(), "Thêm truyện vào kệ sách thành công!", Toast.LENGTH_SHORT).show();
            }
        });

        xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                luachon = true;
                xoaIn(truyenLichSu);
                createData();
            }
        });

        alertDialog = builder.create();
        alertDialog.setCancelable(true);

    }

    private void xoaIn(TruyenLichSu truyenLichSu) {
        Gson gson = new Gson();
        String json = readFromFile(getContext(), fileName);
        TruyenLichSu[] lichSu = gson.fromJson(json, TruyenLichSu[].class);
        if(lichSu != null) {
            int index = -1;
            for (int i = 0; i < lichSu.length; i++) {
                if (lichSu[i].getTruyen().equals(truyenLichSu.getTruyen())) {
                    index = i;
                    break;
                }
            }
            if(index != -1) {
                ArrayList<TruyenLichSu> list = new ArrayList<>(Arrays.asList(lichSu));
                list.remove(index);
                TruyenLichSu[] new_lichSu = list.toArray(new TruyenLichSu[0]);
                String updatedJson = gson.toJson(new_lichSu);
                writeToFile(getContext(), fileName, updatedJson);
                Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveKS(TruyenLichSu truyenLichSu) {
        Gson gson = new Gson();
        String json = readFromFile(getContext(), fileNameKS);
        Truyen[] truyenDS = gson.fromJson(json, Truyen[].class);
        if(truyenDS != null) {
            int index = -1;
            for (int i = 0; i < truyenDS.length; i++) {
                if (truyenDS[i].equals(truyenLichSu.getTruyen())) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                Truyen[] new_truyenDS = Arrays.copyOf(truyenDS, truyenDS.length + 1);
                new_truyenDS[new_truyenDS.length - 1] = truyenLichSu.getTruyen();
                String updatedJson = gson.toJson(new_truyenDS);
                writeToFile(getContext(), fileNameKS, updatedJson);
            }
            else {
                checkKS = true;
                Toast.makeText(getContext(), "Truyện đã ở trong kệ sách!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Truyen[] new_truyenDS = new Truyen[1];
            new_truyenDS[0] = truyenLichSu.getTruyen();
            String updatedJson = gson.toJson(new_truyenDS);
            writeToFile(getContext(), fileNameKS, updatedJson);
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
    public void onDestroy() {
        super.onDestroy();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}
