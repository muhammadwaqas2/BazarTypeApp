package com.app.bizlinked.adapters;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bizlinked.R;
import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.viewmodel.ProductViewModal;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;
import com.daimajia.androidanimations.library.Techniques;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UnlinkedItemAdapter extends RecyclerView.Adapter<UnlinkedItemAdapter.MyViewHolder> {


    final LayoutInflater inflater;
    Context context;
    ArrayList<ProfileViewModal> data;

    DataRecyclerViewClickInterface<ProfileViewModal> dataRecyclerViewClickInterface;

    public UnlinkedItemAdapter(Context context, ArrayList<ProfileViewModal> data, DataRecyclerViewClickInterface<ProfileViewModal> dataRecyclerViewClickInterface) {
        this.context = context;
        this.data = new ArrayList<>();
        this.data.addAll(data);
//        this.data = data;
        inflater = LayoutInflater.from(context);
        this.dataRecyclerViewClickInterface = dataRecyclerViewClickInterface;


    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_unlinked_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final ProfileViewModal customObject = data.get(position);

        holder.tvName.setText(customObject.getName());

        StringBuilder address = new StringBuilder();


        if(!Utils.isEmptyOrNull(customObject.getSuitNumber())){
            if(address.length() > 0)
                address.append(",");
            address.append(customObject.getSuitNumber());
        }

        if(!Utils.isEmptyOrNull(customObject.getMarketName())){
            if(address.length() > 0)
                address.append(",");
            address.append(customObject.getMarketName());
        }

        if(!Utils.isEmptyOrNull(customObject.getStreetAddress())){
            if(address.length() > 0)
                address.append(",");
            address.append(customObject.getStreetAddress());
        }

        if(customObject.getSelectedCity() != null && !Utils.isEmptyOrNull(customObject.getSelectedCity().getName())){
            address.insert(0, customObject.getSelectedCity().getName()  + " - ");
        }

        if(address.toString().length() > 0){
            holder.tvDesc.setText(address.toString());
        }else{
            holder.tvDesc.setText(context.getString(R.string.not_available));
        }

        if(customObject.getImage().getValue() != null && customObject.getImage().getValue().get(customObject.getProfile().getId()) != null){
            if(customObject.getImage().getValue().get(customObject.getProfile().getId()) != null && customObject.getImage().getValue().get(customObject.getProfile().getId()).length > 0){
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(customObject.getImage().getValue().get(customObject.getProfile().getId()), 0, customObject.getImage().getValue().get(customObject.getProfile().getId()).length);
                holder.ivProfileImage.setImageBitmap(imageBitmap);
            }else{
                holder.ivProfileImage.setImageResource(R.drawable.image_placeholder);
            }
        }else{
            holder.ivProfileImage.setImageResource(R.drawable.image_placeholder);
        }

        holder.llMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRecyclerViewClickInterface.onClick(customObject, holder.getAdapterPosition());

            }
        });

        holder.ivAddLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRecyclerViewClickInterface.onEditClick(customObject, holder.getAdapterPosition());

            }
        });

//        AnimationHelpers.animate(Techniques.SlideInDown, 700, holder.llMainView);
    }

    public void clearAllList() {
        data.clear();
    }

    public void addAllList(ArrayList<ProfileViewModal> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public ProfileViewModal getItem(int position) {
        return this.data.get(position);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeItem(int selectedItemPosition) {

        if(selectedItemPosition < data.size()){
            data.remove(selectedItemPosition);
            notifyItemRemoved(selectedItemPosition);
        }
//        notifyItemRangeChanged(selectedItemPosition, getItemCount());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        LinearLayout llMainView;
        TextView tvName;
        TextView tvDesc;
        ImageView ivAddLink;
        CircleImageView ivProfileImage;


        private MyViewHolder(View itemView) {
            super(itemView);
            llMainView = (LinearLayout) itemView;
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
            ivProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImage);
            ivAddLink = (ImageView) itemView.findViewById(R.id.ivAddLink);

        }
    }
}

