package com.example.hbookdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hbookdemo.HBook;
import com.example.hbookdemo.R;
import com.example.hbookdemo.object.Truyen;
import com.example.hbookdemo.object.TruyenLichSu;

import java.util.List;

public class TruyenKeSachAdapter extends RecyclerView.Adapter<TruyenKeSachAdapter.TruyenKeSachViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(Truyen truyen);
        void onItemLongClick(Truyen truyen);
    }

    private List<Truyen> mListTruyen;
    private OnItemClickListener listener;

    public TruyenKeSachAdapter(List<Truyen> mListTruyen, OnItemClickListener listener) {
        this.mListTruyen = mListTruyen;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TruyenKeSachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truyen, parent, false);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        layoutParams.setMargins(4, 6, 4, 6);
        itemView.setLayoutParams(layoutParams);
        return new TruyenKeSachViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TruyenKeSachViewHolder holder, int position) {
        holder.bind(mListTruyen.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mListTruyen.size();
    }

    public class TruyenKeSachViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView chapterCountTextView;
        String tacgia,chuongmoi;

        public TruyenKeSachViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            chapterCountTextView = itemView.findViewById(R.id.chapterCountTextView);
        }

        public void bind(Truyen truyen, final OnItemClickListener listener) {
            String lang = HBook.languageManager.getLang();
            if(lang.equals("en")){
                tacgia = "Author";
                chuongmoi = "Lastest chapter";
            }else{
                tacgia="Tác giả";
                chuongmoi = "Chương mới nhất";
            }
            titleTextView.setText(truyen.getTenTruyen());
            authorTextView.setText(tacgia + ": " + truyen.getTacGia());
            chapterCountTextView.setText(chuongmoi + ": " + truyen.getSoChuong());
            Glide.with(itemView.getContext())
                    .load(truyen.getImgUrl())
                    .into(imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(truyen);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(truyen);
                    return true;
                }
            });
        }
    }
}
