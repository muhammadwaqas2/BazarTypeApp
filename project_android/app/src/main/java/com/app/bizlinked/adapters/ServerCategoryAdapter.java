package com.app.bizlinked.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.viewmodel.CategoryViewModal;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServerCategoryAdapter extends RecyclerView.Adapter<ServerCategoryAdapter.MyViewHolder> {


    final LayoutInflater inflater;
    Context context;
    ArrayList<CategoryViewModal> data;
    String layoutType = null;

    DataRecyclerViewClickInterface<CategoryViewModal> dataRecyclerViewClickInterface;

    public ServerCategoryAdapter(Context context, ArrayList<CategoryViewModal> data, DataRecyclerViewClickInterface<CategoryViewModal> dataRecyclerViewClickInterface) {
        this.context = context;
        this.data = new ArrayList<>();
        this.data.addAll(data);
//        this.data = data;
        inflater = LayoutInflater.from(context);
        this.dataRecyclerViewClickInterface = dataRecyclerViewClickInterface;


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

        final CategoryViewModal customObject = data.get(position);

        holder.llMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRecyclerViewClickInterface.onClick(customObject, holder.getAdapterPosition());

            }
        });

        holder.tvCatName.setText(customObject.getTitle() + "");

        holder.ivEditCat.setVisibility(View.GONE);
//        holder.ivEditCat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dataRecyclerViewClickInterface.onEditClick(customObject, holder.getAdapterPosition());
//            }
//        });

        if(customObject.getImage() != null && customObject.getImage().getData() != null && customObject.getImage().getData().length > 0){
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(customObject.getImage().getData(), 0, customObject.getImage().getData().length);
            holder.ivCatImg.setImageBitmap(imageBitmap);
        }else{
            holder.ivCatImg.setImageResource(R.drawable.image_placeholder);
        }

//        AnimationHelpers.animate(Techniques.SlideInDown, 700, holder.llMainView);
    }

    public void clearAllList() {
        data.clear();
    }

    public void addAllList(ArrayList<CategoryViewModal> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public CategoryViewModal getItem(int position) {
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
