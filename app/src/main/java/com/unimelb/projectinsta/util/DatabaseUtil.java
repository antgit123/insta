package com.unimelb.projectinsta.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unimelb.projectinsta.model.UserPojo;

import java.util.ArrayList;

public class DatabaseUtil {

    DatabaseReference db= FirebaseDatabase.getInstance().getReference();
    ArrayList<String> imageList = new ArrayList<>();

    public DatabaseUtil() {
    }

    //Retrieve
    public ArrayList<String> getImagesPosted(UserPojo user) {
        fetchData(user);
        return imageList;
    }

    private void  fetchData(UserPojo user) {
        for(int i = 0; i<10 ; i++) {
            imageList.add(user.getProfilePhoto());
        }
    }
}
