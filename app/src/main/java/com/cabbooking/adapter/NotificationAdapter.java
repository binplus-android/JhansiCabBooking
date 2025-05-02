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
import com.cabbooking.utils.Common;
import com.cabbooking.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    ArrayList<NotificationResp> list;
    Common common;

    public NotificationAdapter(Context context, ArrayList<NotificationResp> list) {
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
//        NotificationResp.Datum model=list.get (position);
//        if(!common.checkNullString(model.getTitle()).isEmpty()){
//            holder.tv_title.setText(model.getTitle());
//            holder.tv_title.setVisibility(View.VISIBLE);
//        }
//
//        if(!common.checkNullString(model.getMessage()).isEmpty()){
//            holder.tv_desc.setText(model.getMessage());
//            holder.tv_desc.setVisibility(View.VISIBLE);
//        }
//
//        if(!common.checkNullString(model.getCreate_at()).isEmpty()){
//            String[] date=model.getCreate_at().split(" ");
//            holder.tv_date.setText(date[0]+" | "+date[1]+" "+date[2]);
//            holder.tv_date.setVisibility(View.VISIBLE);
//        }
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
