package com.unimelb.projectinsta.likes;
import java.util.List;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.UserPojo;

public class LikesArrayAdapter extends RecyclerView.Adapter<LikesHolder> {

    private List<UserPojo> userList;
    private UserItemListener listener;
    private Context mContext;
    private UserPojo loggedInUser;
    private String currentUserId;
    private FirebaseFirestore instadb = FirebaseFirestore.getInstance();

    public LikesArrayAdapter(Context likesContext, List<UserPojo> userList){
        this.mContext = likesContext;
        this.userList = userList;
        Log.i("USERList",this.userList.get(0).getEmail());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        currentUserId = user.getUid();

//        this.listener = listener;
    }

    @NonNull
    @Override
    public LikesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View likesView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_likes_item_list,null);
        LikesHolder likesHolder = new LikesHolder(likesView);
        return likesHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LikesHolder likesHolder, final int position) {
        final UserPojo user = userList.get(position);
        final LikesHolder likesHolderObject = likesHolder;
        likesHolderObject.userName.setText(user.getUserName());
        likesHolderObject.userFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Log.i("debug", userList.get(position).getUserName());
            addFollowing(userList.get(position));
            addFollowers(userList.get(position));
            likesHolderObject.userFollowButton.setEnabled(false);
            likesHolderObject.userFollowButton.setText("Following");
            }
        });
    }

    private void addFollowing(final UserPojo user) {
        if(loggedInUser != null) {
            updateFollowingList(loggedInUser, user.getUserId());
        } else {
            CollectionReference userDocuments = instadb.collection("users");
            userDocuments.document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        //retrieve user Details
                        DocumentSnapshot document = task.getResult();
                        Log.i("DB Success", document.getId() + " => " + document.getData());
                        loggedInUser = document.toObject(UserPojo.class);
                        updateFollowingList(loggedInUser, user.getUserId());
                    }
                }
            });
        }
    }

    private void updateFollowingList(UserPojo user, String followingUserId) {
        user.getFollowingList().add(followingUserId);
        DocumentReference userRef = instadb.collection("users")
                .document(currentUserId);
        userRef.set(user);
    }

    private void addFollowers(UserPojo user) {
        user.getFollowerList().add(currentUserId);
        DocumentReference userRef = instadb.collection("users")
                .document(user.getUserId());
        userRef.set(user);
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface UserItemListener {
        void onUserSelected(String userName);
    }
}
