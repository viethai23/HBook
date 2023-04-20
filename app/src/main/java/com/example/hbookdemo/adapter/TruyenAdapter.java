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

import java.util.List;

public class TruyenAdapter extends RecyclerView.Adapter<TruyenAdapter.TruyenViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(Truyen truyen);
    }

    private List<Truyen> mListTruyen;
    private OnItemClickListener listener;

    public TruyenAdapter(List<Truyen> mListTruyen, OnItemClickListener listener) {
        this.mListTruyen = mListTruyen;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TruyenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truyen, parent, false);
        return new TruyenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TruyenViewHolder holder, int position) {
        holder.bind(mListTruyen.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mListTruyen.size();
    }

    public class TruyenViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView chapterCountTextView;

        public TruyenViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            chapterCountTextView = itemView.findViewById(R.id.chapterCountTextView);
        }

        public void bind(Truyen truyen, final OnItemClickListener listener) {
            titleTextView.setText(truyen.getTenTruyen());
            authorTextView.setText("Tác giả: " + truyen.getTacGia());
            chapterCountTextView.setText("Chương mới nhất: " + truyen.getSoChuong());
            Glide.with(itemView.getContext())
                    .load(truyen.getImgUrl())
                    .into(imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(truyen);
                }
            });
        }
    }
}
