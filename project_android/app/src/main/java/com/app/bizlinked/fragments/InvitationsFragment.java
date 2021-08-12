package com.app.bizlinked.fragments;

import android.arch.lifecycle.Observer;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.app.bizlinked.adapters.InvitationsAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.listener.custom.InvitationsClickListener;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.enums.LinkRelationEnum;
import com.app.bizlinked.models.enums.LinkStatusEnum;
import com.app.bizlinked.models.enums.ProfileViewEnum;
import com.app.bizlinked.models.viewmodel.LinkViewModel;
import com.app.bizlinked.models.viewmodel.LinksViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;

public class InvitationsFragment extends BaseFragment {


    //Received Section
    @BindView(R.id.llReceivedSectionContainer)
    LinearLayout llReceivedSectionContainer;
    @BindView(R.id.llNoReceivedDataFound)
    LinearLayout llNoReceivedDataFound;
    @BindView(R.id.flReceivedListSectionContainer)
    FrameLayout flReceivedListSectionContainer;
    @BindView(R.id.swipeRefreshReceived)
    SwipeRefreshLayout swipeRefreshReceived;
    @BindView(R.id.rvReceived)
    RecyclerView rvReceived;
    //Received Section

    // Sent Section
    @BindView(R.id.llSentSectionContainer)
    LinearLayout llSentSectionContainer;
    @BindView(R.id.llNoSentDataFound)
    LinearLayout llNoSentDataFound;
    @BindView(R.id.flSentListSectionContainer)
    FrameLayout flSentListSectionContainer;
    @BindView(R.id.swipeRefreshSent)
    SwipeRefreshLayout swipeRefreshSent;
    @BindView(R.id.rvSent)
    RecyclerView rvSent;
    //Sent Section


    //Tabs
    @BindView(R.id.llTabs)
    LinearLayout llTabs;
    @BindView(R.id.tvReceived)
    TextView tvReceived;
    @BindView(R.id.tvSent)
    TextView tvSent;
    //Tabs


    //View Model
    LinksViewModel receivedLinksViewModel;
    LinksViewModel sentLinksViewModel;

    InvitationsAdapter receivedLinkAdapter;
    ArrayList<LinkViewModel> receivedLinks = null;

    InvitationsAdapter sentLinkAdapter;
    ArrayList<LinkViewModel> sentLinks = null;


    LinkRelationEnum invitationType;
    String invitationTypeTitleText;

    TitleBar titleBar = null;

    public InvitationsFragment() {
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
        titleBar.showHeaderTitle(invitationTypeTitleText);
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
        return R.layout.fragment_invitations;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

        initializeViewModal();
        setupScreenForReceivedListScreen();
        setupScreenForSentListScreen();
    }

    @Override
    public void afterBackStackChange(){
        setTitleBar(titleBar);
        getMainActivity().setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.LINKS);
    }


    private void initializeViewModal() {
        // Get the ViewModel.
//        receivedLinksViewModel = ViewModelProviders.of(this).get(LinksViewModel.class);
        receivedLinksViewModel = new LinksViewModel();
        sentLinksViewModel = new LinksViewModel();
    }


    @Override
    public void onDestroy() {

        //Remove Change Listener
        if(receivedLinksViewModel != null){
            receivedLinksViewModel.getAllLinksFromDB(invitationType, LinkStatusEnum.RECEIVED).removeAllChangeListeners();
        }

        //Remove Change Listener
        if(sentLinksViewModel != null){
            sentLinksViewModel.getAllLinksFromDB(invitationType, LinkStatusEnum.SENT).removeAllChangeListeners();
        }

        super.onDestroy();

    }


    /* *****************************************************************************************
     ******************************************************************************************
     * ****************************** Received Section Starts *********************************
     ******************************************************************************************
     ******************************************************************************************/

    private void setupScreenForReceivedListScreen() {
        //resetSearchView();
        flReceivedListSectionContainer.setVisibility(View.VISIBLE);
        llNoReceivedDataFound.setVisibility(View.GONE);

        initializeReceivedAdapter();
        getAllReceivedLinks();

        swipeRefreshReceived.setColorSchemeResources(R.color.appColorPrimary);
        swipeRefreshReceived.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                receivedLinksViewModel.syncLinks();
//                getAllReceivedLinks();
                swipeRefreshReceived.setRefreshing(false);
            }
        });

