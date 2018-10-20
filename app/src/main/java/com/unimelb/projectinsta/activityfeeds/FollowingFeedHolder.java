package com.unimelb.projectinsta.activityfeeds;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.unimelb.projectinsta.ItemClickListener;
import com.unimelb.projectinsta.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingFeedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    CircleImageView userProfileImage;
    TextView feedDescription;
    Button userFollowButton;
    ItemClickListener itemClickListener;

    public FollowingFeedHolder(View myFeedView) {
        super(myFeedView);
        this.feedDescription= (TextView) myFeedView.findViewById(R.id.following_feed_item_description);
        this.userProfileImage = (CircleImageView) myFeedView.findViewById(R.id.following_feed_user_profile_image);
        this.userFollowButton = myFeedView.findViewById(R.id.following_feed_follow_user_button);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic) {
        this.itemClickListener=ic;
    }
}


