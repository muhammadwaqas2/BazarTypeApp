package com.app.bizlinked.fragments;


import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
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

import com.app.bizlinked.R;
import com.app.bizlinked.adapters.OrderListAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.listener.custom.DataUpdateListenerInterface;
import com.app.bizlinked.listener.custom.OnBackPressInterface;
import com.app.bizlinked.models.enums.LinkRelationEnum;
import com.app.bizlinked.models.enums.OrderScreenStatusEnum;
import com.app.bizlinked.models.enums.ProfileViewEnum;
import com.app.bizlinked.models.viewmodel.LinkViewModel;
import com.app.bizlinked.models.viewmodel.OrderListViewModel;
import com.app.bizlinked.models.viewmodel.OrderViewModel;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderListFragment extends BaseFragment {

//
//    //Received Section
//    @BindView(R.id.flReceivedSection)
//    FrameLayout flReceivedSection;
//    @BindView(R.id.llReceivedSectionContainer)
//    LinearLayout llReceivedSectionContainer;
//    @BindView(R.id.llReceivedPendingSectionContainer)
//    LinearLayout llReceivedPendingSectionContainer;
//    @BindView(R.id.llReceivedProcessedSectionContainer)
//    LinearLayout llReceivedProcessedSectionContainer;
//    @BindView(R.id.tvNoReceivedDataFound)
//    TextView tvNoReceivedDataFound;
//    @BindView(R.id.rvReceivedPending)
//    RecyclerView rvReceivedPending;
//    @BindView(R.id.rvReceivedProcessed)
//    RecyclerView rvReceivedProcessed;
//    //Received Section
//
//    //Placed Section
//    @BindView(R.id.flPlacedSection)
//    FrameLayout flPlacedSection;
//    @BindView(R.id.llPlacedSectionContainer)
//    LinearLayout llPlacedSectionContainer;
//    @BindView(R.id.llPlacedPendingSectionContainer)
//    LinearLayout llPlacedPendingSectionContainer;
//    @BindView(R.id.llPlacedProcessedSectionContainer)
//    LinearLayout llPlacedProcessedSectionContainer;
//    @BindView(R.id.tvNoPlacedDataFound)
//    TextView tvNoPlacedDataFound;
//    @BindView(R.id.rvPlacedPending)
//    RecyclerView rvPlacedPending;
//    @BindView(R.id.rvPlacedProcessed)
//    RecyclerView rvPlacedProcessed;
//    //Placed Section
//
//    //Draft Section
//    @BindView(R.id.flDraftSection)
//    FrameLayout flDraftSection;
//    @BindView(R.id.llDraftSectionContainer)
//    LinearLayout llDraftSectionContainer;
//    @BindView(R.id.llDraftPendingSectionContainer)
//    LinearLayout llDraftPendingSectionContainer;
//    @BindView(R.id.llDraftProcessedSectionContainer)
//    LinearLayout llDraftProcessedSectionContainer;
//    @BindView(R.id.tvNoDraftDataFound)
//    TextView tvNoDraftDataFound;
//    @BindView(R.id.rvDraftPending)
//    RecyclerView rvDraftPending;
//    @BindView(R.id.rvDraftProcessed)
//    RecyclerView rvDraftProcessed;
//    //Draft Section
//

    // Section
    @BindView(R.id.flSection)
    FrameLayout flSection;
    @BindView(R.id.llSectionContainer)
    LinearLayout llSectionContainer;
    @BindView(R.id.llPendingSectionContainer)
    LinearLayout llPendingSectionContainer;
    @BindView(R.id.llProcessedSectionContainer)
    LinearLayout llProcessedSectionContainer;
    @BindView(R.id.tvNoDataFound)
    TextView tvNoDataFound;
    @BindView(R.id.rvPending)
    RecyclerView rvPending;
    @BindView(R.id.rvProcessed)
    RecyclerView rvProcessed;
    // Section


    @BindView(R.id.swipeRefreshOrder)
    SwipeRefreshLayout swipeRefreshOrder;

//    @BindView(R.id.swipeRefreshPending)
//    SwipeRefreshLayout swipeRefreshPending;
//    @BindView(R.id.swipeRefreshProcessed)
//    SwipeRefreshLayout swipeRefreshProcessed;




    //Tabs
    @BindView(R.id.llTabs)
    LinearLayout llTabs;
    @BindView(R.id.tvReceived)
    TextView tvReceived;
    @BindView(R.id.tvPlaced)
    TextView tvPlaced;
    @BindView(R.id.tvDrafts)
    TextView tvDrafts;
    //Tabs


    //View Model
    OrderListViewModel orderListViewModel;

    OrderListAdapter orderPendingListAdapter;
    ArrayList<OrderViewModel> orderPendingList = null;
    OrderListAdapter orderProcessedListAdapter;
    ArrayList<OrderViewModel> orderProcessedList = null;

    private OrderScreenStatusEnum screenStatus;
    private LinkViewModel linkViewModel;
    private ProfileViewModal companyProfileViewModel;
    private boolean showSearchFilter;

    TitleBar titleBar;

    OrderScreenStatusEnum selectedTab = null;

    public OrderListFragment() {
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


        //for any company products show
        if (linkViewModel != null) {
            titleBar.showHeaderTitle(linkViewModel.getName() + "'s" + " " + activityReference.getString(R.string.orders_text));
            titleBar.setHeaderClickListener(v -> openProfileScreen());
        } else if (companyProfileViewModel != null) {
            titleBar.showHeaderTitle(companyProfileViewModel.getName() + "'s" + " " + activityReference.getString(R.string.orders_text));
            titleBar.setHeaderClickListener(v -> openProfileScreen());

        }else{
            titleBar.showHeaderTitle(activityReference.getString(R.string.my_order));
        }


        if(linkViewModel !=null || companyProfileViewModel != null){
            titleBar.setLeftTitleText(getResources().getString(R.string.back));
            titleBar.showLeftIconAndListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityReference.onBackPressed();
                }
            });
        }

    }

    private void openProfileScreen() {

        CompanyDetailFragment companyDetailFragment = new CompanyDetailFragment();
        companyDetailFragment.setCompanyRelation(LinkRelationEnum.CUSTOMER); // because of Showing Product
        companyDetailFragment.setType(ProfileViewEnum.COMPANY_PROFILE);

        if(linkViewModel != null){
            companyDetailFragment.setLinkViewModel(linkViewModel);
            companyDetailFragment.setCompanyProfileViewModel(linkViewModel.getProfileViewModel());
        }else if(companyProfileViewModel != null){
            companyDetailFragment.setCompanyProfileViewModel(companyProfileViewModel);
        }

        activityReference.addSupportFragment(companyDetailFragment, AppConstant.TRANSITION_TYPES.SLIDE);
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_order_list;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

        initializeViewModal();
        setupScreenForListScreen();
    }

    @Override
    public void onResume() {
        super.onResume();
        doResumeWork();
    }

    @Override
    public void afterBackStackChange(){
        doResumeWork();
    }

    private void doResumeWork() {
        if(showSearchFilter){

            getMainActivity().setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.ORDERS);

            if(selectedTab != null){ //For push and selected tab work

                if(selectedTab.getValue().equalsIgnoreCase(OrderScreenStatusEnum.PLACED.getValue())){
                    onClickOfPlaced();
                }else if(selectedTab.getValue().equalsIgnoreCase(OrderScreenStatusEnum.DRAFT.getValue())){
                    onClickOfDraft();
                }
            }

        }else{
            getMainActivity().setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.LINKS);
        }
    }


    private void initializeViewModal() {
        // Get the ViewModel.
        orderListViewModel = new OrderListViewModel();

    }

    /* *****************************************************************************************
     ******************************************************************************************
     * ****************************** List Section Starts *********************************
     ******************************************************************************************
     ******************************************************************************************/


    private void setupScreenForListScreen() {

        if(showSearchFilter){
            llTabs.setVisibility(View.VISIBLE);
        }else{
            llTabs.setVisibility(View.GONE);
        }


        swipeRefreshOrder.setColorSchemeResources(R.color.appColorPrimary);
        swipeRefreshOrder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderListViewModel.syncOrders();
                swipeRefreshOrder.setRefreshing(false);
            }
        });

        setupDataForList();
        initializeAdapter();
    }

    private void setupDataForList() {

        String companyID = null;

        if (screenStatus != null){

            if (linkViewModel != null) {
                companyID = linkViewModel.getLinkedCompanyID();
            } else if (companyProfileViewModel != null) {
                companyID = companyProfileViewModel.getId();
            }

            orderListViewModel.init(screenStatus, companyID, new DataUpdateListenerInterface() {
                @Override
                public void updateUI() {

                    if(isVisible() && isAdded()){
                        setupScreenForListScreen();
                    }
                }
            });
        }
    }

    private void initializeAdapter() {

        //Pending section data
        orderPendingList = new ArrayList<>();
        orderPendingList.addAll(orderListViewModel.getPendingOrderViewModels());

        orderPendingListAdapter = new OrderListAdapter(activityReference, orderPendingList, new DataRecyclerViewClickInterface<OrderViewModel>() {
            @Override
            public void onClick(OrderViewModel object, int position) {

                openOrderStatusScreen(object);
            }

            @Override
            public void onEditClick(OrderViewModel object, int position) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(activityReference);
        rvPending.setLayoutManager(layoutManager);
        rvPending.setAdapter(orderPendingListAdapter);
        rvPending.setNestedScrollingEnabled(false);


        // Received / Processed List Section

        orderProcessedList = new ArrayList<>();
        orderProcessedList.addAll(orderListViewModel.getReceivedOrderViewModels());

        orderProcessedListAdapter = new OrderListAdapter(activityReference, orderProcessedList, new DataRecyclerViewClickInterface<OrderViewModel>() {
            @Override
            public void onClick(OrderViewModel object, int position) {
                openOrderStatusScreen(object);

            }

            @Override
            public void onEditClick(OrderViewModel object, int position) {

            }
        });

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(activityReference);
        rvProcessed.setLayoutManager(layoutManager1);
        rvProcessed.setAdapter(orderProcessedListAdapter);
        rvProcessed.setNestedScrollingEnabled(false);

        //Received section data



//        swipeRefreshPending.setColorSchemeResources(R.color.appColorPrimary);
//        swipeRefreshPending.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                orderListViewModel.syncOrders();
//                swipeRefreshPending.setRefreshing(false);
//            }
//        });
//
//        swipeRefreshPending.setEnabled(false);
//        swipeRefreshPending.setRefreshing(false);

//        swipeRefreshProcessed.setColorSchemeResources(R.color.appColorPrimary);
//        swipeRefreshProcessed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                orderListViewModel.syncOrders();
//                swipeRefreshProcessed.setRefreshing(false);
//            }
//        });
//
//        swipeRefreshProcessed.setEnabled(false);
//        swipeRefreshProcessed.setRefreshing(false);



        //Check Data and set no data found
        checkBothSectionHasData();


    }

    private void openOrderStatusScreen(OrderViewModel object) {
        OrderStatusFragment orderStatusFragment = new OrderStatusFragment();
        orderStatusFragment.setOrderViewModelObject(object);
        orderStatusFragment.setScreenStatus(screenStatus);
        orderStatusFragment.setShowSearchFilter(showSearchFilter);
        orderStatusFragment.setBackPressListener(new OnBackPressInterface() {
            @Override
            public void onBackPressListener() {
                setTitleBar(titleBar);
                setupScreenForListScreen();
            }
        });
        activityReference.realAddSupportFragment(orderStatusFragment, AppConstant.TRANSITION_TYPES.SLIDE);



        // Remove Databse Change Listener
        if(orderListViewModel != null && orderListViewModel.getResultOrderStatuses() != null){
            orderListViewModel.getResultOrderStatuses().removeAllChangeListeners();
        }
    }

    private void checkBothSectionHasData() {


        if(orderPendingListAdapter != null && orderPendingListAdapter.getItemCount() > 0){
            llPendingSectionContainer.setVisibility(View.VISIBLE);
        }else{
            llPendingSectionContainer.setVisibility(View.GONE);
        }

        if(orderProcessedListAdapter != null && orderProcessedListAdapter.getItemCount() > 0){
            llProcessedSectionContainer.setVisibility(View.VISIBLE);
        }else{
            llProcessedSectionContainer.setVisibility(View.GONE);
        }


        //For No Data TextView
        if(llProcessedSectionContainer.getVisibility() == View.GONE && llPendingSectionContainer.getVisibility() == View.GONE){
            tvNoDataFound.setVisibility(View.VISIBLE);
        }else{
            tvNoDataFound.setVisibility(View.GONE);
        }

    }

    /* *****************************************************************************************
     ******************************************************************************************
     * ****************************** List Section Ends *********************************
     ******************************************************************************************
     ******************************************************************************************/



    @OnClick({
            R.id.tvReceived,
            R.id.tvPlaced,
            R.id.tvDrafts
    })
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.tvReceived:
                onClickOfReceived();
                break;

            case R.id.tvPlaced:
                onClickOfPlaced();
                break;

            case R.id.tvDrafts:
                onClickOfDraft();
                break;
        }
    }

    private void onClickOfDraft() {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activityReference.getTheme();
        @ColorInt int color;
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        color = typedValue.data;

        tvReceived.setBackgroundColor(Color.TRANSPARENT);
        tvPlaced.setBackgroundColor(Color.TRANSPARENT);
        tvDrafts.setBackgroundColor(color);

        //Placed List Show
        screenStatus = OrderScreenStatusEnum.DRAFT;
        setupScreenForListScreen();

    }


    private void onClickOfPlaced() {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activityReference.getTheme();
        @ColorInt int color;
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        color = typedValue.data;

        tvReceived.setBackgroundColor(Color.TRANSPARENT);
        tvPlaced.setBackgroundColor(color);
        tvDrafts.setBackgroundColor(Color.TRANSPARENT);

        //Placed List Show
        screenStatus = OrderScreenStatusEnum.PLACED;
        setupScreenForListScreen();
    }

    private void onClickOfReceived() {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activityReference.getTheme();
        @ColorInt int color;
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        color = typedValue.data;


        tvReceived.setBackgroundColor(color);
        tvPlaced.setBackgroundColor(Color.TRANSPARENT);
        tvDrafts.setBackgroundColor(Color.TRANSPARENT);


        //Received List Show
        screenStatus = OrderScreenStatusEnum.RECEIVED;
        setupScreenForListScreen();

    }

    public void setScreenStatus(OrderScreenStatusEnum screenStatus) {
        this.screenStatus = screenStatus;
    }


    public void setLinkViewModel(LinkViewModel linkViewModel) {
        this.linkViewModel = linkViewModel;
    }

    public void setCompanyProfileViewModel(ProfileViewModal companyProfileViewModel) {
        this.companyProfileViewModel = companyProfileViewModel;
    }


    public void setShowSearchFilter(boolean showSearchFilter) {
        this.showSearchFilter = showSearchFilter;
    }

    public void setSelectedTab(OrderScreenStatusEnum selectedTab) {
        this.selectedTab = selectedTab;
    }
}
