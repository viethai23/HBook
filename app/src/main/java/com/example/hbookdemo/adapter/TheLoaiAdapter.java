package com.example.hbookdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hbookdemo.R;
import com.example.hbookdemo.object.TheLoai;
import java.util.List;

public class TheLoaiAdapter extends RecyclerView.Adapter<TheLoaiAdapter.TheLoaiViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(TheLoai theloai);
    }

    private List<TheLoai> mListTheLoai;
    private OnItemClickListener listener;

    public TheLoaiAdapter(List<TheLoai> mListTheLoai, OnItemClickListener listener) {
        this.mListTheLoai = mListTheLoai;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TheLoaiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theloai, parent, false);
        return new TheLoaiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TheLoaiViewHolder holder, int position) {
        holder.bind(mListTheLoai.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mListTheLoai.size();
    }

    public class TheLoaiViewHolder extends RecyclerView.ViewHolder {

        TextView theLoai;

        public TheLoaiViewHolder(@NonNull View itemView) {
            super(itemView);
            theLoai = itemView.findViewById(R.id.the_loai);
        }

        public void bind(TheLoai theloai, final OnItemClickListener listener) {
            theLoai.setText(theloai.getTenTheLoai());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(theloai);
                }
            });
        }
    }
}
