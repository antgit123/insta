/*
    The purpose of this class is to define the view elements present in the user likes view
 */

package com.unimelb.projectinsta.likes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.unimelb.projectinsta.ItemClickListener;
import com.unimelb.projectinsta.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class LikesHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    Button userFollowButton;
    CircleImageView userImageView;
    TextView userName;
    ItemClickListener itemClickListener;

    public LikesHolder(View likesView) {
        super(likesView);
        this.userName= (TextView) likesView.findViewById(R.id.list_item_username);
        this.userFollowButton = (Button) likesView.findViewById(R.id.follow_user_button);
        this.userImageView = likesView.findViewById(R.id.user_likes_user_profile_image);
        this.userFollowButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic) {
        this.itemClickListener=ic;
    }
}

