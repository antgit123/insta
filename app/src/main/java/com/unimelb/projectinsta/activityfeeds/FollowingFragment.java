package com.unimelb.projectinsta.activityfeeds;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.unimelb.projectinsta.model.MyNotificationsPojo;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FollowingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView followingFeedListView;
    private List<FollowingUserNotificationsPojo> followingFeedList = new ArrayList<>();
    private FollowingFeedAdapter notificationAdapter;
    private String currentUserId;

    public FollowingFragment() {
        // Required empty public constructor
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
    // TODO: Rename and change types and number of parameters
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

        getFollowingNotifications();
        notificationAdapter = new FollowingFeedAdapter(getContext(), followingFeedList);
        followingFeedListView.setAdapter(notificationAdapter);
        return view;
    }

    private void getFollowingNotifications() {
        FirebaseFirestore.getInstance().collection("followersNotifications")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                followingFeedList.clear();
                if (e != null) {
                    Log.w("fail", "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    FollowingUserNotificationsPojo notification = doc.toObject(FollowingUserNotificationsPojo.class);
                    if(notification.getUserId().equals(currentUserId)) {
                        followingFeedList.add(notification);
                    }
                }
                notificationAdapter.notifyDataSetChanged();
                }
            });

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
}
