package com.unimelb.projectinsta;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unimelb.projectinsta.model.UserPojo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.unimelb.projectinsta.util.CommonUtil;
import com.unimelb.projectinsta.util.DatabaseUtil;


import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsersAdapter extends ArrayAdapter<UserPojo> {

    private LayoutInflater mInflator;
    private List<UserPojo> mUsers = null;
    private int layoutResource;

    private Context mContext;

    public SearchUsersAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<UserPojo> objects) {

        super(context, resource, objects);
        mContext = context;
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
    }

    private static class ViewHolder {
        TextView username, realname;
        CircleImageView profileimage;
        Button follow_button;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflator.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.realname = (TextView) convertView.findViewById(R.id.realname);
            holder.profileimage = (CircleImageView) convertView.findViewById(R.id.profile_image);
            holder.follow_button=(Button)convertView.findViewById(R.id.follow_user_button);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.username.setText(getItem(position).getUserName());
        holder.realname.setText(getItem(position).getUserRealName());
        Glide.with(getContext())
                .load(getItem(position).getProfilePhoto())
                .into(holder.profileimage);

        UserPojo loggedInUser = CommonUtil.getInstance().getLoggedInUser();
        if(!loggedInUser.getFollowingList().contains(getItem(position).getUserId())) {
            holder.follow_button.setVisibility(View.VISIBLE);
        } else {
            holder.follow_button.setVisibility(View.INVISIBLE);
        }
        holder.follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseUtil dbHelper = new DatabaseUtil();
                dbHelper.followFunction(getItem(position));
                holder.follow_button.setEnabled(false);
                holder.follow_button.setText("Following");
            }
        });

        return convertView;
    }

}