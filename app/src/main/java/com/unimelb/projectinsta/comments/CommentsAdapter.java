package com.unimelb.projectinsta.comments;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.Comment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends ArrayAdapter<Comment> {

    private Context mContext;
    private List<Comment> commentList = new ArrayList<>();
    private LayoutInflater mInflater;
    private int layoutResource;

    public CommentsAdapter(@NonNull Context context, @LayoutRes int resource,
                              @NonNull List<Comment> comments) {
        super(context, resource, comments);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    ImageView like;

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.comment = (TextView) convertView.findViewById(R.id.comment);
//            holder.username = (TextView) convertView.findViewById(R.id.comment_username);
//            holder.timestamp = (TextView) convertView.findViewById(R.id.comment_time_posted);
//            holder.reply = (TextView) convertView.findViewById(R.id.comment_reply);
//            holder.like = (ImageView) convertView.findViewById(R.id.comment_like);
//            holder.likes = (TextView) convertView.findViewById(R.id.comment_likes);
//            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.comment_profile_image);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //set the comment
        holder.comment.setText(getItem(position).getDescription());

        //set the timestamp difference
//        String timestampDifference = getTimestampDifference(getItem(position));
//        if(!timestampDifference.equals("0")){
//            holder.timestamp.setText(timestampDifference + " d");
//        }else{
//            holder.timestamp.setText("today");
//        }
//
//        //set the username and profile image
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(mContext.getString(R.string.dbname_user_account_settings))
//                .orderByChild(mContext.getString(R.string.field_user_id))
//                .equalTo(getItem(position).getUser_id());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//                    holder.username.setText(
//                            singleSnapshot.getValue(UserAccountSettings.class).getUsername());
//
//                    ImageLoader imageLoader = ImageLoader.getInstance();
//
//                    imageLoader.displayImage(
//                            singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
//                            holder.profileImage);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: query cancelled.");
//            }
//        });

        return convertView;
    }

     private static class ViewHolder{
        TextView comment, username, timestamp, reply, likes;
        CircleImageView profileImage;
    }

}
