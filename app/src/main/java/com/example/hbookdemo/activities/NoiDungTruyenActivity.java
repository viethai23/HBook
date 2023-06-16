package com.example.hbookdemo.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.widget.NestedScrollView;
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
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
    private ArrayList<Chuong> mchuongList;
    private Chuong chuong;
    private String url, urlT;
    private ScrollView scrollView;
    private RelativeLayout RLController;
    private GestureDetectorCompat gestureDetector;
    private AlertDialog alertDialog;
    private TruyenLichSu truyenLichSu;
    private Disposable disposable, disposable1, disposable2, disposable3, disposable4;
    private RecyclerView rcvViewChuongDL;
    private ImageView nextC, prevC;
    private EditText toC;
    private int from;
    private int page=1, lastP=1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noi_dung_truyen);

        Bundle b = getIntent().getBundleExtra("data chuong");
        from = (int) b.getSerializable("from");
        from = (int) b.getSerializable("from");
        chuong = (Chuong) b.getSerializable("chuong");
        truyenLichSu = (TruyenLichSu) b.getSerializable("truyenLS");
        if(b.getSerializable("vi tri page") == null) page = 1;
        else page = (int) b.getSerializable("vi tri page");
        url = chuong.getUrlChuong();
        urlT = truyenLichSu.getTruyen().getTruyenUrl();

        tenTruyenTV = findViewById(R.id.tenTruyen);
        tenChuongTV = findViewById(R.id.tenChuong);
        noiDungTV = findViewById(R.id.noiDung);
        scrollView = findViewById(R.id.scrollView);
        RLController = findViewById(R.id.view_controller_2);
        back = findViewById(R.id.backND);
        menu = findViewById(R.id.menu);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);

        mchuongList = new ArrayList<>();
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
                else if(from == 3) {
                    intent = new Intent(NoiDungTruyenActivity.this, DanhSachChuongActivity.class);
                }
                else {
                    intent = new Intent(NoiDungTruyenActivity.this, MainActivity.class);
                }
                saveIn();
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
        rcvViewChuongDL = customLayout.findViewById(R.id.rcv_chuongtruyen_DL);

        nextC = customLayout.findViewById(R.id.next_chuong);
        prevC = customLayout.findViewById(R.id.prev_chuong);
        toC = customLayout.findViewById(R.id.to_chuong);

        rcvViewChuongDL.setLayoutManager(new LinearLayoutManager(customLayout.getContext()));
        loadDataChuong();
        toC.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_GO) {
                    String text = toC.getText().toString();
                    page = Integer.parseInt(text);
                    if(page > lastP)
                        page = lastP;
                    else if(page < 1)
                        page = 1;
                    Log.d("TTT", String.valueOf(page));
                    toC.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isAcceptingText()) {
                        imm.hideSoftInputFromWindow(alertDialog.getCurrentFocus().getWindowToken(), 0);
                    }
                    mchuongList.clear();
                    loadDataChuong();
                    return true;
                }
                return false;
            }
        });

        nextC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page < lastP) {
                    mchuongList.clear();
                    page++;
                    loadDataChuong();
                }
                else {
                    Toast.makeText(NoiDungTruyenActivity.this, "Đây là trang cuối cùng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        prevC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page > 1) {
                    mchuongList.clear();
                    page--;
                    loadDataChuong();
                }
                else {
                    Toast.makeText(NoiDungTruyenActivity.this, "Đây là trang đầu tiên", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog = builder.create();
        alertDialog.setCancelable(true);

    }

    private void saveIn() {
        Gson gson = new Gson();
        String json = readFromFile(this, fileName);
        TruyenLichSu[] lichSu = gson.fromJson(json, TruyenLichSu[].class);
        truyenLichSu.setChuong(chuong);
        LocalDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
        }
        truyenLichSu.setThoiGianUpdate(String.valueOf(currentDateTime));
        int index = -1;
        if(lichSu != null) {
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
                lichSu[index].setChuong(chuong);
                lichSu[index].setThoiGianUpdate(String.valueOf(currentDateTime));
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

    private void loadDataChuong() {
        disposable3 = Observable.fromCallable(() -> {
                    String currentPage= urlT + "?page="+Integer.toString(page);
                    List<Chuong> list = new ArrayList<>();
                    try {
                        Document maindoc = Jsoup.connect(urlT).get();
                        String lastPage = maindoc.select("li.last").select("a").attr("data-page");
                        lastP = Integer.parseInt(lastPage)+1;
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
                    rcvViewChuongDL.setAdapter(new ChuongAdapter(mchuongList, new ChuongAdapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(Chuong chuong) {
                            Bundle b = new Bundle();
                            b.putSerializable("from", from);
                            b.putSerializable("chuong", chuong);
                            b.putSerializable("truyenLS", truyenLichSu);
                            b.putSerializable("vi tri page", page);
                            Intent intent = new Intent(NoiDungTruyenActivity.this,NoiDungTruyenActivity.class);
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
                }, error -> {
                    Log.d("TTT", "Loi lay truyen");
                });

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
                    Log.d("TTT", "Loi lay noi dung truyen");
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
        if(disposable2 != null) {
            disposable2.dispose();
        }
        if(disposable3 != null) {
            disposable3.dispose();
        }
        if(disposable4 != null) {
            disposable4.dispose();
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    private void nextChapter() {
        disposable1 = Observable.fromCallable(() -> {
                    Document maindoc = Jsoup.connect(url).get();
                    if(maindoc.select("#next_chap").hasAttr("disabled")) {
                        return null;
                    }
                    else {
                        String tenChuong = maindoc.select("#next_chap").attr("title");
                        String urlChuong = "https://novelfull.com" + maindoc.select("#next_chap").attr("href");
                        return new Chuong(tenChuong, urlChuong);
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if(data == null) {
                        Toast.makeText(NoiDungTruyenActivity.this, "Đây là chương cuối cùng!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        chuong = data;
                        Next();
                    }
                }, error -> {
                    Log.d("TTT", "Loi lay next chuong");
                });
    }

    private void prevChapter() {
        disposable2 = Observable.fromCallable(() -> {
                    Document maindoc = Jsoup.connect(url).get();
                    if(maindoc.select("#prev_chap").hasAttr("disabled")) {
                        return null;
                    }
                    else {
                        String tenChuong = maindoc.select("#prev_chap").attr("title");
                        String urlChuong = "https://novelfull.com" + maindoc.select("#prev_chap").attr("href");
                        return new Chuong(tenChuong, urlChuong);
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if(data == null) {
                        Toast.makeText(NoiDungTruyenActivity.this, "Đây là chương đầu!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        chuong = data;
                        Prev();
                    }
                }, error -> {
                    Log.d("TTT", "Loi lay prev chuong");
                });
    }

    private void Next() {
        Bundle b = new Bundle();
        b.putSerializable("from", from);
        b.putSerializable("chuong",chuong);
        b.putSerializable("vi tri page", page);
        b.putSerializable("truyenLS", truyenLichSu);
        Intent intent = new Intent(NoiDungTruyenActivity.this,NoiDungTruyenActivity.class);
//        finish();
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
        b.putSerializable("chuong",chuong);
        b.putSerializable("vi tri page", page);
        b.putSerializable("truyenLS", truyenLichSu);
        Intent intent = new Intent(NoiDungTruyenActivity.this,NoiDungTruyenActivity.class);
//        finish();
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

    //Thao tác vuốt
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