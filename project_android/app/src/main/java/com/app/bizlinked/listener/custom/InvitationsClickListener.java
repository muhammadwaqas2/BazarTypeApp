package com.app.bizlinked.listener.custom;


public interface InvitationsClickListener<T> {
    void onClick(T object, int position);
    void onEditClick(T object, int position);
    void onAccept(T object, int position);
    void onReject(T object, int position);
}