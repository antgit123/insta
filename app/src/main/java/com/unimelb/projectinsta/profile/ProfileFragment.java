package com.unimelb.projectinsta.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.model.UserPojo;
import com.unimelb.projectinsta.util.CommonUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * A fragment for the entire profile view
 */
public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int PICK_IMAGE = 1;

    private String mParam1;
    private String mParam2;
    private ImageView profileImageView;
    private UserPojo currentUser = null;
    private GridView gridViewofPost;
    private ArrayList<String> postedImagesList = new ArrayList<>();
    private FirebaseFirestore instadb = FirebaseFirestore.getInstance();
    private ProfileViewImageAdapter imageAdapter;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        currentUser = CommonUtil.getInstance().getLoggedInUser();
    }

    /**
     * This factory method create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View profileFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = profileFragmentView.findViewById(R.id.profile_pic);
        registerForContextMenu(profileImageView);

        gridViewofPost = profileFragmentView.findViewById(R.id.gridView);

        addUserDetails(profileFragmentView);
        return profileFragmentView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.profile_pic) {
            menu.add(0, v.getId(), 0, "Upload profile pic");
            menu.add(0,v.getId(), 0, "Remove profile pic");
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem sortDateItem=menu.findItem(R.id.action_sortDate);
        sortDateItem.setVisible(false);
        MenuItem sortLocationItem=menu.findItem(R.id.action_sortLocation);
        sortLocationItem.setVisible(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Upload profile pic")) { //Add pic
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } else { //Remove pic
            profileImageView.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            updateUserProfile(currentUser, null);
        }

        return super.onContextItemSelected(item);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            encodeBitmapAndSaveToFirebase(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the image bitmap (profle photo) to firebase
     */
    private void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadtask;
        Random r = new Random();
        int low = 2000;
        int high = 3000;
        int n = r.nextInt(high-low) + low;
        String fname = "Image-" + n ;
        String path = "images/"+fname;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(path);

        uploadtask = imageRef.putBytes(data);
        uploadtask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    updateUserProfile(currentUser, uri.toString());
                }
            });
            }
        });
    }

    /**
     * Updates the profile pic.
     */
    private void updateUserProfile(UserPojo user, String uri) {
        user.setProfilePhoto(uri);
        DocumentReference userRef = instadb.collection("users")
                .document(user.getUserId());
        userRef.set(user);
        RequestOptions options = new RequestOptions();
        options.centerCrop();

        if(uri != null) {
            Glide.with(ProfileFragment.this)
                    .load(uri)
                    .apply(options)
                    .into(profileImageView);
        } else {
            Glide.with(ProfileFragment.this)
                    .load(R.drawable.com_facebook_profile_picture_blank_square)
                    .into(profileImageView);
        }
    }

    private void addUserDetails(final View v) {
        addPostedImages(v);
        setValuesToProfile(v);
    }

    /**
     * This function fetches the posts and display it in the grid view.
     */
    private void addPostedImages(View view) {
        postedImagesList = getImagesPosted(view);
        imageAdapter = new ProfileViewImageAdapter(getContext(), postedImagesList);
        gridViewofPost.setAdapter(imageAdapter);
    }

    /**
     * This function fetches all the posts added by the current user from firebase.
     */
    private ArrayList<String> getImagesPosted(final View view) {
        CollectionReference feedsDocuments = instadb.collection("feeds");
        feedsDocuments.whereEqualTo("userId", currentUser.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    postedImagesList.clear();
                    //retrieve user Details
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        postedImagesList.add((String) document.get("photo"));
                    }
                    imageAdapter.notifyDataSetChanged();
                    TextView posts = (TextView)view.findViewById(R.id.no_of_posts);
                    posts.setText((Integer.toString(postedImagesList.size())));
                }
            }
        });

        return postedImagesList;
    }

    /**
     * This function sets the values fetched from firebase to the views
     * Values include - Followers, Following, Posts, Profile pic.
     */
    private void setValuesToProfile(View view) {
        TextView name = (TextView) view.findViewById(R.id.profile_name);
        TextView followers = (TextView) view.findViewById(R.id.no_of_followers);
        TextView followings = (TextView) view.findViewById(R.id.no_of_followings);
        ImageView profilePic = view.findViewById(R.id.profile_pic);
        int followersValue = currentUser.getFollowerList().size();
        int followingValue= currentUser.getFollowingList().size();
        String realName = currentUser.getUserRealName();
        String url = currentUser.getProfilePhoto();
        name.setText(realName);
        followers.setText(Integer.toString(followersValue));
        followings.setText(Integer.toString(followingValue));

        if(url != null) {
            RequestOptions options = new RequestOptions();
            options.centerCrop();

            Glide.with(ProfileFragment.this)
                    .load(url)
                    .apply(options)
                    .into(profilePic);
        }
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
