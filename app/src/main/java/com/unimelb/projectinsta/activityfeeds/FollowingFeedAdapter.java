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
        UserPojo loggedInUser = CommonUtil.getInstance().getLoggedInUser();
//        if(notification.getType().equals("follow") && !loggedInUser.getFollowingList().contains(user.getUserId())) {
////            followingFeedHolder.userFollowButton.setVisibility(View.VISIBLE);
////        } else {
////            followingFeedHolder.userFollowButton.setVisibility(View.INVISIBLE);
////        }

        followingFeedHolder.userFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseUtil dbHelper = new DatabaseUtil();
//                dbHelper.followFunction(user);
                followingFeedHolder.userFollowButton.setEnabled(false);
                followingFeedHolder.userFollowButton.setText("Following");
            }
        });
    }

    @Override
    public int getItemCount() {
        return followingNotificationsList.size();
    }

    public interface UserItemListener {
        void onUserSelected(String userName);
    }
}


