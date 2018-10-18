package com.unimelb.projectinsta;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unimelb.projectinsta.model.UserFeed;

import java.util.ArrayList;

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedHolder> {

    ArrayList<UserFeed> userFeed;
    Context userFeedContext;
    ItemClickListener listener;
    DecelerateInterpolator decelerate = new DecelerateInterpolator();
    AccelerateInterpolator accelerate = new AccelerateInterpolator();

    public UserFeedAdapter(Context userFeedContext, ArrayList<UserFeed> userFeed){
        this.userFeedContext = userFeedContext;
        this.userFeed = userFeed;
    }
    @NonNull
    @Override
    public UserFeedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View userFeedView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout,null);
        UserFeedHolder feedHolder = new UserFeedHolder(userFeedView);
        return feedHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserFeedHolder userFeedHolder, int i) {

        userFeedHolder.userName_txt.setText(userFeed.get(i).getM_UserName());
        userFeedHolder.description_txt.setText(userFeed.get(i).getM_Description());
        userFeedHolder.title.setText(userFeed.get(i).getM_Location());
//        userFeedHolder.img.setImageResource(userFeed.get(i).getM_Img());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        //adding image to view
        storageRef.child("/appl.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("URI:",uri.toString());
                Glide.with(userFeedContext).load(uri.toString()).into(userFeedHolder.img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        userFeedHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if(v.getId() == userFeedHolder.white_heart_icon.getId()){
                    toggleLike(pos,false,userFeedHolder);
                }
                if(v.getId() == userFeedHolder.red_heart_icon.getId()){
                    toggleLike(pos,true,userFeedHolder);
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
