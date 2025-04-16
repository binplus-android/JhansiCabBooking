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

public class RideMateAdapter extends RecyclerView.Adapter<RideMateAdapter.ViewHolder> {
    Context context;
    ArrayList<DestinationModel> list;

    public RideMateAdapter(Context context, ArrayList<DestinationModel> list) {
        this.context = context;
        this.list = list;
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



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView tv_title;
//        ImageView img_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

//            tv_title = itemView.findViewById(R.id.tv_title);
//            img_icon = itemView.findViewById(R.id.iv_icon);
        }
    }
}
