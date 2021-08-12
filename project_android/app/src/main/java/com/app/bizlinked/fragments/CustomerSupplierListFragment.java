package com.app.bizlinked.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bizlinked.R;
import com.app.bizlinked.adapters.LinkAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.realm.RealmLiveData;
import com.app.bizlinked.helpers.recycler_touchHelper.RecyclerTouchListener;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.listener.ClickListenerRecycler;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.enums.LinkRelationEnum;
import com.app.bizlinked.models.enums.LinkStatusEnum;
import com.app.bizlinked.models.enums.OrderScreenStatusEnum;
import com.app.bizlinked.models.enums.ProductViewEnum;
import com.app.bizlinked.models.viewmodel.LinkViewModel;
import com.app.bizlinked.models.viewmodel.LinksViewModel;
import com.app.bizlinked.models.viewmodel.ProductCategoryListViewModal;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerSupplierListFragment extends BaseFragment {


    //Customer Section
    @BindView(R.id.llCustomerSectionContainer)
    LinearLayout llCustomerSectionContainer;
    @BindView(R.id.llCustomerInvitationSection)
    LinearLayout llCustomerInvitationSection;
    @BindView(R.id.llNoCustomerDataFound)
    LinearLayout llNoCustomerDataFound;
    @BindView(R.id.flCustomerListSectionContainer)
    FrameLayout flCustomerListSectionContainer;
    @BindView(R.id.swipeRefreshCustomer)
    SwipeRefreshLayout swipeRefreshCustomer;
    @BindView(R.id.rvCustomer)
    RecyclerView rvCustomer;
    //Customer Section

    // Supplier Section
    @BindView(R.id.llSupplierSectionContainer)
    LinearLayout llSupplierSectionContainer;
    @BindView(R.id.llSupplierInvitationSection)
    LinearLayout llSupplierInvitationSection;
    @BindView(R.id.llNoSupplierDataFound)
    LinearLayout llNoSupplierDataFound;
    @BindView(R.id.flSupplierListSectionContainer)
    FrameLayout flSupplierListSectionContainer;
    @BindView(R.id.swipeRefreshSupplier)
    SwipeRefreshLayout swipeRefreshSupplier;
    @BindView(R.id.rvSupplier)
    RecyclerView rvSupplier;
    //Supplier Section


    //Tabs
    @BindView(R.id.llTabs)
    LinearLayout llTabs;
    @BindView(R.id.tvCustomer)
    TextView tvCustomer;
    @BindView(R.id.tvSupplier)
    TextView tvSupplier;
    //Tabs


    //View Model
    LinksViewModel customerLinksViewModel;
    LinksViewModel supplierLinksViewModel;

    LinkAdapter customerLinkAdapter;
    ArrayList<LinkViewModel> customerLinks = null;

    LinkAdapter supplierLinkAdapter;
    ArrayList<LinkViewModel> supplierLinks = null;

    TitleBar titleBar = null;

    public CustomerSupplierListFragment() {
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
        titleBar.removeShadowBottomFromTitleBar();
        titleBar.showHeaderTitle(activityReference.getString(R.string.links));
        titleBar.showRightSearchIconAndSetListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinksSearchFragment linksSearchFragment = new LinksSearchFragment();
                activityReference.realAddSupportFragment(linksSearchFragment, AppConstant.TRANSITION_TYPES.SLIDE);
            }
        });
    }

    @Override
    public void afterBackStackChange() {
        super.afterBackStackChange();
        setTitleBar(titleBar);

        initializeViewModal();
        setupScreenForCustomerListScreen();
        setupScreenForSupplierListScreen();

    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_customer_supplier_list;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {
        initializeViewModal();
        setupScreenForCustomerListScreen();
        setupScreenForSupplierListScreen();

    }

    private void initializeViewModal() {
        // Get the ViewModel.
        customerLinksViewModel = new LinksViewModel();
        supplierLinksViewModel =  new LinksViewModel();
    }

    /* *****************************************************************************************
     ******************************************************************************************
     * ****************************** Customer Section Starts *********************************
     ******************************************************************************************
     ******************************************************************************************/

    private void setupScreenForCustomerListScreen() {
        //resetSearchView();
        flCustomerListSectionContainer.setVisibility(View.VISIBLE);
        llNoCustomerDataFound.setVisibility(View.GONE);

        initializeCustomerAdapter();
        getAllCustomerLinks();

        swipeRefreshCustomer.setColorSchemeResources(R.color.appColorPrimary);
        swipeRefreshCustomer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                customerLinksViewModel.syncLinks();
                //getAllCustomerLinks();
                swipeRefreshCustomer.setRefreshing(false);
            }
        });

        //Disbaled Refresh
