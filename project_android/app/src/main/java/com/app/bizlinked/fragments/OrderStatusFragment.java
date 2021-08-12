package com.app.bizlinked.fragments;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bizlinked.R;
import com.app.bizlinked.adapters.OrderProductListAdapter;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.dialog.DialogClass;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.recycler_swipe_to_delete.SwipeToDeleteCallback;
import com.app.bizlinked.listener.custom.DataRecyclerViewClickInterface;
import com.app.bizlinked.listener.custom.DataUpdateListenerInterface;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.listener.custom.OnBackPressInterface;
import com.app.bizlinked.models.enums.OrderScreenStatusEnum;
import com.app.bizlinked.models.viewmodel.OrderDetailViewModel;
import com.app.bizlinked.models.viewmodel.OrderViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class OrderStatusFragment extends BaseFragment {


    @BindView(R.id.ivCompanyImage)
    CircleImageView ivCompanyImage;
    @BindView(R.id.tvCompanyName)
    TextView tvCompanyName;
    @BindView(R.id.tvCompanyAddress)
    TextView tvCompanyAddress;


    //Status Icons and Bars
    @BindView(R.id.ivSend)
    ImageView ivSend;
    @BindView(R.id.viewBar1)
    View viewBar1;
    @BindView(R.id.ivApprovedAndReject)
    ImageView ivApprovedAndReject;
    @BindView(R.id.viewBar2)
    View viewBar2;
    @BindView(R.id.ivParcelDelivered)
    ImageView ivParcelDelivered;
    @BindView(R.id.viewBar3)
    View viewBar3;
    @BindView(R.id.ivParcelReceived)
    ImageView ivParcelReceived;
    //Status Icons and Bars

    //Status Text
    @BindView(R.id.tvSendStatusAndDate)
    TextView tvSendStatusAndDate;
    @BindView(R.id.tvApprovedRejectStatusAndDate)
    TextView tvApprovedRejectStatusAndDate;
    @BindView(R.id.tvParcelDeliveredStatusAndDate)
    TextView tvParcelDeliveredStatusAndDate;
    @BindView(R.id.tvParcelReceivedStatusAndDate)
    TextView tvParcelReceivedStatusAndDate;
    //Status Text

    @BindView(R.id.tvItemsCount)
    TextView tvItemsCount;
    @BindView(R.id.tvTotalAmount)
    TextView tvTotalAmount;

    @BindView(R.id.rvOrders)
    RecyclerView rvOrders;

    @BindView(R.id.llMainView)
    LinearLayout llMainView;

    TitleBar titleBar = null;


    OrderViewModel orderViewModel = null;
    private OrderViewModel orderViewModelObject;
    private OrderScreenStatusEnum screenStatus;
    private boolean showSearchFilter;

    private OnBackPressInterface onBackPressInterface = null;

    OrderProductListAdapter orderProductListAdapter = null;
    private String orderId = null;

    public OrderStatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCustomBackPressed() {

        if (onBackPressInterface != null) {
            onBackPressInterface.onBackPressListener();
        }
        activityReference.onPageBack();

    }

    @Override
    protected void setTitleBar(TitleBar titleBar) {

        this.titleBar = titleBar;
        titleBar.showHeaderView();
        titleBar.showHeaderTitle(activityReference.getString(R.string.orders));
//        titleBar.showRightSearchIconAndSetListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activityReference.addSupportFragment(new ProductSearchFragment(), AppConstant.TRANSITION_TYPES.SLIDE);
////                Toast.makeText(activityReference, "Search Click...", Toast.LENGTH_SHORT).show();
//                //showSearchViewAndSearchFromServer();
//            }
//        });


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
        return R.layout.fragment_order_status;
    }

    @Override
    protected void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View rootView) {

        //Setup Screen For Orders
        initializeViewModal();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataAndPopulateOnUI();
    }

    @Override
    public void afterBackStackChange(){
        if(showSearchFilter){
            getMainActivity().setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.ORDERS);
        }else{
            getMainActivity().setBottomMenuFromSelectedOption(AppConstant.BOTTOM_MENU.LINKS);
        }
    }



    private void initializeViewModal() {

        // Get the ViewModel.
        orderViewModel = new OrderViewModel();
        orderViewModel.setActivityReference(activityReference);

    }

    public void getDataAndPopulateOnUI() {

        if (orderViewModelObject != null && !Utils.isEmptyOrNull(orderViewModelObject.getOrderId())) {
            orderViewModel.init(orderViewModelObject.getOrderId(), screenStatus);
        }else if(!Utils.isEmptyOrNull(orderId)) {
            orderViewModel.init(orderId, screenStatus);
        }else{
            if(orderViewModel != null && orderViewModel.getOrder().getOrderDetails().size() == 0){
                onCustomBackPressed();
                return;
            }
        }

        populateDataOnUI();

    }


    private void populateDataOnUI() {


        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        @ColorInt int color;
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;

        if (orderViewModel != null) {

            //Name
            if (!Utils.isEmptyOrNull(orderViewModel.getCompanyName())) {
                tvCompanyName.setText(orderViewModel.getCompanyName());
            }

            //Address
            if (!Utils.isEmptyOrNull(orderViewModel.getAddress())) {
                tvCompanyAddress.setText(orderViewModel.getAddress());
            }

            //Company Image
            if (orderViewModel.getImage() != null && orderViewModel.getImage().length > 0) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(orderViewModel.getImage(), 0, orderViewModel.getImage().length);
                ivCompanyImage.setImageBitmap(imageBitmap);
            }

            //Set Send Status
            if (!Utils.isEmptyOrNull(orderViewModel.getSubmittedDate())) {
                ivSend.setBackgroundResource(R.drawable.primary_color_circle);
                tvSendStatusAndDate.setText("Sent\n" + orderViewModel.getSubmittedDate());
                tvSendStatusAndDate.setVisibility(View.VISIBLE);
            }

            //Set Approved Status
            if (!Utils.isEmptyOrNull(orderViewModel.getApprovedDate())) {

                if (orderViewModel.getApproved()) {

                    ivApprovedAndReject.setBackgroundResource(R.drawable.primary_color_circle);
                    ivApprovedAndReject.setImageResource(R.drawable.ic_svg_user_check);
                    tvApprovedRejectStatusAndDate.setText("Accepted\n" + orderViewModel.getApprovedDate());
                    tvApprovedRejectStatusAndDate.setVisibility(View.VISIBLE);

                } else {

                    ivApprovedAndReject.setBackgroundResource(R.drawable.red_circle);
                    ivApprovedAndReject.setImageResource(R.drawable.ic_svg_user_uncheck);
                    tvApprovedRejectStatusAndDate.setText("Rejected\n" + orderViewModel.getApprovedDate());
                    tvApprovedRejectStatusAndDate.setVisibility(View.VISIBLE);

                }

//                viewBar1.setBackgroundColor(color);
                AnimationHelpers.animateTextViewWithAppendString(R.color.gray_color, color, viewBar1);
            }


            //Set Delivered Status
            if (!Utils.isEmptyOrNull(orderViewModel.getDeliveredDate())) {

                ivParcelDelivered.setBackgroundResource(R.drawable.primary_color_circle);
                tvParcelDeliveredStatusAndDate.setText("Delivered\n" + orderViewModel.getDeliveredDate());
                tvParcelDeliveredStatusAndDate.setVisibility(View.VISIBLE);

                AnimationHelpers.animateTextViewWithAppendString(R.color.gray_color, color, viewBar2);
            }

            //Set Received Status
            if (!Utils.isEmptyOrNull(orderViewModel.getReceivedDate())) {

                ivParcelReceived.setBackgroundResource(R.drawable.primary_color_circle);
                tvParcelReceivedStatusAndDate.setText("Received\n" + orderViewModel.getReceivedDate());
                tvParcelReceivedStatusAndDate.setVisibility(View.VISIBLE);

                AnimationHelpers.animateTextViewWithAppendString(R.color.gray_color, color, viewBar3);
            }


            //reset button if enabled
            titleBar.showRightIcon1AndSetListener(View.GONE, R.drawable.ic_svg_send, color, null);
            titleBar.showRightIcon2AndSetListener(View.GONE, R.drawable.ic_svg_user_check, color, null);

            //screen status
            if (screenStatus.getValue().equalsIgnoreCase(OrderScreenStatusEnum.DRAFT.getValue())) {

                if (Utils.isEmptyOrNull(orderViewModel.getSubmittedDate())) {

                    // show send button
                    titleBar.showRightIcon2AndSetListener(View.VISIBLE, R.drawable.ic_svg_send, color, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            orderViewModel.markSubmitted(new DatabaseTransactionInterface() {
                                @Override
                                public void onSuccessTransaction() {
                                    screenStatus = OrderScreenStatusEnum.PLACED;
                                    getDataAndPopulateOnUI();
                                }

                                @Override
                                public void onErrorTransaction() {

                                }
                            });
                        }
                    });
                }

            } else if (screenStatus.getValue().equalsIgnoreCase(OrderScreenStatusEnum.PLACED.getValue())) {

                if (!Utils.isEmptyOrNull(orderViewModel.getDeliveredDate()) && Utils.isEmptyOrNull(orderViewModel.getReceivedDate())) {
                    // show send button
                    titleBar.showRightIcon2AndSetListener(View.VISIBLE, R.drawable.ic_svg_parcel_received, color, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            orderViewModel.markReceived(new DatabaseTransactionInterface() {
                                @Override
                                public void onSuccessTransaction() {
                                    getDataAndPopulateOnUI();
                                }

                                @Override
                                public void onErrorTransaction() {

                                }
                            });
                        }
                    });

                }

            } else if (screenStatus.getValue().equalsIgnoreCase(OrderScreenStatusEnum.RECEIVED.getValue())) {

                if (Utils.isEmptyOrNull(orderViewModel.getApprovedDate())) {

                    // Show Reject Icon and Listener
                    titleBar.showRightIcon1AndSetListener(View.VISIBLE, R.drawable.ic_svg_user_uncheck, ContextCompat.getColor(activityReference, R.color.red), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            orderViewModel.markApproved(false, new DatabaseTransactionInterface() {
                                @Override
                                public void onSuccessTransaction() {
                                    getDataAndPopulateOnUI();
                                }

                                @Override
                                public void onErrorTransaction() {

                                }
                            });
                        }
                    });

                    // Show Accept Icon and Listener
                    titleBar.showRightIcon2AndSetListener(View.VISIBLE, R.drawable.ic_svg_user_check, color, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            orderViewModel.markApproved(true, new DatabaseTransactionInterface() {
                                @Override
                                public void onSuccessTransaction() {
                                    getDataAndPopulateOnUI();
                                }

                                @Override
                                public void onErrorTransaction() {
                                    DialogClass.createMessageDialog(activityReference, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }, activityReference.getString(R.string.credit_ins_err), activityReference.getString(R.string.credit_ins_titile_err)).show();
                                }
                            });
                        }
                    });

                } else if (Utils.isEmptyOrNull(orderViewModel.getDeliveredDate()) && orderViewModel.getApproved()) {


                    // Show Delivered button
                    titleBar.showRightIcon2AndSetListener(View.VISIBLE, R.drawable.ic_svg_parcel_delivered, color, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            orderViewModel.markDelivered(new DatabaseTransactionInterface() {
                                @Override
                                public void onSuccessTransaction() {
                                    getDataAndPopulateOnUI();
                                }

                                @Override
                                public void onErrorTransaction() {

                                }
                            });
                        }
                    });

                }


            }


            tvItemsCount.setText("Items(" + orderViewModel.getOrderDetailViewModels().size() + ")");
            tvTotalAmount.setText("Total: Rs. " + orderViewModel.getTotalPrice() + "/= ");


            //This work is for image download
            if(this.orderViewModel.getOrderDetailViewModels() != null){
                //This Work is For Image Show when Image Downloads
                for (int index = 0; index < this.orderViewModel.getOrderDetailViewModels().size(); index++) {
                    int finalIndex = index;
                    Observer<byte[]> imageObserver = new Observer<byte[]>() {
                        @Override
                        public void onChanged(@Nullable byte[] imageData) {
                            orderProductListAdapter.notifyItemChanged(finalIndex);
                        }
                    };

                    this.orderViewModel.getOrderDetailViewModels().get(index).updateImage();
                    this.orderViewModel.getOrderDetailViewModels().get(index).getProdImage().removeObserver(imageObserver);
                    this.orderViewModel.getOrderDetailViewModels().get(index).getProdImage().observe(activityReference, imageObserver);
                }
            }



            setupOrderScreenProductList(orderViewModel.getOrderDetailViewModels());

        }

    }

    private void setupOrderScreenProductList(ArrayList<OrderDetailViewModel> orderDetailViewModels) {


        orderProductListAdapter = new OrderProductListAdapter(activityReference, orderDetailViewModels, new DataRecyclerViewClickInterface<OrderDetailViewModel>() {
            @Override
            public void onClick(OrderDetailViewModel object, int position) {

            }

            @Override
            public void onEditClick(OrderDetailViewModel object, int position) {

            }
        }, new DataUpdateListenerInterface() {
            @Override
            public void updateUI() {
                if (isAdded() && isVisible())
                    getDataAndPopulateOnUI();
            }
        }, Utils.isEmptyOrNull(orderViewModel.getSubmittedDate()));

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(activityReference);
        rvOrders.setLayoutManager(layoutManager1);
        rvOrders.setAdapter(orderProductListAdapter);


        //Order Delete option only on draft
        if (screenStatus.getValue().equalsIgnoreCase(OrderScreenStatusEnum.DRAFT.getValue())){
            enableSwipeToDeleteAndUndo();
        }


        //This Work is For list refresh
        for (int index = 0; index < orderProductListAdapter.getItemCount(); index++) {
            int finalIndex = index;
            Observer<byte[]> imageObserver = new Observer<byte[]>() {
                @Override
                public void onChanged(@Nullable byte[] imageData) {
                    orderProductListAdapter.notifyItemChanged(finalIndex);
                }
            };

            orderProductListAdapter.getItem(index).getProdImage().removeObserver(imageObserver);
            orderProductListAdapter.getItem(index).getProdImage().observe(activityReference, imageObserver);
        }
        //This Work is For Image Show when Image Downloads


    }

    private void enableSwipeToDeleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(activityReference) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                    orderDetailRemove(viewHolder);

