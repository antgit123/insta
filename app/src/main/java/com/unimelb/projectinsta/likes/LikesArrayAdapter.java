/*
    The purpose of this java class is to add the likes adapter which links to the likes
    holder for the user likes recycler view.
 */

package com.unimelb.projectinsta.likes;
import java.util.List;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.Like;
import com.unimelb.projectinsta.model.UserPojo;
import com.unimelb.projectinsta.util.CommonUtil;
import com.unimelb.projectinsta.util.DatabaseUtil;

public class LikesArrayAdapter extends RecyclerView.Adapter<LikesHolder> {

    private List<Like> userLikesList;
    private Context mContext;
    private UserPojo loggedInUser;
    private String currentUserId;
    private FirebaseFirestore instadb = FirebaseFirestore.getInstance();

    public LikesArrayAdapter(Context likesContext, List<Like> userLikesList){
        this.mContext = likesContext;
        this.userLikesList = userLikesList;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        currentUserId = user.getUid();
    }

    @NonNull
    @Override
    public LikesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View likesView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_likes_item_list,null);
        LikesHolder likesHolder = new LikesHolder(likesView);
        return likesHolder;
    }

    /*
        View holder function which defines the elements and displays data from database in each item
        of the user likes list
     */
    @Override
    public void onBindViewHolder(@NonNull LikesHolder likesHolder, final int position) {
        final UserPojo user = userLikesList.get(position).getUser();
        final LikesHolder likesHolderObject = likesHolder;
        likesHolderObject.userName.setText(user.getUserName());
        String userProfilePhoto = userLikesList.get(position).getUser().getProfilePhoto();
        if(userProfilePhoto == null){
            Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_square).into(likesHolder.userImageView);
        }else{
            Glide.with(mContext).load(userProfilePhoto).into(likesHolder.userImageView);
        }
        UserPojo loggedInUser = CommonUtil.getInstance().getLoggedInUser();
        if(!loggedInUser.getFollowingList().contains(user.getUserId()) ||
                !user.getUserId().equals(loggedInUser.getUserId())) {
            likesHolderObject.userFollowButton.setVisibility(View.VISIBLE);
        } else {
            likesHolderObject.userFollowButton.setVisibility(View.INVISIBLE);
        }

        likesHolderObject.userFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            DatabaseUtil dbHelper = new DatabaseUtil();
            dbHelper.followFunction(userLikesList.get(position).getUser());
            likesHolderObject.userFollowButton.setEnabled(false);
            likesHolderObject.userFollowButton.setText("Following");
            }
        });
    }

    @Override
    public int getItemCount() {
        return userLikesList.size();
    }

    public interface UserItemListener {
        void onUserSelected(String userName);
    }
}
