package com.app.bizlinked.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.app.bizlinked.R;
import com.app.bizlinked.adapters.CategorySelectionAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.viewmodel.ProductCategoryListViewModal;
import butterknife.BindView;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategorySelectionFragment extends BaseFragment {


    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    @BindView(R.id.llNoCategoryDataFound)
    LinearLayout llNoCategoryDataFound;


    @BindView(R.id.flCategoryContainer)
    FrameLayout flCategoryContainer;
    @BindView(R.id.rvCategory)
    RecyclerView rvCategory;


    TitleBar titleBar;


    //All Category/Product View Model
    ProductCategoryListViewModal productCategoryListViewModal;

//    //Category View MOdel
//    CategoryViewModal categoryViewModal;



    //Adapter
    CategorySelectionAdapter categorySelectionAdapter;


    //Parent Cat Id
    private String parentCategoryID = null;

    //Selected Parent Cat
    ProductCategory selectedParentCategory = null;


    //Listener
    DataRecyclerViewClickInterface<ProductCategory> productCategoryDataRecyclerViewClickInterface = null;


    //Selected Category
    String selectedCatId = null;




    public CategorySelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCustomBackPressed() {

        activityReference.onPageBack();
    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        this.titleBar = titleBar;
        titleBar.showHeaderView();

        if(selectedParentCategory != null){
            titleBar.showHeaderTitle(selectedParentCategory.getTitle());
        }else{
            titleBar.showHeaderTitle(activityReference.getString(R.string.main_category));
        }

        titleBar.setLeftTitleText(getResources().getString(R.string.back));
        titleBar.showLeftIconAndListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activityReference.onBackPressed();
            }
        });
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_category_selection;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                if (isVisible() && isAdded()) {
//                    if (llMainView != null) {
//                        int duration = 500;
//                        for (int index = 0; index < llMainView.getChildCount(); index++) {
//                            AnimationHelpers.animate(Techniques.SlideInUp, duration, llMainView.getChildAt(index));
//                            duration += 100;
//                        }
//                    }
//                    //AnimationHelpers.animate(Techniques.BounceInUp, 600, flLoginView);
//                }
//            }
//        }, 100);
//

        //Setup Screen For category
        initializeViewModal();

        //Setup Adapter
        initializeAdapter();
    }

    private void initializeViewModal() {

        // Get the ViewModel.
        productCategoryListViewModal = ViewModelProviders.of(this).get(ProductCategoryListViewModal.class);

    }


    private void initializeAdapter() {

        RealmResults<ProductCategory> productCategories = productCategoryListViewModal.getAllSelectionCategoriesFromDB(parentCategoryID, selectedCatId);

        if(productCategories != null && productCategories.size() > 0){
            rvCategory.setVisibility(View.VISIBLE);
            llNoCategoryDataFound.setVisibility(View.GONE);

        }else{
            rvCategory.setVisibility(View.GONE);
            llNoCategoryDataFound.setVisibility(View.VISIBLE);
        }


        categorySelectionAdapter = new CategorySelectionAdapter(activityReference, productCategories, new DataRecyclerViewClickInterface<ProductCategory>() {
            @Override
            public void onClick(ProductCategory object, int position) {
                productCategoryDataRecyclerViewClickInterface.onClick(object, 0);

            }

            @Override
            public void onEditClick(ProductCategory object, int position) {
                CategorySelectionFragment categorySelectionFragment = new CategorySelectionFragment();
                categorySelectionFragment.setSelectionListener(productCategoryDataRecyclerViewClickInterface);
                if(object != null){
                    categorySelectionFragment.setParentCategoryID(object.getId());
                    categorySelectionFragment.setSelectedParentObject(object);
                }
                categorySelectionFragment.setCatId(selectedCatId);
                activityReference.addSupportFragment(categorySelectionFragment, AppConstant.TRANSITION_TYPES.SLIDE);
            }
        });

        rvCategory.setLayoutManager(new LinearLayoutManager(activityReference));
        rvCategory.setAdapter(categorySelectionAdapter);

    }


    public void setParentCategoryID(String parentCategoryID) {
        this.parentCategoryID = parentCategoryID;
    }

    public void setSelectedParentObject(ProductCategory selectedParentCategory) {
        this.selectedParentCategory = selectedParentCategory;
    }

    public void setSelectionListener(DataRecyclerViewClickInterface<ProductCategory> productCategoryDataRecyclerViewClickInterface) {
        this.productCategoryDataRecyclerViewClickInterface = productCategoryDataRecyclerViewClickInterface;
    }

    public void setCatId(String selectedCatId) {
        this.selectedCatId = selectedCatId;
    }
}