//                orderProductListAdapter.removeItem(position);


//                Snackbar snackbar = Snackbar
//                        .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
//                snackbar.setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        mAdapter.restoreItem(item, position);
//                        recyclerView.scrollToPosition(position);
//                    }
//                });
//
//                snackbar.setActionTextColor(Color.YELLOW);
//                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rvOrders);
    }

    private void orderDetailRemove(RecyclerView.ViewHolder viewHolder) {

        if(screenStatus.getValue().equalsIgnoreCase(OrderScreenStatusEnum.DRAFT.getValue())){
            final int position = viewHolder.getAdapterPosition();
            OrderDetailViewModel orderDetailViewModel = orderProductListAdapter.getItem(position);

            orderViewModel.removeOrderDetail(orderDetailViewModel, new DatabaseTransactionInterface() {
                @Override
                public void onSuccessTransaction() {

                    if(!orderViewModel.getOrder().isValid()){
                        onCustomBackPressed();
                    }else if(!orderViewModel.getOrder().getOrderDetails().isValid()){
                        onCustomBackPressed();
                    }else{
                        getDataAndPopulateOnUI();
                    }
                }

                @Override
                public void onErrorTransaction() {

                }
            });

        }else{
            Utils.showSnackBar(activityReference, getView(),
                    activityReference.getString(R.string.order_catt_be_deleted), ContextCompat.getColor(activityReference, R.color.red));        }

    }


    public void setOrderViewModelObject(OrderViewModel orderViewModelObject) {
        this.orderViewModelObject = orderViewModelObject;
    }


    public void setScreenStatus(OrderScreenStatusEnum screenStatus) {
        this.screenStatus = screenStatus;
    }


    public void setShowSearchFilter(boolean showSearchFilter) {
        this.showSearchFilter = showSearchFilter;
    }


    public void setBackPressListener(OnBackPressInterface onBackPressInterface) {
        this.onBackPressInterface = onBackPressInterface;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


//    private void populateDataOnUI() {
//
//        if(profileViewModal != null){
//
//
//            //Name
//            if(!Utils.isEmptyOrNull(profileViewModal.getName())){
//                tvCompanyName.setText(profileViewModal.getName());
//            }
//
//            if(profileViewModal.getCoverImage() != null && profileViewModal.getCoverImage().getData() != null && profileViewModal.getCoverImage().getData().length > 0){
//                Bitmap imageBitmap = BitmapFactory.decodeByteArray(profileViewModal.getCoverImage().getData(), 0, profileViewModal.getCoverImage().getData().length);
//                ivCompanyImage.setImageBitmap(imageBitmap);
//            }else if(profileViewModal.getLogo() != null && profileViewModal.getLogo().getData() != null && profileViewModal.getLogo().getData().length > 0){
//                Bitmap imageBitmap = BitmapFactory.decodeByteArray(profileViewModal.getLogo().getData(), 0, profileViewModal.getLogo().getData().length);
//                ivCompanyImage.setImageBitmap(imageBitmap);
//            }
//        }
//    }


//
//    @OnClick({R.id.llLogout, R.id.tvEditProfile, R.id.tvCompanyName})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.tvEditProfile:
//            case R.id.tvCompanyName:
//
//                CompanyDetailFragment companyDetailFragment = new CompanyDetailFragment();
//                companyDetailFragment.setType(ProfileViewEnum.LOGGED_IN_COMPANY_PROFILE);
//                if(preferenceHelper != null && !Utils.isEmptyOrNull(preferenceHelper.getProfileId())){
//                    companyDetailFragment.setProfileId(preferenceHelper.getProfileId());
//                }
//                activityReference.addSupportFragment(companyDetailFragment, AppConstant.TRANSITION_TYPES.SLIDE);
//                break;
//            case R.id.llLogout:
//
//                DialogClass.createMessageDialog(activityReference, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        logoutFromServer();
//                    }
//                }, activityReference.getString(R.string.are_you_sure_you_want_to_logout), activityReference.getString(R.string.alert)).show();
//
//                break;
//        }
//    }


}