package com.unimelb.projectinsta.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unimelb.projectinsta.comments.CommentsAdapter;
import com.unimelb.projectinsta.model.Comment;
import com.unimelb.projectinsta.model.FollowingUserNotificationsPojo;
import com.unimelb.projectinsta.UserFeedHolder;
import com.unimelb.projectinsta.model.Like;
import com.unimelb.projectinsta.model.MyNotificationsPojo;
import com.unimelb.projectinsta.model.UserFeed;
import com.unimelb.projectinsta.model.UserPojo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DatabaseUtil {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;
    private Context mContext;
    public UserPojo loggedInUser = new UserPojo();
    public FirebaseFirestore instadb = FirebaseFirestore.getInstance();

    public DatabaseUtil() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        loggedInUser = CommonUtil.getInstance().getLoggedInUser();

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public DatabaseUtil(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;
        loggedInUser = CommonUtil.getInstance().getLoggedInUser();
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void savePost(String uri, String caption, Location location, String address) {
        Log.d("test", "savePost: "+userID);
        final UserFeed feed = new UserFeed();
        feed.setPhoto(uri);
        feed.setCaption(caption);
        feed.setLocationName(address);
        feed.setLatitude(location.getLatitude());
        feed.setLongitude(location.getLongitude());
        feed.setUserId(userID);
        int n = 1000;
        int feedId = new Random().nextInt(n);
        feed.setUser(loggedInUser);
        feed.setDate(new Date());
        feed.setFeed_Id(feedId);
        FirebaseFirestore instadb = FirebaseFirestore.getInstance();
        instadb.collection("feeds").document(String.valueOf(feedId)).set(feed);
    }

    public void followFunction(UserPojo follower) {
        addFollowing(follower);
        addFollowers(follower);
        notifyFollowers(follower);
    }

    private void notifyFollowers(UserPojo follower) {
        String type = "follow";
        String feedDescription = loggedInUser.getUserName() + " started following " + follower.getUserName();

        List<String> followersList = loggedInUser.getFollowerList();
        for(String followerId : followersList) {
            FollowingUserNotificationsPojo notification = new FollowingUserNotificationsPojo
                    (loggedInUser, follower, type, feedDescription, new Date(), followerId);
            instadb.collection("followersNotifications").add(notification);
        }
    }


    private void notifyFollowersAboutLike(UserPojo follower) {
        String type = "like";
        String feedDescription = loggedInUser.getUserName() + " liked the post added by " + follower.getUserName();

        List<String> followersList = loggedInUser.getFollowerList();
        for(String followerId : followersList) {
            if (!followerId.equals(follower.getUserId())){
                FollowingUserNotificationsPojo notification = new FollowingUserNotificationsPojo
                        (loggedInUser, follower, type, feedDescription, new Date(), followerId);
                instadb.collection("followersNotifications").add(notification);
            }
        }
    }

    private void addFollowing(final UserPojo user) {
        updateFollowingList(loggedInUser, user.getUserId());
    }

    private void updateFollowingList(UserPojo user, String followingUserId) {
        user.getFollowingList().add(followingUserId);
        DocumentReference userRef = instadb.collection("users")
                .document(userID);
        userRef.set(user);
    }

    private void addFollowers(final UserPojo user) {
        user.getFollowerList().add(userID);
        DocumentReference userRef = instadb.collection("users")
                .document(user.getUserId());
        userRef.set(user);

        String type = "follow";
        String feedDescription = loggedInUser.getUserRealName() + " started following you";
        updateMyNotification(type, feedDescription, user.getUserId(), loggedInUser);
    }

    private void updateMyNotification(String type, String feedDescription, String userId, UserPojo user) {
        MyNotificationsPojo notification = new MyNotificationsPojo(userId,type, feedDescription, user, new Date());
        instadb.collection("myNotifications").add(notification);
    }

    public void updatePostLikes(final Context userFeedContext, final UserFeed userFeed, final UserFeedHolder userFeedHolder, final boolean isLiked){
        int feedId = userFeed.getFeed_Id();
        loggedInUser = CommonUtil.getInstance().getLoggedInUser();
        final String userId = userFeed.getUserId();
        Like currentUserLike = new Like(loggedInUser,new Date());
        if(isLiked){
            //iterating through the likelist and delete the like of current user
            Iterator iterator = userFeed.getLikeList().iterator();
            Like like = null;
            while (iterator.hasNext()){
                like = (Like) iterator.next();
                if (like.getUser().getUserId().equals(loggedInUser.getUserId())){
                    iterator.remove();
                }
            }
        }else{
            userFeed.getLikeList().add(currentUserLike);
        }
        int numberOflikes = userFeed.getLikeList().size();
        final String likesString = numberOflikes + " likes";
        CollectionReference feedDocuments = instadb.collection("feeds");
        final AnimatorSet animatorSet = new AnimatorSet();
        final DecelerateInterpolator decelerate = new DecelerateInterpolator();
        final AccelerateInterpolator accelerate = new AccelerateInterpolator();
        feedDocuments.document(Integer.toString(feedId)).set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
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
                    userFeedHolder.like_by.setText(likesString);
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
                    userFeedHolder.like_by.setText(likesString);
                    //updating notifications for like
                    String type = "like";
                    String feedDescription = loggedInUser.getUserRealName() + " liked your post";
                    updateMyNotification(type, feedDescription, userId, loggedInUser);
                    notifyFollowersAboutLike(userFeed.getUser());
                }
                animatorSet.start();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(userFeedContext,"Failed to update Likes, please check connection",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postComment(final Context userFeedContext, final UserFeed userFeed, final UserFeedHolder userFeedHolder, final String comment_Description){
        int feedId = userFeed.getFeed_Id();
        loggedInUser = CommonUtil.getInstance().getLoggedInUser();
        Comment comment = new Comment(loggedInUser,new Date(),comment_Description);
        userFeed.addCommentList(comment);
        final String userId = userFeed.getUserId();
        final int numberOfComments = userFeed.getCommentList().size();
        final String commentsString = "View all " + numberOfComments + " comments";
        CollectionReference feedDocuments = instadb.collection("feeds");
        feedDocuments.document(Integer.toString(feedId)).set(userFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    userFeedHolder.comments_link.setText(commentsString);
                    userFeedHolder.userFeedEditComment.setText("");

                    //updating notifications for comment
                    String type = "comment";
                    String feedDescription = loggedInUser.getUserRealName() + " commented on your post: '"
                            + comment_Description + "'";
                    updateMyNotification(type, feedDescription, userId, loggedInUser);
                    notifyFollowersAboutComments(userFeed.getUser(), comment_Description);

                    Toast.makeText(userFeedContext,"Comment Succesfully posted!",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(userFeedContext,"Failed to post comment, please check connection",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyFollowersAboutComments(UserPojo follower, String comment) {
        String type = "comment";
        String feedDescription = loggedInUser.getUserName() + " commented on the post added by "
                + follower.getUserName() + ": '" + comment + "'";

        List<String> followersList = loggedInUser.getFollowerList();
        for(String followerId : followersList) {
            if (!followerId.equals(follower.getUserId())){
                FollowingUserNotificationsPojo notification = new FollowingUserNotificationsPojo
                        (loggedInUser, follower, type, feedDescription, new Date(), followerId);
                instadb.collection("followersNotifications").add(notification);
            }
        }
    }

    public String getTimestampDifference(Date date){
        String difference = "";
        Calendar c = Calendar.getInstance();
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
        Date today = c.getTime();
        simpleDateFormat.format(today);
        difference = String.valueOf(Math.round(((today.getTime() - date.getTime()) / 1000 / 60 / 60 / 24 )));
        if(difference.equals("0")){
            long hours = TimeUnit.MILLISECONDS.toHours(today.getTime() - date.getTime());
            if(Long.toString(hours).equals("0")){
                long minutes = TimeUnit.MILLISECONDS.toMinutes(today.getTime() - date.getTime());
                if(minutes < 1){
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(today.getTime() - date.getTime());
                    return Long.toString(seconds) + "s ago";
                }else{
                    return Long.toString(minutes) + "m ago";
                }
            }else{
                return Long.toString(hours) + "h ago";
            }
        }else{
            return difference + "d ago";
        }
    }
}