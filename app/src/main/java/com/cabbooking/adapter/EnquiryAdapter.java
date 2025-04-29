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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.ViewHolder> {
    Context context;
    ArrayList<EnquiryModel> list;


    public EnquiryAdapter(Context context, ArrayList<EnquiryModel> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_enquiry,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //TextView tv_title;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            lin_main = itemView.findViewById(R.id.lin_main);
//            tv_title = itemView.findViewById(R.id.tv_title);
//            iv_img = itemView.findViewById(R.id.iv_img);
        }
    }
}
