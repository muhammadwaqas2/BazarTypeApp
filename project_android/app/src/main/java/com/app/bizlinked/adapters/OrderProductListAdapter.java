package com.app.bizlinked.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.listener.custom.DataUpdateListenerInterface;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.models.viewmodel.OrderDetailViewModel;
import com.app.bizlinked.models.viewmodel.OrderViewModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderProductListAdapter extends RecyclerView.Adapter<OrderProductListAdapter.MyViewHolder> {


    final LayoutInflater inflater;
    Context context;
    ArrayList<OrderDetailViewModel> data;

    DataRecyclerViewClickInterface<OrderDetailViewModel> dataRecyclerViewClickInterface;
    DataUpdateListenerInterface dataUpdateListenerInterface = null;
    boolean isEditable = true;

    public OrderProductListAdapter(Context context, ArrayList<OrderDetailViewModel> data, DataRecyclerViewClickInterface<OrderDetailViewModel> dataRecyclerViewClickInterface, DataUpdateListenerInterface dataUpdateListenerInterface, boolean isEditable) {
        this.context = context;
        this.data = new ArrayList<>();
        this.data.addAll(data);
//        this.data = data;
        inflater = LayoutInflater.from(context);
        this.dataRecyclerViewClickInterface = dataRecyclerViewClickInterface;
        this.dataUpdateListenerInterface = dataUpdateListenerInterface;
        this.isEditable = isEditable;


    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_order_product_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final OrderDetailViewModel customObject = data.get(position);

        holder.tvProductName.setText(customObject.getProductName());

        if(!Utils.isEmptyOrNull(customObject.getProductCategory())){
            holder.tvProductCat.setText(customObject.getProductCategory());
        }else{
            holder.tvProductCat.setText("-");
        }

        if(customObject.getProdImage().getValue() != null && customObject.getProdImage().getValue().length > 0){
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(customObject.getProdImage().getValue(), 0, customObject.getProdImage().getValue().length);
            holder.ivProductImage.setImageBitmap(imageBitmap);
        }else{
            holder.ivProductImage.setImageResource(R.drawable.image_placeholder);
        }

        holder.etProductCartCount.setText(String.valueOf(customObject.getQuantity()));

        holder.tvProductUnitPrice.setText("Rs. "+customObject.getPrice()+"/=");

        holder.tvProductTotalPrice.setText("Amount: Rs. "+(customObject.getQuantity() * customObject.getPrice())+"/=");



        if(isEditable){
            //Add Icon Logic
            holder.ivAddIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int count = 0;
                    if(!Utils.isEmptyOrNull(holder.etProductCartCount.getText().toString().trim())){
                        count = Integer.parseInt(holder.etProductCartCount.getText().toString().trim());
                        count+=1;
                        holder.etProductCartCount.setText(String.valueOf(count));
                    }else{
                        count = 1;
                        holder.etProductCartCount.setText("1");
                    }

                    customObject.setQuantity(count);
                    customObject.updateQuantity(new DatabaseTransactionInterface() {
                        @Override
                        public void onSuccessTransaction() {
                            dataUpdateListenerInterface.updateUI();
                        }

                        @Override
                        public void onErrorTransaction() {

                        }
                    });


                }
            });

            //Subtract Icon Logic
            holder.ivSubIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int count = 0;
                    if(!Utils.isEmptyOrNull(holder.etProductCartCount.getText().toString().trim())) {


                        count = Integer.parseInt(holder.etProductCartCount.getText().toString().trim());

                        if (count <= 1) {
                            holder.etProductCartCount.setText("1");
                        } else {
                            count -= 1;
                            holder.etProductCartCount.setText(String.valueOf(count));
                        }
                    }else{
                        count = 1;
                        holder.etProductCartCount.setText("1");
                    }

                    customObject.setQuantity(count);
                    customObject.updateQuantity(new DatabaseTransactionInterface() {
                        @Override
                        public void onSuccessTransaction() {
                            dataUpdateListenerInterface.updateUI();
                        }

                        @Override
                        public void onErrorTransaction() {

                        }
                    });


                }
            });

            holder.etProductCartCount.setEnabled(true);
        }else{
            holder.ivAddIcon.setOnClickListener(null);
            holder.ivSubIcon.setOnClickListener(null);
            holder.etProductCartCount.setEnabled(false);
        }



        holder.llMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRecyclerViewClickInterface.onClick(customObject, holder.getAdapterPosition());
            }
        });

//        AnimationHelpers.animate(Techniques.SlideInDown, 700, holder.llMainView);
    }

    public void clearAllList() {
        data.clear();
    }

    public void addAllList(ArrayList<OrderDetailViewModel> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public OrderDetailViewModel getItem(int position) {
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
        TextView tvProductName;
        TextView tvProductCat;
        TextView tvProductUnitPrice;
        TextView tvProductTotalPrice;
        CircleImageView ivProductImage;
        ImageView ivSubIcon;
        ImageView ivAddIcon;
        EditText etProductCartCount;


        private MyViewHolder(View itemView) {
            super(itemView);
            llMainView = (LinearLayout) itemView;
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            etProductCartCount = (EditText) itemView.findViewById(R.id.etProductCartCount);
            tvProductUnitPrice = (TextView) itemView.findViewById(R.id.tvProductUnitPrice);
            tvProductCat = (TextView) itemView.findViewById(R.id.tvProductCat);
            tvProductTotalPrice = (TextView) itemView.findViewById(R.id.tvProductTotalPrice);
            ivProductImage = (CircleImageView) itemView.findViewById(R.id.ivProductImage);
            ivSubIcon = (ImageView) itemView.findViewById(R.id.ivSubIcon);
            ivAddIcon = (ImageView) itemView.findViewById(R.id.ivAddIcon);

        }
    }
}