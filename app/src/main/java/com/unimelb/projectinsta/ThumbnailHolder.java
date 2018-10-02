package com.unimelb.projectinsta;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ThumbnailHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView thumbnail_img;
    TextView thumbnail_text;

    ItemClickListener itemClickListener;

    public ThumbnailHolder(View thumbnailView) {
        super(thumbnailView);
        this.thumbnail_img= (ImageView) thumbnailView.findViewById(R.id.tnail_img);
        this.thumbnail_text = (TextView) thumbnailView.findViewById(R.id.thumbnail_text);
        thumbnailView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic) {
        this.itemClickListener=ic;
    }
}
