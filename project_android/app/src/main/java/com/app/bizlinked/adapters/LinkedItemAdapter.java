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

import com.app.bizlinked.R;
import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.viewmodel.LinkViewModel;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LinkedItemAdapter extends RecyclerView.Adapter<LinkedItemAdapter.MyViewHolder> {


    final LayoutInflater inflater;
    Context context;
    ArrayList<LinkViewModel> data;

    DataRecyclerViewClickInterface<LinkViewModel> dataRecyclerViewClickInterface;

    public LinkedItemAdapter(Context context, ArrayList<LinkViewModel> data, DataRecyclerViewClickInterface<LinkViewModel> dataRecyclerViewClickInterface) {
        this.context = context;
        this.data = new ArrayList<>();
        this.data.addAll(data);
//        this.data = data;
        inflater = LayoutInflater.from(context);
        this.dataRecyclerViewClickInterface = dataRecyclerViewClickInterface;


    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_linked_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final LinkViewModel customObject = data.get(position);

        holder.tvName.setText(customObject.getName());

        if(!Utils.isEmptyOrNull(customObject.getRelation())){
            holder.tvDesc.setText(customObject.getRelation());
        }else{
            holder.tvDesc.setText(context.getString(R.string.not_available));
        }

        if(customObject.getImage().getValue() != null && customObject.getImage().getValue().length > 0){
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(customObject.getImage().getValue(), 0, customObject.getImage().getValue().length);
            holder.ivLinkImage.setImageBitmap(imageBitmap);
        }else{
            holder.ivLinkImage.setImageResource(R.drawable.image_placeholder);
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

    public void addAllList(ArrayList<LinkViewModel> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        LinearLayout llMainView;
        TextView tvName;
        TextView tvDesc;
        ImageView ivNavigation;
        CircleImageView ivLinkImage;


        private MyViewHolder(View itemView) {
            super(itemView);
            llMainView = (LinearLayout) itemView;
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
            ivLinkImage = (CircleImageView) itemView.findViewById(R.id.ivLinkImage);
            ivNavigation = (ImageView) itemView.findViewById(R.id.ivNavigation);

        }
    }
}


