package com.app.bizlinked.fragments;


import android.arch.lifecycle.Observer;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.adapters.CategoryAdapter;
import com.app.bizlinked.adapters.ProductAdapter;
import com.app.bizlinked.adapters.ServerCategoryAdapter;
import com.app.bizlinked.adapters.ServerProductAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.listener.custom.OnBackPressInterface;
import com.app.bizlinked.models.db.OrderStatus;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.enums.LinkRelationEnum;
import com.app.bizlinked.models.enums.OrderScreenStatusEnum;
import com.app.bizlinked.models.enums.ProductViewEnum;
import com.app.bizlinked.models.enums.ProfileViewEnum;
import com.app.bizlinked.models.viewmodel.CategoryViewModal;
import com.app.bizlinked.models.viewmodel.LinkViewModel;
import com.app.bizlinked.models.viewmodel.ProductCategoryListViewModal;
import com.app.bizlinked.models.viewmodel.ProductViewModal;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;
import com.daimajia.androidanimations.library.Techniques;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import droidninja.filepicker.utils.GridSpacingItemDecoration;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductCategoryListFragment extends BaseFragment {


    @BindView(R.id.llMainView)
    RelativeLayout llMainView;

    @BindView(R.id.tvNoDataFound)
    TextView tvNoDataFound;


    @BindView(R.id.flOverlay)
    FrameLayout flOverlay;

    @BindView(R.id.llCategoryContainer)
    LinearLayout llCategoryContainer;
    @BindView(R.id.llProductContainer)
    LinearLayout llProductContainer;

    @BindView(R.id.rvCategories)
    RecyclerView rvCategories;
    @BindView(R.id.rvProducts)
    RecyclerView rvProducts;

    @BindView(R.id.swipeRefreshCatAndProduct)
    SwipeRefreshLayout swipeRefreshCatAndProduct;


    //Floating Buttons
    @BindView(R.id.fabBtn)
    FloatingActionButton fabBtn;
    @BindView(R.id.llAddProduct)
    LinearLayout llAddProduct;
    @BindView(R.id.llAddCategory)
    LinearLayout llAddCategory;
    //Floating Buttons


    //Category Adapter
    CategoryAdapter itemLocalCategoryAdapter;
    //Product Adapter
    ProductAdapter itemLocalProductAdapter;


    //Title Bar
    TitleBar titleBar;

    //View Modal
    ProductCategoryListViewModal productCategoryListViewModal;

    //Selected Category ID
    String parentCategoryID = null;
    ProductCategory parentCategory;

    //View Type with diffrent use case
    ProductViewEnum productViewType;

    LinkViewModel linkViewModel = null;

    ProfileViewModal companyProfileViewModel = null;

    //For Server Cat/Product Work
    ArrayList<CategoryViewModal> serverCategoryViewModals = null;
    ArrayList<ProductViewModal> serverProductViewModals = null;
    ServerCategoryAdapter itemServerCategoryAdapter;
    ServerProductAdapter itemServerProductAdapter;

    //For Pagination (Server Cat/Product) Work
    int currentPage = 0;
    boolean isLoadingData = false;

    //For Cart BUtton Show/Hide
    OrderScreenStatusEnum orderScreenStatus = null;
    String orderId = null;

    OnBackPressInterface onBackPressInterface = null;

    public ProductCategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCustomBackPressed() {
        activityReference.onPageBack();
        if (onBackPressInterface != null) {
            onBackPressInterface.onBackPressListener();
        }
    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        this.titleBar = titleBar;


//        this.titleBar = titleBar;
//        titleBar.showHeaderView();
//        titleBar.setLeftTitleText(getResources().getString(R.string.profile_text));
//        titleBar.showLeftIconAndListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activityReference.onBackPressed();
//            }
//        });

        setTitleBarForParentCategory();
    }

    private void openSupplierProfileScreen() {
        CompanyDetailFragment companyDetailFragment = new CompanyDetailFragment();
        companyDetailFragment.setCompanyRelation(LinkRelationEnum.CUSTOMER); // because of Showing Product
        companyDetailFragment.setType(ProfileViewEnum.COMPANY_PROFILE);

        if (linkViewModel != null) {
            companyDetailFragment.setLinkViewModel(linkViewModel);
            companyDetailFragment.setCompanyProfileViewModel(linkViewModel.getProfileViewModel());
        } else if (companyProfileViewModel != null) {
            companyDetailFragment.setCompanyProfileViewModel(companyProfileViewModel);
        }

//        companyDetailFragment.setonBackPressListener(new OnBackPressInterface() {
//            @Override
//            public void onBackPressListener() {
//                //setTitleBar(titleBar);
//            }
//        });
        activityReference.realAddSupportFragment(companyDetailFragment, AppConstant.TRANSITION_TYPES.SLIDE);

    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_product_category_list;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

        //Setup Screen For profile
        initializeViewModal();

        if (productViewType.equals(ProductViewEnum.LOGGED_IN_COMPANY_PROFILE)) {
            //Setup Screen
            setupScreenForLocalCatAndProductList();
            setupSwipeToRefreshLayout(false);
        } else if (productViewType.equals(ProductViewEnum.COMPANY_PROFILE)) {
            //Setup Screen
            setupScreenForServerCatAndProductList();
            setupSwipeToRefreshLayout(true);
        }
    }

    private void setupSwipeToRefreshLayout(boolean isForServerCatAndProduct) {

        if (isForServerCatAndProduct) {

            swipeRefreshCatAndProduct.setEnabled(true);
            swipeRefreshCatAndProduct.setColorSchemeResources(R.color.appColorPrimary);
            swipeRefreshCatAndProduct.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if(serverCategoryViewModals != null && serverCategoryViewModals.size() > 0){
                        serverCategoryViewModals.clear();
                    }

                    if(itemServerCategoryAdapter != null && itemServerCategoryAdapter.getItemCount() > 0){
                        itemServerCategoryAdapter.clearAllList();
                    }

                    if(serverProductViewModals != null && serverProductViewModals.size() > 0){
                        serverProductViewModals.clear();
                    }

                    if(itemServerProductAdapter != null && itemServerProductAdapter.getItemCount() > 0){
                        itemServerProductAdapter.clearAllList();
                    }

                    productCategoryListViewModal.getDataFromServer(true);
                    swipeRefreshCatAndProduct.setRefreshing(false);
                }
            });
        } else {
            swipeRefreshCatAndProduct.setEnabled(false);
            swipeRefreshCatAndProduct.setRefreshing(false);
        }

    }

    @Override
    public void afterBackStackChange() {
        super.afterBackStackChange();


        //When goes to any child category set titlebar
        setTitleBarForParentCategory();


        //Add Button Show/Hide
        if (productViewType.equals(ProductViewEnum.LOGGED_IN_COMPANY_PROFILE)) {
            //Add Cat/Prod Option
            fabBtn.setVisibility(View.VISIBLE);
        } else if (productViewType.equals(ProductViewEnum.COMPANY_PROFILE)) {
            //Remove Cat/Prod Option
            fabBtn.setVisibility(View.GONE);


            showHideCartButton();
        }

    }

    private void showHideCartButton() {

        String companyID = null;

        if (linkViewModel != null) {
            companyID = linkViewModel.getLinkedCompanyID();
        } else if (companyProfileViewModel != null) {
            companyID = companyProfileViewModel.getId();
        }

        RealmResults<OrderStatus> resultOrders = AppDBHelper.getRealmInstance().where(OrderStatus.class)
                .equalTo("order.supplierCompanyID", companyID)
                .and()
                .isNull("submitted")
                .findAll();


        if (!resultOrders.isEmpty()) {
            OrderStatus status = resultOrders.first();
            if (status != null) {
                orderId = status.getOrder().getId();
                if (status.getSubmitted() == null) {
                    orderScreenStatus = OrderScreenStatusEnum.DRAFT;
                } else {
                    orderScreenStatus = OrderScreenStatusEnum.PLACED;
                }
            } else {
                orderScreenStatus = OrderScreenStatusEnum.PLACED;
            }


            //Only Show for Draft Case
            if (orderScreenStatus.getValue().equalsIgnoreCase(OrderScreenStatusEnum.DRAFT.getValue())) {
                // Color Primary
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getContext().getTheme();
                @ColorInt int color;
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                color = typedValue.data;
                // Show Cart Icon and Listener

                if (titleBar != null) {
                    titleBar.showRightIcon1AndSetListener(View.VISIBLE, R.drawable.ic_svg_cart, color, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OrderStatusFragment orderStatusFragment = new OrderStatusFragment();
                            orderStatusFragment.setOrderId(orderId);
                            orderStatusFragment.setScreenStatus(orderScreenStatus);
                            orderStatusFragment.setShowSearchFilter(false);
//                            orderStatusFragment.setBackPressListener(new OnBackPressInterface() {
//                                @Override
//                                public void onBackPressListener() {
//                                    setTitleBar(titleBar);
//                                    //setupScreenForListScreen();
//                                }
//                            });
                            activityReference.realAddSupportFragment(orderStatusFragment, AppConstant.TRANSITION_TYPES.SLIDE);

                        }
                    });
                }
            }
        } else {
            titleBar.showRightIcon1AndSetListener(View.GONE, R.drawable.ic_svg_cart, R.color.appColorPrimary, null);
        }
    }


    private void setTitleBarForParentCategory() {

        if (parentCategory != null) {
            if (titleBar != null) {

                titleBar.showHeaderView();

                titleBar.showHeaderTitle(parentCategory.getTitle());

                titleBar.setLeftTitleText(getResources().getString(R.string.back));
                titleBar.showLeftIconAndListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityReference.onBackPressed();
                    }
                });

                titleBar.showRightSearchIconAndSetListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openProductSearchPage();
                    }
                });

            }

        } else {

            //For Root Category
            titleBar.showHeaderView();

            //Logged in user category and products
            if (productViewType.equals(ProductViewEnum.LOGGED_IN_COMPANY_PROFILE)) {
                titleBar.showHeaderTitle(activityReference.getString(R.string.products));
            } else if (productViewType.equals(ProductViewEnum.COMPANY_PROFILE)) {

                //for any company products show
                if (linkViewModel != null) {
                    titleBar.showHeaderTitle(linkViewModel.getName() + " " + activityReference.getString(R.string.products_with_appos));
                    titleBar.setHeaderClickListener(v -> openSupplierProfileScreen());

                } else if (companyProfileViewModel != null) {
                    titleBar.showHeaderTitle(companyProfileViewModel.getName() + " " + activityReference.getString(R.string.products_with_appos));
                    titleBar.setHeaderClickListener(v -> openSupplierProfileScreen());
                }

                // Back Icon and Back Work
                titleBar.setLeftTitleText(getResources().getString(R.string.back));
                titleBar.showLeftIconAndListener(v -> activityReference.onBackPressed());
            }

            titleBar.showRightSearchIconAndSetListener(v -> openProductSearchPage());

        }
    }

    private void initializeViewModal() {

        // Get the ViewModel.
        productCategoryListViewModal = new ProductCategoryListViewModal();
        productCategoryListViewModal.setActivityReference(activityReference);

        //Logged in user category and products
        if (productViewType.equals(ProductViewEnum.LOGGED_IN_COMPANY_PROFILE)) {
            //Set observer for DB data change
            setObserverForCategoryAndProductFromDB();
        } else if (productViewType.equals(ProductViewEnum.COMPANY_PROFILE)) {

            String companyId = null;

            if (linkViewModel != null) {
                companyId = linkViewModel.getLinkedCompanyID();
            } else if (companyProfileViewModel != null) {
                companyId = companyProfileViewModel.getId();
            }
            //for any company products show
            productCategoryListViewModal.getAllCategoriesDataFromServer(companyId, parentCategoryID).observe(activityReference, new Observer<ArrayList<CategoryViewModal>>() {
                @Override
                public void onChanged(@Nullable ArrayList<CategoryViewModal> categoryViewModals) {

                    if (itemServerCategoryAdapter != null) {

                        serverCategoryViewModals.addAll(categoryViewModals);
                        itemServerCategoryAdapter.addAllList(categoryViewModals);

                        //This Work is For Image Show when Image Downloads
                        for (int index = 0; index < itemServerCategoryAdapter.getItemCount(); index++) {
                            int finalIndex = index;
                            Observer<HashMap<String, byte[]>> imageObserver = new Observer<HashMap<String, byte[]>>() {
                                @Override
                                public void onChanged(@Nullable HashMap<String, byte[]> imageData) {
                                    itemServerCategoryAdapter.notifyItemChanged(finalIndex);
                                }
                            };

                            itemServerCategoryAdapter.getItem(index).getCatImage().removeObserver(imageObserver);
                            itemServerCategoryAdapter.getItem(index).getCatImage().observe(activityReference, imageObserver);
                        }
                        //This Work is For Image Show when Image Downloads

                    }
                    setupScreenForServerCatAndProductOnUI();
                }
            });

            productCategoryListViewModal.getAllProductDataFromServer(companyId, parentCategoryID).observe(activityReference, new Observer<ArrayList<ProductViewModal>>() {
                @Override
                public void onChanged(@Nullable ArrayList<ProductViewModal> productViewModal) {

                    if (itemServerProductAdapter != null && productViewModal != null) {

                        serverProductViewModals.addAll(productViewModal);
                        itemServerProductAdapter.addAllList(productViewModal);

                        //This Work is For Image Show when Image Downloads
                        for (int index = 0; index < itemServerProductAdapter.getItemCount(); index++) {
                            int finalIndex = index;
                            Observer<HashMap<String, byte[]>> imageObserver = new Observer<HashMap<String, byte[]>>() {
                                @Override
                                public void onChanged(@Nullable HashMap<String, byte[]> imageData) {
                                    itemServerProductAdapter.notifyItemChanged(finalIndex);
                                }
                            };

                            itemServerProductAdapter.getItem(index).getProductImage().removeObserver(imageObserver);
                            itemServerProductAdapter.getItem(index).getProductImage().observe(activityReference, imageObserver);
                        }
                        //This Work is For Image Show when Image Downloads
                    }
                    setupScreenForServerCatAndProductOnUI();
                }
            });

            //Get data from server
            productCategoryListViewModal.getDataFromServer(false);
        }

    }


    /*****************************************************************************
     * ************* Local Category And Product Section Start ********************
     * ****************************************************************************
     */

    private void setObserverForCategoryAndProductFromDB() {
        //Set Cat Observer
        productCategoryListViewModal.getAllCategories(parentCategoryID).observe(this, new Observer<RealmResults<ProductCategory>>() {
            @Override
            public void onChanged(@Nullable RealmResults<ProductCategory> categories) {
                setupScreenForLocalCatAndProductOnUI();
            }
        });


        //Set Cat Observer
        productCategoryListViewModal.getAllProducts(parentCategoryID).observe(this, new Observer<RealmResults<Product>>() {
            @Override
            public void onChanged(@Nullable RealmResults<Product> products) {
                setupScreenForLocalCatAndProductOnUI();
            }
        });

    }

    private void setupScreenForLocalCatAndProductList() {

        initializeLocalCategoryAdapter(getLayoutManager());
        initializeLocalProductAdapter(getLayoutManager());

        //Setup Screen for Local Cat and Product
        setupScreenForLocalCatAndProductOnUI();
    }


    private void setupScreenForLocalCatAndProductOnUI() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isVisible() && isAdded()) {
                    if (itemLocalCategoryAdapter.getData() != null && itemLocalCategoryAdapter.getData().size() > 0
                            && itemLocalProductAdapter.getData() != null && itemLocalProductAdapter.getData().size() > 0) {
                        changeLocalCatAndProductLayoutManager(true, true);
                    } else if (itemLocalProductAdapter.getData() != null && itemLocalProductAdapter.getData().size() > 0) {
                        changeLocalCatAndProductLayoutManager(false, true);
                    } else if (itemLocalCategoryAdapter.getData() != null && itemLocalCategoryAdapter.getData().size() > 0) {
                        changeLocalCatAndProductLayoutManager(true, false);
                    } else {
                        llCategoryContainer.setVisibility(View.GONE);
                        llProductContainer.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, 100);

    }


    public void changeLocalCatAndProductLayoutManager(boolean isCategoryHasData, boolean isProductHasData) {

//        rvCategories.setLayoutManager(isProduct ? new LinearLayoutManager(activityReference, LinearLayoutManager.HORIZONTAL, false) : new GridLayoutManager(activityReference, 2));
//        rvCategories.setAdapter(itemCategoryAdapter);
//

        if (isCategoryHasData && isProductHasData) {

            //Category Adapter

            rvCategories.setLayoutManager(new LinearLayoutManager(activityReference, LinearLayoutManager.HORIZONTAL, false));
            itemLocalCategoryAdapter.setLayoutType(AppConstant.RECYCLER_VIEW.LAYOUT_TYPE_LINEAR);
            rvCategories.setAdapter(itemLocalCategoryAdapter);
            rvCategories.setNestedScrollingEnabled(false);

            //Product Adapter
            rvProducts.setLayoutManager(new GridLayoutManager(activityReference, 2));
            rvProducts.setAdapter(itemLocalProductAdapter);
            rvProducts.setNestedScrollingEnabled(false);


            //Set on UI
            llCategoryContainer.setVisibility(View.VISIBLE);
            llProductContainer.setVisibility(View.VISIBLE);
            tvNoDataFound.setVisibility(View.GONE);

        } else if (isCategoryHasData) {
            //Product Adapter
            rvCategories.setLayoutManager(new GridLayoutManager(activityReference, 2));
            itemLocalCategoryAdapter.setLayoutType(AppConstant.RECYCLER_VIEW.LAYOUT_TYPE_GRID);
            rvCategories.addItemDecoration(getItemDecoration());
            rvCategories.setAdapter(itemLocalCategoryAdapter);


            //Set on UI
            llCategoryContainer.setVisibility(View.VISIBLE);
            llProductContainer.setVisibility(View.GONE);
            tvNoDataFound.setVisibility(View.GONE);


        } else if (isProductHasData) {

            //Product Adapter
            rvProducts.setLayoutManager(new GridLayoutManager(activityReference, 2));
            rvProducts.setAdapter(itemLocalProductAdapter);
            rvProducts.setNestedScrollingEnabled(false);


            //Set on UI
            llCategoryContainer.setVisibility(View.GONE);
            llProductContainer.setVisibility(View.VISIBLE);
            tvNoDataFound.setVisibility(View.GONE);


        } else {
            llCategoryContainer.setVisibility(View.GONE);
            llProductContainer.setVisibility(View.GONE);
            tvNoDataFound.setVisibility(View.VISIBLE);
        }


    }


    private void initializeLocalCategoryAdapter(RecyclerView.LayoutManager layoutManager) {


        RealmResults<ProductCategory> productCategories = productCategoryListViewModal.getAllCategoriesFromDB(parentCategoryID);


        itemLocalCategoryAdapter = new CategoryAdapter(activityReference, productCategories, new DataRecyclerViewClickInterface<ProductCategory>() {
            @Override
            public void onClick(ProductCategory object, int position) {

                openProductCategoryListScreen(object, null);
            }

            @Override
            public void onEditClick(ProductCategory object, int position) {
                AddOrEditCategoryFragment addOrEditCategoryFragment = new AddOrEditCategoryFragment();
                addOrEditCategoryFragment.setSelectedCatObject(object);
                activityReference.realAddSupportFragment(addOrEditCategoryFragment, AppConstant.TRANSITION_TYPES.SLIDE);
            }
        });

        rvCategories.setLayoutManager(layoutManager);
        rvCategories.setAdapter(itemLocalCategoryAdapter);

    }

    private void openProductCategoryListScreen(ProductCategory object1, CategoryViewModal object2) {

        ProductCategoryListFragment productCategoryListFragment = new ProductCategoryListFragment();
        productCategoryListFragment.setProductViewType(productViewType);
        productCategoryListFragment.setLinkViewModel(linkViewModel);
        productCategoryListFragment.setCompanyProfileViewModel(companyProfileViewModel);
        if (object1 != null) {
            productCategoryListFragment.setParentCategoryID(object1.getId());
            productCategoryListFragment.setParentCategory(object1);
        } else if (object2 != null) {
            productCategoryListFragment.setParentCategoryID(object2.getCategoryID());
            productCategoryListFragment.setParentCategory(object2.getProductCategory());
        }
//        productCategoryListFragment.setonBackPressListener(new OnBackPressInterface() {
//            @Override
//            public void onBackPressListener() {
//                //setTitleBar(titleBar);
//            }
//        });
        activityReference.realAddSupportFragment(productCategoryListFragment, AppConstant.TRANSITION_TYPES.SLIDE);
    }

    private void setonBackPressListener(OnBackPressInterface onBackPressInterface) {
        this.onBackPressInterface = onBackPressInterface;
    }

    private void initializeLocalProductAdapter(RecyclerView.LayoutManager layoutManager) {


        RealmResults<Product> products = productCategoryListViewModal.getAllProductsFromDB(parentCategoryID);

//        if(products != null && products.size() > 0)
//            llProductContainer.setVisibility(View.VISIBLE);
//        else
//            llProductContainer.setVisibility(View.GONE);


        itemLocalProductAdapter = new ProductAdapter(activityReference, products, new DataRecyclerViewClickInterface<Product>() {
            @Override
            public void onClick(Product object, int position) {
                openProductDetailPage(object);
            }

            @Override
            public void onEditClick(Product object, int position) {
            }
        });

        rvProducts.setLayoutManager(layoutManager);
        rvProducts.addItemDecoration(getItemDecoration());
        rvProducts.setAdapter(itemLocalProductAdapter);
        rvProducts.setNestedScrollingEnabled(false);

    }


    /*****************************************************************************
     * ************* Local Category And Product Section Ends ********************
     * ****************************************************************************
     */


    private RecyclerView.LayoutManager getLayoutManager() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(activityReference, 2);
        return gridLayoutManager;
    }


    private RecyclerView.ItemDecoration getItemDecoration() {
        int spanCount = 2; // 2 columns
        int spacing = 0; // 50px
        boolean includeEdge = false;
        return new GridSpacingItemDecoration(spanCount, spacing, includeEdge);

    }


    /*****************************************************************************
     * ************* Server Category And Product Section Starts ********************
     * ****************************************************************************
     */


    private void setupScreenForServerCatAndProductList() {

        initializeServerCategoryAdapter(getLayoutManager());
        initializeServerProductAdapter(getLayoutManager());

        setupScreenForServerCatAndProductOnUI();
    }

    private void setupScreenForServerCatAndProductOnUI() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isVisible() && isAdded()) {
                    if (itemServerCategoryAdapter != null && itemServerCategoryAdapter.getItemCount() > 0
                            && itemServerProductAdapter != null && itemServerProductAdapter.getItemCount() > 0) {
                        changeServerCatAndProductLayoutManager(true, true);
                    } else if (itemServerProductAdapter != null && itemServerProductAdapter.getItemCount() > 0) {
                        changeServerCatAndProductLayoutManager(false, true);
                    } else if (itemServerCategoryAdapter != null && itemServerCategoryAdapter.getItemCount() > 0) {
                        changeServerCatAndProductLayoutManager(true, false);
                    } else {
                        llCategoryContainer.setVisibility(View.GONE);
                        llProductContainer.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, 100);
    }

    public void changeServerCatAndProductLayoutManager(boolean isCategoryHasData, boolean isProductHasData) {


        if (isCategoryHasData && isProductHasData) {

            //Category Adapter

            rvCategories.setLayoutManager(new LinearLayoutManager(activityReference, LinearLayoutManager.HORIZONTAL, false));
            itemServerCategoryAdapter.setLayoutType(AppConstant.RECYCLER_VIEW.LAYOUT_TYPE_LINEAR);
            rvCategories.setAdapter(itemServerCategoryAdapter);

            //Product Adapter
            rvProducts.setLayoutManager(new GridLayoutManager(activityReference, 2));
            rvProducts.setAdapter(itemServerProductAdapter);
            rvProducts.setNestedScrollingEnabled(false);


            //Set on UI
            llCategoryContainer.setVisibility(View.VISIBLE);
            llProductContainer.setVisibility(View.VISIBLE);
            tvNoDataFound.setVisibility(View.GONE);

        } else if (isCategoryHasData) {
            //Product Adapter
            rvCategories.setLayoutManager(new GridLayoutManager(activityReference, 2));
            itemServerCategoryAdapter.setLayoutType(AppConstant.RECYCLER_VIEW.LAYOUT_TYPE_GRID);
            rvCategories.addItemDecoration(getItemDecoration());
            rvCategories.setAdapter(itemServerCategoryAdapter);


            //Set on UI
            llCategoryContainer.setVisibility(View.VISIBLE);
            llProductContainer.setVisibility(View.GONE);
            tvNoDataFound.setVisibility(View.GONE);


        } else if (isProductHasData) {

            //Product Adapter
            rvProducts.setLayoutManager(new GridLayoutManager(activityReference, 2));
            rvProducts.setAdapter(itemServerProductAdapter);
            rvProducts.setNestedScrollingEnabled(false);


            //Set on UI
            llCategoryContainer.setVisibility(View.GONE);
            llProductContainer.setVisibility(View.VISIBLE);
            tvNoDataFound.setVisibility(View.GONE);


        } else {
            llCategoryContainer.setVisibility(View.GONE);
            llProductContainer.setVisibility(View.GONE);
            tvNoDataFound.setVisibility(View.VISIBLE);
        }


    }


    private void initializeServerCategoryAdapter(RecyclerView.LayoutManager layoutManager) {


        serverCategoryViewModals = new ArrayList<>();

        itemServerCategoryAdapter = new ServerCategoryAdapter(activityReference, serverCategoryViewModals, new DataRecyclerViewClickInterface<CategoryViewModal>() {
            @Override
            public void onClick(CategoryViewModal object, int position) {

                openProductCategoryListScreen(null, object);
            }

            @Override
            public void onEditClick(CategoryViewModal object, int position) {
            }
        });


        rvCategories.setLayoutManager(layoutManager);
        rvCategories.setAdapter(itemServerCategoryAdapter);

    }

    private void initializeServerProductAdapter(RecyclerView.LayoutManager layoutManager) {


        serverProductViewModals = new ArrayList<>();

        itemServerProductAdapter = new ServerProductAdapter(activityReference, serverProductViewModals, new DataRecyclerViewClickInterface<Product>() {
            @Override
            public void onClick(Product object, int position) {
                openProductDetailPage(object);
            }

            @Override
            public void onEditClick(Product object, int position) {
            }
        });


        rvProducts.setLayoutManager((GridLayoutManager) layoutManager);
        rvProducts.addItemDecoration(getItemDecoration());
        rvProducts.setAdapter(itemServerProductAdapter);
        rvProducts.setNestedScrollingEnabled(false);



        //For Pagination in Grid Layout
        Paginate.Callbacks callbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next page of data (e.g. network or database)
                isLoadingData = true;
                currentPage += 1;

                Log.i(TAG, "isLoading? " + isLoadingData + " currentPage " + currentPage + " Total Page " + productCategoryListViewModal.getTotalPages());

                productCategoryListViewModal.setPage(currentPage);
                productCategoryListViewModal.getProductsFromServer();

                isLoadingData = false;
            }

            @Override
            public boolean isLoading() {
                // Indicate whether new page loading is in progress or not
                return isLoadingData;
            }

            @Override
            public boolean hasLoadedAllItems() {
                // Indicate whether all data (pages) are loaded or not
                return productCategoryListViewModal.getTotalPages() <= (currentPage + 1);
            }
        };


        Paginate.with(rvProducts, callbacks)
                .setLoadingTriggerThreshold(1)
