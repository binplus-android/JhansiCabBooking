package com.cabbooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabbooking.R;
import com.cabbooking.model.DestinationModel;

import java.util.ArrayList;

public class RideMateAdapter extends RecyclerView.Adapter<RideMateAdapter.ViewHolder> {
    Context context;
    ArrayList<DestinationModel> list;
    int pos=0;
    onTouchMethod listener;
    String page_type="";
    public interface onTouchMethod{
        void onSelection(int pos);
    }

    public RideMateAdapter(String page_type,Context context, ArrayList<DestinationModel> list, onTouchMethod listener) {
        this.context = context;
        this.list = list;
        this.listener=listener;
        this.page_type=page_type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ride_mate,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DestinationModel DestinationModel = list.get(position);
        if(page_type.equalsIgnoreCase("payment")){
            holder.tv_rate.setVisibility(View.GONE);
        } else{
            holder.tv_rate.setVisibility(View.VISIBLE);
        }
        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelection (position);
                pos = position;

            }
        });
        if(position==pos) {
            holder.lin_main.setBackground(context.getDrawable(R.drawable.bg_outline_box));
        }
        else{
            holder.lin_main.setBackground(context.getDrawable(R.drawable.bg_shadow_box));

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rate,tv_ridername,tv_vname,tv_vdesc;
         ImageView iv_vimg,iv_rimg;
        LinearLayout lin_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lin_main = itemView.findViewById(R.id.lin_main);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            tv_ridername = itemView.findViewById(R.id.tv_ridername);
            tv_vname = itemView.findViewById(R.id.tv_vname);
            tv_vdesc = itemView.findViewById(R.id.tv_vdesc);
            iv_vimg = itemView.findViewById(R.id.iv_vimg);
            iv_rimg = itemView.findViewById(R.id.iv_rimg);
        }
    }
}
