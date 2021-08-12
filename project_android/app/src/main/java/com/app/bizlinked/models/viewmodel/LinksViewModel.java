package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.Toast;

import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.realm.RealmLiveData;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.enums.LinkRelationEnum;
import com.app.bizlinked.models.enums.LinkStatusEnum;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class LinksViewModel extends ViewModel {

    private MutableLiveData<RealmResults<Link>> linkResults;
    private ArrayList<Link> links = new ArrayList<>();
    private ArrayList<LinkViewModel> linkViewModels = new ArrayList<>();


    public MutableLiveData<RealmResults<Link>> getLinkResults(LinkRelationEnum relation, LinkStatusEnum status) {
        if (linkResults == null) {
            linkResults = new MutableLiveData<>();
//            getAllLinksFromDB(relation, status);

            //getAllLinksFromDB(relation, status).removeAllChangeListeners();
            linkResults.setValue(getAllLinksFromDB(relation, status));

            getAllLinksFromDB(relation, status).addChangeListener(new RealmChangeListener<RealmResults<Link>>() {
                @Override
                public void onChange(RealmResults<Link> links) {
                    linkResults.setValue(links);
                }
            });

            //linkResults.setValue(getAllLinksFromDB(relation, status));
        }
        return linkResults;
    }

//    public void init(LinkRelationEnum relation, LinkStatusEnum status) {
//
//        linkResults = new MutableLiveData<>();
//        linkResults.setValue(getAllLinksFromDB(relation, status));
//
//        setLinksDataForUI(linkResults.getValue());
//
//        //TODO: Observer set
//
////        linkToken = linkResults?.observe({ [weak self](changes) in
////            self?.linkViewModels = self?.linkResults?.compactMap({LinkViewModel(link: $0)})
////            self?.links = self?.linkResults?.compactMap({$0})
////            self?.delegate?.updateUI()
////        })
//    }

    public RealmResults<Link> getAllLinksFromDB(LinkRelationEnum relation, LinkStatusEnum status) {
        return AppDBHelper.getRealmInstance().where(Link.class)
                .equalTo("linkedCompanyRelation", relation.getValue())
                .and()
                .equalTo("status", status.getValue())
                .findAll();
    }

    public ArrayList<LinkViewModel> getLinksDataForUI(RealmResults<Link> linkResults) {

        linkViewModels.clear();

        for (Link link : linkResults) {
            LinkViewModel linkViewModel = new LinkViewModel();
            linkViewModel.init(link);
            linkViewModels.add(linkViewModel);
        }

        return linkViewModels;
    }


    public void syncLinks(){
        SyncManager.getInstance().addEntityToQueue(EntityEnum.Link);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);
    }


    public void updateStatus(LinkStatusEnum status, LinkViewModel linkViewModel){

        Link link = linkViewModel.getLink();
        Log.d("link", link.getLinkedCompany().getName());
        AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                link.setStatus(status.getValue());
                link.setDirty(true);
                link.setLastModifiedDate(new Date());
            }
        });
        syncLinks();
    }
}
