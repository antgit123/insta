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
        TextView username, email;
        CircleImageView profileimage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflator.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.email = (TextView) convertView.findViewById(R.id.email);
            holder.profileimage = (CircleImageView) convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.username.setText(getItem(position).getUserName());
        holder.email.setText(getItem(position).getEmail());
        Glide.with(getContext())
                .load(getItem(position).getProfilePhoto())
                .into(holder.profileimage);

//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseFirestore instadb = FirebaseFirestore.getInstance();
//
//
//        CollectionReference userDocuments = instadb.collection("users");
//
//        //com.google.firebase.firestore.Query query = userDocuments.whereEqualTo("UserId",position);
//        userDocuments.whereEqualTo("UserName",holder.username.toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    QuerySnapshot doc = task.getResult();
//                    for (UserPojo user: task.getResult().toObjects(UserPojo.class)) {
////                        UserPojo userInfo = document.toObject(UserPojo.class);
//
////                        holder.profileimage.setImageURI(Uri.parse(UserInfo.getProfilePhoto()));
//                        Glide.with(getContext())
//                                .load(user.getProfilePhoto())
//                                .into(holder.profileimage);
//
//                        //Log.d(TAG, document.getId() + " => " + document.getData());
//                    }
//                }
//            }
//        });
        return convertView;
    }

    public UserPojo returnUser(DocumentSnapshot userDoc){
        if(userDoc.getData().get("userId") != null) {
            String userId = (String) userDoc.getData().get("userId");
            String userName = (String) userDoc.getData().get("userName");
            String realName = (String) userDoc.getData().get("userRealName");
            String email = (String) userDoc.getData().get("email");
            String password = (String) userDoc.getData().get("password");

            UserPojo user = new UserPojo(userId, userName, realName, email, password);
            return user;
        }
        return null;
    }
}