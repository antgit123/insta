package com.unimelb.projectinsta;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.unimelb.projectinsta.activityfeeds.FollowingFragment;
import com.unimelb.projectinsta.activityfeeds.NotificationFragment;
import com.unimelb.projectinsta.activityfeeds.YouFragment;
import com.unimelb.projectinsta.comments.CommentsFragment;
import com.unimelb.projectinsta.likes.LikesFragment;
import com.unimelb.projectinsta.profile.EnlargedPostViewFragment;
import com.unimelb.projectinsta.profile.ProfileFragment;
import com.unimelb.projectinsta.search.DiscoverFragment;
import com.unimelb.projectinsta.search.SearchDiscover;
import com.unimelb.projectinsta.search.SearchFragment;
import com.unimelb.projectinsta.util.CommonUtil;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, UploadFragment.OnPhotoListener,
        LikesFragment.OnListFragmentInteractionListener,
        NotificationFragment.OnFragmentInteractionListener, FollowingFragment.OnFragmentInteractionListener,
        YouFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener,
        SearchDiscover.OnFragmentInteractionListener,DiscoverFragment.OnFragmentInteractionListener,
        EnlargedPostViewFragment.OnFragmentInteractionListener, CommentsFragment.OnListFragmentInteractionListener {


    static
    {
        System.loadLibrary("NativeImageProcessor");
    }
    private TextView mTextMessage;
    private FragmentManager mFragment = getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Fragment home = new HomeFragment();
                    fragmentTransaction.replace(R.id.fragment_container, home).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_dashboard:
                    Fragment search = new SearchDiscover();
                    fragmentTransaction.replace(R.id.fragment_container, search).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_notifications:
                    fragmentTransaction.replace(R.id.fragment_container, new NotificationFragment()).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_profile:
                    Fragment profile = new ProfileFragment();
                    fragmentTransaction.replace(R.id.fragment_container, profile).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_upload:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, UploadFragment.newInstance()).addToBackStack(null)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //on startup check if fragment is empty and load initial fragment
        loadFragment(new HomeFragment());
        CommonUtil.getInstance();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
        startActivity(discoverableIntent);

    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            CommonUtil.getInstance().setLoggedInUserNull();
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    });

            //logout();
            return true;
        }else if(id == R.id.action_sortDate){
            sortByDate();
            return true;
        }else if(id == R.id.action_sortLocation){
            sortByLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortByDate() {
       if(HomeFragment.changeDate.booleanValue() == Boolean.TRUE){
           HomeFragment.changeDate = Boolean.FALSE;
       }else {
           HomeFragment.changeDate = Boolean.TRUE;
       }
        loadFragment(new HomeFragment());
    }

    private void sortByLocation() {
        if(HomeFragment.changeLocation.booleanValue() == Boolean.TRUE){
            HomeFragment.changeLocation = Boolean.FALSE;
        }else {
            HomeFragment.changeLocation = Boolean.TRUE;
        }
        loadFragment(new HomeFragment());
    }

    private boolean loadFragment(Fragment fragment) {
        //check for empty fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPhotoCaptured(Bitmap mBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        Intent filterEditActivity = new Intent(this,PhotoViewPager.class);
        filterEditActivity.putExtra("imageBitmap",byteArray);
        startActivity(filterEditActivity);
    }

    @Override
    public void onListFragmentInteraction(String item) {

    }
}
