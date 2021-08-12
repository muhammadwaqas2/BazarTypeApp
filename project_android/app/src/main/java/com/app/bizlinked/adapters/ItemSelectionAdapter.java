package com.app.bizlinked.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.models.GenericNameIdModal;
import com.daimajia.androidanimations.library.Techniques;
import java.util.ArrayList;

public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.MyViewHolder> implements Filterable {


    final LayoutInflater inflater;
    Context context;
    ArrayList<GenericNameIdModal> data;
    ArrayList<GenericNameIdModal> dataListTemp;

    boolean isSingleSelection;


    public ItemSelectionAdapter(Context context, ArrayList<GenericNameIdModal> data, boolean isSingleSelection) {
        this.context = context;
        this.isSingleSelection = isSingleSelection;
//        this.data = new ArrayList<>();
//        this.data.addAll(data);
        this.data = data;
        this.dataListTemp = new ArrayList<>(data);
        inflater = LayoutInflater.from(context);

    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_category_generic, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final GenericNameIdModal customObject  = data.get(position);

        holder.tvTitle.setText(customObject.getTitle());

        if(isSingleSelection){
            data.get(position).setSelect(false);
            holder.ivCheckbox.setVisibility(View.GONE);
        }else {
            if(customObject.isSelect()){
                holder.ivCheckbox.setImageResource(R.drawable.check_signed_filled);
            }else{
                holder.ivCheckbox.setImageResource(R.drawable.check_sign);
            }
        }

        AnimationHelpers.animate(Techniques.SlideInDown, 700, holder.llMainView);

    }

    public void clearAllList() {
        data.clear();
    }

    public void addAllList(ArrayList<GenericNameIdModal> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateDataAtPosition(boolean isSelectFlag, int position) {
        data.get(position).setSelect(isSelectFlag);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                ArrayList<GenericNameIdModal> filteredList = new ArrayList<>();

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList.addAll(dataListTemp);
                } else {
                    for (GenericNameIdModal row : dataListTemp) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    //data = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                data = (ArrayList<CityModal>) filterResults.values;
                data.clear();
                data.addAll((ArrayList<GenericNameIdModal>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }



    class MyViewHolder extends RecyclerView.ViewHolder {


        LinearLayout llMainView;
        TextView tvTitle;
        TextView tvDesc;
        ImageView ivCheckbox;


        private MyViewHolder(View itemView) {
            super(itemView);
            llMainView = (LinearLayout) itemView;
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
            ivCheckbox = (ImageView) itemView.findViewById(R.id.ivCheckbox);

        }
    }
}

