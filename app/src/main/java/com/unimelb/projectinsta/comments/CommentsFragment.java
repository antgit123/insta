package com.unimelb.projectinsta.comments;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.likes.LikesArrayAdapter;
import com.unimelb.projectinsta.model.Comment;
import com.unimelb.projectinsta.model.UserFeed;
import com.unimelb.projectinsta.util.DatabaseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.unimelb.projectinsta.likes.LikesFragment.OnListFragmentInteractionListener}
 * interface.
 */
public class CommentsFragment extends Fragment implements LikesArrayAdapter.UserItemListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    RecyclerView commentsListView;
    ImageView commentsBackArrow;
    TextView postComment;
    EditText postCommentText;
    String feedId, trigger;
    DatabaseUtil dbUtil = new DatabaseUtil();
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommentsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CommentsFragment newInstance(int columnCount) {
        CommentsFragment fragment = new CommentsFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_comments, container, false);
        commentsListView = view.findViewById(R.id.listView_comments);
        postCommentText = view.findViewById(R.id.edit_comment);
        postComment = view.findViewById(R.id.post_comment_text);

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == postComment.getId()) {
                    Toast.makeText(getContext(),"aaa",Toast.LENGTH_SHORT);
                    if (postCommentText.getText().toString().equals("")) {
                        postCommentText.setEnabled(false);
                    } else {
                        queryUserComments(feedId, true);
                    }
                }
            }
        });
        postComment.setEnabled(false);
        Context context = view.getContext();
        Bundle bundle = getArguments();
        feedId = (String) bundle.get("feedId");
        trigger = (String) bundle.get("trigger");
        if (mColumnCount <= 1) {
            commentsListView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            commentsListView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        queryUserComments(feedId,false);
        if(trigger.equals("icon")){
            postCommentText.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
        if(postCommentText.length() > 0){
            postComment.setEnabled(true);
        }else{
            postComment.setEnabled(false);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof com.unimelb.projectinsta.likes.LikesFragment.OnListFragmentInteractionListener) {
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

    public void queryUserComments(String feedId, final boolean postComment){
        FirebaseFirestore instadb = FirebaseFirestore.getInstance();
        CollectionReference feedDocuments = instadb.collection("feeds");
        feedDocuments.document(feedId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                List<Comment> userComments = new ArrayList<>();
                UserFeed feed;
                CommentsAdapter adapter;
                if(task.isSuccessful()){
                    DocumentSnapshot feedDocument = task.getResult();
                    feed = feedDocument.toObject(UserFeed.class);
                    if(postComment){
//                        dbUtil.postComment(getContext(),feed,postCommentText.getText().toString(),postCommentText);
                    }else {
                        if (feed != null) {
                            userComments = feed.getCommentList();
                        } else {
                            Toast.makeText(getContext(), "Error fetching feed document", Toast.LENGTH_SHORT).show();
                        }
                        adapter = new CommentsAdapter(getContext(), userComments);
                        commentsListView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error fetching feed document",Toast.LENGTH_SHORT);
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

