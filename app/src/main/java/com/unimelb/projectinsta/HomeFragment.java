package com.unimelb.projectinsta;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unimelb.projectinsta.model.UserFeed;
import com.unimelb.projectinsta.model.UserPojo;
import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public List<DocumentReference> mFollowingList;
    public List<DocumentReference> feeds;
    public List<DocumentReference> comments;
    public UserPojo loggedInUser = new UserPojo();
    public RecyclerView mUserFeedRecyclerView;
    public UserFeedAdapter mAdapter;
    public FirebaseFirestore instadb = FirebaseFirestore.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        queryDB();
        View homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        mUserFeedRecyclerView = (RecyclerView) homeFragmentView.findViewById(R.id.fragment_userfeed_recycler);
        mUserFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUserFeedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new UserFeedAdapter(getContext(),getUserFeeds());
        mUserFeedRecyclerView.setAdapter(mAdapter);
        return homeFragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void queryDB(){
        //fetches user doc, followed by users following list and the feeds which have those users to display in home view
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        CollectionReference userDocuments = instadb.collection("users");
        //query to fetch logged in user doc, get users following list and check with feeds
        userDocuments.whereEqualTo("userId",userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //retrieve user Details
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.i("DB Success", document.getId() + " => " + document.getData());
                        final List<UserPojo> followingUsers = new ArrayList<>();
                        loggedInUser = returnUser(document);
                        List<DocumentReference> followingList = (ArrayList<DocumentReference>) document.getData().get("followingList");
                        mFollowingList = (ArrayList<DocumentReference>) document.getData().get("followingList");
                        for(DocumentReference userFollowing : followingList){
                            userFollowing.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                      if(task.isSuccessful()) {
                                          if (task.getResult().getId() != null) {
                                              UserPojo followingUser = task.getResult().toObject(UserPojo.class);
                                              followingUsers.add(followingUser);
                                          }
                                      }
                                }
                            });
                        }
                        loggedInUser.setFollowingList(followingUsers);
                        getFeeds();
                        Log.i("Logged User",loggedInUser.getEmail());
                    }
                } else {
                    Log.i("DB ERROR", "Error getting documents.", task.getException());
                }
            }
        });
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

    public ArrayList<UserFeed> getUserFeeds(){
        ArrayList<UserFeed> users=new ArrayList<>();
        UserPojo u1 = new UserPojo("ant_1192","ant_192" , "Ant", "Ant.mav@gmail.com", "abc123");
        UserPojo u2 = new UserPojo("sin_1991","sin_1991" , "Sindhu", "sin.mav@gmail.com", "abc123");
        UserFeed u1f=new UserFeed(u1,"Melbourne, Australia",R.drawable.com_facebook_profile_picture_blank_portrait,"pehla");
        users.add(u1f);
        UserFeed u2f = new UserFeed(u2,"Auckland,New Zealand",R.drawable.messenger_bubble_large_blue,"dusra");
        users.add(u2f);
        return users;
    }

    public void getFeeds(){
        List<UserPojo> following = loggedInUser.getFollowingList();
        List<String> followingUserIds = new ArrayList<>();
        for(UserPojo followingUser : following){
           String uid = followingUser.getUserId();
           followingUserIds.add(uid);
        }
        //query feeds
        for(DocumentReference followingDocRef: mFollowingList){
            Query feedQuery = instadb.collection("feeds").whereEqualTo("user",followingDocRef);
            feedQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot d2: task.getResult()){
                            Log.i("feed1",d2.getId());
                        }
                    }
                }
            });
        }

    }

}
