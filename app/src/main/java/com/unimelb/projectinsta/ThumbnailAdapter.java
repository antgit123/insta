/*
    The purpose of this class file is to define the view adapter for the thumbnail images present
    in the thumbnail recycler view. This adapter links to the thumbnail view holder
 */

package com.unimelb.projectinsta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailHolder> {

    private List<ThumbnailItem> thumbnailItemList;
    private ThumbnailImageListener listener;
    private Context mContext;
    private int selectedIndex = 0;

    public ThumbnailAdapter(Context thumbnailContext, List<ThumbnailItem> thumbnailItemList,ThumbnailImageListener listener){
        this.mContext = thumbnailContext;
        this.thumbnailItemList = thumbnailItemList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ThumbnailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View thumbnailView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.thumbnail_items,null);
        ThumbnailHolder thumbnailHolder = new ThumbnailHolder(thumbnailView);
        return thumbnailHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailHolder thumbnailHolder, final int position) {
        final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);
        thumbnailHolder.thumbnail_img.setImageBitmap(thumbnailItem.image);
        thumbnailHolder.thumbnail_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFilterSelected(thumbnailItem.filter);
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });

        thumbnailHolder.thumbnail_text.setText(thumbnailItem.filterName);

        if (selectedIndex == position) {
            thumbnailHolder.thumbnail_text.setTextColor(ContextCompat.getColor(mContext, R.color.filter_label_highlighted));
        } else {
            thumbnailHolder.thumbnail_text.setTextColor(ContextCompat.getColor(mContext, R.color.filter_label_normal));
        }
    }

    @Override
    public int getItemCount() {
        return thumbnailItemList.size();
    }

    public interface ThumbnailImageListener {
        void onFilterSelected(Filter filter);
    }
}
