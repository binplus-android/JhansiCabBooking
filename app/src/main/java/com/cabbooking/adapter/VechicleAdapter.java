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
import com.cabbooking.model.VechicleModel;
import com.cabbooking.model.VechicleModel;

import java.util.ArrayList;

public class VechicleAdapter extends RecyclerView.Adapter<VechicleAdapter.ViewHolder> {
    Context context;
    ArrayList<VechicleModel> list;
    int pos=0;
    onTouchMethod listener;
    public interface onTouchMethod{
        void onSelection(int pos);
    }

    public VechicleAdapter(Context context, ArrayList<VechicleModel> list, onTouchMethod listener) {
        this.context = context;
        this.list = list;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_vechicle,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VechicleModel model = list.get(position);
        holder.tv_vname.setText(model.getName());
        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelection (position);
                pos = position;

            }
        });
        if(position==pos) {
            holder.rel_main.setBackground(context.getDrawable(R.drawable.bg_outline_box));
        }
        else{
            holder.rel_main.setBackground(context.getDrawable(R.drawable.bg_shadow_box));

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_vname;
//        ImageView img_icon;
        RelativeLayout rel_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rel_main = itemView.findViewById(R.id.rel_main);
            tv_vname = itemView.findViewById(R.id.tv_vname);
//            img_icon = itemView.findViewById(R.id.iv_icon);
        }
    }
}
