package com.app.bizlinked.listener.custom;

public interface DataRecyclerViewClickInterface<T> {
  void onClick(T object, int position);
  void onEditClick(T object, int position);
}