package com.app.bizlinked.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bizlinked.R;
import com.app.bizlinked.adapters.LinkedItemAdapter;
import com.app.bizlinked.adapters.UnlinkedItemAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.common.KeyboardHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.recyclerview_pagination.PaginationScrollListener;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.listener.custom.DataSaveAndConvertInterface;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.enums.LinkRelationEnum;
import com.app.bizlinked.models.enums.LinkStatusEnum;
import com.app.bizlinked.models.enums.OrderScreenStatusEnum;
import com.app.bizlinked.models.enums.ProductViewEnum;
import com.app.bizlinked.models.enums.ProfileViewEnum;
import com.app.bizlinked.models.viewmodel.LinkViewModel;
import com.app.bizlinked.models.viewmodel.LinksSearchViewModel;
import com.app.bizlinked.models.viewmodel.ProfileViewModal;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

public class LinksSearchFragment extends BaseFragment {


    @BindView(R.id.llMainView)
    LinearLayout llMainView;


    @BindView(R.id.etSearch)
    EditText etSearch;

    @BindView(R.id.llLinkedSection)
    LinearLayout llLinkedSection;
    @BindView(R.id.llUnlinkedSection)
    LinearLayout llUnlinkedSection;

    // Linked Section
    @BindView(R.id.llNoLinkedDataFound)
    LinearLayout llNoLinkedDataFound;
    @BindView(R.id.flLinkedContainer)
    FrameLayout flLinkedContainer;
    @BindView(R.id.rvLinked)
    RecyclerView rvLinked;
    // Linked Section

    // UnLinked Section
    @BindView(R.id.llNoUnLinkedDataFound)
    LinearLayout llNoUnLinkedDataFound;
    @BindView(R.id.flUnLinkedContainer)
    FrameLayout flUnLinkedContainer;
    @BindView(R.id.rvUnLinked)
    RecyclerView rvUnLinked;
    // UnLinked Section


//    SearchProductAdapter itemsearchProductAdapter;
//    ArrayList<Product> dataProducts;
//    private int totalProductsCount = 0;

    private TitleBar titleBar = null;


    //For Pagination
    int currentPage = 0;
//    int TOTAL_PAGES = 1;
    boolean isLoadingData = false;
    boolean isLastPage = false;


    private ArrayList<LinkViewModel> dataLink = null;
    LinkedItemAdapter linkedItemAdapter;

    private ArrayList<ProfileViewModal> dataUnlink = null;
    UnlinkedItemAdapter unlinkedItemAdapter;

    //View Model
    LinksSearchViewModel linksSearchViewModel;


