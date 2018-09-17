package com.unimelb.projectinsta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfileFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
      //  RecyclerView mUserFeedRecyclerView = (RecyclerView) homeFragmentView.findViewById(R.id.fragment_userfeed_recycler);
     //   mUserFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    //    mUserFeedRecyclerView.setItemAnimator(new DefaultItemAnimator());
       // UserFeedAdapter mAdapter  = new UserFeedAdapter(getContext(),getUsers());
     //   mUserFeedRecyclerView.setAdapter(mAdapter);
        return profileFragmentView;
    }
}
