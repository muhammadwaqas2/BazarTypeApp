package com.app.bizlinked.fragments;


import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.adapters.ProductImageAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.dialog.DialogClass;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataSaveAndConvertInterface;
import com.app.bizlinked.listener.custom.OnBackPressInterface;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.Order;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.db.ProductImage;
import com.app.bizlinked.models.enums.LinkStatusEnum;
import com.app.bizlinked.models.enums.ProductViewEnum;
import com.app.bizlinked.models.viewmodel.LinkViewModel;
import com.app.bizlinked.models.viewmodel.ProductViewModal;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;
import com.rd.PageIndicatorView;

import java.util.HashMap;

import butterknife.BindView;
import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailViewFragment extends BaseFragment {


    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    //Image Section
    @BindView(R.id.vpProductImage)
    ViewPager vpProductImage;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView pageIndicatorView;
    //Image Section

    //Add To Cart Section
    @BindView(R.id.llAddToCartSection)
    LinearLayout llAddToCartSection;
    @BindView(R.id.ivAddIcon)
    ImageView ivAddIcon;
    @BindView(R.id.ivSubIcon)
    ImageView ivSubIcon;
    @BindView(R.id.etProductCartCount)
    EditText etProductCartCount;
    @BindView(R.id.btnAddToCart)
    Button btnAddToCart;
    //Add To Cart Section


    @BindView(R.id.tvProductTitle)
    TextView tvProductTitle;

    @BindView(R.id.tvProductDesc)
    TextView tvProductDesc;

    @BindView(R.id.tvProductPrice)
    TextView tvProductPrice;

    @BindView(R.id.tvProductStatus)
    TextView tvProductStatus;

    @BindView(R.id.tvProductPublishedStatus)
    TextView tvProductPublishedStatus;

    @BindView(R.id.tvProductCategory)
    TextView tvProductCategory;


    //Title Bar
    TitleBar titleBar;


    //View Modal
    ProductViewModal productViewModal;

    //Selected Product
    Product selectedProductObject;

    //Selected Category
    ProductCategory category;

    Boolean isFromServer = false;

    ProductViewEnum productViewType;
    LinkViewModel linkViewModel = null;
    ProfileViewModal companyProfileViewModel = null;

    OnBackPressInterface onBackPressInterface = null;


    public ProductDetailViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCustomBackPressed() {
        activityReference.onPageBack();

        if (onBackPressInterface != null)
            onBackPressInterface.onBackPressListener();
    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        this.titleBar = titleBar;
        titleBar.showHeaderView();
        titleBar.showHeaderTitle(activityReference.getString(R.string.product_detail));
        titleBar.setLeftTitleText(getResources().getString(R.string.back));
        titleBar.showLeftIconAndListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityReference.onBackPressed();
            }
        });

        if (productViewType != null && productViewType.equals(ProductViewEnum.LOGGED_IN_COMPANY_PROFILE)) {
            if (!selectedProductObject.getDeleted()) {
                titleBar.showRightTextAndSetListener(activityReference.getString(R.string.edit_text), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddOrEditProductFragment addOrEditCategoryFragment = new AddOrEditProductFragment();
                        addOrEditCategoryFragment.setSelectedProductObject(selectedProductObject);
                        activityReference.addSupportFragment(addOrEditCategoryFragment, AppConstant.TRANSITION_TYPES.SLIDE);
                    }
                });
            }
        }
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_product_detail_view;
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
    public void afterBackStackChange() {

        getMainActivity().setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.PRODUCTS);

    }


    private void initializeViewModal() {

        // Get the ViewModel.
        productViewModal = new ProductViewModal();


//        If product object has datas
        if (selectedProductObject != null) {
            productViewModal.init(
                    selectedProductObject, // First Selected object
                    (productViewType != null && productViewType.equals(ProductViewEnum.COMPANY_PROFILE)) // is From Server or not condition
            );
        }


        //In Case of Add Set Parent Category
        if (category != null) {
            productViewModal.setCategory(category);
        }
    }

    private void populateDataInUI() {

        if (productViewModal != null) {

            //Title Set
            if (!Utils.isEmptyOrNull(productViewModal.getTitle())) {
                tvProductTitle.setText(productViewModal.getTitle());
            }

            //Desc Set
            if (!Utils.isEmptyOrNull(productViewModal.getDesc())) {
                tvProductDesc.setText(productViewModal.getDesc());
            }

            //Product Status
            tvProductStatus.setText((productViewModal.getActive() ? activityReference.getString(R.string.active_text) : activityReference.getString(R.string.inactive_text)));

            //Product Published Status
            tvProductPublishedStatus.setText((productViewModal.getPublished() ? activityReference.getString(R.string.public_text) : activityReference.getString(R.string.private_text)));

            //Product Price
            tvProductPrice.setText("Rs. " + productViewModal.getPrice() + "/=");


            //Product Category
            if (productViewModal.getCategory() != null && !Utils.isEmptyOrNull(productViewModal.getCategory().getTitle())) {
                tvProductCategory.setText(productViewModal.getCategory().getTitle());
            }

//            Product Images
            if (productViewModal.getProduct() != null && productViewModal.getProduct().getImages() != null && productViewModal.getProduct().getImages().size() > 0) {
                renderImagesInPhotoSectionFromRealM(productViewModal.getProduct().getImages());
            } else {
                //initializeViewPagerImages(new RealmList<>());

                Observer<HashMap<String, byte[]>> imageObserver = new Observer<HashMap<String, byte[]>>() {
                    @Override
                    public void onChanged(@Nullable HashMap<String, byte[]> imageData) {
                        renderImagesInPhotoSectionFromRealM(productViewModal.getProduct().getImages());
                    }
                };
                //For Server Product
                productViewModal.getProductImage().removeObserver(imageObserver);
                productViewModal.getProductImage().observe(activityReference, imageObserver);
                //This Work is For Image Show when Image Downloads


            }


            //Add To Cart Section

            if (productViewType != null && productViewType.equals(ProductViewEnum.COMPANY_PROFILE)) {

                String companyId = null;

                //for any company products show
                if (linkViewModel != null) {
                    companyId = linkViewModel.getLinkedCompanyID();
                } else if (companyProfileViewModel != null) {
                    companyId = companyProfileViewModel.getId();
                }

                Link companyLink = AppDBHelper.getRealmInstance().where(Link.class)
                        .equalTo("linkedCompany.id", companyId)
                        .and()
                        .equalTo("status", LinkStatusEnum.LINKED.getValue())
                        .findFirst();

                //Supplier product add to cart option enabled if both linked
                if (companyLink != null) {
                    setListenerForAddAndRemoveCart();
                    llAddToCartSection.setVisibility(View.VISIBLE);
                } else {
                    llAddToCartSection.setVisibility(View.GONE);
                }

            } else {
                llAddToCartSection.setVisibility(View.GONE);
            }
        }
    }

    private void setListenerForAddAndRemoveCart() {

        //First Digit not be 0
        etProductCartCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable edt) {
                if (edt.length() == 1 && edt.toString().equals("0"))
                    etProductCartCount.setText("");
            }
        });

        //Add Icon Logic
        ivAddIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Utils.isEmptyOrNull(etProductCartCount.getText().toString().trim())) {
                    int count = Integer.parseInt(etProductCartCount.getText().toString().trim());
                    count += 1;
                    etProductCartCount.setText(String.valueOf(count));
                } else {
                    etProductCartCount.setText("1");
                }


            }
        });

        //Subtract Icon Logic
        ivSubIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Utils.isEmptyOrNull(etProductCartCount.getText().toString().trim())) {


                    int count = Integer.parseInt(etProductCartCount.getText().toString().trim());

                    if (count <= 1) {
                        etProductCartCount.setText("1");
                    } else {
                        count -= 1;
                        etProductCartCount.setText(String.valueOf(count));
                    }
                } else {
                    etProductCartCount.setText("1");
                }

            }
        });


        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Utils.isEmptyOrNull(etProductCartCount.getText().toString().trim())) {
                    Utils.showSnackBar(activityReference, getView(), activityReference.getString(R.string.quantity_not_be_empty), ContextCompat.getColor(activityReference, R.color.red));
                    return;
                }


                String companyId = null;
                int quantity = 0;

                quantity = Integer.parseInt(etProductCartCount.getText().toString().trim());


                //for any company products show
                if (linkViewModel != null) {
                    companyId = linkViewModel.getLinkedCompanyID();
                } else if (companyProfileViewModel != null) {
                    companyId = companyProfileViewModel.getId();
                }


                productViewModal.addProductToOrder(quantity, companyId, new DataSaveAndConvertInterface<Object, Order>() {
                    @Override
                    public void onSuccess(Object syncResponse, Order order) {
                        activityReference.onPageBack();
                    }

                    @Override
                    public void onError() {
                        //Error Show
                        DialogClass.createMessageDialog(activityReference, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, activityReference.getString(R.string.there_is_already_one_order), activityReference.getString(R.string.order_can_not_be_created)).show();

                    }
                });
            }
        });

    }


    private void renderImagesInPhotoSectionFromRealM(RealmList<ProductImage> images) {
        if (images != null && images.size() > 0) {
            initializeViewPagerImages(images);
        }
    }

    private void initializeViewPagerImages(RealmList<ProductImage> images) {
        //ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        vpProductImage.setAdapter(new ProductImageAdapter(activityReference, images));
        pageIndicatorView.setViewPager(vpProductImage);
        //pageIndicatorView.setCount(images.size());

    }


    public void setSelectedProductObject(Product selectedProductObject) {
        this.selectedProductObject = selectedProductObject;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public void setSelectedProductViewType(ProductViewEnum productViewType) {
        this.productViewType = productViewType;
    }

    public void setLinkViewModel(LinkViewModel linkViewModel) {
        this.linkViewModel = linkViewModel;
    }

    public void setCompanyProfileViewModel(ProfileViewModal companyProfileViewModel) {
        this.companyProfileViewModel = companyProfileViewModel;
    }

    public void setonBackPressListener(OnBackPressInterface onBackPressInterface) {
        this.onBackPressInterface = onBackPressInterface;
    }
}
