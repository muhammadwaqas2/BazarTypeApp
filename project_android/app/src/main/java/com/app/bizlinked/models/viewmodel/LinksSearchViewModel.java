package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.Toast;

import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.models.db.Image;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.Profile;
import com.app.bizlinked.models.enums.LinkStatusEnum;
import com.app.bizlinked.models.server_response.LinkSearchListResponse;
import com.app.bizlinked.models.sync_response.ProfileSyncResponse;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Case;
import io.realm.RealmResults;

public class LinksSearchViewModel extends ViewModel {

    private Integer page = 0;
    private Integer totalPages = 0;
    private BaseActivity activityReference = null;

    private MutableLiveData<ArrayList<LinkViewModel>> linkedCompanies;
    private MutableLiveData<ArrayList<ProfileViewModal>> unlinkedCompanies;


    public BaseActivity getActivityReference() {
        return activityReference;
    }

    public void setActivityReference(BaseActivity activityReference) {
        this.activityReference = activityReference;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public MutableLiveData<ArrayList<LinkViewModel>> getLinkedCompanies() {
        if (linkedCompanies == null) {
            linkedCompanies = new MutableLiveData<>();
        }
        return linkedCompanies;
    }


//    public void setLinkedCompanies(ArrayList<LinkViewModel> linkedCompanies) {
//        this.linkedCompanies = linkedCompanies;
//    }
    public MutableLiveData<ArrayList<ProfileViewModal>> getUnlinkedCompanies() {
        if (unlinkedCompanies == null) {
            unlinkedCompanies = new MutableLiveData<>();
        }
        return unlinkedCompanies;
    }


//    public void setUnlinkedCompanies(ArrayList<ProfileViewModal> unlinkedCompanies) {
//        this.unlinkedCompanies = unlinkedCompanies;
//    }



    public void search(String keyword) {

        if (!Utils.isEmptyOrNull(keyword)) {
            this.page = 0;
            searchLocal(keyword);
            searchServer(keyword);
        }
    }

    public void searchLocal(String keyword) {

        activityReference.onLoadingStarted();

        RealmResults<Link> companyResults = AppDBHelper.getRealmInstance()
                .where(Link.class)
                    .beginGroup()
                        .contains("linkedCompany.name", keyword, Case.INSENSITIVE)
                        .and()
                            .beginGroup()
                                .equalTo("status", LinkStatusEnum.LINKED.getValue())
                                .or()
                                .equalTo("status", LinkStatusEnum.SENT.getValue())
                                .or()
                                .equalTo("status", LinkStatusEnum.RECEIVED.getValue())
                            .endGroup()
                    .endGroup()
                .findAll();


        addInLinkedSection(companyResults);

        activityReference.onLoadingFinished();
    }

    private void addInLinkedSection(RealmResults<Link> companyResults){

        ArrayList<LinkViewModel> linked = new ArrayList<>();

        for (int index = 0; index < companyResults.size(); index++) {

            Link company = companyResults.get(index);
            LinkViewModel profileViewModel = new LinkViewModel();
            profileViewModel.init(company);
            linked.add(profileViewModel);
        }

        if (linked.size() > 0) {
            getLinkedCompanies().setValue(linked);
        }
    }

    public void addLinkedCompany(Link linkObject){

        ArrayList<LinkViewModel> linked = new ArrayList<>();

        LinkViewModel profileViewModel = new LinkViewModel();
        profileViewModel.init(linkObject);
        linked.add(profileViewModel);

        if (linked.size() > 0) {
            getLinkedCompanies().setValue(linked);
        }
    }

    public void searchServer(String keyword) {



        HashMap<String, Object> params = new HashMap<>();

        params.put("term", keyword);
        params.put("page", page);
        params.put("size", AppConstant.CONFIGURATION.PAGE_SIZE);

        WebApiRequest.getInstance(activityReference, true).getLinkSearchFromServer(AppConstant.ServerAPICalls.linksSearchURL, params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if(response != null && response.isJsonObject()){

                    JsonObject responseObject = response.getAsJsonObject();
                    Gson gson = new Gson();

                    //Parsing Gson
                    LinkSearchListResponse linkSearchSyncResponse = gson.fromJson(responseObject, new TypeToken<LinkSearchListResponse>() {}.getType());

                    ArrayList<ProfileViewModal> unlinked = new ArrayList<>();

                    for (ProfileSyncResponse link: linkSearchSyncResponse.getContent()) {

                        Profile profile = new Profile();
                        profile.setId(link.getId());
                        profile.decode(link);

                        if(link.getLogoImage() != null){
                            Image logoImage = new Image();
                            logoImage.setId(link.getLogoImage().getId());
                            logoImage.decode(link.getLogoImage());
                            profile.setLogo(logoImage);
                        }

                        if(link.getCoverImage() != null){
                            Image coverImage = new Image();
                            coverImage.setId(link.getCoverImage().getId());
                            coverImage.decode(link.getCoverImage());
                            profile.setCoverImage(coverImage);
                        }

                        ProfileViewModal profileViewModal = new ProfileViewModal();
                        profileViewModal.init(profile);

                        //Unlink list Add
                        unlinked.add(profileViewModal);
                    }

                    getUnlinkedCompanies().setValue(unlinked);
                    totalPages = linkSearchSyncResponse.getTotalPages();
                }
            }

            @Override
            public void onError(String errorResponse) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }
}