package com.unimelb.projectinsta;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unimelb.projectinsta.likes.LikesFragment;
import com.unimelb.projectinsta.model.Comment;
import com.unimelb.projectinsta.model.Like;
import com.unimelb.projectinsta.model.UserFeed;
import com.unimelb.projectinsta.model.UserPojo;
import com.unimelb.projectinsta.util.CommonUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedHolder> {

    List<UserFeed> userFeed;
    Context userFeedContext;
    ItemClickListener listener;
    DecelerateInterpolator decelerate = new DecelerateInterpolator();
    AccelerateInterpolator accelerate = new AccelerateInterpolator();
    View userFeedView;

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
        String currentUserProfilePhoto = currentUser.getProfilePhoto();
        String userName = userFeed.get(i).getUser().getUserName();
        userFeedHolder.userName_txt.setText(userName);
        userFeedHolder.description_username.setText(userName);
        userFeedHolder.description_txt.setText(userFeed.get(i).getCaption());
        userFeedHolder.location.setText(userFeed.get(i).getLocationName());
        String photoUri = userFeed.get(i).getPhoto();
        String userPhoto = userFeed.get(i).getUser().getProfilePhoto();
        List<Comment> commentsList = userFeed.get(i).getCommentList();
        String commentString = "View all " + commentsList.size() + " comments";
        userFeedHolder.comments_link.setText(commentString);
        List<Like> likeList = userFeed.get(i).getLikeList();
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
//        userFeedHolder.img.setImageResource(userFeed.get(i).getM_Img());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();


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
                    MainActivity mainActivity = (MainActivity)userFeedContext;
                    mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
            }
        });
    }

    public void toggleLike(int position,boolean isLiked,UserFeedHolder userFeedHolder){
        AnimatorSet animatorSet = new AnimatorSet();
        if(isLiked){
            userFeedHolder.red_heart_icon.setScaleX(0.1f);
            userFeedHolder.red_heart_icon.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(userFeedHolder.red_heart_icon,"scaleY",1f,0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(accelerate);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(userFeedHolder.red_heart_icon,"scaleX",1f,0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(accelerate);
            userFeedHolder.red_heart_icon.setVisibility(View.INVISIBLE);
            userFeedHolder.white_heart_icon.setVisibility(View.VISIBLE);
            animatorSet.playTogether(scaleDownY,scaleDownX);
        }else{
            userFeedHolder.red_heart_icon.setScaleX(0.1f);
            userFeedHolder.red_heart_icon.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(userFeedHolder.red_heart_icon,"scaleY",0.1f,1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(decelerate);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(userFeedHolder.red_heart_icon,"scaleX",0.1f,1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(decelerate);
            userFeedHolder.red_heart_icon.setVisibility(View.VISIBLE);
            userFeedHolder.white_heart_icon.setVisibility(View.INVISIBLE);
            animatorSet.playTogether(scaleDownY,scaleDownX);
        }
        animatorSet.start();
    }

    @Override
    public int getItemCount() {
        return userFeed.size();
    }

}
