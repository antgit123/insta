package com.unimelb.projectinsta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimelb.projectinsta.model.UserFeed;

import java.util.ArrayList;

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedHolder> {

    ArrayList<UserFeed> userFeed;
    Context userFeedContext;

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
    public void onBindViewHolder(@NonNull UserFeedHolder userFeedHolder, int i) {
        userFeedHolder.userName_txt.setText(userFeed.get(i).getM_UserName());
        userFeedHolder.description_txt.setText(userFeed.get(i).getM_Description());
        userFeedHolder.title.setText(userFeed.get(i).getM_Location());
        userFeedHolder.img.setImageResource(userFeed.get(i).getM_Img());

        userFeedHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Snackbar.make(v,userFeed.get(pos).getM_UserName(),Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userFeed.size();
    }
}
