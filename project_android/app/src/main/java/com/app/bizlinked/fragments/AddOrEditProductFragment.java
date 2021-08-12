package com.app.bizlinked.fragments;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.bizlinked.R;
import com.app.bizlinked.activities.MainActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.dialog.DialogClass;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.MediaTypePicker;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.db.ProductImage;
import com.app.bizlinked.models.viewmodel.CategoryViewModal;
import com.app.bizlinked.models.viewmodel.ProductViewModal;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddOrEditProductFragment extends BaseFragment implements MediaTypePicker {


    @BindView(R.id.llMainView)
    LinearLayout llMainView;


    //Image Section
    @BindView(R.id.llPhotoSection)
    LinearLayout llPhotoSection;
    @BindView(R.id.ivCameraIcon)
    ImageView ivCameraIcon;
    //Image Section


    @Order(1)
    @NotEmpty(trim = true, messageResId = R.string.err_product_name_not_empty)
    @BindView(R.id.etTitle)
    EditText etTitle;

    @Order(2)
    @NotEmpty(trim = true, messageResId = R.string.err_product_price)
    @BindView(R.id.etPrice)
    EditText etPrice;

    @BindView(R.id.etDesc)
    EditText etDesc;

    @BindView(R.id.swActiveInActive)
    SwitchCompat swActiveInActive;
    @BindView(R.id.swPublicPrivate)
    SwitchCompat swPublicPrivate;


    @BindView(R.id.btnProductDelete)
    Button btnProductDelete;


    //Category Selection Section
    @BindView(R.id.btnCatSelected)
    Button btnCatSelected;
    @BindView(R.id.ivRemoveSelectedParentCat)
    ImageView ivRemoveSelectedParentCat;
    //Category Selection Section

    //selectedObject
    Product selectedProductObject = null;

    private boolean isDeletedClick = false;

    //Image File
    private ArrayList<File> imageFiles = null;


    //type add/edit
    private String type;

    //Title Bar
    TitleBar titleBar;


    //View Modal
    ProductViewModal productViewModal;

    //View Modal
    CategoryViewModal categoryViewModal;


    //Parent Category
    ProductCategory productCategory = null;


    public AddOrEditProductFragment() {
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
        titleBar.showHeaderTitle(activityReference.getString(R.string.add_product));
        titleBar.setLeftTitleText(getResources().getString(R.string.back));
        titleBar.showLeftIconAndListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityReference.onBackPressed();
            }
        });
        titleBar.showRightTextAndSetListener(activityReference.getString(R.string.done_text), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
                //showSearchViewAndSearchFromServer();
            }
        });


    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_add_or_edit_product;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                int duration = 500;
