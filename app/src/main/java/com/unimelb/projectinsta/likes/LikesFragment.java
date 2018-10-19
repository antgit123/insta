package com.unimelb.projectinsta.likes;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.UserPojo;


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
    List<UserPojo> userLikes = new ArrayList<>();
    ImageView likesBackArrow;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        userListView = view.findViewById(R.id.listView_followers);

        // Set the adapter
//        if (view instanceof RecyclerView) {
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            userListView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            userListView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        LikesArrayAdapter adapter = new LikesArrayAdapter(getContext(),getUserLikes(),this);
        userListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        }
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

    public List<UserPojo> getUserLikes(){
        UserPojo u1 = new UserPojo("ant_1192","ant_192" , "Ant", "Ant.mav@gmail.com", "abc123");
        UserPojo u2 = new UserPojo("sin_1991","sin_1991" , "Sindhu", "sin.mav@gmail.com", "abc123");
        UserPojo u3 = new UserPojo("tej_1992","tej_1992" , "Tejal", "tej.mav@gmail.com", "abc123");
        userLikes.add(u1);
        userLikes.add(u2);
        userLikes.add(u3);
        return userLikes;
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
