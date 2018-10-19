package com.unimelb.projectinsta.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unimelb.projectinsta.model.UserFeed;
import com.unimelb.projectinsta.model.UserPojo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DatabaseUtil {

    private ArrayList<String> imageList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;
    private Context mContext;
    public UserPojo loggedInUser = new UserPojo();
    public FirebaseFirestore instadb = FirebaseFirestore.getInstance();
    private ArrayList<String> followingNotificationList = new ArrayList<>();
    private ArrayList<UserPojo> allUsers = new ArrayList<>();

    public DatabaseUtil() {
//        this.db = db;
    }

    public DatabaseUtil(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    //Retrieve
    public ArrayList<String> getImagesPosted(UserPojo user) {
        fetchData(user);
        return imageList;

    }

    private void fetchData(UserPojo user) {
        for (int i = 0; i < 10; i++) {
            imageList.add(user.getProfilePhoto());
//            followingNotificationList.add(user.getProfilePhoto());
        }
    }


    public void savePost(String uri, String caption, Location location) {
        Log.d("test", "savePost: "+userID);
        final UserFeed feed = new UserFeed();
        feed.setPhoto(uri);
        feed.setCaption(caption);
        feed.setLocation(location);
        feed.setUserId(userID);
        int n = 1000;
        final int feedId = new Random().nextInt(n);
        CollectionReference userDocuments = instadb.collection("users");
        userDocuments.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if (task.isSuccessful()) {
                   //retrieve user Details
                   DocumentSnapshot document = task.getResult();
                   Log.i("DB Success", document.getId() + " => " + document.getData());
                   loggedInUser = document.toObject(UserPojo.class);
                   feed.setUser(loggedInUser);
                   feed.setDate(new Date());
                   feed.setFeed_Id(feedId);
                   FirebaseFirestore instadb = FirebaseFirestore.getInstance();
                   instadb.collection("feeds").document(String.valueOf(feedId)).set(feed);
               }
           }
       });

        //query to fetch logged in user doc, get users following list and check with feeds

//        userDocuments.whereEqualTo("userId",userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    //retrieve user Details
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.i("DB Success", document.getId() + " => " + document.getData());
//                        final List<UserPojo> followingUsers = new ArrayList<>();
//                        loggedInUser = returnUser(document);
//                        Log.i("Logged User",loggedInUser.getEmail());
//                    }
//                } else {
//                    Log.i("DB ERROR", "Error getting documents.", task.getException());
//                }
//            }
//        });


    }

    public UserPojo returnUser(DocumentSnapshot userDoc) {
        if (userDoc.getData().get("userId") != null) {
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