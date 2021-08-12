package com.app.bizlinked.models.viewmodel;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.listener.custom.DataUpdateListenerInterface;
import com.app.bizlinked.models.db.Order;
import com.app.bizlinked.models.db.OrderStatus;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.enums.OrderScreenStatusEnum;

import java.util.ArrayList;

import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.app.bizlinked.models.enums.OrderScreenStatusEnum.DRAFT;
import static com.app.bizlinked.models.enums.OrderScreenStatusEnum.PLACED;

public class OrderListViewModel extends BaseViewModal {

    private RealmResults<OrderStatus> resultOrderStatuses;
    private ArrayList<OrderStatus> statuses = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();
    //    private var ordersToken: NotificationToken?
    private ArrayList<OrderViewModel> pendingOrderViewModels = new ArrayList<>();
    private ArrayList<OrderViewModel> receivedOrderViewModels = new ArrayList<>();


    public RealmResults<OrderStatus> getResultOrderStatuses() {
        return resultOrderStatuses;
    }

    public ArrayList<OrderStatus> getStatuses() {
        return statuses;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public ArrayList<OrderViewModel> getPendingOrderViewModels() {
        return pendingOrderViewModels;
    }

    public ArrayList<OrderViewModel> getReceivedOrderViewModels() {
        return receivedOrderViewModels;
    }

    public void init(OrderScreenStatusEnum status, String companyId, DataUpdateListenerInterface dataUpdateListenerInterface) {

        if (status.getValue().equalsIgnoreCase(OrderScreenStatusEnum.RECEIVED.getValue())) {

            if (!Utils.isEmptyOrNull(companyId)) {
                resultOrderStatuses = AppDBHelper.getRealmInstance().where(OrderStatus.class)
                        .beginGroup()
                        .equalTo("order.supplierCompanyID", BizLinkedApplicationClass.getPreference().getProfileId())
                        .and()
                        .isNotNull("submitted")
                        .and()
                        .equalTo("order.customerCompanyID", companyId)
                        .endGroup()
                        .sort("baseEntity.lastModifiedDate", Sort.DESCENDING)
                        .findAll();


            } else {
                resultOrderStatuses = AppDBHelper.getRealmInstance().where(OrderStatus.class)
                        .beginGroup()
                        .equalTo("order.supplierCompanyID", BizLinkedApplicationClass.getPreference().getProfileId())
                        .and()
                        .isNotNull("submitted")
                        .endGroup()
                        .sort("baseEntity.lastModifiedDate", Sort.DESCENDING)
                        .findAll();
            }

        } else if (status.getValue().equalsIgnoreCase(OrderScreenStatusEnum.PLACED.getValue())) {
            resultOrderStatuses = AppDBHelper.getRealmInstance().where(OrderStatus.class)
                    .beginGroup()
                    .equalTo("order.customerCompanyID", BizLinkedApplicationClass.getPreference().getProfileId())
                    .and()
                    .isNotNull("submitted")
                    .endGroup()
                    .sort("baseEntity.lastModifiedDate", Sort.DESCENDING)
                    .findAll();
        } else if (status.getValue().equalsIgnoreCase(DRAFT.getValue())) {
            resultOrderStatuses = AppDBHelper.getRealmInstance().where(OrderStatus.class)
                    .beginGroup()
                    .equalTo("order.customerCompanyID", BizLinkedApplicationClass.getPreference().getProfileId())
                    .and()
                    .isNull("submitted")
                    .endGroup()
                    .sort("baseEntity.lastModifiedDate", Sort.DESCENDING)
                    .findAll();
        }


        //Clear all data
        statuses.clear();
        orders.clear();
        pendingOrderViewModels.clear();
        receivedOrderViewModels.clear();

        //Set Data
        setOrderStatusData(status, companyId, resultOrderStatuses);

        //Set Listener
        resultOrderStatuses.removeAllChangeListeners();
        resultOrderStatuses.addChangeListener(resultOrderStatuses -> {

            setOrderStatusData(status, companyId, resultOrderStatuses);

            //Call
            if (dataUpdateListenerInterface != null)
                dataUpdateListenerInterface.updateUI();

        });

    }

    private void setOrderStatusData(OrderScreenStatusEnum status, String companyId, RealmResults<OrderStatus> resultOrderStatuses) {


        for (OrderStatus orderStatus : resultOrderStatuses) {
            statuses.add(orderStatus);
            orders.add(orderStatus.getOrder());
        }


        if (resultOrderStatuses != null && resultOrderStatuses.size() > 0) {

            //Pending List Section
            RealmResults<OrderStatus> pending = resultOrderStatuses.where()
                    .beginGroup()
                        .isNull("received")
                        .and()
                        .beginGroup()
                            .notEqualTo("isApproved", false)
                            .or()
                            .isNull("isApproved")
                        .endGroup()
                    .endGroup().findAll();

            for (OrderStatus orderStatus : pending) {

                OrderViewModel orderViewModel = new OrderViewModel();
                orderViewModel.init(orderStatus.getOrder(), orderStatus, status);
                pendingOrderViewModels.add(orderViewModel);
            }


            //Received/Processed List Section

            RealmResults<OrderStatus> received = resultOrderStatuses.where()
                    .beginGroup()
                    .isNotNull("received")
                    .or()
                    .equalTo("isApproved", false)
                    .endGroup().findAll();


            for (OrderStatus orderStatus : received) {

                OrderViewModel orderViewModel = new OrderViewModel();
                orderViewModel.init(orderStatus.getOrder(), orderStatus, status);
                receivedOrderViewModels.add(orderViewModel);
            }

        }


//        ordersToken = resultOrderStatuses ?.observe({[weak self](change) in self ?.statuses = self ?.resultOrderStatuses ?.compactMap({$0})
//        self ?.orders = self ?.resultOrderStatuses ?.compactMap({$0.order})
//        let pending = self ?.resultOrderStatuses ?.filter("received == nil && isApproved != false")
//        self ?.pendingOrderViewModels = pending ?.compactMap({OrderViewModel(order:
//        $0.order, orderStatus:$0, screenStatus:status)}) ?? []
//        let received = self ?.resultOrderStatuses ?.filter("received != nil || isApproved == false")
//        self ?.receivedOrderViewModels = received ?.compactMap({OrderViewModel(order:
//        $0.order, orderStatus:$0, screenStatus:status)}) ?? []
//        self ?.delegate ?.updateUI()
//        })


    }


    public void syncOrders() {
        SyncManager.getInstance().addEntityToQueue(EntityEnum.Order);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.OrderStatus);
    }
}
