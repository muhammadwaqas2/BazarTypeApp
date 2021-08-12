package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.ImageView;

import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.enums.LinkStatusEnum;

import java.io.File;

public class LinkViewModel extends BaseViewModal {

    private MutableLiveData<byte[]> image;
    private String name;
    private String relation;
    private String city;
    private String market;
    private String shop;
    private String linkedCompanyID;
    private String status;
    private Link link;

    public MutableLiveData<byte[]> getImage() {
        if (image == null) {
            image = new MutableLiveData<>();
        }
        return image;
    }


    public void setImage(MutableLiveData<byte[]> image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public void setLinkedCompanyID(String linkedCompanyID) {
        this.linkedCompanyID = linkedCompanyID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getRelation() {
        return relation;
    }

    public String getCity() {
        return city;
    }

    public String getMarket() {
        return market;
    }

    public String getShop() {
        return shop;
    }

    public String getLinkedCompanyID() {
        return linkedCompanyID;
    }

    public String getStatus() {
        return status;
    }

    public Link getLink() {
        return link;
    }

    public void init(Link link) {
        this.link = link;
        this.populateViewModel();
    }

    public void populateViewModel(){

        if(link != null && link.getLinkedCompany() != null){

            //Image
            if(link.getLinkedCompany().getLogo() != null
                    && link.getLinkedCompany().getLogo().getData() != null
                    && link.getLinkedCompany().getLogo().getData().length > 0){

//                this.image = link.getLinkedCompany().getLogo().getData();
                getImage().setValue(link.getLinkedCompany().getLogo().getData());
            }

            //Name
            if(!Utils.isEmptyOrNull(link.getLinkedCompany().getName())){
                this.name = link.getLinkedCompany().getName();
            }

            //City
            if(link.getLinkedCompany().getCity() != null && !Utils.isEmptyOrNull(link.getLinkedCompany().getCity().getName())){
                this.city = link.getLinkedCompany().getCity().getName();
            }

            //Market Name
            if(!Utils.isEmptyOrNull(link.getLinkedCompany().getMarketName())){
                this.city = link.getLinkedCompany().getMarketName();
            }

            //Shop
            if(!Utils.isEmptyOrNull(link.getLinkedCompany().getSuitNumber())){
                this.shop = link.getLinkedCompany().getSuitNumber();
            }

            //LinkedCompanyID
            if(!Utils.isEmptyOrNull(link.getLinkedCompany().getId())){
                this.linkedCompanyID = link.getLinkedCompany().getId();
            }

        }

        //relation
        if(!Utils.isEmptyOrNull(link.getLinkedCompanyRelation())){
            this.relation = link.getLinkedCompanyRelation();
        }

        //Status
        if(!Utils.isEmptyOrNull(link.getStatus())){
            this.status = link.getStatus();
        }
    }

    public ProfileViewModal getProfileViewModel(){

        if(link != null && link.getLinkedCompany() != null){
            ProfileViewModal profileViewModal = new ProfileViewModal();
            profileViewModal.init(link.getLinkedCompany());
            return profileViewModal;
        }
        return null;

    }
}
