package com.cabbooking.adapter;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.cabbooking.R;
import com.cabbooking.fragement.AfterPaymentDoneFragment;
import com.cabbooking.model.BookingHistoryModel;
import com.cabbooking.model.EnquiryModel;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.utils.Common;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    Context context;
    ArrayList<BookingHistoryModel.RecordList> list;
    int pos=0;
    onTouchMethod listener;
    Common common;
    public interface onTouchMethod{
        void onSelection(int pos);
    }

    public BookingAdapter(Context context, ArrayList<BookingHistoryModel.RecordList> list,onTouchMethod listener) {
        this.context = context;
        this.list = list;
        this.listener=listener;
        common=new Common(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_booking_history,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BookingHistoryModel.RecordList model=list.get(position);
        //String status=common.getStatusText(model.getTripStatus());
          if (model.getTripStatus() < 7) {
              holder.tv_track.setVisibility(View.VISIBLE);
          }
          else{
              holder.tv_track.setVisibility(View.GONE);
          }

        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelection (position);
                pos = position;

            }
        });
        holder.tv_id.setText("ID:#"+model.getTripId());
        String[] date=model.getCreatedAt().split(" ");
        holder.tv_date.setText(date[0]+" | "+common.convertToAmPm(date[1]));
        // holder.tv_date.setText(common.convertToAmPm(model.getCreatedAt()));
        if(!common.checkNullString(model.getVehicleModelName()).equalsIgnoreCase("")){
            holder.tv_vname.setText("By "+model.getVehicleModelName());
        }

        holder.tv_amt.setText("-Rs."+String.valueOf(model.getAmount()));
        holder.tv_status.setText(model.getTripStatusName());
        if(!common.checkNullString(model.getVehicleTypeImage()).equalsIgnoreCase("")){
            Picasso.get().load(IMAGE_BASE_URL + model.getVehicleTypeImage()).placeholder(R.drawable.logo).into(holder.iv_vimg);
        }
        if(!common.checkNullString(model.getProfileImage()).equalsIgnoreCase("")){
            Picasso.get().load(IMAGE_BASE_URL + model.getProfileImage()).placeholder(R.drawable.logo).into(holder.iv_dimg);
        }



        holder.tv_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new AfterPaymentDoneFragment();
                Bundle bundle=new Bundle();
                bundle.putString("tripId", String.valueOf(model.getTripId()));
                fragment.setArguments(bundle);
                common.switchFragment(fragment);
            }
        });
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
        LinearLayout lin_main;
        ImageView iv_detail;
        TextView tv_id,tv_date,tv_vname,tv_amt,tv_status;
        ImageView iv_vimg;
        CircleImageView iv_dimg;
        Button tv_track;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lin_main=itemView.findViewById(R.id.lin_main);
            iv_detail=itemView.findViewById(R.id.iv_detail);

            tv_id=itemView.findViewById(R.id.tv_id);
            tv_track=itemView.findViewById(R.id.tv_track);
            tv_date=itemView.findViewById(R.id.tv_date);
            tv_vname=itemView.findViewById(R.id.tv_vname);
            iv_vimg=itemView.findViewById(R.id.iv_vimg);
            tv_amt=itemView.findViewById(R.id.tv_amt);
            tv_status=itemView.findViewById(R.id.tv_status);
            iv_vimg=itemView.findViewById(R.id.iv_vimg);
            iv_dimg=itemView.findViewById(R.id.iv_dimg);


        }
    }
}
