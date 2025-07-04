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
import com.cabbooking.model.WalletHistoryModel;
import com.cabbooking.utils.Common;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.ViewHolder> {
    Context context;
    ArrayList<WalletHistoryModel.RecordList> list;

    Common common;
    
    public WalletHistoryAdapter(Context context, ArrayList<WalletHistoryModel.RecordList> list) {
        this.context = context;
        this.list = list;
        this.common=new Common(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_history,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WalletHistoryModel.RecordList model = list.get(position);
        holder.tv_msg.setText(model.getMessage());
        String[] date=model.getCreatedAt().split(" ");
        holder.tv_date.setText(date[0]+" | "+common.convertToAmPm(date[1]));
//        holder.tv_date.setText(model.getCreatedAt());
        holder.tv_id.setText("#ID:"+String.valueOf(model.getId()));
        holder.tv_amt.setText("+Rs."+String.valueOf(model.getAmount()));
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_msg,tv_id,tv_date,tv_amt;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_msg = itemView.findViewById(R.id.tv_msg);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_amt = itemView.findViewById(R.id.tv_amt);
        }
    }
}