//        swipeRefreshCustomer.setRefreshing(false);
//        swipeRefreshCustomer.setEnabled(false);

    }

    @Override
    public void onDestroyView() {
        //Remove Change Listener
        if(customerLinksViewModel != null){
            customerLinksViewModel.getAllLinksFromDB(LinkRelationEnum.CUSTOMER, LinkStatusEnum.LINKED).removeAllChangeListeners();
        }

        //Remove Change Listener
        if(supplierLinksViewModel != null){
            supplierLinksViewModel.getAllLinksFromDB(LinkRelationEnum.SUPPLIER, LinkStatusEnum.LINKED).removeAllChangeListeners();
        }

        super.onDestroyView();
    }

    private void getAllCustomerLinks() {

        customerLinks = new ArrayList<>();
        customerLinksViewModel.getLinkResults(LinkRelationEnum.CUSTOMER, LinkStatusEnum.LINKED).observe(activityReference, new Observer<RealmResults<Link>>() {
            @Override
            public void onChanged(@Nullable RealmResults<Link> links) {


                if (customerLinkAdapter != null) {

                    //Clear the list
                    customerLinkAdapter.clearAllList();
                    customerLinks.clear();

                    customerLinks.addAll(customerLinksViewModel.getLinksDataForUI(links));
                    customerLinkAdapter.addAllList(customerLinks);

                    //This Work is For Image Show when Image Downloads
                    for (int index = 0; index < customerLinkAdapter.getItemCount(); index++) {
                        int finalIndex = index;
                        Observer<byte[]> imageObserver = new Observer<byte[]>() {
                            @Override
                            public void onChanged(@Nullable byte[] imageData) {
                                customerLinkAdapter.notifyItemChanged(finalIndex);
                            }
                        };
                        customerLinkAdapter.getItem(index).getImage().removeObserver(imageObserver);
                        customerLinkAdapter.getItem(index).getImage().observe(activityReference, imageObserver);
                    }
                    //This Work is For Image Show when Image Downloads

                }

                if(isAdded() && isVisible())
                    checkCustomerSectionHasData(customerLinkAdapter != null && customerLinkAdapter.getItemCount() > 0);
            }
        });
    }

    private void initializeCustomerAdapter() {

        customerLinkAdapter = new LinkAdapter(activityReference, customerLinks, new DataRecyclerViewClickInterface<LinkViewModel>() {
            @Override
            public void onClick(LinkViewModel object, int position) {

                OrderListFragment orderListFragment = new OrderListFragment();
                orderListFragment.setShowSearchFilter(false);
                orderListFragment.setScreenStatus(OrderScreenStatusEnum.RECEIVED);
                orderListFragment.setLinkViewModel(object);
                orderListFragment.setCompanyProfileViewModel(object.getProfileViewModel());
                activityReference.realAddSupportFragment(orderListFragment, AppConstant.TRANSITION_TYPES.SLIDE);
            }

            @Override
            public void onEditClick(LinkViewModel object, int position) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(activityReference);
        rvCustomer.setLayoutManager(layoutManager);
        rvCustomer.setAdapter(customerLinkAdapter);

    }

    private void checkCustomerSectionHasData(boolean isData) {

        if (isData) {
            rvCustomer.setVisibility(View.VISIBLE);
            llNoCustomerDataFound.setVisibility(View.GONE);

        } else {
            rvCustomer.setVisibility(View.GONE);
            llNoCustomerDataFound.setVisibility(View.VISIBLE);
        }
    }

    /* *****************************************************************************************
     ******************************************************************************************
     * ****************************** Customer Section Ends ****************************************
     ******************************************************************************************
     ******************************************************************************************/


    /* *****************************************************************************************
     ******************************************************************************************
     * ****************************** Supplier Section Starts **********************************
     ******************************************************************************************
     ******************************************************************************************/

    private void setupScreenForSupplierListScreen() {
        //resetSearchView();
        flSupplierListSectionContainer.setVisibility(View.VISIBLE);
        llNoSupplierDataFound.setVisibility(View.GONE);

        initializeSupplierAdapter();
        getAllSupplierLinks();

        swipeRefreshSupplier.setColorSchemeResources(R.color.appColorPrimary);
        swipeRefreshSupplier.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                supplierLinksViewModel.syncLinks();
                //getAllSupplierLinks();
                swipeRefreshSupplier.setRefreshing(false);
            }
        });

        //Disbaled Refresh
