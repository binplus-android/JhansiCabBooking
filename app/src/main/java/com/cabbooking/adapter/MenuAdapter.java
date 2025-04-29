package com.cabbooking.adapter;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;

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
import com.cabbooking.model.MenuModel;
import com.cabbooking.model.VechicleModel.RecordList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    Context context;
    ArrayList<MenuModel> list;
    onTouchMethod listener;
    public interface onTouchMethod{
        void onSelection(int pos);
    }

    public MenuAdapter(Context context, ArrayList<MenuModel> list, onTouchMethod listener) {
        this.context = context;
        this.list = list;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_menu,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuModel model = list.get(position);
        holder.tv_title.setText(model.getTitle());
        Picasso.get().load(model.getImage()).placeholder(R.drawable.logo).error(R.drawable.logo).into(holder.iv_img);

        holder.lin_main.setOnClickListener(new View.OnClickListener() {
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
       ImageView iv_img;
        LinearLayout lin_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lin_main = itemView.findViewById(R.id.lin_main);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_img = itemView.findViewById(R.id.iv_img);
        }
    }
}