//                .addLoadingListItem(true)
                .build();


    }

    private void openProductDetailPage(Product object) {
        ProductDetailViewFragment productDetailViewFragment = new ProductDetailViewFragment();
        productDetailViewFragment.setSelectedProductViewType(productViewType);
        productDetailViewFragment.setLinkViewModel(linkViewModel);
        productDetailViewFragment.setCompanyProfileViewModel(companyProfileViewModel);
        productDetailViewFragment.setSelectedProductObject(object);
        productDetailViewFragment.setonBackPressListener(new OnBackPressInterface() {
            @Override
            public void onBackPressListener() {
                //setTitleBar(titleBar);
            }
        });
        activityReference.realAddSupportFragment(productDetailViewFragment, AppConstant.TRANSITION_TYPES.SLIDE);
    }


    private void openProductSearchPage() {
        ProductSearchFragment productSearchFragment = new ProductSearchFragment();
        productSearchFragment.setProductViewType(productViewType);
        productSearchFragment.setLinkViewModel(linkViewModel);
        productSearchFragment.setCompanyProfileViewModel(companyProfileViewModel);
        productSearchFragment.setonBackPressListener(new OnBackPressInterface() {
            @Override
            public void onBackPressListener() {
                //setTitleBar(titleBar);
            }
        });
        activityReference.realAddSupportFragment(productSearchFragment, AppConstant.TRANSITION_TYPES.SLIDE);
    }


    /*****************************************************************************
     * ************* Server Category And Product Section Ends ********************
     * ****************************************************************************
     */


    @OnClick({R.id.fabBtn, R.id.llAddCategory, R.id.llAddProduct, R.id.flOverlay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llAddCategory:

                //Hide Overlay and Buttons
                hideCategoryAndProductActionButtonView();

                //Start Screen
                AddOrEditCategoryFragment addOrEditCategoryFragment = new AddOrEditCategoryFragment();
                addOrEditCategoryFragment.setType(AppConstant.CATEGORY_TYPE.ADD);
                addOrEditCategoryFragment.setParentCategory(parentCategory);
                activityReference.realAddSupportFragment(addOrEditCategoryFragment, AppConstant.TRANSITION_TYPES.SLIDE);

                break;
            case R.id.llAddProduct:

                //Hide Overlay and Buttons
                hideCategoryAndProductActionButtonView();

                //Start Screen
                AddOrEditProductFragment addOrEditProductFragment = new AddOrEditProductFragment();
                addOrEditProductFragment.setType(AppConstant.CATEGORY_TYPE.ADD);
                addOrEditProductFragment.setProductCategory(parentCategory);
                activityReference.addSupportFragment(addOrEditProductFragment, AppConstant.TRANSITION_TYPES.SLIDE);

                break;

            case R.id.flOverlay:
                hideCategoryAndProductActionButtonView();
                break;

            case R.id.fabBtn:

                if (llAddCategory.getVisibility() == View.GONE && llAddCategory.getVisibility() == View.GONE) {
                    AnimationHelpers.animate(Techniques.FadeIn, 700, flOverlay);
                    AnimationHelpers.animate(Techniques.FadeIn, 300, llAddCategory);
                    AnimationHelpers.animate(Techniques.FadeIn, 600, llAddProduct);
                } else
                    hideCategoryAndProductActionButtonView();


                break;
        }
    }

    private void hideCategoryAndProductActionButtonView() {
        AnimationHelpers.animate(Techniques.FadeOut, 300, llAddProduct, View.GONE);
        AnimationHelpers.animate(Techniques.FadeOut, 600, llAddCategory, View.GONE);
        AnimationHelpers.animate(Techniques.FadeOut, 700, flOverlay, View.GONE);
    }


    public void setParentCategoryID(String parentCategoryID) {
        this.parentCategoryID = parentCategoryID;
    }

    private void setParentCategory(ProductCategory parentCategory) {
        this.parentCategory = parentCategory;
    }

    public void setProductViewType(ProductViewEnum productViewType) {
        this.productViewType = productViewType;
    }

    public void setCompanyProfileViewModel(ProfileViewModal companyProfileViewModel) {
        this.companyProfileViewModel = companyProfileViewModel;
    }


    public void setLinkViewModel(LinkViewModel linkViewModel) {
        this.linkViewModel = linkViewModel;
    }
}
