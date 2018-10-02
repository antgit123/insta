package com.unimelb.projectinsta;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserFeedHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView img;
    TextView userName_txt,description_txt, title;

    ItemClickListener itemClickListener;

    public UserFeedHolder(View itemView) {
        super(itemView);
        this.img= (ImageView) itemView.findViewById(R.id.user_story_photo);
        this.userName_txt = (TextView) itemView.findViewById(R.id.username);
        this.title = (TextView) itemView.findViewById(R.id.feed_title);
        this.description_txt = (TextView) itemView.findViewById(R.id.user_story_photo_description);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic)
    {
        this.itemClickListener=ic;
    }
}
