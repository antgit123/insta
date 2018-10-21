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
import com.unimelb.projectinsta.model.MyNotificationsPojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * A fragment which displays a list of all the notifications
 */
public class YouFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    RecyclerView myFeedRecyclerView;
    private List<MyNotificationsPojo> myNotifications = new ArrayList<>();
    private MyFeedAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private String currentUserId;

    public YouFragment() {
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YouFragment.
     */
    public static YouFragment newInstance(String param1, String param2) {
        YouFragment fragment = new YouFragment();
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
        View view = inflater.inflate(R.layout.fragment_you, container, false);
        myFeedRecyclerView = view.findViewById(R.id.listView_myNotifications);

        //Adapter logic to load the list in the recycler view
        getMyNotifications();
        adapter = new MyFeedAdapter(getContext(),myNotifications);
        myFeedRecyclerView.setAdapter(adapter);
        return view;
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
     * Fetches all the notifications for the current user and displays notifications
     * with recent ones displayed first
     */
    public void getMyNotifications() {
        FirebaseFirestore.getInstance().collection("myNotifications")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    myNotifications.clear();
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    MyNotificationsPojo notification = doc.toObject(MyNotificationsPojo.class);
                    if(notification.getUserId().equals(currentUserId)) {
                        myNotifications.add(notification);
                    }
                }
                Collections.sort(myNotifications);
                adapter.notifyDataSetChanged();
                }
            });
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