//        swipeRefreshSupplier.setRefreshing(false);
//        swipeRefreshSupplier.setEnabled(false);

    }

    private void getAllSupplierLinks() {

        supplierLinks = new ArrayList<>();
        supplierLinksViewModel.getLinkResults(LinkRelationEnum.SUPPLIER, LinkStatusEnum.LINKED).observe(activityReference, new Observer<RealmResults<Link>>() {
            @Override
            public void onChanged(@Nullable RealmResults<Link> links) {

                if (supplierLinkAdapter != null) {

                    //Clear the list
                    supplierLinkAdapter.clearAllList();
                    supplierLinks.clear();

                    supplierLinks.addAll(supplierLinksViewModel.getLinksDataForUI(links));
                    supplierLinkAdapter.addAllList(supplierLinks);

                    //This Work is For Image Show when Image Downloads
                    for (int index = 0; index < supplierLinkAdapter.getItemCount(); index++) {
                        int finalIndex = index;
                        Observer<byte[]> imageObserver = new Observer<byte[]>() {
                            @Override
                            public void onChanged(@Nullable byte[] imageData) {
                                supplierLinkAdapter.notifyItemChanged(finalIndex);
                            }
                        };
                        supplierLinkAdapter.getItem(index).getImage().removeObserver(imageObserver);
                        supplierLinkAdapter.getItem(index).getImage().observe(activityReference, imageObserver);
                    }
                    //This Work is For Image Show when Image Downloads

                }
                if(isVisible() && isAdded())
                    checkSupplierSectionHasData(supplierLinkAdapter != null && supplierLinkAdapter.getItemCount() > 0);
            }
        });
    }

    private void initializeSupplierAdapter() {


        supplierLinkAdapter = new LinkAdapter(activityReference, supplierLinks, new DataRecyclerViewClickInterface<LinkViewModel>() {
            @Override
            public void onClick(LinkViewModel object, int position) {
                ProductCategoryListFragment productCategoryListFragment = new ProductCategoryListFragment();
                productCategoryListFragment.setProductViewType(ProductViewEnum.COMPANY_PROFILE);
                productCategoryListFragment.setLinkViewModel(object);
                productCategoryListFragment.setCompanyProfileViewModel(object.getProfileViewModel());
                activityReference.realAddSupportFragment(productCategoryListFragment, AppConstant.TRANSITION_TYPES.SLIDE);

            }

            @Override
            public void onEditClick(LinkViewModel object, int position) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(activityReference);
        rvSupplier.setLayoutManager(layoutManager);
        rvSupplier.setAdapter(supplierLinkAdapter);

//        rvSupplier.addOnItemTouchListener(new RecyclerTouchListener(activityReference, rvSupplier,
//                new ClickListenerRecycler() {
//                    @Override
//                    public void onClick(View view, int position) {
//                    }
//
//                    @Override
//                    public void onLongClick(View view, final int position) {
//
//                    }
//                }
//        ));

//        rvFindNearByLands.addOnScrollListener(new PaginationScrollListener(layoutManager){
//            @Override
//            protected void loadMoreItems(){
//                isLoadingData = true;
//                currentPage += 1;
//
//                Log.i(TAG, "LAND MARK::: isLoading? " + isLoadingData + " currentPage " + currentPage+ " Total Page " + TOTAL_PAGES);
//                //Toast.makeText(activityReference, "isLoading? " + isLoadingData + " currentPage " + currentPage+ " Total Page " + TOTAL_PAGES, Toast.LENGTH_SHORT).show();
//
//                getAllLinksFromServer(currentPage, AppConstant.CONFIGURATION.PAGE_SIZE, true, selectedGlobalFilter);
//            }
//
//            @Override
//            public int getTotalPageCount(){
//                return TOTAL_PAGES;
//            }
//
//            @Override
//            public boolean isLastPage(){
//                return isLastPage;
//            }
//
//            @Override
//            public boolean isLoadingData(){
//                return isLoadingData;
//            }
//        });
    }

    private void checkSupplierSectionHasData(boolean isData) {
        if (isData) {
            rvSupplier.setVisibility(View.VISIBLE);
            llNoSupplierDataFound.setVisibility(View.GONE);

        } else {
            rvSupplier.setVisibility(View.GONE);
            llNoSupplierDataFound.setVisibility(View.VISIBLE);
        }
    }

    /* *****************************************************************************************
     ******************************************************************************************
     * ****************************** Supplier Section Ends ***********************************
     ******************************************************************************************
     ******************************************************************************************/

    @OnClick({
            R.id.llCustomerInvitationSection,
            R.id.llSupplierInvitationSection,
            R.id.tvCustomer, R.id.tvSupplier
    })
    public void onViewClicked(View view) {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activityReference.getTheme();
        @ColorInt int color;
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        color = typedValue.data;

        switch (view.getId()) {


            case R.id.llCustomerInvitationSection:
                InvitationsFragment customerInvitationsFragment = new InvitationsFragment();
                customerInvitationsFragment.setType(LinkRelationEnum.CUSTOMER);
                customerInvitationsFragment.setInvitationTypeTitleText(activityReference.getString(R.string.customer_invitations));
                activityReference.realAddSupportFragment(customerInvitationsFragment, AppConstant.TRANSITION_TYPES.SLIDE);
                break;

            case R.id.llSupplierInvitationSection:
                InvitationsFragment supplierInvitationsFragment = new InvitationsFragment();
                supplierInvitationsFragment.setType(LinkRelationEnum.SUPPLIER);
                supplierInvitationsFragment.setInvitationTypeTitleText(activityReference.getString(R.string.supplier_invitations));
                activityReference.realAddSupportFragment(supplierInvitationsFragment, AppConstant.TRANSITION_TYPES.SLIDE);
                break;

            case R.id.tvCustomer:

                tvSupplier.setBackgroundColor(Color.TRANSPARENT);
                tvCustomer.setBackgroundColor(color);
                llCustomerSectionContainer.setVisibility(View.VISIBLE);
                llSupplierSectionContainer.setVisibility(View.GONE);

                break;

            case R.id.tvSupplier:

                tvCustomer.setBackgroundColor(Color.TRANSPARENT);
                tvSupplier.setBackgroundColor(color);
                llCustomerSectionContainer.setVisibility(View.GONE);
                llSupplierSectionContainer.setVisibility(View.VISIBLE);

                break;
        }
    }
}
