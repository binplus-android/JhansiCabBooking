package com.cabbooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabbooking.R;
import com.cabbooking.model.DestinationModel;

import java.util.ArrayList;

public class DestinationHomeAdapter extends RecyclerView.Adapter<DestinationHomeAdapter.ViewHolder> {
    Context context;
    ArrayList<DestinationModel> list;

    public DestinationHomeAdapter(Context context, ArrayList<DestinationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_home_destination,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DestinationModel DestinationModel = list.get(position);
        if(list.size()-1==position){
            holder.vline.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView tv_title;
//        ImageView img_icon;
        View vline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vline=itemView.findViewById(R.id.vline);
//            tv_title = itemView.findViewById(R.id.tv_title);
//            img_icon = itemView.findViewById(R.id.iv_icon);
        }
    }
}
