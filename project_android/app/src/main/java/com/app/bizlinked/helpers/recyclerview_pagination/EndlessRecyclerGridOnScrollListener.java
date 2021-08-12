package com.app.bizlinked.helpers.recyclerview_pagination;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerGridOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndlessRecyclerGridOnScrollListener.class.getSimpleName();

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 4;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 0;

    private GridLayoutManager mLinearLayoutManager;

    public EndlessRecyclerGridOnScrollListener(GridLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading
                && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}