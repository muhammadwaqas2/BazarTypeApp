package com.app.bizlinked.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.viewmodel.OrderViewModel;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {


    final LayoutInflater inflater;
    Context context;
    ArrayList<OrderViewModel> data;

    DataRecyclerViewClickInterface<OrderViewModel> dataRecyclerViewClickInterface;

    public OrderListAdapter(Context context, ArrayList<OrderViewModel> data, DataRecyclerViewClickInterface<OrderViewModel> dataRecyclerViewClickInterface) {
        this.context = context;
        this.data = new ArrayList<>();
        this.data.addAll(data);
//        this.data = data;
        inflater = LayoutInflater.from(context);
        this.dataRecyclerViewClickInterface = dataRecyclerViewClickInterface;


    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_order_list_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final OrderViewModel customObject = data.get(position);

        holder.tvName.setText(customObject.getCompanyName());

        StringBuilder address = new StringBuilder();


        if(!Utils.isEmptyOrNull(customObject.getAddress())){
            if(address.length() > 0)
                address.append(",");
            address.append(customObject.getAddress());
        }


        if(!Utils.isEmptyOrNull(customObject.getStatus())){
            holder.tvStatus.setText(customObject.getStatus());
        }

        if(!Utils.isEmptyOrNull(customObject.getDate())){
            holder.tvDate.setText(customObject.getDate());
        }

//        if(!Utils.isEmptyOrNull(customObject.getMarketName())){
//            if(address.length() > 0)
//                address.append(",");
//            address.append(customObject.getMarketName());
//        }
//
//        if(!Utils.isEmptyOrNull(customObject.getStreetAddress())){
//            if(address.length() > 0)
//                address.append(",");
//            address.append(customObject.getStreetAddress());
//        }
//
//        if(customObject.getSelectedCity() != null && !Utils.isEmptyOrNull(customObject.getSelectedCity().getName())){
//            address.insert(0, customObject.getSelectedCity().getName()  + " - ");
//        }

        if(address.toString().length() > 0){
            holder.tvAddress.setText(address.toString());
        }else{
            holder.tvAddress.setText(context.getString(R.string.not_available));
        }

        if(customObject.getImage() != null && customObject.getImage().length > 0){
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(customObject.getImage(), 0, customObject.getImage().length);
            holder.ivProfileImage.setImageBitmap(imageBitmap);
        }else{
            holder.ivProfileImage.setImageResource(R.drawable.image_placeholder);
        }

        holder.llMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRecyclerViewClickInterface.onClick(customObject, holder.getAdapterPosition());
            }
        });

//        holder.ivAddLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dataRecyclerViewClickInterface.onEditClick(customObject, holder.getAdapterPosition());
//
//            }
//        });

//        AnimationHelpers.animate(Techniques.SlideInDown, 700, holder.llMainView);
    }

    public void clearAllList() {
        data.clear();
    }

    public void addAllList(ArrayList<OrderViewModel> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public OrderViewModel getItem(int position) {
        return this.data.get(position);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeItem(int selectedItemPosition) {
        data.remove(selectedItemPosition);
        notifyItemRemoved(selectedItemPosition);
//        notifyItemRangeChanged(selectedItemPosition, getItemCount());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        LinearLayout llMainView;
        TextView tvStatus;
        TextView tvDate;
        TextView tvName;
        TextView tvAddress;
//        ImageView ivAddLink;
        CircleImageView ivProfileImage;


        private MyViewHolder(View itemView) {
            super(itemView);
            llMainView = (LinearLayout) itemView;
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            ivProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImage);
  //          ivAddLink = (ImageView) itemView.findViewById(R.id.ivAddLink);

        }
    }
}