package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.models.db.Order;
import com.app.bizlinked.models.db.OrderDetail;
import com.app.bizlinked.models.db.OrderStatus;
import com.app.bizlinked.models.db.Profile;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.enums.OrderScreenStatusEnum;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class OrderViewModel extends BaseViewModal {

    private String companyName;
    private String address;
    private String status;
    private String date;
    private byte[] image;
    //var image: UIImage?
    private Order order;
    private OrderStatus orderStatus;
    private RealmResults<Order> resultOrder;
    private RealmResults<OrderStatus> resultOrderStatus;
    //private var orderToken: NotificationToken?
    //private var orderStatusToken: NotificationToken?
    OrderScreenStatusEnum screenStatus;
    private String submittedDate;
    private String approvedDate;
    private String deliveredDate;
    private String receivedDate;
    private Boolean isApproved;
    ArrayList<OrderDetailViewModel> orderDetailViewModels = new ArrayList<>();
    //var delegate: BaseViewModelProtocol?
    private String orderId;
    private Double totalPrice = 0.0;

    private BaseActivity activityReference = null;

    public BaseActivity getActivityReference() {
        return activityReference;
    }

    public void setActivityReference(BaseActivity activityReference) {
        this.activityReference = activityReference;
    }




    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public byte[] getImage() {
        return image;
    }

    public Order getOrder() {
        return order;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public RealmResults<Order> getResultOrder() {
        return resultOrder;
    }

    public RealmResults<OrderStatus> getResultOrderStatus() {
        return resultOrderStatus;
    }

    public OrderScreenStatusEnum getScreenStatus() {
        return screenStatus;
    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public String getDeliveredDate() {
        return deliveredDate;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public ArrayList<OrderDetailViewModel> getOrderDetailViewModels() {
        return orderDetailViewModels;
    }

    public String getOrderId() {
        return orderId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void init(Order order, OrderStatus orderStatus, OrderScreenStatusEnum screenStatus) {
        this.order = order;
        this.orderStatus = orderStatus;
        this.screenStatus = screenStatus;
        populateViewModel();
    }



    public void init(String orderId, OrderScreenStatusEnum screenStatus) {

        this.resultOrder = AppDBHelper.getRealmInstance().where(Order.class).equalTo("id", orderId).findAll();
        if(resultOrder != null && resultOrder.size() > 0){

            this.order = resultOrder.first();
            this.resultOrderStatus = AppDBHelper.getRealmInstance().where(OrderStatus.class).equalTo("order.id", this.order.getId()).findAll();

            if(resultOrderStatus != null && resultOrderStatus.size() > 0){
                this.orderStatus = resultOrderStatus.first();
            }
            this.screenStatus = screenStatus;

            populateViewModel();

        }
//            orderToken = resultOrder?.observe({ [weak self](change) in
//            switch change{
//                case .update:
//                guard let ord = self?.resultOrder?.first, let stats = self?.resultOrderStatus?.first else{
//                    self?.orderDetailViewModels.removeAll()
//                    self?.delegate?.updateUI()
//                    return
//                }
//                self?.order = ord
//                self?.orderStatus = stats
//                self?.populateViewModel()
//                self?.delegate?.updateUI()
//                default:
//                    print("None")
//            }
//
//        })
//            orderStatusToken = resultOrderStatus?.observe({ [weak self](change) in
//            switch change{
//                case .update:
//                guard let ord = self?.resultOrder?.first, let stats = self?.resultOrderStatus?.first else{
////                    self?.orderDetailViewModels.removeAll()
////                    self?.delegate?.updateUI()
//                    return
//                }
//                self?.order = ord
//                self?.orderStatus = stats
//                self?.populateViewModel()
//                self?.delegate?.updateUI()
//                default:
//                    print("None")
//            }
//        })
    }

    public void populateViewModel() {

        //orderId
        this.orderId = this.order.getId();


        if (screenStatus != null) {

            String id = null;

            //For Received case
            if(screenStatus.getValue().equalsIgnoreCase(OrderScreenStatusEnum.RECEIVED.getValue())){
                id = order.getCustomerCompanyID();
            }else if(screenStatus.getValue().equalsIgnoreCase(OrderScreenStatusEnum.PLACED.getValue()) || screenStatus.getValue().equalsIgnoreCase(OrderScreenStatusEnum.DRAFT.getValue())){
                //For Placed and Draft Case
                id = order.getSupplierCompanyID();
            }


            Profile company = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Profile.class, id);

            if (company != null) {

                //companyName
                this.companyName = company.getName();

                StringBuilder address = new StringBuilder();
                if (!Utils.isEmptyOrNull(company.getSuitNumber())) {
                    if (address.length() > 0)
                        address.append(",");
                    address.append(company.getSuitNumber());
                }

                if (!Utils.isEmptyOrNull(company.getMarketName())) {
                    if (address.length() > 0)
                        address.append(",");
                    address.append(company.getMarketName());
                }

                if (!Utils.isEmptyOrNull(company.getStreetAddress())) {
                    if (address.length() > 0)
                        address.append(",");
                    address.append(company.getStreetAddress());
                }

                //address
                this.address = address.toString().trim();

                if (company.getLogo() != null && company.getLogo().getData() != null && company.getLogo().getData().length > 0) {
                    this.image = company.getLogo().getData();
                }

            }

            if (this.orderStatus.getReceived() == null) {

                if (this.orderStatus.getApproved() == null) {
                    this.status = "Pending";
                } else {

                    if(!orderStatus.getApproved()){
                        this.status = "Rejected";
                    }else{
                        this.status = "Pending";
                    }
                }

            } else {
                this.status = "Received";
            }


            SimpleDateFormat dateFormatterPrint = new SimpleDateFormat("MMM dd,yyyy", Locale.getDefault());

            //submittedDate
            //date
            if(this.orderStatus.getSubmitted() != null){
                this.submittedDate = dateFormatterPrint.format(this.orderStatus.getSubmitted());
                this.date = dateFormatterPrint.format(this.orderStatus.getSubmitted());
            }else{
                //date
                this.date = dateFormatterPrint.format(this.order.getInitiationDate());
            }

            //approvedDate
            if(this.orderStatus.getApprovalDate() != null){
                this.approvedDate = dateFormatterPrint.format(this.orderStatus.getApprovalDate());
            }

            //deliveredDate
            if(this.orderStatus.getDelivered() != null){
                this.deliveredDate = dateFormatterPrint.format(this.orderStatus.getDelivered());
            }

            //receivedDate
            if(this.orderStatus.getReceived() != null){
                this.receivedDate = dateFormatterPrint.format(this.orderStatus.getReceived());
            }

            //isApproved
            if(this.orderStatus.getApproved() != null){
                this.isApproved = this.orderStatus.getApproved();
            }

            //orderDetailViewModels
            this.orderDetailViewModels.clear();
            double sum = 0.0;
            for (OrderDetail detail: this.order.getOrderDetails()) {
                OrderDetailViewModel orderDetailVM = new OrderDetailViewModel();
                orderDetailVM.init(detail);

                this.orderDetailViewModels.add(orderDetailVM);
                sum += detail.getPrice() * detail.getQuantity();
            }

            //totalPrice
            this.totalPrice = sum;

        }
    }


    public void markSubmitted(DatabaseTransactionInterface databaseTransactionInterface){


        try {
            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Date date = new Date();

                    submittedDate = DateFormatHelper.convertDateToServerFormattedString(date);
                    getOrderStatus().setSubmitted(date);
                    getOrderStatus().setDirty(true);
                    getOrderStatus().setLastModifiedDate(date);
                    getOrder().setDirty(true);
                    getOrder().setLastModifiedDate(date);
                }
            });

            screenStatus = OrderScreenStatusEnum.PLACED;

            //Sync Orders
            syncOrders();

            //Success callback fire
            databaseTransactionInterface.onSuccessTransaction();
        }catch (Exception e){
            e.printStackTrace();
            databaseTransactionInterface.onErrorTransaction();
        }
    }

    public void markDelivered(DatabaseTransactionInterface databaseTransactionInterface){


        try {
            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Date date = new Date();

                    deliveredDate = DateFormatHelper.convertDateToServerFormattedString(date);
                    getOrderStatus().setDelivered(date);
                    getOrderStatus().setDirty(true);
                    getOrderStatus().setLastModifiedDate(date);

                }
            });

            //Sync Orders
            SyncManager.getInstance().addEntityToQueue(EntityEnum.OrderStatus);

            //Success callback fire
            databaseTransactionInterface.onSuccessTransaction();
        }catch (Exception e){
            e.printStackTrace();
            databaseTransactionInterface.onErrorTransaction();
        }
    }

    public void markReceived(DatabaseTransactionInterface databaseTransactionInterface){


        try {
            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Date date = new Date();

                    receivedDate = DateFormatHelper.convertDateToServerFormattedString(date);
                    getOrderStatus().setReceived(date);
                    getOrderStatus().setDirty(true);
                    getOrderStatus().setLastModifiedDate(date);

                }
            });

            //Sync Orders
            SyncManager.getInstance().addEntityToQueue(EntityEnum.OrderStatus);

            //Success callback fire
            databaseTransactionInterface.onSuccessTransaction();
        }catch (Exception e){
            e.printStackTrace();
            databaseTransactionInterface.onErrorTransaction();
        }
    }

    public void syncApproved(Boolean isApproved, DatabaseTransactionInterface databaseTransactionInterface){


        try {
            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Date date = new Date();

                    approvedDate = DateFormatHelper.convertDateToServerFormattedString(date);
                    getOrderStatus().setApprovalDate(date);
                    getOrderStatus().setApproved(isApproved);

                    getOrderStatus().setDirty(true);
                    getOrderStatus().setLastModifiedDate(date);
                }
            });

            //Sync Orders
            SyncManager.getInstance().addEntityToQueue(EntityEnum.OrderStatus);

            //Success callback fire
            databaseTransactionInterface.onSuccessTransaction();
        }catch (Exception e){
            e.printStackTrace();
            databaseTransactionInterface.onErrorTransaction();
        }
    }


    public void removeOrderDetail(OrderDetailViewModel orderDetailViewModel, DatabaseTransactionInterface databaseTransactionInterface){

        try {
            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    order.getOrderDetails().remove(orderDetailViewModel.getOrderDetail());

                    if(getOrder().getOrderDetails().size() == 0){
//                        realm.delete(orderDetailViewModel.getOrderDetail().getClass());
//                        realm.delete(orderStatus.getClass());
//                        realm.delete(order.getClass());

                        orderDetailViewModel.getOrderDetail().deleteFromRealm();
                        orderStatus.deleteFromRealm();
                        order.deleteFromRealm();

                    }else{
                        orderDetailViewModel.getOrderDetail().deleteFromRealm();
                    }
                }
            });

            //Success callback fire
            databaseTransactionInterface.onSuccessTransaction();
        }catch (Exception e){
            e.printStackTrace();
            databaseTransactionInterface.onErrorTransaction();
        }
    }


    public void markApproved(Boolean isApproved, DatabaseTransactionInterface databaseTransactionInterface){

        if(isApproved == null || !isApproved){
            syncApproved(isApproved, databaseTransactionInterface);
            return;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId", getOrder().getId());

        WebApiRequest.getInstance(activityReference, true).markOrderApproveOrRejectFromServer(AppConstant.ServerAPICalls.orderApproveURL, params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {


                syncApproved(isApproved, databaseTransactionInterface);

                if(response != null && response.isJsonObject()){
                    JsonObject responseObject = response.getAsJsonObject();
                }
            }

            @Override
            public void onError(String errorResponse) {

                databaseTransactionInterface.onErrorTransaction();

            }

            @Override
            public void onNoNetwork() {
            }
        });
    }



    public void syncOrders(){
        SyncManager.getInstance().addEntityToQueue(EntityEnum.Order);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.OrderStatus);

    }

}
