package com.app.bizlinked.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.app.bizlinked.R;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.dialog.DialogClass;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.MediaTypePicker;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.viewmodel.CategoryViewModal;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import java.io.File;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddOrEditCategoryFragment extends BaseFragment implements MediaTypePicker {


    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    //Image Section
    @BindView(R.id.ivCatImage)
    ImageView ivCatImage;
    @BindView(R.id.ivEditIcon)
    ImageView ivEditIcon;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    //Image Section


    //    @Order(1)
    @NotEmpty(trim = true, messageResId = R.string.err_cat_name_not_empty)
    @BindView(R.id.etTitle)
    EditText etTitle;

    @BindView(R.id.etDesc)
    EditText etDesc;


    @BindView(R.id.btnCatDelete)
    Button btnCatDelete;
    @BindView(R.id.btnCatSelected)
    Button btnCatSelected;
    @BindView(R.id.ivRemoveSelectedParentCat)
    ImageView ivRemoveSelectedParentCat;


    //selectedObject
    ProductCategory selectedCatObject = null;


    private boolean isDeleteedClick = false;

    //Image File
    private File imageFile = null;


    //type add/edit
    private String type;

    //Title Bar
    TitleBar titleBar;


    //View Modal
    CategoryViewModal categoryViewModal;

    //Parent Category
    ProductCategory parentCategory = null;


    public AddOrEditCategoryFragment() {
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
        titleBar.showHeaderTitle(activityReference.getString(R.string.add_cat));
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
        return R.layout.fragment_add_or_edit_category;
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
        //setupPageByType();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleBarForEditCat();
    }

    @Override
    public void afterBackStackChange(){
        getMainActivity().setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.PRODUCTS);
    }


    private void setTitleBarForEditCat() {

        //If category object has datas
        if (selectedCatObject != null && titleBar != null) {
            titleBar.showHeaderTitle(activityReference.getString(R.string.edit_cat));
        }
    }

    private void initializeViewModal() {

        // Get the ViewModel.
        categoryViewModal = ViewModelProviders.of(this).get(CategoryViewModal.class);


        //If category object has datas
        if(selectedCatObject != null){
            categoryViewModal.init(selectedCatObject, false);
        }


        //In Case of Add Set Parent Category
        if(type != null && type.equalsIgnoreCase(AppConstant.CATEGORY_TYPE.ADD) && parentCategory != null) {
            categoryViewModal.setParentCategory(parentCategory);
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

        if(categoryViewModal != null){

            //Delete button set
            if(!Utils.isEmptyOrNull(categoryViewModal.getCategoryID())){
                btnCatDelete.setVisibility(View.VISIBLE);
            }else {
                btnCatDelete.setVisibility(View.GONE);
            }

            //Title Set
            if(!Utils.isEmptyOrNull(categoryViewModal.getTitle())){
                etTitle.setText(categoryViewModal.getTitle());
            }

            //Desc Set
            if(!Utils.isEmptyOrNull(categoryViewModal.getDesc())){
                etDesc.setText(categoryViewModal.getDesc());
            }

            //Selected Parent Category
            if(categoryViewModal.getParentCategory() != null){
                btnCatSelected.setText(categoryViewModal.getParentCategory().getTitle());
                ivRemoveSelectedParentCat.setVisibility(View.VISIBLE);
            }else{
                resetParentCategoryButton();
            }

            //Category Image
            if(categoryViewModal.getImage() != null && categoryViewModal.getImage().getData() != null && categoryViewModal.getImage().getData().length > 0){
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(categoryViewModal.getImage().getData(), 0, categoryViewModal.getImage().getData().length);
                ivCatImage.setImageBitmap(imageBitmap);
            }else if(categoryViewModal.getImageFile() != null){
                ivCatImage.setImageBitmap(BitmapFactory.decodeFile(categoryViewModal.getImageFile().getAbsolutePath()));
            }
        }
    }

//
//    private void setupPageByType() {
//        if(!Utils.isEmptyOrNull(type)){
//
//            if(type.equalsIgnoreCase(AppConstant.CATEGORY_TYPE.ADD)){
//
//                btnCatDelete.setVisibility(View.GONE);
//                ivRemoveSelectedParentCat.setVisibility(View.GONE);
//
//            }else if(type.equalsIgnoreCase(AppConstant.CATEGORY_TYPE.EDIT)){
//
//            }
//        }
//    }

    private void resetParentCategoryButton(){
        btnCatSelected.setText(activityReference.getString(R.string.select_cat));
        btnCatSelected.setVisibility(View.VISIBLE);
        ivRemoveSelectedParentCat.setVisibility(View.GONE);
    }


    @OnClick({R.id.ivRemoveSelectedParentCat, R.id.btnCatDelete, R.id.btnCatSelected, R.id.ivEditIcon, R.id.ivCatImage })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivRemoveSelectedParentCat:
                resetParentCategoryButton();
                categoryViewModal.setParentCategory(null);
                break;
            case R.id.btnCatDelete:

                if(categoryViewModal.checkForSubOptions()){
                    DialogClass.createYesNoDialog(activityReference, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isDeleteedClick = true;
                            validateFields();
                            dialog.dismiss();
                        }
                    }, R.string.are_you_sure_you_want_to_delete_cat).show();
                }else{
                    DialogClass.createMessageDialog(activityReference, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, getString(R.string.cat_cant_be_delete), getString(R.string.category_not_be_deleted)).show();
                }


                break;
            case R.id.btnCatSelected:
                openCategorySelectionListScreen();
                break;
            case R.id.ivCatImage:
            case R.id.ivEditIcon:

                if(selectedCatObject != null && categoryViewModal.getImage() != null){

                    TypedValue typedValue = new TypedValue();
                    @ColorInt int color;
                    activityReference.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    color = typedValue.data;


                    BottomSheetMenuDialog mBottomSheetDialog = new BottomSheetBuilder(activityReference)
                            .setMode(BottomSheetBuilder.MODE_LIST)
                            .setIconTintColor(color)
                            .addItem(0, activityReference.getString(R.string.upload_photo), R.drawable.ic_camera)
                            .addDividerItem()
                            .addItem(1, activityReference.getString(R.string.remove), R.drawable.ic_cancel)
                            .setItemClickListener(new BottomSheetItemClickListener() {
                                @Override
                                public void onBottomSheetItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case 0:
                                            activityReference.openMediaPicker(AddOrEditCategoryFragment.this);
                                            break;
                                        case 1:
                                            ivCatImage.setImageResource(R.drawable.image_placeholder);
                                            categoryViewModal.setImageFile(null);
                                            categoryViewModal.setImageDeleted(true);
                                            break;
                                    }
                                    Log.d("Item click", item.getTitle() + "");
                                }
                            })
                            .createDialog();
                    mBottomSheetDialog.show();

                }else{
                    activityReference.openMediaPicker(AddOrEditCategoryFragment.this);
                }
                break;
        }
    }

    private void openCategorySelectionListScreen() {

        if(categoryViewModal.getParentCategory() != null){
            ArrayList<ProductCategory> heirarchy = categoryViewModal.getHierarchy(categoryViewModal.getParentCategory());

            //because of one previous category selection
            heirarchy.remove(0);

            if(selectedCatObject != null){
                openScreenWithObject(null, selectedCatObject.getId());
            }else{
                openScreenWithObject(null, null);
            }

            for (int i = heirarchy.size() - 1; i >= 0; i--) {
                if(selectedCatObject != null){
                    openScreenWithObject(heirarchy.get(i), selectedCatObject.getId());
                }else{
                    openScreenWithObject(heirarchy.get(i), null);
                }

            }

        }else{
            if(selectedCatObject != null){
                openScreenWithObject(null, selectedCatObject.getId());
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
                    categoryViewModal.setParentCategory(object);
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


    @Override
    public void onPhotoClicked(ArrayList<File> file) {

        Log.d(AddOrEditCategoryFragment.class.getName(), file.toString());

        if(file.size() > 0){
            imageFile = file.get(0);
            ivCatImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            categoryViewModal.setImageFile(imageFile);
        }
    }

    @Override
    public void onDocClicked(ArrayList<String> files) {

    }

    @Override
    public void onValidationSuccess() {

        //Title Set
        if(!Utils.isEmptyOrNull(etTitle.getText().toString().trim()))
            categoryViewModal.setTitle(etTitle.getText().toString().trim());


        //Desc Set
        if(!Utils.isEmptyOrNull(etDesc.getText().toString().trim()))
            categoryViewModal.setDesc(etDesc.getText().toString().trim());
        else
            categoryViewModal.setDesc("");



        if(isDeleteedClick)
            categoryViewModal.setDeleted(true);
        else
            categoryViewModal.setDeleted(false);


        if(selectedCatObject != null){
            categoryViewModal.updateCategory(new DatabaseTransactionInterface() {
                @Override
                public void onSuccessTransaction() {
                    activityReference.onBackPressed();
                }

                @Override
                public void onErrorTransaction() {
                    Utils.showSnackBar(activityReference, getView(),
                            activityReference.getString(R.string.err_cat_db_update), ContextCompat.getColor(activityReference, R.color.red));

                }
            });
        }else{
            categoryViewModal.addCategory(new DatabaseTransactionInterface() {
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




    public void setType(String type){
        this.type = type;
    }

    public void setSelectedCatObject(ProductCategory selectedCatObject) {
        this.selectedCatObject = selectedCatObject;
    }

    public void setParentCategory(ProductCategory parentCategory) {
        this.parentCategory = parentCategory;
    }
}