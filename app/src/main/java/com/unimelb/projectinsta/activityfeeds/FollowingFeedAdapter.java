package com.unimelb.projectinsta.activityfeeds;
import java.util.List;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.data.model.User;
import com.unimelb.projectinsta.MainActivity;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.FollowingUserNotificationsPojo;
import com.unimelb.projectinsta.model.MyNotificationsPojo;
import com.unimelb.projectinsta.model.UserPojo;
import com.unimelb.projectinsta.util.CommonUtil;
import com.unimelb.projectinsta.util.DatabaseUtil;

import java.util.List;

public class FollowingFeedAdapter extends RecyclerView.Adapter<FollowingFeedHolder> {

    private List<FollowingUserNotificationsPojo> followingNotificationsList;
    private Context mContext;
    private DatabaseUtil dbUtil = new DatabaseUtil();

    public FollowingFeedAdapter(Context likesContext, List<FollowingUserNotificationsPojo> followingNotificationsList){
        this.mContext = likesContext;
        this.followingNotificationsList = followingNotificationsList;
    }

    @NonNull
    @Override
    public FollowingFeedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View followingFeedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_following_list_item,null);
        FollowingFeedHolder followingFeedHolder = new FollowingFeedHolder(followingFeedView);
        return followingFeedHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowingFeedHolder followingFeedHolder, final int position) {
        FollowingUserNotificationsPojo notification = followingNotificationsList.get(position);
        final UserPojo user1 = notification.getUser1();
        final UserPojo user2 = notification.getUser2();
        String feed_description = followingNotificationsList.get(position).getFeedDescription();
        followingFeedHolder.feedDescription.setText(feed_description);

        if(user1.getProfilePhoto() != null) {
            Glide.with(mContext).load(user1.getProfilePhoto()).into(followingFeedHolder.userProfileImage);
        } else {
            Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_square).into(followingFeedHolder.userProfileImage);
        }

        String diff = dbUtil.getTimestampDifference(followingNotificationsList.get(position).getNotificationTimestamp());
        followingFeedHolder.postedTime.setText(diff);

    }

    @Override
    public int getItemCount() {
        return followingNotificationsList.size();
    }

    public interface UserItemListener {
        void onUserSelected(String userName);
    }
}


