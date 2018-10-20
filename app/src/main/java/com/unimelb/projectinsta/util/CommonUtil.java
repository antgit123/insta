package com.unimelb.projectinsta.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unimelb.projectinsta.model.UserPojo;

import java.util.Date;

public class CommonUtil {

    private static CommonUtil single_instance = null;

    // variable of type String
    private UserPojo loggedInUser;
    private String userId;

    // private constructor restricted to this class itself
    private CommonUtil()
    {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference userDocuments = FirebaseFirestore.getInstance().collection("users");
        userDocuments.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                //retrieve user Details
                DocumentSnapshot document = task.getResult();
                Log.i("DB Success", document.getId() + " => " + document.getData());
                loggedInUser = document.toObject(UserPojo.class);
            }
            }
        });
    }

    // static method to create instance of Singleton class
    public static CommonUtil getInstance()
    {
        if (single_instance == null)
            single_instance = new CommonUtil();

        return single_instance;
    }

    public UserPojo getLoggedInUser() {
        return loggedInUser;
    }
}
