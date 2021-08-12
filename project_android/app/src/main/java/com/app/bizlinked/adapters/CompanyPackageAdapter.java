package com.app.bizlinked.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.db.CompanyPackage;
import com.app.bizlinked.models.db.ProductCategory;
import com.daimajia.androidanimations.library.Techniques;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CompanyPackageAdapter extends RealmRecyclerViewAdapter<CompanyPackage, CompanyPackageAdapter.MyViewHolder> {


    final LayoutInflater inflater;
    Context context;
    OrderedRealmCollection<CompanyPackage> data;
    DataRecyclerViewClickInterface<CompanyPackage> dataRecyclerViewClickInterface;

    public CompanyPackageAdapter(Context context, OrderedRealmCollection<CompanyPackage> data, DataRecyclerViewClickInterface<CompanyPackage> dataRecyclerViewClickInterface) {
        super(data, true);

        this.context = context;
        this.data = data;
        this.dataRecyclerViewClickInterface = dataRecyclerViewClickInterface;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CompanyPackageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_company_packages, parent, false);
        return new CompanyPackageAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final CompanyPackage customObject  = getItem(position);

        if(customObject != null && !Utils.isEmptyOrNull(customObject.getPackage().getName()))
            holder.tvCompanyPackagePromoText.setText(customObject.getPackage().getName());

        if(customObject != null && customObject.getPackage().getPrice() != null && customObject.getPackage().getPrice().equals(0.0)){
            holder.tvCompanyPackagePromoPrice.setText(context.getString(R.string.claim_fo_free));
        }else{
            holder.tvCompanyPackagePromoPrice.setText(context.getString(R.string.currency) + " " + customObject.getPackage().getPrice() + " " + context.getString(R.string.only_text));
        }


//        holder.llClaimPackageContainer.setOnClickListener(new View.OnClickListener() {
        holder.llMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRecyclerViewClickInterface.onClick(customObject, holder.getAdapterPosition());
            }
        });

        AnimationHelpers.animate(Techniques.FadeIn, 200 + position * 100, holder.llMainView);

    }


    public CompanyPackage getItemFromPosition(int position) {

        if(data != null && position <=  data.size())
            return data.get(position);

        return null;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        LinearLayout llMainView;
        LinearLayout llClaimPackageContainer;
        ImageView ivArrow;
        TextView tvCompanyPackagePromoText;
        TextView tvCompanyPackagePromoPrice;


        private MyViewHolder(View itemView) {
            super(itemView);
            llMainView = (LinearLayout) itemView;
            llClaimPackageContainer = (LinearLayout) itemView.findViewById(R.id.llClaimPackageContainer);
            ivArrow = (ImageView) itemView.findViewById(R.id.ivArrow);
            tvCompanyPackagePromoText = (TextView) itemView.findViewById(R.id.tvCompanyPackagePromoText);
            tvCompanyPackagePromoPrice = (TextView) itemView.findViewById(R.id.tvCompanyPackagePromoPrice);

        }
    }
}
