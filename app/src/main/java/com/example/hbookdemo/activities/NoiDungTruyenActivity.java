package com.example.hbookdemo.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hbookdemo.R;
import com.example.hbookdemo.adapter.ChuongAdapter;
import com.example.hbookdemo.fragments.LichSuFragment;
import com.example.hbookdemo.object.Chuong;
import com.example.hbookdemo.object.NoiDung;
import com.example.hbookdemo.object.TruyenLichSu;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NoiDungTruyenActivity extends AppCompatActivity {

    private String fileName = "data1.json";
    private TextView tenTruyenTV;
    private TextView tenChuongTV;
    private TextView noiDungTV;
    private ImageView back;
    private ImageView menu;
    private ImageView next;
    private ImageView prev;
    private int viTri;
    private Chuong chuong;
    private String url;
    private ScrollView scrollView;
    private RelativeLayout RLController;
    private GestureDetectorCompat gestureDetector;
    private AlertDialog alertDialog;
    private TruyenLichSu truyenLichSu;
    private ArrayList mchuongList;
    private Disposable disposable;
    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noi_dung_truyen);

        Bundle b = getIntent().getBundleExtra("data chuong");
        from = (int) b.getSerializable("from");
        viTri = (int) b.getSerializable("vi tri");
        truyenLichSu = (TruyenLichSu) b.getSerializable("truyen lich su");
        if(b.getSerializable("danh sach chuong") != null)
            mchuongList = LichSuFragment.mChuongList;
        else
            mchuongList = GioiThieuTruyenActivity.mchuongList;
        chuong = (Chuong) mchuongList.get(viTri);

        url = chuong.getUrlChuong();

        tenTruyenTV = findViewById(R.id.tenTruyen);
        tenChuongTV = findViewById(R.id.tenChuong);
        noiDungTV = findViewById(R.id.noiDung);
        scrollView = findViewById(R.id.scrollView);
        RLController = findViewById(R.id.view_controller_2);
        back = findViewById(R.id.backND);
        menu = findViewById(R.id.menu);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);

        gestureDetector = new GestureDetectorCompat(this, new MyGestureListener());
        createDialog();

        loadData();

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (!scrollView.canScrollVertically(1)) {
                    RLController.setVisibility(View.VISIBLE);
                    RLController.setClickable(true);
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            nextChapter();
                        }
                    });
                    prev.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            prevChapter();
                        }
                    });
                }
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                if(from == 1) {
                    intent = new Intent(NoiDungTruyenActivity.this, GioiThieuTruyenActivity.class);
                }
                else {
                    intent = new Intent(NoiDungTruyenActivity.this, MainActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Fade fade = new Fade();
                    fade.setDuration(500);
                    getWindow().setEnterTransition(fade);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(NoiDungTruyenActivity.this);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
                Window window = alertDialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.copyFrom(window.getAttributes());
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.gravity = Gravity.RIGHT;
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            window.getDecorView().startAnimation(AnimationUtils.loadAnimation(NoiDungTruyenActivity.this, R.anim.slide_right_in));
                        }
                    });
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            window.getDecorView().startAnimation(AnimationUtils.loadAnimation(NoiDungTruyenActivity.this, R.anim.slide_right_out));
                        }
                    });

                    window.setAttributes(layoutParams);
                }

            }
        });


    }

    private void createDialog() {
        int themeResId = R.style.MyDialogTheme;
        AlertDialog.Builder builder = new AlertDialog.Builder(NoiDungTruyenActivity.this, themeResId);

        View customLayout = getLayoutInflater().inflate(R.layout.custom_chapter_dialog_layout, null);
        builder.setView(customLayout);

        RecyclerView rcvViewChuongDL = customLayout.findViewById(R.id.rcv_chuongtruyen_DL);

        rcvViewChuongDL.setLayoutManager(new LinearLayoutManager(customLayout.getContext()));

        rcvViewChuongDL.setAdapter(new ChuongAdapter(mchuongList, new ChuongAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                Bundle b = new Bundle();
                b.putSerializable("from", from);
                b.putSerializable("danh sach chuong", 1);
                b.putSerializable("vi tri", position);
                b.putSerializable("truyen lich su", truyenLichSu);
                Intent intent = new Intent(NoiDungTruyenActivity.this,NoiDungTruyenActivity.class);
                finish();
                intent.putExtra("data chuong",b);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Fade fade = new Fade();
                    fade.setDuration(500);
                    getWindow().setEnterTransition(fade);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(NoiDungTruyenActivity.this);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
            }

        }));

        alertDialog = builder.create();
        alertDialog.setCancelable(true);

    }

    private void saveIn() {
        Gson gson = new Gson();
        String json = readFromFile(this, fileName);
        TruyenLichSu[] lichSu = gson.fromJson(json, TruyenLichSu[].class);

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
            LocalDateTime currentDateTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                currentDateTime = LocalDateTime.now();
            }
            lichSu[index].setViTriChuong(viTri);
            lichSu[index].setThoiGianUpdate(String.valueOf(currentDateTime));
            Log.d("Luu noi dung: ", "Ten truyen: " + truyenLichSu.getTruyen().getTenTruyen() + " Vi tri chuong: " + truyenLichSu.getViTriChuong() + " Thoi gian: " + truyenLichSu.getThoiGianUpdate());
            String updatedJson = gson.toJson(lichSu);
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

    private void loadData() {
        disposable = Observable.fromCallable(() -> {
                            Document maindoc = Jsoup.connect(url).get();
                            String tenTruyen = maindoc.select(".truyen-title").text();
                            String tenChuong = maindoc.select(".chapter-text").text();
                            String noiDung = "";
                            Elements content = maindoc.select("div#chapter-content").select("p");
                            for(int i=0;i<content.size();i++){
                                if(!content.eq(i).text().equals("") && !content.eq(i).text().startsWith("Chapter")) {
                                    noiDung += content.eq(i).text()+"\n     ";
                                }
                            }
                            return new NoiDung(tenTruyen, tenChuong, noiDung);
                        })
                        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    tenTruyenTV.setText(data.getTenTruyen());
                    tenChuongTV.setText(data.getTenChuong());
                    noiDungTV.setText(data.getNoiDung());
                }, error -> {
                    Log.d("TTT", "Loi lay truyen");
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        saveIn();
        if(disposable != null) {
            disposable.dispose();
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    private void nextChapter() {
        if(viTri==mchuongList.size()-1){
            Toast.makeText(NoiDungTruyenActivity.this, "Đây là chương cuối cùng!", Toast.LENGTH_SHORT).show();
        }else{
            viTri += 1;
            Next();
        }
    }

    private void prevChapter() {
        if(viTri==0){
            Toast.makeText(NoiDungTruyenActivity.this, "Đây là chương đầu tiên!", Toast.LENGTH_SHORT).show();
        }else{
            viTri -= 1;
            Prev();
        }
    }

    private void Next() {
        Bundle b = new Bundle();
        b.putSerializable("from", from);
        b.putSerializable("vi tri",viTri);
        b.putSerializable("truyen lich su", truyenLichSu);
        Intent intent = new Intent(NoiDungTruyenActivity.this,NoiDungTruyenActivity.class);
        finish();
        intent.putExtra("data chuong",b);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.START);
            slide.setDuration(100);
            getWindow().setExitTransition(slide);

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void Prev() {
        Bundle b = new Bundle();
        b.putSerializable("from", from);
        b.putSerializable("vi tri",viTri);
        b.putSerializable("truyen lich su", truyenLichSu);
        Intent intent = new Intent(NoiDungTruyenActivity.this,NoiDungTruyenActivity.class);
        finish();
        intent.putExtra("data chuong",b);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.END);
            slide.setDuration(100);
            getWindow().setExitTransition(slide);

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffX = event2.getX() - event1.getX();
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Vuốt từ trái sang phải
                        prevChapter();
                    } else {
                        // Vuốt từ phải sang trái
                        nextChapter();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

}