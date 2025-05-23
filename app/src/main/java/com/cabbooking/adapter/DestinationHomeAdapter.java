package com.cabbooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabbooking.R;
import com.cabbooking.model.NearAreaNameModel;

import java.util.ArrayList;

public class DestinationHomeAdapter extends RecyclerView.Adapter<DestinationHomeAdapter.ViewHolder> {
    Context context;
    ArrayList<NearAreaNameModel> list;
    onTouchMethod listener;
    public interface onTouchMethod{
        void onSelection(int pos);
    }
    public DestinationHomeAdapter(Context context, ArrayList<NearAreaNameModel> list, onTouchMethod listener) {
        this.context = context;
        this.list = list;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_home_destination,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NearAreaNameModel model = list.get(position);
        if(list.size()-1==position){
            holder.vline.setVisibility(View.GONE);
        }
        holder.tv_title.setText(model.getName());
        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelection (position);


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView tv_title;
       RelativeLayout rel_main;
        View vline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vline=itemView.findViewById(R.id.vline);
           tv_title = itemView.findViewById(R.id.tv_address);
          rel_main = itemView.findViewById(R.id.rel_main);
        }
    }
}
