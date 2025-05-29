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
import com.cabbooking.model.EnquiryModel;
import com.cabbooking.model.MenuModel;
import com.cabbooking.utils.Common;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.ViewHolder> {
    Context context;
    ArrayList<EnquiryModel.RecordList> list;
    Common common;


    public EnquiryAdapter(Context context, ArrayList<EnquiryModel.RecordList> list) {
        this.context = context;
        this.list = list;
        common=new Common(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_enquiry,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EnquiryModel.RecordList data=list.get(position);

        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.lin_des.getVisibility()==View.VISIBLE){
                 holder.lin_des.setVisibility(View.GONE);
                 holder.iv_arrow.setRotation(90);
                }
                else{
                    holder.iv_arrow.setRotation(270);
                    holder.lin_des.setVisibility(View.VISIBLE);
                }
            }
        });
        String[] date=data.getCreatedAt().split(" ");
        holder.tv_dateTime.setText(date[0]+" | "+common.convertToAmPm(date[1]));
      //  holder.tv_dateTime.setText(common.convertToAmPm(data.getCreatedAt()));
        holder.tv_title.setText(data.getType());
        holder.tv_desctination.setText(data.getDescription());




    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_desctination,tv_sol,tv_dateTime;
        LinearLayout lin_main,lin_des;
        ImageView iv_arrow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_arrow=itemView.findViewById(R.id.iv_arrow);
           lin_main = itemView.findViewById(R.id.lin_main);
          tv_title = itemView.findViewById(R.id.tv_title);
            tv_sol = itemView.findViewById(R.id.tv_sol);
          tv_desctination = itemView.findViewById(R.id.tv_desctination);
            lin_des = itemView.findViewById(R.id.lin_des);
           tv_dateTime = itemView.findViewById(R.id.tv_dateTime);
        }
    }
}
