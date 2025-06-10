package com.cabbooking.adapter;

import static com.cabbooking.utils.RetrofitClient.BASE_URL;
import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabbooking.R;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.model.VechicleModel.RecordList;
import com.cabbooking.model.VechicleModel.RecordList;
import com.cabbooking.utils.Common;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class VechicleAdapter extends RecyclerView.Adapter<VechicleAdapter.ViewHolder> {
    Context context;
    ArrayList<VechicleModel.RecordList> list;
    int pos=0;
    onTouchMethod listener;
    Common common;
    String outstation_type="";
    public interface onTouchMethod{
        void onSelection(int pos);
    }

    public VechicleAdapter(String outstation_type,Context context, ArrayList<VechicleModel.RecordList> list, onTouchMethod listener) {
        this.context = context;
        this.outstation_type = outstation_type;
        this.list = list;
        this.listener=listener;
        common=new Common(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_vechicle,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VechicleModel.RecordList model = list.get(position);
        holder.tv_vname.setText(model.getName());
        holder.tv_vdesc.setText(model.getDescription());
        String distance= String.valueOf(common.getDistanceInKm(context));
        String amount=String.format(Locale.US, "%.0f", model.getFare()*Double.parseDouble(distance));
        if(outstation_type.equalsIgnoreCase("1")){
            String returndistance= String.valueOf(common.getReturnDistanceInKm(context));
            String returnamount=String.format(Locale.US, "%.0f", model.getFare()*Double.parseDouble(returndistance));
            holder.tv_rate.setText("Rs."+String.valueOf(Double.parseDouble(amount)+Double.parseDouble(returnamount)));
        }else{
        holder.tv_rate.setText("Rs."+amount);}
        Picasso.get().load(IMAGE_BASE_URL+model.getIcon()).placeholder(R.drawable.logo).error(R.drawable.logo).into(holder.iv_vimg);

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
        TextView tv_vname,tv_vdesc,tv_rate;
       ImageView iv_vimg;
        RelativeLayout rel_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rel_main = itemView.findViewById(R.id.rel_main);
            tv_vname = itemView.findViewById(R.id.tv_vname);
            tv_vdesc = itemView.findViewById(R.id.tv_vdesc);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            iv_vimg = itemView.findViewById(R.id.iv_vimg);
        }
    }
}
