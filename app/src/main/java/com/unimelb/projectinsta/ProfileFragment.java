package com.unimelb.projectinsta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.internal.LockOnGetVariable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unimelb.projectinsta.model.UserPojo;
import com.unimelb.projectinsta.util.DatabaseUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int PICK_IMAGE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView profileImageView;
    private UserPojo currentUser = null;
    private GridView gridViewofPost;
    FirebaseUser user;
    String userId;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore instadb = FirebaseFirestore.getInstance();
    }

    /**
     * Use this factory method to create a new instance of
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View profileFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = profileFragmentView.findViewById(R.id.profile_pic);
        registerForContextMenu(profileImageView);

        gridViewofPost = profileFragmentView.findViewById(R.id.gridView);

        DatabaseUtil db = new DatabaseUtil();
        ArrayList<String> imageList = db.getImagesPosted(currentUser);
        ProfileViewImageAdapter imageAdapter = new ProfileViewImageAdapter(getContext(), imageList);
        gridViewofPost.setAdapter(imageAdapter);

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
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Upload profile pic")) { //Add pic
            Log.d("debug", "onContextItemSelected: profile pic");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } else { //Remove pic
            Log.d("debug", "onContextItemSelected: remove pic");
            profileImageView.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
        }

        return super.onContextItemSelected(item);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("debug", "onActivityResult: picked pic from gallery");
        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            profileImageView.setImageBitmap(selectedImage);
            encodeBitmapAndSaveToFirebase(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadtask;
        int n = 100;
        n = new Random().nextInt(n);
        String fname = "Image-" + n ;
        String path = "images/"+fname;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(path);

        uploadtask = imageRef.putBytes(data);
        uploadtask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i("PHOTO FAIL", "Upload Failed");


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("test", "onSuccess: uri= "+ uri.toString());
                        updateUserProfile(currentUser, uri.toString());
                    }
                });
            }
        });
    }

    private void updateUserProfile(UserPojo user, String uri) {
        user.setProfilePhoto(uri);
        FirebaseFirestore instadb = FirebaseFirestore.getInstance();
        DocumentReference userRef = instadb.collection("users")
                .document(user.getUserId());
        userRef.set(user);
    }

    private void addUserDetails(final View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        FirebaseFirestore instadb = FirebaseFirestore.getInstance();
        CollectionReference userDocuments = instadb.collection("users");
        userDocuments.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //retrieve user Details
                    DocumentSnapshot document = task.getResult();
                    Log.i("DB Success", document.getId() + " => " + document.getData());
                    currentUser = document.toObject(UserPojo.class);
                    TextView name = (TextView) v.findViewById(R.id.profile_name);
                    TextView posts = (TextView) v.findViewById(R.id.no_of_posts);
                    TextView followers = (TextView) v.findViewById(R.id.no_of_followers);
                    TextView followings = (TextView) v.findViewById(R.id.no_of_followings);
                    ImageView profilePic = v.findViewById(R.id.profile_pic);
                    int followersValue = currentUser.getFollowerList().size();
                    int followingValue= currentUser.getFollowingList().size();
                    String realName = currentUser.getUserRealName();
                    String url = currentUser.getProfilePhoto();
                    name.setText(realName);
                    followers.setText(Integer.toString(followersValue));
                    followings.setText(Integer.toString(followingValue));

                    if(url != "") {
                        Glide.with(ProfileFragment.this)
                                .load(url)
                                .into(profilePic);
                    }
                } else {
                    Log.i("DB ERROR", "Error getting documents.", task.getException());
                }
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
