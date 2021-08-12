package com.app.bizlinked.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.viewmodel.ProductViewModal;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ServerProductAdapter extends RecyclerView.Adapter<ServerProductAdapter.MyViewHolder> {


    final LayoutInflater inflater;
    Context context;
    ArrayList<ProductViewModal> data;
    String layoutType = null;

    DataRecyclerViewClickInterface<Product> dataRecyclerViewClickInterface;

    public ServerProductAdapter(Context context, ArrayList<ProductViewModal> data, DataRecyclerViewClickInterface<Product> dataRecyclerViewClickInterface) {
        this.context = context;
        this.data = new ArrayList<>();
        this.data.addAll(data);
//        this.data = data;
        inflater = LayoutInflater.from(context);
        this.dataRecyclerViewClickInterface = dataRecyclerViewClickInterface;


    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final ProductViewModal productViewModal = data.get(position);
        final Product customObject = productViewModal.getProduct();

        holder.llMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRecyclerViewClickInterface.onClick(customObject, holder.getAdapterPosition());
            }
        });

        holder.tvTitle.setText(customObject.getTitle() + "");

        holder.tvPrice.setText("Rs. " + customObject.getPrice() + "/=");

//        holder.ivEditCat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dataRecyclerViewClickInterface.onEditClick(customObject);
//            }
//        });

        if(customObject.getImages() != null
                && customObject.getImages().size() > 0
                && customObject.getImages().get(0) != null
                && customObject.getImages().get(0).getImage() != null
                && customObject.getImages().get(0).getImage().getData() != null
                && customObject.getImages().get(0).getImage().getData().length > 0){
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(customObject.getImages().get(0).getImage().getData(), 0, customObject.getImages().get(0).getImage().getData().length);
            holder.ivProductImg.setImageBitmap(imageBitmap);
        }else{
            holder.ivProductImg.setImageResource(R.drawable.image_placeholder);
        }


//        AnimationHelpers.animate(Techniques.SlideInDown, 700, holder.llMainView);
    }

    public void clearAllList() {
        data.clear();
    }

    public void addAllList(ArrayList<ProductViewModal> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public ProductViewModal getItem(int position) {
        return this.data.get(position);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeItem(int selectedItemPosition) {
        data.remove(selectedItemPosition);
        notifyItemRemoved(selectedItemPosition);
    }

    public void setLayoutType(String layoutType){
        this.layoutType = layoutType;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        LinearLayout llMainView;
        RoundedImageView ivProductImg;
        TextView tvTitle;
        TextView tvPrice;


        private MyViewHolder(View itemView) {
            super(itemView);
            llMainView = (LinearLayout) itemView;
            ivProductImg = (RoundedImageView) itemView.findViewById(R.id.ivProductImg);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        }
    }

}
