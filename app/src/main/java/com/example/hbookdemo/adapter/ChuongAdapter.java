package com.example.hbookdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hbookdemo.R;
import com.example.hbookdemo.object.Chuong;

import java.util.List;

public class ChuongAdapter extends RecyclerView.Adapter<ChuongAdapter.ChuongViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private List<Chuong> mListChuong;
    private OnItemClickListener listener;

    public ChuongAdapter(List<Chuong> mListChuong, OnItemClickListener listener) {
        this.mListChuong = mListChuong;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChuongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chuong, parent, false);
        return new ChuongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChuongViewHolder holder, int position) {
        holder.bind(mListChuong.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return mListChuong.size();
    }

    public class ChuongViewHolder extends RecyclerView.ViewHolder {

        TextView tenChuongTV;

        public ChuongViewHolder(@NonNull View itemView) {
            super(itemView);
            tenChuongTV = itemView.findViewById(R.id.tenChuongTV);
        }

        public void bind(Chuong chuong, int position, final OnItemClickListener listener) {
            tenChuongTV.setText(chuong.getTenChuong());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }
    }
}
