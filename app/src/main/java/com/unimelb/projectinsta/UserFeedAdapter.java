package com.unimelb.projectinsta;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unimelb.projectinsta.comments.CommentsFragment;
import com.unimelb.projectinsta.likes.LikesFragment;
import com.unimelb.projectinsta.model.Comment;
import com.unimelb.projectinsta.model.Like;
import com.unimelb.projectinsta.model.UserFeed;
import com.unimelb.projectinsta.model.UserPojo;
import com.unimelb.projectinsta.util.CommonUtil;
import com.unimelb.projectinsta.util.DatabaseUtil;

import java.util.Iterator;
import java.util.List;

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedHolder> {

    List<UserFeed> userFeed;
    Context userFeedContext;
    ItemClickListener listener;
    View userFeedView;
    DatabaseUtil dbUtil = new DatabaseUtil();

    public UserFeedAdapter(Context userFeedContext, List<UserFeed> userFeed){
        this.userFeedContext = userFeedContext;
        this.userFeed = userFeed;
    }
    @NonNull
    @Override
    public UserFeedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        userFeedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout,null);
        UserFeedHolder feedHolder = new UserFeedHolder(userFeedView);
        return feedHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserFeedHolder userFeedHolder, int i) {
        UserPojo currentUser = CommonUtil.getInstance().getLoggedInUser();
        final UserFeed selectedUserFeed = userFeed.get(i);
        boolean isLiked = false;
        String currentUserProfilePhoto = currentUser.getProfilePhoto();
        String userName = selectedUserFeed.getUser().getUserName();
        userFeedHolder.userName_txt.setText(userName);
        userFeedHolder.description_username.setText(userName);
        userFeedHolder.description_txt.setText(selectedUserFeed.getCaption());
        userFeedHolder.location.setText(selectedUserFeed.getLocationName());
        String photoUri = selectedUserFeed.getPhoto();
        String userPhoto = selectedUserFeed.getUser().getProfilePhoto();
        List<Comment> commentsList = selectedUserFeed.getCommentList();
        String commentString = "View all " + commentsList.size() + " comments";
        userFeedHolder.comments_link.setText(commentString);
        List<Like> likeList = selectedUserFeed.getLikeList();
        if(likeList.size() != 0) {
            Iterator iterator = likeList.iterator();
            Like like = null;
            while (iterator.hasNext()) {
                like = (Like) iterator.next();
                if (like.getUser().getUserId().equals(currentUser.getUserId())) {
                    isLiked = true;
                }
            }
        }
        //toggle heart display
        if(isLiked){
            userFeedHolder.red_heart_icon.setVisibility(View.VISIBLE);
            userFeedHolder.white_heart_icon.setVisibility(View.INVISIBLE);
        }else{
            userFeedHolder.red_heart_icon.setVisibility(View.INVISIBLE);
            userFeedHolder.white_heart_icon.setVisibility(View.VISIBLE);
        }
        String likeByString = likeList.size() + " likes";
        userFeedHolder.like_by.setText(likeByString);
        if(userPhoto == null){
            Glide.with(userFeedContext).load(R.drawable.com_facebook_profile_picture_blank_square).into(userFeedHolder.userProfileImageView);
        }else{
            Glide.with(userFeedContext).load(userPhoto).into(userFeedHolder.userProfileImageView);
        }
        if(currentUserProfilePhoto == null){
            Glide.with(userFeedContext).load(R.drawable.com_facebook_profile_picture_blank_square).into(userFeedHolder.commentProfileImageView);
        }else{
            Glide.with(userFeedContext).load(userPhoto).into(userFeedHolder.commentProfileImageView);
        }
        Glide.with(userFeedContext).load(photoUri).into(userFeedHolder.feedImageView);

        userFeedHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if(v.getId() == userFeedHolder.white_heart_icon.getId()){
                    toggleLike(pos,false,userFeedHolder);
                }
                if(v.getId() == userFeedHolder.red_heart_icon.getId()){
                    toggleLike(pos,true,userFeedHolder);
                }
                if(v.getId() == userFeedHolder.like_by.getId()){
                    LikesFragment fragment  = new LikesFragment();
                    Bundle likeBundle = new Bundle();
                    likeBundle.putString("feedId",Integer.toString(selectedUserFeed.getFeed_Id()));
                    fragment.setArguments(likeBundle);
                    MainActivity mainActivity = (MainActivity)userFeedContext;
                    mainActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
                if(v.getId() == userFeedHolder.comments_link.getId()){
                    CommentsFragment fragment = new CommentsFragment();
                    Bundle commentsBundle = new Bundle();
                    commentsBundle.putString("feedId",Integer.toString(selectedUserFeed.getFeed_Id()));
                    commentsBundle.putString("trigger","textLink");
                    fragment.setArguments(commentsBundle);
                    MainActivity mainActivity = (MainActivity)userFeedContext;
                    mainActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
                if(v.getId() ==  userFeedHolder.chat_icon.getId()){
                    CommentsFragment fragment = new CommentsFragment();
                    Bundle commentsBundle = new Bundle();
                    commentsBundle.putString("feedId",Integer.toString(selectedUserFeed.getFeed_Id()));
                    commentsBundle.putString("trigger","icon");
                    fragment.setArguments(commentsBundle);
                    MainActivity mainActivity = (MainActivity)userFeedContext;
                    mainActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
                if(v.getId() == userFeedHolder.userFeedPostCommentText.getId()){
                    String commentDescription = userFeedHolder.userFeedEditComment.getText().toString();
                    if(!commentDescription.equals("")){
                        dbUtil.postComment(userFeedContext,selectedUserFeed,userFeedHolder,commentDescription);
                    }else{
                        Toast.makeText(userFeedContext,"Add some text",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void toggleLike(int position,boolean isLiked,UserFeedHolder userFeedHolder){
        UserFeed selectedUserFeed = userFeed.get(position);
        dbUtil.updatePostLikes(userFeedContext, selectedUserFeed, userFeedHolder, isLiked);
    }

    @Override
    public int getItemCount() {
        return userFeed.size();
    }

}
