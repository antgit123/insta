/*
    The purpose of this class is to define the view elements present in the comments view
 */

package com.unimelb.projectinsta.comments;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.unimelb.projectinsta.ItemClickListener;
import com.unimelb.projectinsta.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView userName, commentText, postedTime;
    ItemClickListener itemClickListener;
    CircleImageView userProfileImage;

    public CommentsHolder(View commentsView) {
        super(commentsView);
        this.userName= (TextView) commentsView.findViewById(R.id.comment_username);
        this.commentText = (TextView) commentsView.findViewById(R.id.comment_text);
        this.postedTime = (TextView) commentsView.findViewById(R.id.comment_time_posted);
        this.userProfileImage = commentsView.findViewById(R.id.comment_profile_image);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic) {
        this.itemClickListener=ic;
    }
}