//                for (int index = 0; index < llMainView.getChildCount(); index++) {
//                    AnimationHelpers.animate(Techniques.SlideInUp, duration, llMainView.getChildAt(index));
//                    duration += 100;
//                }
//                //AnimationHelpers.animate(Techniques.BounceInUp, 600, flLoginView);
//            }
//        },100);


        //Setup Screen For category
        initializeViewModal();
        populateDataInUI();
        setSwitchListenerOnUI();


    }

    @Override
    public void afterBackStackChange(){
        getMainActivity().setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.PRODUCTS);
    }


    private void setSwitchListenerOnUI() {

        swActiveInActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    buttonView.setText(activityReference.getString(R.string.active_text));
                }else{
                    buttonView.setText(activityReference.getString(R.string.inactive_text));

                }
            }
        });

        swPublicPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    buttonView.setText(activityReference.getString(R.string.public_text));
                }else{
                    buttonView.setText(activityReference.getString(R.string.private_text));

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleBarForEditCat();
    }

    private void setTitleBarForEditCat() {

        //If category object has datas
        if (selectedProductObject != null && titleBar != null) {
            titleBar.showHeaderTitle(activityReference.getString(R.string.edit_product));
        }
    }

    private void initializeViewModal() {


        // Get the ViewModel.
        categoryViewModal = new CategoryViewModal();


        // Get the ViewModel.
        productViewModal = new ProductViewModal();


        //If category object has datas
        if(selectedProductObject != null){
            productViewModal.init(selectedProductObject, false);
        }


        //In Case of Add Set Parent Category
        if(type != null && type.equalsIgnoreCase(AppConstant.CATEGORY_TYPE.ADD) && productCategory != null) {
            productViewModal.setProductCategory(productCategory);
        }

        //Set Parent Category Observer
//        categoryViewModal.getAllCitiesFromDB().observe(this, new Observer<RealmResults<City>>() {
//            @Override
//            public void onChanged(@Nullable RealmResults<City> cities) {
//
//            }
//        });

    }

    private void populateDataInUI() {

        if(productViewModal != null){

            //Delete button set
            if(productViewModal.getProduct() != null && !Utils.isEmptyOrNull(productViewModal.getProduct().getId())){
                btnProductDelete.setVisibility(View.VISIBLE);
            }else {
                btnProductDelete.setVisibility(View.GONE);
            }

            //Title Set
            if(!Utils.isEmptyOrNull(productViewModal.getTitle())){
                etTitle.setText(productViewModal.getTitle());
            }

            //Desc Set
            if(!Utils.isEmptyOrNull(productViewModal.getDesc())){
                etDesc.setText(productViewModal.getDesc());
            }

            //Price Set
            if(!Utils.isEmptyOrNull(productViewModal.getPrice())){
                etPrice.setText(productViewModal.getPrice());
            }


            //Active/inActive
            if(productViewModal.getActive()){
                swActiveInActive.setChecked(true);
                swActiveInActive.setText(activityReference.getString(R.string.active_text));
            }else{
                swActiveInActive.setChecked(false);
                swActiveInActive.setText(activityReference.getString(R.string.inactive_text));

            }

            // Public / Private
            if(productViewModal.getPublished()){
                swPublicPrivate.setChecked(true);
                swPublicPrivate.setText(activityReference.getString(R.string.public_text));
            }else{
                swPublicPrivate.setChecked(false);
                swPublicPrivate.setText(activityReference.getString(R.string.private_text));
            }

            //Selected Parent Category
            if(productViewModal.getCategory() != null){
                btnCatSelected.setText(productViewModal.getCategory().getTitle());
                ivRemoveSelectedParentCat.setVisibility(View.VISIBLE);
            }else{
                resetParentCategoryButton();
            }


             if(productViewModal.getProduct() != null && productViewModal.getProduct().getImages() != null &&  productViewModal.getProduct().getImages().size() > 0){

                //For Edit Product Case
                renderImagesInPhotoSectionFromRealM(productViewModal.getProduct().getImages());

             }

             if(productViewModal.getImages() != null && productViewModal.getImages().size() > 0){
                //For Add Case But if navigation happen not remove images from the UI that's  why wrote
                renderImagesInPhotoSection();

             }
        }
    }


    private void resetParentCategoryButton(){
        btnCatSelected.setText(activityReference.getString(R.string.select_cat));
        btnCatSelected.setVisibility(View.VISIBLE);
        ivRemoveSelectedParentCat.setVisibility(View.GONE);
    }



    @OnClick({R.id.ivRemoveSelectedParentCat, R.id.btnProductDelete, R.id.btnCatSelected, R.id.ivCameraIcon})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivRemoveSelectedParentCat:
                resetParentCategoryButton();
                productViewModal.setProductCategory(null);
                break;
            case R.id.btnProductDelete:
                DialogClass.createYesNoDialog(activityReference, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isDeletedClick = true;
                        validateFields();
                    }
                }, R.string.are_you_sure_you_want_to_delete_product).show();

                break;
            case R.id.btnCatSelected:
                openCategorySelectionListScreen();
                break;
            case R.id.ivCameraIcon:
                activityReference.openMediaPicker(AddOrEditProductFragment.this);
                break;
        }
    }


    @Override
    public void onPhotoClicked(ArrayList<File> file) {

        Log.d(AddOrEditProductFragment.class.getName(), file.toString());

        if(file.size() > 0){
            if(imageFiles == null)
                imageFiles = new ArrayList<>();

//            if(imageFiles.size() == 0){
//            }else{
//                for (int i = 0; i < imageFiles.size(); i++) {
//                    if(file.get(0).getAbsolutePath().equalsIgnoreCase(imageFiles.get(i).getAbsolutePath())){
//                        imageFiles.add(file.get(0));
//                        break;
//                    }
//
//                }
//            }

            imageFiles.add(file.get(0));
            productViewModal.setImages(imageFiles);
            renderImagesInPhotoSection();
        }
    }

    private void renderImagesInPhotoSection() {

        if (productViewModal.getImages() != null && productViewModal.getImages().size() > 0){

            ArrayList<File> imageList = productViewModal.getImages();

            llPhotoSection.removeAllViews();

            if(selectedProductObject != null){
                // For Edit Product we render images first from product images
                renderImagesInPhotoSectionFromRealM(productViewModal.getProduct().getImages());
            }

            for (int index = 0; index < imageList.size(); index++) {

                View productImageView = getLayoutInflater().inflate(R.layout.item_photo_upload, null);

                ImageView ivImage = ((ImageView)productImageView.findViewById(R.id.ivImage));

                ivImage.setImageBitmap(BitmapFactory.decodeFile(imageList.get(index).getAbsolutePath()));
                ivImage.setTag(imageList.get(index));

                ((ImageView)productImageView.findViewById(R.id.ivRemoveIcon)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageFiles.remove(ivImage.getTag());
                        llPhotoSection.removeView(productImageView);
                        productViewModal.setImages(imageFiles);
                    }
                });

                //Add to child view
                llPhotoSection.addView(productImageView);
            }
        }
    }
    

    private void renderImagesInPhotoSectionFromRealM(RealmList<ProductImage> images) {


        if (images != null && images.size() > 0){

            RealmList<ProductImage> imageList = images;

            llPhotoSection.removeAllViews();

            for (int index = 0; index < imageList.size(); index++) {

                if(imageList.get(index) != null &&
                        imageList.get(index).getImage() != null &&
                        imageList.get(index).getImage().getData() != null &&
                        imageList.get(index).getImage().getData().length > 0){


                    View productImageView = getLayoutInflater().inflate(R.layout.item_photo_upload, null);
                    ImageView ivImage = ((ImageView)productImageView.findViewById(R.id.ivImage));

                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageList.get(index).getImage().getData(), 0, imageList.get(index).getImage().getData().length);
                    ivImage.setImageBitmap(imageBitmap);
                    ivImage.setTag(imageList.get(index));

                    ((ImageView)productImageView.findViewById(R.id.ivRemoveIcon)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Remove From View Modal

                            productViewModal.deleteProductImgFromProductImgList((ProductImage) ivImage.getTag(), new DatabaseTransactionInterface() {
                                @Override
                                public void onSuccessTransaction() {
                                    productViewModal.addDeletedProductImages((ProductImage)ivImage.getTag());
                                    llPhotoSection.removeView(productImageView);

                                }

                                @Override
                                public void onErrorTransaction() {

                                }
                            });
                        }
                    });

                    //Add to child view
                    llPhotoSection.addView(productImageView);

                }

            }
        }

    }


    @Override
    public void onDocClicked(ArrayList<String> files) {

    }

    @Override
    public void onValidationSuccess() {

        //Title Set
        if(!Utils.isEmptyOrNull(etTitle.getText().toString().trim()))
            productViewModal.setTitle(etTitle.getText().toString().trim());


        //Desc Set
        if(!Utils.isEmptyOrNull(etDesc.getText().toString().trim()))
            productViewModal.setDesc(etDesc.getText().toString().trim());
        else
            productViewModal.setDesc("");

        if(!Utils.isEmptyOrNull(etPrice.getText().toString().trim()))
            productViewModal.setPrice(etPrice.getText().toString().trim());
        else
            productViewModal.setPrice("");

        productViewModal.setDeleted(isDeletedClick);
        productViewModal.setActive(swActiveInActive.isChecked());
        productViewModal.setPublished(swPublicPrivate.isChecked());


        if(selectedProductObject != null){
            productViewModal.updateProduct(new DatabaseTransactionInterface() {
                @Override
                public void onSuccessTransaction() {


                    //For Deleted product case redirect to back page
                    if (isDeletedClick) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activityReference.emptyBackStack();
                                ((MainActivity)activityReference).setOnBottomMenuClickListener(new ProductCategoryListFragment());
                            }
                        }, 100);
                    }else{
                        activityReference.onBackPressed();
                    }
                }

                @Override
                public void onErrorTransaction() {
                    Utils.showSnackBar(activityReference, getView(),
                            activityReference.getString(R.string.err_product_db_update), ContextCompat.getColor(activityReference, R.color.red));

                }
            });
        }else{
            productViewModal.addProduct(new DatabaseTransactionInterface() {
                @Override
                public void onSuccessTransaction() {
                    activityReference.onBackPressed();
                }

                @Override
                public void onErrorTransaction() {

                }
            });
        }

    }

    @Override
    public void onValidationFail() {}



    private void openCategorySelectionListScreen() {

        if(productViewModal.getCategory() != null){
            ArrayList<ProductCategory> heirarchy = categoryViewModal.getHierarchy(productViewModal.getCategory());

            //because of one previous category selection
            heirarchy.remove(0);

            if(selectedProductObject != null){
                openScreenWithObject(null, selectedProductObject.getId());
            }else{
                openScreenWithObject(null, null);
            }

            for (int i = heirarchy.size() - 1; i >= 0; i--) {
                if(selectedProductObject != null){
                    openScreenWithObject(heirarchy.get(i), selectedProductObject.getId());
                }else{
                    openScreenWithObject(heirarchy.get(i), null);
                }

            }

        }else{
            if(selectedProductObject != null){
                openScreenWithObject(null, selectedProductObject.getId());
            }else{
                openScreenWithObject(null, null);
            }
        }


    }

    public void openScreenWithObject(ProductCategory parentCategory, String selectedCatId){
        CategorySelectionFragment categorySelectionFragment = new CategorySelectionFragment();

        categorySelectionFragment.setSelectionListener(new DataRecyclerViewClickInterface<ProductCategory>() {
            @Override
            public void onClick(ProductCategory object, int position) {


                //Clear a fragment and set the value
                while (activityReference.isFragmentPresent(CategorySelectionFragment.class.getName()) != null){
                    activityReference.onBackPressed();
                }

                if(btnCatSelected != null){
                    productViewModal.setProductCategory(object);
                    btnCatSelected.setText(object.getTitle());

                    btnCatSelected.setVisibility(View.VISIBLE);
                    ivRemoveSelectedParentCat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEditClick(ProductCategory object, int position) {

            }
        });

        if(parentCategory != null){
            categorySelectionFragment.setParentCategoryID(parentCategory.getId());
            categorySelectionFragment.setSelectedParentObject(parentCategory);
        }
        categorySelectionFragment.setCatId(selectedCatId);

        activityReference.addSupportFragment(categorySelectionFragment, AppConstant.TRANSITION_TYPES.SLIDE);
    }




    public void setType(String type){
        this.type = type;
    }

    public void setSelectedProductObject(Product selectedProductObject) {
        this.selectedProductObject = selectedProductObject;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }
}