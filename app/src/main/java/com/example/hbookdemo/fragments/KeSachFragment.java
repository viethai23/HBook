package com.example.hbookdemo.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hbookdemo.R;
import com.example.hbookdemo.activities.GioiThieuTruyenActivity;
import com.example.hbookdemo.activities.NoiDungTruyenActivity;
import com.example.hbookdemo.adapter.TruyenAdapter;
import com.example.hbookdemo.adapter.TruyenKeSachAdapter;
import com.example.hbookdemo.adapter.TruyenLishSuAdapter;
import com.example.hbookdemo.object.Truyen;
import com.example.hbookdemo.object.TruyenLichSu;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class KeSachFragment extends Fragment {

    String fileName = "data_kesach.json";
    ArrayList<Truyen> mtruyenList;
    private RecyclerView truyenRecyclerView;
    private AlertDialog alertDialog;
    private boolean luachon = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kesach, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        truyenRecyclerView = view.findViewById(R.id.rcv_kesach);
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
        Truyen[] truyenDS = gson.fromJson(json, Truyen[].class);

        if(truyenDS != null) {
            mtruyenList = new ArrayList<>(Arrays.asList(truyenDS));
            truyenRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            mtruyenList.sort(new Comparator<Truyen>() {

                @Override
                public int compare(Truyen t1, Truyen t2) {
                    if(t1.getTenTruyen().compareTo(t2.getTenTruyen()) < 0)
                        return -1;
                    return 1;
                }
            });

            truyenRecyclerView.setAdapter(new TruyenKeSachAdapter(mtruyenList, new TruyenKeSachAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(Truyen truyen) {
                    Bundle b = new Bundle();
                    b.putSerializable("truyen", truyen);
                    Intent intent = new Intent(getActivity(), GioiThieuTruyenActivity.class);
                    intent.putExtra("data truyen",b);
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(Truyen truyen) {
                    createDialog(truyen);
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

    private void createDialog(Truyen truyen) {
        int themeResId = R.style.MyDialogTheme;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), themeResId);

        View customLayout = getLayoutInflater().inflate(R.layout.custom_kesach_dialog_layout, null);
        builder.setView(customLayout);

        TextView ten_truyen = customLayout.findViewById(R.id.ten_truyen_KS);
        TextView xoa = customLayout.findViewById(R.id.xoa_khoi_ke_sach);

        ten_truyen.setText(truyen.getTenTruyen());

        xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                luachon = true;
                xoaKS(truyen);
                createData();
            }
        });

        alertDialog = builder.create();
        alertDialog.setCancelable(true);

    }

    private void xoaKS(Truyen truyen) {
        Gson gson = new Gson();
        String json = readFromFile(getContext(), fileName);
        Truyen[] truyenDS = gson.fromJson(json, Truyen[].class);
        if(truyenDS != null) {
            int index = -1;
            for (int i = 0; i < truyenDS.length; i++) {
                if (truyenDS[i].equals(truyen)) {
                    index = i;
                    break;
                }
            }
            if(index != -1) {
                ArrayList<Truyen> list = new ArrayList<>(Arrays.asList(truyenDS));
                list.remove(index);
                Truyen[] new_truyen = list.toArray(new Truyen[0]);
                String updatedJson = gson.toJson(new_truyen);
                writeToFile(getContext(), fileName, updatedJson);
                Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
            }
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

    private void writeToFile(Context context, String fileName, String data) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }


}
