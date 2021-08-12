package com.app.bizlinked.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductImage;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmList;

public class ProductImageAdapter extends PagerAdapter {

    final LayoutInflater inflater;
    Context context;
    RealmList<ProductImage> data;


    public ProductImageAdapter(Context context, RealmList<ProductImage> data){
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        View itemView = inflater.inflate(R.layout.item_product_image_slider, container, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);

        ProductImage customObject = data.get(position);



//        if(customObject != null && customObject.getImage() != null && customObject.getImage().getImageAvailable() && customObject.getImage().getData() != null){
        if(customObject != null && customObject.getImage() != null && customObject.getImage().getData() != null){
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(customObject.getImage().getData(), 0, customObject.getImage().getData().length);
            viewHolder.ivProductImage.setImageBitmap(imageBitmap);
        }else{
            viewHolder.ivProductImage.setImageResource(R.drawable.image_placeholder);
        }

//        viewHolder.progress_bar;

        container.addView(itemView);

        return itemView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    class MyViewHolder {


        LinearLayout llMainView;
        ImageView ivProductImage;
        ProgressBar progress_bar;


        private MyViewHolder(View itemView) {
            llMainView = (LinearLayout) itemView;
            ivProductImage = (ImageView) itemView.findViewById(R.id.ivProductImage);
            progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);

        }
    }

}
