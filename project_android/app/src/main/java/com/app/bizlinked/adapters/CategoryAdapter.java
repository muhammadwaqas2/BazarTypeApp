package com.app.bizlinked.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bizlinked.R;
import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.ProductCategory;
import com.daimajia.androidanimations.library.Techniques;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CategoryAdapter extends RealmRecyclerViewAdapter<ProductCategory, CategoryAdapter.MyViewHolder> {


    final LayoutInflater inflater;
    Context context;
    OrderedRealmCollection<ProductCategory> data;
    DataRecyclerViewClickInterface<ProductCategory> dataRecyclerViewClickInterface;
    String layoutType = null;

    public CategoryAdapter(Context context, OrderedRealmCollection<ProductCategory> data, DataRecyclerViewClickInterface<ProductCategory> dataRecyclerViewClickInterface) {
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
        View view = inflater.inflate(R.layout.item_category, parent, false);
        changeWidthOnBasesOfLayoutType(view);
        return new MyViewHolder(view);
    }

    private void changeWidthOnBasesOfLayoutType(View view) {

        //because of linear type
        if(!Utils.isEmptyOrNull(layoutType) && layoutType.equalsIgnoreCase(AppConstant.RECYCLER_VIEW.LAYOUT_TYPE_LINEAR)){
            // This code is used to get the screen dimensions of the user's device
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((BaseActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    width/2,
                    (int)Utils.convertDpToPixel(context.getResources().getDimension(R.dimen.x60), context)
            );
            params.setMargins(
                    (int)Utils.convertDpToPixel(context.getResources().getDimension(R.dimen.x0), context),
                    (int)Utils.convertDpToPixel(context.getResources().getDimension(R.dimen.x0), context),
                    (int)Utils.convertDpToPixel(context.getResources().getDimension(R.dimen.x5), context),
                    (int)Utils.convertDpToPixel(context.getResources().getDimension(R.dimen.x0), context));

            // Set the ViewHolder width to be a third of the screen size, and height to wrap content
//        view.setLayoutParams(new RecyclerView.LayoutParams(width/3, (int)Utils.convertDpToPixel(context.getResources().getDimension(R.dimen.x60), context)));
            view.setLayoutParams(params);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final ProductCategory customObject  = getItem(position);

        holder.llMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRecyclerViewClickInterface.onClick(customObject, holder.getAdapterPosition());

            }
        });

        holder.tvCatName.setText(customObject.getTitle() + "");

        holder.ivEditCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRecyclerViewClickInterface.onEditClick(customObject, holder.getAdapterPosition());
            }
        });

        if(customObject.getImage() != null && customObject.getImage().getData() != null && customObject.getImage().getData().length > 0){
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(customObject.getImage().getData(), 0, customObject.getImage().getData().length);
            holder.ivCatImg.setImageBitmap(imageBitmap);
        }else{
            holder.ivCatImg.setImageResource(R.drawable.image_placeholder);
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


    public ProductCategory getItemFromPosition(int position) {

        if(data != null && position <=  data.size())
            return data.get(position);

        return null;
    }

    public void setLayoutType(String layoutType){
        this.layoutType = layoutType;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        RelativeLayout llMainView;
        RoundedImageView ivCatImg;
        ImageView ivEditCat;
        TextView tvCatName;


        private MyViewHolder(View itemView) {
            super(itemView);
            llMainView = (RelativeLayout) itemView;
            ivCatImg = (RoundedImageView) itemView.findViewById(R.id.ivCatImg);
            ivEditCat = (ImageView) itemView.findViewById(R.id.ivEditCat);
            tvCatName = (TextView) itemView.findViewById(R.id.tvCatName);

        }
    }
}
