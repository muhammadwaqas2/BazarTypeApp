package com.app.bizlinked.fragments;


import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.app.bizlinked.R;
import com.app.bizlinked.adapters.ServerProductAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.listener.custom.OnBackPressInterface;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.enums.ProductViewEnum;
import com.app.bizlinked.models.viewmodel.LinkViewModel;
import com.app.bizlinked.models.viewmodel.ProductSearchViewModal;
import com.app.bizlinked.models.viewmodel.ProductViewModal;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;
import com.paginate.Paginate;
import android.view.inputmethod.EditorInfo;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductSearchFragment extends BaseFragment {



    @BindView(R.id.llMainView)
    LinearLayout llMainView;


    @BindView(R.id.etSearch)
    EditText etSearch;

    @BindView(R.id.llNoProductDataFound)
    LinearLayout llNoProductDataFound;
    @BindView(R.id.flProductContainer)
    FrameLayout flProductContainer;
    @BindView(R.id.rvSearchProduct)
    RecyclerView rvSearchProduct;
    @BindView(R.id.swipeRefreshProduct)
    SwipeRefreshLayout swipeRefreshProduct;


    private TitleBar titleBar = null;


    ArrayList<ProductViewModal> searchProductViewModals = null;
    ServerProductAdapter itemSearchProductAdapter;

    //For Pagination (Server Cat/Product) Work
    int currentPage = 0;
    //    int TOTAL_PAGES = 1;
    boolean isLoadingData = false;
    boolean isLastPage = false;


    //View Model
    ProductSearchViewModal productSearchViewModal;


    //For Company Search from Server
    String companyID = null;
    ProductViewEnum productViewType = null;
    LinkViewModel linkViewModel = null;
    ProfileViewModal companyProfileViewModel = null;

    OnBackPressInterface onBackPressInterface = null;



    public ProductSearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCustomBackPressed() {
        activityReference.onPageBack();

        if(onBackPressInterface != null){
            onBackPressInterface.onBackPressListener();
        }

    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        this.titleBar = titleBar;
        titleBar.showHeaderView();
        titleBar.setLeftTitleText(getResources().getString(R.string.search_products));
        titleBar.showLeftIconAndListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityReference.onBackPressed();
            }
        });
    }

    @Override
    public void afterBackStackChange() {
        super.afterBackStackChange();
        setTitleBar(titleBar);
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_product_search;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

        initializeViewModal();
        setupScreenForProductSearchListing();
    }

    private void initializeViewModal() {

        // Get the ViewModel.
        productSearchViewModal = new ProductSearchViewModal();
        productSearchViewModal.setActivityReference(activityReference);


        productSearchViewModal.getSearchedProduct().observe(activityReference, new Observer<ArrayList<ProductViewModal>>() {
            @Override
            public void onChanged(@Nullable ArrayList<ProductViewModal> productViewModal) {


                if (itemSearchProductAdapter != null) {

                    searchProductViewModals.addAll(productViewModal);
                    itemSearchProductAdapter.addAllList(productViewModal);

                    if(isVisible() && isAdded())
                        checkProductSectionHasData(itemSearchProductAdapter != null && itemSearchProductAdapter.getItemCount() > 0);


                    //If Product Coming From Server set image dynamically
                    if(productViewType.equals(ProductViewEnum.COMPANY_PROFILE)){
                        //This Work is For Image Show when Image Downloads
                        for (int index = 0; index < itemSearchProductAdapter.getItemCount(); index++) {
                            int finalIndex = index;
                            Observer<HashMap<String, byte[]>> imageObserver = new Observer<HashMap<String, byte[]>>() {
                                @Override
                                public void onChanged(@Nullable HashMap<String, byte[]> imageData) {
                                    itemSearchProductAdapter.notifyItemChanged(finalIndex);
                                }
                            };

                            itemSearchProductAdapter.getItem(index).getProductImage().removeObserver(imageObserver);
                            itemSearchProductAdapter.getItem(index).getProductImage().observe(activityReference, imageObserver);
                        }
                        //This Work is For Image Show when Image Downloads

                    }
                }
            }
        });


        //From Server
        if(productViewType.equals(ProductViewEnum.COMPANY_PROFILE)){

            if(linkViewModel != null){
                this.companyID = linkViewModel.getLinkedCompanyID();
                productSearchViewModal.setCompanyID(linkViewModel.getLinkedCompanyID());
            }else if(companyProfileViewModel != null){
                productSearchViewModal.setCompanyID(companyProfileViewModel.getId());
            }

        }else if(productViewType.equals(ProductViewEnum.LOGGED_IN_COMPANY_PROFILE)){

        }
    }



    /* *******************************************************************
     ******************** Product Container  Start **************************
     *********************************************************************
     ********************************************************************* */

    private void setupScreenForProductSearchListing() {

        initializeProductSearch();
        initializeSearchProductAdapter();

//        swipeRefreshProduct.setColorSchemeResources(R.color.appColorPrimary);
//        swipeRefreshProduct.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                getAllProducts(1, AppConstant.CONFIGURATION.PAGE_SIZE, false, null);
//                swipeRefreshProduct.setRefreshing(false);
//            }
//        });

        swipeRefreshProduct.setEnabled(false);
        swipeRefreshProduct.setRefreshing(false);

    }


    private void initializeProductSearch() {

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = etSearch.getText().toString().trim();

                    //Clear List Before Search
                    searchProductViewModals.clear();
                    itemSearchProductAdapter.clearAllList();
                    checkProductSectionHasData(false);


                    //Search Product
                    productSearchViewModal.searchProducts(searchText);

                    return true;
                }
                return false;
            }
        });
    }


    private void initializeSearchProductAdapter() {


        searchProductViewModals = new ArrayList<>();

        itemSearchProductAdapter = new ServerProductAdapter(activityReference, searchProductViewModals, new DataRecyclerViewClickInterface<Product>() {
            @Override
            public void onClick(Product object, int position) {
                openProductDetailPage(object);
            }

            @Override
            public void onEditClick(Product object, int position) {
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(activityReference, 2);
        rvSearchProduct.setLayoutManager(gridLayoutManager);
        rvSearchProduct.setAdapter(itemSearchProductAdapter);


        //Check if section has data
        checkProductSectionHasData(itemSearchProductAdapter != null && itemSearchProductAdapter.getItemCount() > 0);


        //For Server Product
        if(productViewType.equals(ProductViewEnum.COMPANY_PROFILE)){

            //For Pagination in Grid Layout
            Paginate.Callbacks callbacks = new Paginate.Callbacks() {
                @Override
                public void onLoadMore() {

                    if(etSearch.getText().toString().length() > 0){
                        // Load next page of data (e.g. network or database)
                        isLoadingData = true;
                        currentPage += 1;

                        Log.i(TAG, "isLoading? " + isLoadingData + " currentPage " + currentPage + " Total Page " + productSearchViewModal.getTotalPages());

                        productSearchViewModal.setPage(currentPage);
                        productSearchViewModal.searchFromServer(etSearch.getText().toString());

                        isLoadingData = false;
                    }
                }

                @Override
                public boolean isLoading() {
                    // Indicate whether new page loading is in progress or not
                    return isLoadingData;
                }

                @Override
                public boolean hasLoadedAllItems() {
                    // Indicate whether all data (pages) are loaded or not
                    return productSearchViewModal.getTotalPages() <= (currentPage + 1);
                }
            };


            Paginate.with(rvSearchProduct, callbacks)
                    .setLoadingTriggerThreshold(1)
//                    .addLoadingListItem(true)
                    .build();


        }


    }

    private void openProductDetailPage(Product object) {
        ProductDetailViewFragment productDetailViewFragment = new ProductDetailViewFragment();
        productDetailViewFragment.setSelectedProductViewType(productViewType);
        productDetailViewFragment.setSelectedProductObject(object);
        productDetailViewFragment.setLinkViewModel(linkViewModel);
        productDetailViewFragment.setCompanyProfileViewModel(companyProfileViewModel);
        activityReference.realAddSupportFragment(productDetailViewFragment, AppConstant.TRANSITION_TYPES.SLIDE);
    }

    private void checkProductSectionHasData(boolean isData) {

        if (isData) {
            rvSearchProduct.setVisibility(View.VISIBLE);
            llNoProductDataFound.setVisibility(View.GONE);

        } else {
            rvSearchProduct.setVisibility(View.GONE);
            llNoProductDataFound.setVisibility(View.VISIBLE);
        }
    }




    /* *******************************************************************
     ******************** Product Container End **************************
     *********************************************************************
     ********************************************************************* */

    
    public void setProductViewType(ProductViewEnum productViewType) {
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
