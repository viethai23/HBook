package com.example.hbookdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hbookdemo.R;
import com.example.hbookdemo.object.Truyen;
import com.example.hbookdemo.object.TruyenLichSu;

import java.util.List;

public class TruyenLishSuAdapter extends RecyclerView.Adapter<TruyenLishSuAdapter.TruyenLichSuViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(TruyenLichSu truyenLichSu);
        void onItemLongClick(TruyenLichSu truyenLichSu);
    }

    private List<TruyenLichSu> mListTruyenLS;
    private OnItemClickListener listener;

    public TruyenLishSuAdapter(List<TruyenLichSu> mListTruyenLS, OnItemClickListener listener) {
        this.mListTruyenLS = mListTruyenLS;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TruyenLichSuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truyenlichsu, parent, false);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        layoutParams.setMargins(4, 6, 4, 6);
        itemView.setLayoutParams(layoutParams);
        return new TruyenLichSuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TruyenLichSuViewHolder holder, int position) {
        holder.bind(mListTruyenLS.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mListTruyenLS.size();
    }

    public class TruyenLichSuViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView chapterCurrentTextView;

        public TruyenLichSuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_LS);
            titleTextView = itemView.findViewById(R.id.titleTextView_LS);
            authorTextView = itemView.findViewById(R.id.authorTextView_LS);
            chapterCurrentTextView = itemView.findViewById(R.id.chapterCurrentTextView_LS);
        }

        public void bind(TruyenLichSu truyenlichsu, final OnItemClickListener listener) {
            titleTextView.setText(truyenlichsu.getTruyen().getTenTruyen());
            authorTextView.setText("Tác giả: " + truyenlichsu.getTruyen().getTacGia());
            chapterCurrentTextView.setText("Chương hiện tại: " + truyenlichsu.getChuong().getTenChuong());
            Glide.with(itemView.getContext())
                    .load(truyenlichsu.getTruyen().getImgUrl())
                    .into(imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(truyenlichsu);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(truyenlichsu);
                    return true;
                }
            });
        }
    }
}
