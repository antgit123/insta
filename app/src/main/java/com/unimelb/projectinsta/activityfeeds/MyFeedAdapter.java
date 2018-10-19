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
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.likes.LikesHolder;
import com.unimelb.projectinsta.model.MyNotificationsPojo;
import com.unimelb.projectinsta.model.UserPojo;
import java.util.List;

public class MyFeedAdapter extends RecyclerView.Adapter<MyFeedHolder> {

    private List<MyNotificationsPojo> myNotificationsList;
    private Context mContext;

    public MyFeedAdapter(Context likesContext, List<MyNotificationsPojo> myNotificationsList){
        this.mContext = likesContext;
        this.myNotificationsList = myNotificationsList;
    }

    @NonNull
    @Override
    public MyFeedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View myFeedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_you_list_item,null);
        MyFeedHolder myFeedHolder = new MyFeedHolder(myFeedView);
        return myFeedHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyFeedHolder myFeedHolder, final int position) {
        final UserPojo user = myNotificationsList.get(position).getUser();
        Glide.with(mContext).load(user.getProfilePhoto()).into(myFeedHolder.userProfileImage);
        String feed_description = myNotificationsList.get(position).getFeedDescription();
        myFeedHolder.feedDescription.setText(feed_description);
    }

    @Override
    public int getItemCount() {
        return myNotificationsList.size();
    }

    public interface UserItemListener {
        void onUserSelected(String userName);
    }
}

