package com.app.bizlinked.helpers.recyclerview_pagination;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener
{
    private static final String TAG = PaginationScrollListener.class.getSimpleName();
    private LinearLayoutManager layoutManager;

    protected PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /*
     Method gets callback when user scroll the search list
     */
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (!isLoadingData() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                Log.i(TAG, "Loading more items");
                loadMoreItems();
            }
        }

    }
    protected abstract void loadMoreItems();

    //optional
    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoadingData();
}