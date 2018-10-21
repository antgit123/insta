package com.unimelb.projectinsta.activityfeeds;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.FollowingUserNotificationsPojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * A fragment class to display a list of notificaions of followers the current user is following

 */
public class FollowingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView followingFeedListView;
    private List<FollowingUserNotificationsPojo> followingFeedList = new ArrayList<>();
    private FollowingFeedAdapter notificationAdapter;
    private String currentUserId;

    public FollowingFragment() {
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowingFragment.
     */
    public static FollowingFragment newInstance(String param1, String param2) {
        FollowingFragment fragment = new FollowingFragment();
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
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        followingFeedListView = view.findViewById(R.id.listView_followingNotifications);

        //Adapter logic to fetch the list of notifications
        getFollowingNotifications();
        notificationAdapter = new FollowingFeedAdapter(getContext(), followingFeedList);
        followingFeedListView.setAdapter(notificationAdapter);
        return view;
    }

    /**
     * Fetches the list of notifications for the current user logged in
     */
    private void getFollowingNotifications() {
        FirebaseFirestore.getInstance().collection("followersNotifications")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                followingFeedList.clear();
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    FollowingUserNotificationsPojo notification = doc.toObject(FollowingUserNotificationsPojo.class);
                    if(notification.getUserId().equals(currentUserId)) {
                        followingFeedList.add(notification);
                    }
                }
                Collections.sort(followingFeedList);
                notificationAdapter.notifyDataSetChanged();
                }
            });

    }

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
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
