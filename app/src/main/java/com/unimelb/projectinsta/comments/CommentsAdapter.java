/*
    /*
    The purpose of this java class is to add the comments adapter which links to the comments
    holder for the comments recycler view.
 */

package com.unimelb.projectinsta.comments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.Comment;
import com.unimelb.projectinsta.model.UserPojo;
import com.unimelb.projectinsta.util.DatabaseUtil;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsHolder> {

    private Context mContext;
    private List<Comment> commentList;
    DatabaseUtil dbUtil = new DatabaseUtil();

    public CommentsAdapter(Context likesContext, List<Comment> commentList){
        this.mContext = likesContext;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View commentsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_comment_item,null);
        CommentsHolder commentsHolder = new CommentsHolder(commentsView);
        return commentsHolder;
    }

    /*
        View holder function which defines the elements and displays data from database in each item
        of the comments list
     */
    @Override
    public void onBindViewHolder(@NonNull CommentsHolder commentsHolder, final int position) {
        final UserPojo user = commentList.get(position).getUser();
        commentsHolder.userName.setText(user.getUserName());
        String userProfilePhoto = commentList.get(position).getUser().getProfilePhoto();
        if(userProfilePhoto == null){
            Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_square).into(commentsHolder.userProfileImage);
        }else{
            Glide.with(mContext).load(userProfilePhoto).into(commentsHolder.userProfileImage);
        }
        commentsHolder.commentText.setText(commentList.get(position).getDescription());
        String diff = dbUtil.getTimestampDifference(commentList.get(position).getDate());
        commentsHolder.postedTime.setText(diff);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
