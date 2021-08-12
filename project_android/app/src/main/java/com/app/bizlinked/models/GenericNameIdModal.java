package com.app.bizlinked.models;

public class GenericNameIdModal {

    private String id;
    private String title;
    private boolean isSelect = false;

    public GenericNameIdModal(String id, String title) {
        this.id = id;
        this.title = title;
        this.isSelect = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
