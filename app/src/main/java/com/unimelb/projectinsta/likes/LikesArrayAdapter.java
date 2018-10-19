package com.unimelb.projectinsta.likes;
import java.util.List;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.UserPojo;
import java.util.List;

public class LikesArrayAdapter extends RecyclerView.Adapter<LikesHolder> {

    private List<UserPojo> userList;
    private UserItemListener listener;
    private Context mContext;

    public LikesArrayAdapter(Context likesContext, List<UserPojo> userList){
        this.mContext = likesContext;
        this.userList = userList;
        Log.i("USERList",this.userList.get(0).getEmail());
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
        likesHolder.userName.setText(user.getUserName());
        likesHolder.userFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onUserSelected(userList.get(position).getUserName());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface UserItemListener {
        void onUserSelected(String userName);
    }
}
