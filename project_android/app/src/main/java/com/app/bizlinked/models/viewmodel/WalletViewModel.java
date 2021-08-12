package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.realm.RealmLiveData;
import com.app.bizlinked.models.db.BusinessCategory;
import com.app.bizlinked.models.db.CompanyPackage;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;

import io.realm.RealmResults;

public class WalletViewModel extends ViewModel {

    private RealmResults<CompanyPackage> companyPackageResults;
    private MutableLiveData<Integer> credit = null;
//    private var companyPackageToken: NotificationToken?
//    private var companyPackages: [CompanyPackage]?
//    var companyPackageViewModels: [CompanyPackageViewModel]?
//    var credit: Int?
//    var delegate: BaseViewModelProtocol?



    public RealmLiveData<CompanyPackage> getAllCompanyPackages(){
        companyPackageResults =  getAllCompanyPackagesFromDB();
        return new RealmLiveData<>(companyPackageResults);
        // Async runs the fetch off the main thread, and returns
        // results as LiveData back on the main.

    }


    public MutableLiveData<Integer> getCredit() {
        if (credit == null) {
            credit = new MutableLiveData<>();
        }
        return credit;
    }


    public RealmResults<CompanyPackage> getAllCompanyPackagesFromDB(){
        return AppDBHelper.getRealmInstance().where(CompanyPackage.class)
                .beginGroup()
                .equalTo("isActive", true)
                .and()
                .equalTo("baseEntity.isDeleted", false)
                .endGroup()
                .findAll();

    }
}