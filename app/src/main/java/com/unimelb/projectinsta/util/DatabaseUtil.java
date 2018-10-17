package com.unimelb.projectinsta.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
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

import java.util.ArrayList;

public class DatabaseUtil {

    DatabaseReference db= FirebaseDatabase.getInstance().getReference();;
//    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    ArrayList<Uri> imageList = new ArrayList<>();
    ArrayList<String> imageListString = new ArrayList<String>();

    public DatabaseUtil() {
//        this.db = db;
    }

    //Retrieve
    public ArrayList<Uri> getImagesPosted() {
        fetchData();

        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return imageList;
    }

    private void fetchData() {

//        imageList.clear();
//        for (DataSnapshot ds: dataSnapshot.getChildren()) {
//            StorageReference storageRef = ds.getRef();
//        }
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//        //adding image to view
//        storageRef.child("images/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//            Log.i("URI:",uri.toString());
//            imageList.add(uri);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference imagesRef = rootRef.child("images");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String url = ds.getValue(String.class);
                    Log.d("TAG", url);
                    imageListString.add(url);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        imagesRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void fetchData(DataSnapshot dataSnapshot) {


//        DatabaseReference imagesRef = db.child("images");
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String url = ds.getValue(String.class);
//                    Log.d("TAG", url);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        };
//        imagesRef.addListenerForSingleValueEvent(valueEventListener);

        imageList.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            String url = ds.getValue(String.class);
            imageListString.add(url);
        }
    }


}
