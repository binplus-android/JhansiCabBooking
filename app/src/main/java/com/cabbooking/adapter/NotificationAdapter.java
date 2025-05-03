package com.cabbooking.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabbooking.Response.NotificationResp;
import com.cabbooking.Response.NotificationResp.RecordList;
import com.cabbooking.utils.Common;
import com.cabbooking.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    ArrayList<NotificationResp.RecordList> list;
    Common common;

    public NotificationAdapter(Context context, ArrayList<NotificationResp.RecordList> list) {
        this.context = context;
        this.list = list;
        common=new Common (context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_notification,null);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NotificationResp.RecordList model=list.get (position);
        if(!common.checkNullString(model.getTitle()).isEmpty()){
            holder.tv_title.setText(model.getTitle());
            holder.tv_title.setVisibility(View.VISIBLE);
        }

        if(!common.checkNullString(model.getDescription()).isEmpty()){
            holder.tv_desc.setText(model.getDescription());
            holder.tv_desc.setVisibility(View.VISIBLE);
        }

        if(!common.checkNullString(model.getCreatedAt()).isEmpty()){
            String[] date=model.getCreatedAt().split(" ");
            holder.tv_date.setText(date[0]+" | "+date[1]);
            holder.tv_date.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView  iv_delete;
        TextView tv_title, tv_desc, tv_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_delete=itemView.findViewById (R.id.iv_delete);
            tv_title=itemView.findViewById (R.id.tv_title);
            tv_desc=itemView.findViewById (R.id.tv_desc);
            tv_date=itemView.findViewById (R.id.tv_date);

        }
    }

}
