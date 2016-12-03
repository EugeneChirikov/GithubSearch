package ru.farpost.githubsearch.ui.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by eugene on 12/2/16.
 */

public class BottomScrollIndicator {

    public interface OnBottomReachedListener {
        void onBottomReached();
    }

    private static final int ITEMS_BEFORE_BOTTOM = 2;
    private boolean mBottomReached = false;

    public BottomScrollIndicator(RecyclerView recyclerView, final OnBottomReachedListener listener) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (!mBottomReached && adapter != null) {
                    View bottomView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    if (bottomView != null) {
                        int bottomItemPosition = recyclerView.getChildAdapterPosition(bottomView);
                        int adapterLastItemPosition = adapter.getItemCount() - 1;
                        if (bottomItemPosition >= adapterLastItemPosition - ITEMS_BEFORE_BOTTOM) {
                            mBottomReached = true;
                            listener.onBottomReached();
                        }
                    }
                }
            }
        });
    }

    public void reset() {
        mBottomReached = false;
    }
}
