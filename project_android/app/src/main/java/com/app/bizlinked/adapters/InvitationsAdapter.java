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
import com.app.bizlinked.listener.custom.InvitationsClickListener;
import com.app.bizlinked.models.enums.LinkStatusEnum;
import com.app.bizlinked.models.viewmodel.LinkViewModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InvitationsAdapter extends RecyclerView.Adapter<InvitationsAdapter.MyViewHolder> {


    final LayoutInflater inflater;
    Context context;
    ArrayList<LinkViewModel> data;
    LinkStatusEnum linkStatus;

    InvitationsClickListener<LinkViewModel> invitationsClickListener;

    public InvitationsAdapter(Context context, ArrayList<LinkViewModel> data, LinkStatusEnum linkStatus, InvitationsClickListener<LinkViewModel> invitationsClickListener) {
        this.context = context;
        this.data = new ArrayList<>();
        if(data != null)
            this.data.addAll(data);
//        this.data = data;
        this.linkStatus = linkStatus;
        inflater = LayoutInflater.from(context);
        this.invitationsClickListener = invitationsClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_invitations_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final LinkViewModel customObject = data.get(position);


        holder.tvName.setText(customObject.getName());


        StringBuilder address = new StringBuilder();
        if(!Utils.isEmptyOrNull(customObject.getProfileViewModel().getSuitNumber())){
            if(address.length() > 0)
                address.append(",");
            address.append(customObject.getProfileViewModel().getSuitNumber());
        }

        if(!Utils.isEmptyOrNull(customObject.getProfileViewModel().getMarketName())){
            if(address.length() > 0)
                address.append(",");
            address.append(customObject.getProfileViewModel().getMarketName());
        }

        if(!Utils.isEmptyOrNull(customObject.getProfileViewModel().getStreetAddress())){
            if(address.length() > 0)
                address.append(",");
            address.append(customObject.getProfileViewModel().getStreetAddress());
        }

        if(customObject.getProfileViewModel().getSelectedCity() != null && !Utils.isEmptyOrNull(customObject.getProfileViewModel().getSelectedCity().getName())){
            address.insert(0, customObject.getProfileViewModel().getSelectedCity().getName()  + " - ");
        }

        if(address.toString().length() > 0){
            holder.tvDesc.setText(address.toString());
        }else{
            holder.tvDesc.setText(context.getString(R.string.not_available));
        }

        if(linkStatus.equals(LinkStatusEnum.RECEIVED)){
            holder.ivAccept.setVisibility(View.VISIBLE);
            holder.ivReject.setVisibility(View.VISIBLE);
        }else if(linkStatus.equals(LinkStatusEnum.SENT)){
            holder.ivAccept.setVisibility(View.GONE);
            holder.ivReject.setVisibility(View.VISIBLE);
        }else{
            holder.ivAccept.setVisibility(View.GONE);
            holder.ivReject.setVisibility(View.GONE);
        }

        if(customObject.getImage().getValue() != null && customObject.getImage().getValue().length > 0){
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(customObject.getImage().getValue(), 0, customObject.getImage().getValue().length);
            holder.ivLinkImage.setImageBitmap(imageBitmap);
        }else{
            holder.ivLinkImage.setImageResource(R.drawable.image_placeholder);
        }

        holder.ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationsClickListener.onAccept(customObject, holder.getAdapterPosition());
            }
        });

        holder.ivReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationsClickListener.onReject(customObject, holder.getAdapterPosition());
            }
        });

        holder.llMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationsClickListener.onClick(customObject, holder.getAdapterPosition());
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

    public LinkViewModel getItem(int index) {
        return this.data.get(index);
    }

    public void removeItem(int selectedItemPosition) {
        if(selectedItemPosition < data.size()){
            data.remove(selectedItemPosition);
            notifyItemRemoved(selectedItemPosition);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        LinearLayout llMainView;
        TextView tvName;
        TextView tvDesc;
        ImageView ivAccept;
        ImageView ivReject;
        CircleImageView ivLinkImage;


        private MyViewHolder(View itemView) {
            super(itemView);
            llMainView = (LinearLayout) itemView;
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
            ivLinkImage = (CircleImageView) itemView.findViewById(R.id.ivLinkImage);
            ivAccept = (ImageView) itemView.findViewById(R.id.ivAccept);
            ivReject = (ImageView) itemView.findViewById(R.id.ivReject);
        }
    }
}
