package com.unimelb.projectinsta.comments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.Comment;
import com.unimelb.projectinsta.model.UserPojo;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsHolder> {

    private Context mContext;
    private List<Comment> commentList;

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
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
