package com.cabbooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabbooking.R;
import com.cabbooking.model.BookingHistoryModel;
import com.cabbooking.model.EnquiryModel;
import com.cabbooking.model.VechicleModel;

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    Context context;
    ArrayList<BookingHistoryModel> list;
    int pos=0;
    onTouchMethod listener;
    public interface onTouchMethod{
        void onSelection(int pos);
    }

    public BookingAdapter(Context context, ArrayList<BookingHistoryModel> list,onTouchMethod listener) {
        this.context = context;
        this.list = list;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_booking_history,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelection (position);
                pos = position;

            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
LinearLayout lin_main;
ImageView iv_detail;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lin_main=itemView.findViewById(R.id.lin_main);
            iv_detail=itemView.findViewById(R.id.iv_detail);


        }
    }
}