    public LinksSearchFragment() {
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
        titleBar.showHeaderTitle(activityReference.getString(R.string.search_links));
        titleBar.setLeftTitleText(getResources().getString(R.string.back));
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
        //If go back to screen don't refresh the list
//        String searchText = etSearch.getText().toString().trim();
//        if (!Utils.isEmptyOrNull(searchText)) {
//            searchLinkUnlinkResults(searchText);
//        }
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_links_search;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

        initializeViewModal();
        setupScreenForLinks();


        //Wherever click hide keybaord
        llMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardHelper.hideSoftKeyboard(activityReference);
            }
        });

    }

    private void setupScreenForLinks() {

        resetScreen();
        initializeLinksSearch();
        initializeLinkedAdapter();
        initializeUnlinkedAdapter();
    }

    private void resetScreen() {

        //Link Reset
        flLinkedContainer.setVisibility(View.VISIBLE);
        llNoLinkedDataFound.setVisibility(View.VISIBLE);
        rvLinked.setVisibility(View.GONE);

        //UnLink Reset
        flUnLinkedContainer.setVisibility(View.VISIBLE);
        llNoUnLinkedDataFound.setVisibility(View.VISIBLE);
        rvUnLinked.setVisibility(View.GONE);



        //Full section gone after work
        llLinkedSection.setVisibility(View.GONE);
        llUnlinkedSection.setVisibility(View.GONE);



    }

    private void initializeViewModal() {

        // Get the ViewModel.
        linksSearchViewModel = new LinksSearchViewModel();
        linksSearchViewModel.setActivityReference(activityReference);


        linksSearchViewModel.getLinkedCompanies().observe(this, new Observer<ArrayList<LinkViewModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<LinkViewModel> linkedCompanies) {

                //Toast.makeText(activityReference, "Linked Companies = " + linkedCompanies.size(), Toast.LENGTH_SHORT).show();
                //tvCreditBalance.setText(String.valueOf(credit));
                if (linkedItemAdapter != null) {
//                    linkedItemAdapter.clearAllList();
                    linkedItemAdapter.addAllList(linkedCompanies);
                }
                if(isAdded() && isVisible())
                    checkLinkedSectionHasData(linkedItemAdapter != null && linkedItemAdapter.getItemCount() > 0);
            }
        });


        linksSearchViewModel.getUnlinkedCompanies().observe(this, new Observer<ArrayList<ProfileViewModal>>() {
            @Override
            public void onChanged(@Nullable ArrayList<ProfileViewModal> unlinkedCompanies) {

                if (unlinkedItemAdapter != null) {

//                    dataUnlink.addAll(unlinkedCompanies);

//                    unlinkedItemAdapter.clearAllList();
                    unlinkedItemAdapter.addAllList(unlinkedCompanies);


                    //This Work is For Image Show when Image Downloads
                    for (int index = 0; index < unlinkedItemAdapter.getItemCount(); index++) {
                        int finalIndex = index;
                        Observer<HashMap<String, byte[]>> imageObserver = new Observer<HashMap<String, byte[]>>() {
                            @Override
                            public void onChanged(@Nullable HashMap<String, byte[]> imageData) {
                                unlinkedItemAdapter.notifyItemChanged(finalIndex);
                            }
                        };
                        unlinkedItemAdapter.getItem(index).getImage().removeObserver(imageObserver);
                        unlinkedItemAdapter.getItem(index).getImage().observe(activityReference, imageObserver);
                    }
                    //This Work is For Image Show when Image Downloads

                }
                if(isVisible() && isAdded())
                    checkUnlinkedSectionHasData(unlinkedItemAdapter != null && unlinkedItemAdapter.getItemCount() > 0);

            }
        });
    }

    private void checkUnlinkedSectionHasData(boolean isData) {

        if (isData) {
            rvUnLinked.setVisibility(View.VISIBLE);
            llNoUnLinkedDataFound.setVisibility(View.GONE);

            llUnlinkedSection.setVisibility(View.VISIBLE);

        } else {
            rvUnLinked.setVisibility(View.GONE);
            llNoUnLinkedDataFound.setVisibility(View.VISIBLE);

            llUnlinkedSection.setVisibility(View.GONE);

        }

    }

    private void checkLinkedSectionHasData(boolean isData) {

        if (isData) {
            rvLinked.setVisibility(View.VISIBLE);
            llNoLinkedDataFound.setVisibility(View.GONE);

            llLinkedSection.setVisibility(View.VISIBLE);

        } else {
            rvLinked.setVisibility(View.GONE);
            llNoLinkedDataFound.setVisibility(View.VISIBLE);

            llLinkedSection.setVisibility(View.GONE);

        }
    }


    private void initializeLinksSearch() {

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String searchText = etSearch.getText().toString().trim();
                    if (!Utils.isEmptyOrNull(searchText)) {

                        searchLinkUnlinkResults(searchText);

                    }
                    return true;
                }
                return false;
            }
        });

    }

    private void searchLinkUnlinkResults(String searchText) {
        //For Unlink Section
        if(dataUnlink != null)
            dataUnlink.clear();
        if (unlinkedItemAdapter != null){
            unlinkedItemAdapter.clearAllList();
            unlinkedItemAdapter.notifyDataSetChanged();
        }

        if(isVisible() && isAdded())
            checkUnlinkedSectionHasData(unlinkedItemAdapter != null && unlinkedItemAdapter.getItemCount() > 0);


        //For Link Section
        if(dataLink != null)
            dataLink.clear();
        if (linkedItemAdapter != null){
            linkedItemAdapter.clearAllList();
            linkedItemAdapter.notifyDataSetChanged();
        }

        if(isVisible() && isAdded())
            checkLinkedSectionHasData(linkedItemAdapter != null && linkedItemAdapter.getItemCount() > 0);



        //Search from local and server
        linksSearchViewModel.search(searchText);

        //Hide Keyboard function
        KeyboardHelper.hideSoftKeyboard(activityReference);

    }


    private void initializeLinkedAdapter() {

        dataLink = new ArrayList<>();
        linkedItemAdapter = new LinkedItemAdapter(activityReference, dataLink, new DataRecyclerViewClickInterface<LinkViewModel>() {
            @Override
            public void onClick(LinkViewModel object, int position) {

                if(object.getRelation().equalsIgnoreCase(LinkRelationEnum.SUPPLIER.getValue()) &&
                    object.getStatus().equalsIgnoreCase(LinkStatusEnum.LINKED.getValue())){

                    ProductCategoryListFragment productCategoryListFragment = new ProductCategoryListFragment();
                    productCategoryListFragment.setProductViewType(ProductViewEnum.COMPANY_PROFILE);
                    productCategoryListFragment.setLinkViewModel(object);
                    productCategoryListFragment.setCompanyProfileViewModel(object.getProfileViewModel());
                    activityReference.realAddSupportFragment(productCategoryListFragment, AppConstant.TRANSITION_TYPES.SLIDE);

                }else if(object.getRelation().equalsIgnoreCase(LinkRelationEnum.CUSTOMER.getValue()) &&
                        object.getStatus().equalsIgnoreCase(LinkStatusEnum.LINKED.getValue())){

                    OrderListFragment orderListFragment = new OrderListFragment();
                    orderListFragment.setShowSearchFilter(false);
                    orderListFragment.setScreenStatus(OrderScreenStatusEnum.RECEIVED);
                    orderListFragment.setLinkViewModel(object);
                    orderListFragment.setCompanyProfileViewModel(object.getProfileViewModel());
                    activityReference.realAddSupportFragment(orderListFragment, AppConstant.TRANSITION_TYPES.SLIDE);

                }else{

                   openCompanyProfile(object);

                }
            }

            @Override
            public void onEditClick(LinkViewModel object, int position) {
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(activityReference);
        rvLinked.setLayoutManager(layoutManager);
        rvLinked.setAdapter(linkedItemAdapter);

        rvLinked.setNestedScrollingEnabled(false);



    }

    private void openCompanyProfile(LinkViewModel object) {
        CompanyDetailFragment companyDetailFragment = new CompanyDetailFragment();
        companyDetailFragment.setCompanyRelation(LinkRelationEnum.SUPPLIER); // because of Showing Product
        companyDetailFragment.setType(ProfileViewEnum.COMPANY_PROFILE);
        companyDetailFragment.setLinkViewModel(object);
        companyDetailFragment.setCompanyProfileViewModel(object.getProfileViewModel());
        activityReference.realAddSupportFragment(companyDetailFragment, AppConstant.TRANSITION_TYPES.SLIDE);
    }

    private void initializeUnlinkedAdapter() {

        dataUnlink = new ArrayList<>();
        unlinkedItemAdapter = new UnlinkedItemAdapter(activityReference, dataUnlink, new DataRecyclerViewClickInterface<ProfileViewModal>() {
            @Override
            public void onClick(ProfileViewModal object, int position) {
                CompanyDetailFragment companyDetailFragment = new CompanyDetailFragment();
                companyDetailFragment.setCompanyRelation(LinkRelationEnum.SUPPLIER); // because of Showing Product
                companyDetailFragment.setType(ProfileViewEnum.COMPANY_PROFILE);
//                companyDetailFragment.setLinkViewModel();
                companyDetailFragment.setCompanyProfileViewModel(object);
                activityReference.realAddSupportFragment(companyDetailFragment, AppConstant.TRANSITION_TYPES.SLIDE);
            }

            @Override
            public void onEditClick(ProfileViewModal object, int position) {
                showSelectAsCustomerOrSupplierOption(object, position);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(activityReference);
        rvUnLinked.setLayoutManager(layoutManager);
        rvUnLinked.setAdapter(unlinkedItemAdapter);
        rvUnLinked.setNestedScrollingEnabled(false);

        rvUnLinked.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoadingData = true;
                currentPage += 1;

                Log.i(TAG, "isLoading? " + isLoadingData + " currentPage " + currentPage + " Total Page " + linksSearchViewModel.getTotalPages());

                linksSearchViewModel.setPage(currentPage);
                linksSearchViewModel.searchServer(etSearch.getText().toString());
                //pr(currentPage, AppConstant.CONFIGURATION.PAGE_SIZE, true, null);
            }

            @Override
            public int getTotalPageCount() {
                return linksSearchViewModel.getTotalPages();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoadingData() {
                return isLoadingData;
            }
        });


    }

    private void showSelectAsCustomerOrSupplierOption(ProfileViewModal object, int selectedItemPosition) {

        TypedValue typedValue = new TypedValue();
        @ColorInt int color;
        activityReference.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;


        BottomSheetMenuDialog mBottomSheetDialog = new BottomSheetBuilder(activityReference)
                .addTitleItem(activityReference.getString(R.string.choose_your_reltion))
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setIconTintColor(color)
                .addItem(0, activityReference.getString(R.string.add_as_my_customer), R.drawable.ic_logo)
                .addDividerItem()
                .addItem(1, activityReference.getString(R.string.add_as_my_supplier), R.drawable.ic_logo)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case 0:
                                object.addAsLink(LinkRelationEnum.CUSTOMER.getValue(), new DataSaveAndConvertInterface<Object, Link>() {
                                    @Override
                                    public void onSuccess(Object syncResponse, Link newlyCreatedLinkedObject) {
                                        linksSearchViewModel.addLinkedCompany(newlyCreatedLinkedObject);
                                        unlinkedItemAdapter.removeItem(selectedItemPosition);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                                break;
                            case 1:
                                object.addAsLink(LinkRelationEnum.SUPPLIER.getValue(), new DataSaveAndConvertInterface<Object, Link>() {
                                    @Override
                                    public void onSuccess(Object syncResponse, Link newlyCreatedLinkedObject) {
                                        linksSearchViewModel.addLinkedCompany(newlyCreatedLinkedObject);
                                        unlinkedItemAdapter.removeItem(selectedItemPosition);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                                break;
                        }
                        Log.d("Item click", item.getTitle() + "");
                    }
                })
                .createDialog();
        mBottomSheetDialog.show();

    }
}