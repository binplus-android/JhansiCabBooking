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
import com.cabbooking.model.nearAreaNameModel;

import java.util.ArrayList;

public class PickUpAddressAdapter extends RecyclerView.Adapter<PickUpAddressAdapter.ViewHolder> {
    Context context;
    ArrayList<nearAreaNameModel> list;
    onTouchMethod listener;
    public interface onTouchMethod{
        void onSelection(int pos);
    }
    public PickUpAddressAdapter(Context context, ArrayList<nearAreaNameModel> list, onTouchMethod listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_destination,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        nearAreaNameModel model = list.get(position);
        holder.tv_title.setText(model.getName());
        holder.tv_subadd.setText(model.getFormatted_address());
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
        TextView tv_title,tv_subadd;
         RelativeLayout rel_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_add);
            tv_subadd = itemView.findViewById(R.id.tv_subadd);
            rel_main = itemView.findViewById(R.id.rel_main);
        }
    }
}
