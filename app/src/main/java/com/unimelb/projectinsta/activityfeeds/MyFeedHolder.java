package com.unimelb.projectinsta.activityfeeds;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unimelb.projectinsta.ItemClickListener;
import com.unimelb.projectinsta.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A custom holder class for the recycler view.
 */
public class MyFeedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    CircleImageView userProfileImage;
    TextView feedDescription;
    Button userFollowButton;
    ItemClickListener itemClickListener;
    TextView postedTime;

    public MyFeedHolder(View myFeedView) {
        super(myFeedView);
        this.feedDescription= (TextView) myFeedView.findViewById(R.id.myfeed_item_description);
        this.userProfileImage = (CircleImageView) myFeedView.findViewById(R.id.feed_user_profile_image);
        this.userFollowButton = myFeedView.findViewById(R.id.follow_user_button);
        this.postedTime = (TextView) myFeedView.findViewById(R.id.time_posted);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic) {
        this.itemClickListener=ic;
    }
}

