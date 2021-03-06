/*
    The purpose of this class file is to format the appearence and look and feel of the thumbnail
    items present in the filter recycler view list
 */
package com.unimelb.projectinsta.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ThumbnailFormatter extends RecyclerView.ItemDecoration {
    private int space;

    public ThumbnailFormatter(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            outRect.left = space;
            outRect.right = 0;
        }else{
            outRect.right = space;
            outRect.left = 0;
        }
    }
}