//        swipeRefreshReceived.setRefreshing(false);
//        swipeRefreshReceived.setEnabled(false);
    }



    private void getAllReceivedLinks() {

        receivedLinks = new ArrayList<>();

        Observer<RealmResults<Link>> receivedLinkObserver = new Observer<RealmResults<Link>>() {
            @Override
            public void onChanged(@Nullable RealmResults<Link> links) {

                if (receivedLinkAdapter != null) {

                    //Clear the list
                    receivedLinkAdapter.clearAllList();
                    receivedLinks.clear();

                    if(links != null){
                        receivedLinks.addAll(receivedLinksViewModel.getLinksDataForUI(links));
                    }
                    receivedLinkAdapter.addAllList(receivedLinks);

                    //This Work is For Image Show when Image Downloads
                    for (int index = 0; index < receivedLinkAdapter.getItemCount(); index++) {
                        int finalIndex = index;
                        Observer<byte[]> imageObserver = new Observer<byte[]>() {
                            @Override
                            public void onChanged(@Nullable byte[] imageData) {
                                receivedLinkAdapter.notifyItemChanged(finalIndex);
                            }
                        };
                        receivedLinkAdapter.getItem(index).getImage().removeObserver(imageObserver);
                        receivedLinkAdapter.getItem(index).getImage().observe(activityReference, imageObserver);
                    }
                    //This Work is For Image Show when Image Downloads

                }
                if(isAdded() && isVisible())
                    checkReceivedSectionHasData(receivedLinkAdapter != null && receivedLinkAdapter.getItemCount() > 0);
            }
        };

        receivedLinksViewModel.getLinkResults(invitationType, LinkStatusEnum.RECEIVED).removeObserver(receivedLinkObserver);
        receivedLinksViewModel.getLinkResults(invitationType, LinkStatusEnum.RECEIVED).observe(activityReference, receivedLinkObserver);
    }

    private void initializeReceivedAdapter() {

        receivedLinkAdapter = new InvitationsAdapter(activityReference, receivedLinks, LinkStatusEnum.RECEIVED, new InvitationsClickListener<LinkViewModel>() {
            @Override
            public void onClick(LinkViewModel linkViewModel, int position) {
                openCompanyProfile(linkViewModel);
            }

            @Override
            public void onEditClick(LinkViewModel object, int position) {
            }

            @Override
            public void onAccept(LinkViewModel object, int position) {
                receivedLinksViewModel.updateStatus(LinkStatusEnum.LINKED, object);
                receivedLinkAdapter.removeItem(position);
                checkReceivedSectionHasData(receivedLinkAdapter != null && receivedLinkAdapter.getItemCount() > 0);
            }

            @Override
            public void onReject(LinkViewModel object, int position) {
                receivedLinksViewModel.updateStatus(LinkStatusEnum.REJECTED, object);
                receivedLinkAdapter.removeItem(position);
                checkReceivedSectionHasData(receivedLinkAdapter != null && receivedLinkAdapter.getItemCount() > 0);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(activityReference);
        rvReceived.setLayoutManager(layoutManager);
        rvReceived.setAdapter(receivedLinkAdapter);
    }

    private void checkReceivedSectionHasData(boolean isData) {

        if (isData) {
            rvReceived.setVisibility(View.VISIBLE);
            llNoReceivedDataFound.setVisibility(View.GONE);

        } else {
            rvReceived.setVisibility(View.GONE);
            llNoReceivedDataFound.setVisibility(View.VISIBLE);
        }
    }

    /* *****************************************************************************************
     ******************************************************************************************
     * ****************************** Received Section Ends ****************************************
     ******************************************************************************************
     ******************************************************************************************/


    /* *****************************************************************************************
     ******************************************************************************************
     * ****************************** Sent Section Starts **********************************
     ******************************************************************************************
     ******************************************************************************************/

    private void setupScreenForSentListScreen() {

        flSentListSectionContainer.setVisibility(View.VISIBLE);
        llNoSentDataFound.setVisibility(View.GONE);

        initializeSentAdapter();
        getAllSentLinks();

        swipeRefreshSent.setColorSchemeResources(R.color.appColorPrimary);
        swipeRefreshSent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                sentLinksViewModel.syncLinks();
//                getAllReceivedLinks();
                swipeRefreshSent.setRefreshing(false);
            }
        });

//        swipeRefreshSent.setRefreshing(false);
//        swipeRefreshSent.setEnabled(false);
    }

    private void getAllSentLinks() {

        sentLinks = new ArrayList<>();

        Observer<RealmResults<Link>> sentLinkObserver = new Observer<RealmResults<Link>>() {
            @Override
            public void onChanged(@Nullable RealmResults<Link> links) {

                if (sentLinkAdapter != null) {

                    //Clear the list
                    sentLinkAdapter.clearAllList();
                    sentLinks.clear();

                    if(links != null)
                        sentLinks.addAll(sentLinksViewModel.getLinksDataForUI(links));

                    sentLinkAdapter.addAllList(sentLinks);

                    //This Work is For Image Show when Image Downloads
                    for (int index = 0; index < sentLinkAdapter.getItemCount(); index++) {
                        int finalIndex = index;
                        Observer<byte[]> imageObserver = new Observer<byte[]>() {
                            @Override
                            public void onChanged(@Nullable byte[] imageData) {
                                sentLinkAdapter.notifyItemChanged(finalIndex);
                            }
                        };
                        sentLinkAdapter.getItem(index).getImage().removeObserver(imageObserver);
                        sentLinkAdapter.getItem(index).getImage().observe(activityReference, imageObserver);
                    }
                    //This Work is For Image Show when Image Downloads

                }
                if(isVisible() && isAdded())
                    checkSentSectionHasData(sentLinkAdapter != null && sentLinkAdapter.getItemCount() > 0);
            }
        };

        sentLinksViewModel.getLinkResults(invitationType, LinkStatusEnum.SENT).removeObserver(sentLinkObserver);
        sentLinksViewModel.getLinkResults(invitationType, LinkStatusEnum.SENT).observe(activityReference, sentLinkObserver);
    }

    private void initializeSentAdapter() {


        sentLinkAdapter = new InvitationsAdapter(activityReference, sentLinks, LinkStatusEnum.SENT, new InvitationsClickListener<LinkViewModel>() {
            @Override
            public void onClick(LinkViewModel linkViewModel, int position) {
                openCompanyProfile(linkViewModel);
            }

            @Override
            public void onEditClick(LinkViewModel object, int position) {

            }

            @Override
            public void onAccept(LinkViewModel object, int position) {

            }

            @Override
            public void onReject(LinkViewModel object, int position) {
                sentLinksViewModel.updateStatus(LinkStatusEnum.CANCELLED, object);
                sentLinkAdapter.removeItem(position);
                checkSentSectionHasData(sentLinkAdapter != null && sentLinkAdapter.getItemCount() > 0);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(activityReference);
        rvSent.setLayoutManager(layoutManager);
        rvSent.setAdapter(sentLinkAdapter);
    }

    private void checkSentSectionHasData(boolean isData) {
        if (isData) {
            rvSent.setVisibility(View.VISIBLE);
            llNoSentDataFound.setVisibility(View.GONE);

        } else {
            rvSent.setVisibility(View.GONE);
            llNoSentDataFound.setVisibility(View.VISIBLE);
        }
    }

    /* *****************************************************************************************
     ******************************************************************************************
     * ****************************** Sent Section Ends ***********************************
     ******************************************************************************************
     ******************************************************************************************/


    private void openCompanyProfile(LinkViewModel linkViewModel) {
        CompanyDetailFragment companyDetailFragment = new CompanyDetailFragment();
        companyDetailFragment.setCompanyRelation(LinkRelationEnum.SUPPLIER); // because of Showing Product
        companyDetailFragment.setType(ProfileViewEnum.COMPANY_PROFILE);
        companyDetailFragment.setLinkViewModel(linkViewModel);
        companyDetailFragment.setCompanyProfileViewModel(linkViewModel.getProfileViewModel());
        activityReference.realAddSupportFragment(companyDetailFragment, AppConstant.TRANSITION_TYPES.SLIDE);

    }



    @OnClick({
            R.id.tvReceived,
            R.id.tvSent
    })
    public void onViewClicked(View view) {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activityReference.getTheme();
        @ColorInt int color;
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        color = typedValue.data;

        switch (view.getId()) {


            case R.id.tvReceived:

                tvReceived.setBackgroundColor(color);
                llReceivedSectionContainer.setVisibility(View.VISIBLE);
                tvSent.setBackgroundColor(Color.TRANSPARENT);
                llSentSectionContainer.setVisibility(View.GONE);

                break;

            case R.id.tvSent:

                tvReceived.setBackgroundColor(Color.TRANSPARENT);
                llReceivedSectionContainer.setVisibility(View.GONE);
                tvSent.setBackgroundColor(color);
                llSentSectionContainer.setVisibility(View.VISIBLE);

                break;
        }
    }

    public void setType(LinkRelationEnum invitationType) {
        this.invitationType = invitationType;
    }

    public void setInvitationTypeTitleText(String invitationTypeTitleText) {
        this.invitationTypeTitleText = invitationTypeTitleText;
    }
}