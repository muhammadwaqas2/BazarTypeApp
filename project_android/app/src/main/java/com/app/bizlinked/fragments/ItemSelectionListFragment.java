package com.app.bizlinked.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.adapters.ItemSelectionAdapter;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.recycler_touchHelper.RecyclerTouchListener;
import com.app.bizlinked.listener.ClickListenerRecycler;
import com.app.bizlinked.listener.custom.ItemPageListenerInterface;
import com.app.bizlinked.models.GenericNameIdModal;
import com.daimajia.androidanimations.library.Techniques;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemSelectionListFragment extends BaseFragment {



    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    // Tabs For Doctor

    @BindView(R.id.llNoItemDataFound)
    LinearLayout llNoItemDataFound;


    @BindView(R.id.flItemContainer)
    FrameLayout flItemContainer;
    @BindView(R.id.rvItems)
    RecyclerView rvItems;

    @BindView(R.id.etSearch)
    EditText etSearch;



    ItemSelectionAdapter itemAdapter;
    ArrayList<GenericNameIdModal> dataItems = null;



    private ItemPageListenerInterface itemPageListenerInterface = null;
    private TitleBar titleBar = null;

    private boolean isSingleSelection;




    public ItemSelectionListFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCustomBackPressed() {

        activityReference.onPageBack();

        if(itemPageListenerInterface != null){
            itemPageListenerInterface.itemPageListenerInterfaceCallBack(null, isSingleSelection);
        }
    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        this.titleBar = titleBar;
        titleBar.showHeaderView();
        titleBar.setLeftTitleText(getResources().getString(R.string.select_item));
        titleBar.showLeftIconAndListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activityReference.onBackPressed();

                if(itemPageListenerInterface != null){
                    itemPageListenerInterface.itemPageListenerInterfaceCallBack(null, isSingleSelection);
                }
            }
        });

        if(!isSingleSelection()){
            titleBar.showRightTextAndSetListener(activityReference.getString(R.string.continue_text), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemPageListenerInterface != null){
                        itemPageListenerInterface.itemPageListenerInterfaceCallBack(dataItems, isSingleSelection);
                    }
                }
            });
        }
    }

    @Override
    protected int getMainLayout() {
        return R.layout.fragment_item_selection_list;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(isVisible() && isAdded()){
                    if(llMainView != null){
                        int duration = 500;
                        for (int index = 0; index < llMainView.getChildCount(); index++) {
                            AnimationHelpers.animate(Techniques.SlideInUp, duration, llMainView.getChildAt(index));
                            duration += 100;
                        }
                    }
                    //AnimationHelpers.animate(Techniques.BounceInUp, 600, flLoginView);
                }
            }
        },100);

        setupScreenForItemsListing();
    }



    /* *******************************************************************
     ******************** Item Start **************************
     *********************************************************************
     ********************************************************************* */

    private void setupScreenForItemsListing() {


        //resetSearchView();
        flItemContainer.setVisibility(View.VISIBLE);
        llNoItemDataFound.setVisibility(View.GONE);


        initializeSearch();
        initializeItemstAdapter();
        checkItemsHasDataOrNot();

//        swipeRefreshDoctor.setColorSchemeResources(R.color.appBlueColorPrimary);
//        swipeRefreshDoctor.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                checkItemsHasDataOrNot(1, AppConstant.CONFIGURATION.PAGE_SIZE, false, null);
//                swipeRefreshDoctor.setRefreshing(false);
//            }
//        });


    }

    private void initializeSearch() {

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = etSearch.getText().toString().trim();
                    itemAdapter.getFilter().filter(searchText);
                    return true;
                }
                return false;
            }
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = s.toString().trim();
                itemAdapter.getFilter().filter(searchString);
            }
        });
    }



    private void initializeItemstAdapter() {

//        dataAddress = new ArrayList<>();
        itemAdapter = new ItemSelectionAdapter(activityReference, dataItems, isSingleSelection());
        LinearLayoutManager layoutManager = new LinearLayoutManager(activityReference);
        rvItems.setLayoutManager(layoutManager);
        rvItems.setAdapter(itemAdapter);

        rvItems.addOnItemTouchListener(new RecyclerTouchListener(activityReference, rvItems,
                new ClickListenerRecycler() {
                    @Override
                    public void onClick(View view, int position) {

                        dataItems.get(position).setSelect(!dataItems.get(position).isSelect());

                        if(!isSingleSelection()){
                            itemAdapter.updateDataAtPosition(dataItems.get(position).isSelect(), position);
                            itemAdapter.notifyItemChanged(position);
                        }else{
                            activityReference.onBackPressed();
                            if(itemPageListenerInterface != null){
                                itemPageListenerInterface.itemPageListenerInterfaceCallBack(dataItems, isSingleSelection);
                            }
                        }
                    }

                    @Override
                    public void onLongClick(View view, final int position) {

                    }
                }
        ));
    }

    private void checkItemsHasDataOrNot() {

        //itemAdapter.clearAllList();

        if(dataItems != null && dataItems.size() > 0){
            //itemAdapter.addAllList(dataItems);
            llNoItemDataFound.setVisibility(View.GONE);
        }else{
            llNoItemDataFound.setVisibility(View.VISIBLE);
        }

    }

    /* *******************************************************************
     ******************** Doctor Container End **************************
     *********************************************************************
     ********************************************************************* */


    @OnClick({R.id.llNoItemDataFound})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llNoItemDataFound:
//            case R.id.btnAddContacts:
//                getAllDialCodes();
////                validateFields();
//                break;
        }
    }


    public void setItemList(List<GenericNameIdModal> items) {
        this.dataItems = new ArrayList<GenericNameIdModal>();
        if(items != null)
            this.dataItems.addAll(items);
    }

    public void setItemPageListener(ItemPageListenerInterface itemPageListenerInterface) {
        this.itemPageListenerInterface = itemPageListenerInterface;
    }

    public boolean isSingleSelection() {
        return isSingleSelection;
    }

    public void setSingleSelection(boolean singleSelection) {
        isSingleSelection = singleSelection;
    }
}
