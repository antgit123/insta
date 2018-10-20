package com.unimelb.projectinsta;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFeedHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView feedImageView;
    TextView userName_txt,description_txt, location,like_by;
    ImageButton white_heart_icon,red_heart_icon,chat_icon;
    ItemClickListener itemClickListener;
    CircleImageView userProfileImageView;

    public UserFeedHolder(View itemView) {
        super(itemView);
        this.feedImageView= (ImageView) itemView.findViewById(R.id.user_story_photo);
        this.userProfileImageView = itemView.findViewById(R.id.profile_image);
        this.userName_txt = (TextView) itemView.findViewById(R.id.username);
        this.location = (TextView) itemView.findViewById(R.id.feed_location);
        this.description_txt = (TextView) itemView.findViewById(R.id.user_story_photo_description);
        this.white_heart_icon = itemView.findViewById(R.id.like_button);
        this.red_heart_icon = itemView.findViewById(R.id.like_button_red);
        this.chat_icon = itemView.findViewById(R.id.comment_button);
        this.like_by = itemView.findViewById(R.id.liked_by);
        this.white_heart_icon.setOnClickListener(this);
        this.red_heart_icon.setOnClickListener(this);
        this.chat_icon.setOnClickListener(this);
        this.like_by.setOnClickListener(this);
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
