package com.app.bizlinked.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.db.ProductCategory;
import com.daimajia.androidanimations.library.Techniques;
import com.makeramen.roundedimageview.RoundedImageView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CategorySelectionAdapter extends RealmRecyclerViewAdapter<ProductCategory, CategorySelectionAdapter.MyViewHolder> {


  final LayoutInflater inflater;
  Context context;
  OrderedRealmCollection<ProductCategory> data;
  DataRecyclerViewClickInterface<ProductCategory> dataRecyclerViewClickInterface;

  public CategorySelectionAdapter(Context context, OrderedRealmCollection<ProductCategory> data, DataRecyclerViewClickInterface<ProductCategory> dataRecyclerViewClickInterface) {
    super(data, true);

    this.context = context;
    this.data = data;
    this.dataRecyclerViewClickInterface = dataRecyclerViewClickInterface;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_category_selection, parent, false);
    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

    final ProductCategory customObject  = getItem(position);

    if(customObject != null && !Utils.isEmptyOrNull(customObject.getTitle()))
      holder.tvTitle.setText(customObject.getTitle());


    holder.llCatSelectionContainer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dataRecyclerViewClickInterface.onClick(customObject, holder.getAdapterPosition());

      }
    });

    holder.flArrow.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dataRecyclerViewClickInterface.onEditClick(customObject, holder.getAdapterPosition());

      }
    });


    AnimationHelpers.animate(Techniques.FadeIn, 700, holder.llMainView);

  }


  public ProductCategory getItemFromPosition(int position) {

    if(data != null && position <=  data.size())
      return data.get(position);

    return null;
  }

  class MyViewHolder extends RecyclerView.ViewHolder {


    LinearLayout llMainView;
    LinearLayout llCatSelectionContainer;
    FrameLayout flArrow;
    TextView tvTitle;


    private MyViewHolder(View itemView) {
      super(itemView);
      llMainView = (LinearLayout) itemView;
      llCatSelectionContainer = (LinearLayout) itemView.findViewById(R.id.llCatSelectionContainer);
      flArrow = (FrameLayout) itemView.findViewById(R.id.flArrow);
      tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

    }
  }
}