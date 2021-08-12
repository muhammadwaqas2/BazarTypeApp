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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;
import com.daimajia.androidanimations.library.Techniques;
import com.makeramen.roundedimageview.RoundedImageView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmRecyclerViewAdapter;

public class ProductAdapter extends RealmRecyclerViewAdapter<Product, ProductAdapter.MyViewHolder> {


    final LayoutInflater inflater;
    Context context;
    OrderedRealmCollection<Product> data;
    DataRecyclerViewClickInterface<Product> dataRecyclerViewClickInterface;

    public ProductAdapter(Context context, OrderedRealmCollection<Product> data, DataRecyclerViewClickInterface<Product> dataRecyclerViewClickInterface) {
        super(data, true);
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
//        setHasStableIds(true);

        this.context = context;
        this.data = data;
        this.dataRecyclerViewClickInterface = dataRecyclerViewClickInterface;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Product customObject  = getItem(position);

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

//        AnimationHelpers.animate(Techniques.FadeIn, 700, holder.llMainView);

    }

//    public void clearAllList() {
//        if(data != null)
//            data.clear();
//    }
//
//    public void addAllList(OrderedRealmCollection<ProductCategory> data) {
//        if(data != null){
//            this.data.addAll(data);
//            notifyDataSetChanged();
//        }
//    }

//    public void notifyData() {
//        notifyDataSetChanged();
//    }


    public Product getItemFromPosition(int position) {

        if(data != null && position <=  data.size())
            return data.get(position);

        return null;
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
