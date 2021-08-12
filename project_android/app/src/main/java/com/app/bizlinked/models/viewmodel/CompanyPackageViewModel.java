package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.app.bizlinked.models.db.CompanyPackage;

public class CompanyPackageViewModel extends ViewModel {

    private Integer orderQuantity = 0;
    private Double price = 0.0;
    private String name;
    private CompanyPackage companyPackage;


    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public Double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public CompanyPackage getCompanyPackage() {
        return companyPackage;
    }

    public void init(CompanyPackage selectedCompanyPackageObj) {

        this.companyPackage = selectedCompanyPackageObj;
        this.orderQuantity = companyPackage.getPackage().getOrderQuantity();
        this.price = companyPackage.getPackage().getPrice();
        this.name = companyPackage.getPackage().getName();
    }

}