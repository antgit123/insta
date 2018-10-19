package com.unimelb.projectinsta;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unimelb.projectinsta.model.UserPojo;
import com.unimelb.projectinsta.DiscoverUsersAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiscoverFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listView;
    public FirebaseFirestore instadb = FirebaseFirestore.getInstance();
    private List<String> mFollowingList;
    private List<UserPojo> mSuggestList;
    private List<UserPojo> allUserList;
    private UserPojo currentUser = null;
    private int required_common_following=1;
    private DiscoverUsersAdapter discoverUsersAdapter;

    private OnFragmentInteractionListener mListener;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
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
        View view= inflater.inflate(R.layout.fragment_discover, container, false);
        listView=view.findViewById(R.id.list_view);
        getSuggestedUsers();










        return view;

    }

    private void getSuggestedUsers() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        allUserList=new ArrayList<UserPojo>();
        mSuggestList=new ArrayList<UserPojo>();
        String userId = user.getUid();


        CollectionReference FollowingUserDocuments=instadb.collection("users");
        FollowingUserDocuments.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //retrieve user Details
                    DocumentSnapshot document = task.getResult();
                    currentUser = document.toObject(UserPojo.class);
                    mFollowingList=currentUser.getFollowingList();

                }


                }
        });


        CollectionReference userDocuments = instadb.collection("users");
        userDocuments.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        //Log.d("jj", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                        //mUserList.add(documentSnapshot.toObject(UserPojo.class));
                        allUserList.add(documentSnapshot.toObject(UserPojo.class));
                    }
                }
                    }
        });

    for (int i = 0; i < allUserList.size(); i++) {
        if (allUserList.get(i) == currentUser) {
            continue;
        } else if (mFollowingList.contains(allUserList.get(i))) {
            continue;
        } else {
            List<String> templist = new ArrayList<String>(allUserList.get(i).getFollowingList());
            templist.retainAll(mFollowingList);
            if (templist != null) {
                if (templist.size() >= required_common_following) {
                    mSuggestList.add(allUserList.get(i));
                    updateSuggestionList();
                }
            }
        }
    }
}






    private void updateSuggestionList() {
        discoverUsersAdapter = new DiscoverUsersAdapter(getContext(), R.layout.discover_userlist, mSuggestList);
        listView.setAdapter(discoverUsersAdapter);



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
