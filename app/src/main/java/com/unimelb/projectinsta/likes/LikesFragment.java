/*
 The purpose of this java class is to add functionality to display list of user who liked
   a userfeed in the user likes fragment view
 */

package com.unimelb.projectinsta.likes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.Like;
import com.unimelb.projectinsta.model.UserFeed;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LikesFragment extends Fragment implements LikesArrayAdapter.UserItemListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    RecyclerView userListView;
    ImageView likesBackArrow;
    String feedId;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LikesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LikesFragment newInstance(int columnCount) {
        LikesFragment fragment = new LikesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    /*
        On create view method which defines the elements present in the user likes fragment view
        and adding listeners for them
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        userListView = view.findViewById(R.id.listView_followers);
        likesBackArrow = view.findViewById(R.id.backArrow_likes);
        Context context = view.getContext();
        Bundle bundle = getArguments();
        feedId = (String) bundle.get("feedId");
        if (mColumnCount <= 1) {
            userListView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            userListView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        queryUserLikes(feedId);
        likesBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
        Util method to query list of user likes of the selected userfeed from the database
     */
    public void queryUserLikes(String feedId){
        FirebaseFirestore instadb = FirebaseFirestore.getInstance();
        CollectionReference feedDocuments = instadb.collection("feeds");
        feedDocuments.document(feedId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                List<Like> userLikes = new ArrayList<>();
                UserFeed feed;
                LikesArrayAdapter adapter;
                if(task.isSuccessful()){
                    DocumentSnapshot feedDocument = task.getResult();
                    feed = feedDocument.toObject(UserFeed.class);
                    if(feed != null) {
                        userLikes = feed.getLikeList();
                    }else{
                        Toast.makeText(getContext(),"Error fetching feed document",Toast.LENGTH_SHORT).show();
                    }
                    adapter = new LikesArrayAdapter(getContext(),userLikes);
                    adapter.notifyDataSetChanged();
                    userListView.setAdapter(adapter);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error fetching feed document",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserSelected(String userName) {
        Log.i("USER CLICKED",userName);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String item);
    }
}
