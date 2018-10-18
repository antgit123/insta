package com.unimelb.projectinsta;

import android.Manifest;
import android.app.Activity;
import android.app.ActionBar;
//import android.app.FragmentTransaction;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unimelb.projectinsta.util.BitmapUtils;
import com.unimelb.projectinsta.util.DatabaseUtil;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PhotoViewPager extends AppCompatActivity implements FilterListFragment.FiltersListFragmentListener,
        EditPhotoFragment.EditPhotoFragmentListener,ActivityCompat.OnRequestPermissionsResultCallback,
        LocationCaptionFragment.OnFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public byte[] bitmapArray;
    public Bitmap imageBitmap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location myLocation;
    public String address;

    Bitmap originalImage;
    Bitmap filteredImage;
    Bitmap finalImage;
    Uri clickedImageUri;
    FilterListFragment filterListFragment;
    EditPhotoFragment editPhotoFragment;
    int brightnessFinal = 0;
    float saturationFinal, contrastFinal = 1.0f;
    ImageView filterPhotoView;
    public static final int PICK_IMAGE = 101;
    public static final int CROP_IMAGE = 102;
    public static final int REQUEST_WRITE_PERMISSION = 103;
    private static final String FRAGMENT_DIALOG = "dialog";
    private HashMap<Integer, Fragment> fragmentHashMap = new HashMap<>();

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view_pager);
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Intent photoContent = getIntent();
        bitmapArray = photoContent.getByteArrayExtra("imageBitmap");
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        filterPhotoView = findViewById(R.id.image_clicked);
        imageBitmap = BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
        originalImage = imageBitmap;
        finalImage = originalImage;
        if (ContextCompat.checkSelfPermission(PhotoViewPager.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted to write to external storage
            requestWritePermission();
        }else {
            //permission granted store image
            String img_path = BitmapUtils.insertImage(getContentResolver(), originalImage, System.currentTimeMillis() + "_pic.jpg", null);
            clickedImageUri = Uri.parse(img_path);
        }
        filterPhotoView.setImageBitmap(imageBitmap);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("location", "onLocationChanged: "+ location);
                myLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            /*if (locationManager != null){
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }*/
        }
        address = getAddreess(myLocation);
        //TextView locationDetail = findViewById(R.id.location_info);
        //locationDetail.setText(address);

        // Set up the action bar.
//        final ActionBar actionBar = getActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
//        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                actionBar.setSelectedNavigationItem(position);
//            }
//        });

        // For each of the sections in the app, add a tab to the action bar.
//        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//            // Create a tab with text corresponding to the page title defined by
//            // the adapter. Also specify this Activity object, which implements
//            // the TabListener interface, as the callback (listener) for when
//            // this tab is selected.
//            actionBar.addTab(
//                    actionBar.newTab()
//                            .setText(mSectionsPagerAdapter.getPageTitle(i))
//                            .setTabListener(this));
//        }

    }

    public void requestWritePermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new PhotoViewPager.ConfirmationDialog().show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                PhotoViewPager.ErrorDialog.newInstance(getString(R.string.request_write_permission))
                        .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            //for storing image on first request permission
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            BitmapUtils.insertImage(getContentResolver(),originalImage,System.currentTimeMillis() + "_pic.jpg", null);
        }
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private String getAddreess(Location location){
        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
            String city = addressList.get(0).getLocality();
            String country = addressList.get(0).getCountryName();
            return country + ", "+city;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String onFragmentInteraction() {
        return address;
    }

    public void share(View view) {
        Log.d("test", "share: ");
        savePost("test");
        ImageView imageView = view.findViewById(R.id.imageView);
        Bitmap bm=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        encodeBitmapAndSaveToFirebase(bm);

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
                        savePost(uri.toString());
                    }
                });
            }
        });
    }

    private void savePost(String string) {
        DatabaseUtil databaseUtil = new DatabaseUtil(PhotoViewPager.this);
        databaseUtil.savePost(string);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_view_pager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id ==R.id.select_gallery_photo){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            return true;
        }
        if (id == R.id.crop_existing_photo){
            performCrop(clickedImageUri);
            return true;
        }
        if(id == R.id.next_edit_action){
            //send final image to share Activity
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            finalImage.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] byteArray = stream.toByteArray();
            Intent shareActivity = new Intent(this,LocationActivity.class);
            shareActivity.putExtra("shareImage",byteArray);
            startActivity(shareActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (requestCode == PICK_IMAGE || requestCode == CROP_IMAGE)) {
//            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);
            try {
                final Uri imageUri = data.getData();
                clickedImageUri = imageUri;
                final InputStream imageStream = getApplicationContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                // clear bitmap memory
                originalImage.recycle();
                finalImage.recycle();
                originalImage = selectedImage.copy(Bitmap.Config.ARGB_8888, true);
                finalImage = originalImage;
                filterPhotoView.setImageBitmap(selectedImage);
                FilterListFragment filterListFragment = (FilterListFragment) fragmentHashMap.get(0);
                filterListFragment.prepareThumbnail(originalImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter brightnessFilter = new Filter();
        brightnessFilter.addSubFilter(new BrightnessSubFilter(brightness));
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888,true);
        filterPhotoView.setImageBitmap(brightnessFilter.processFilter(filteredImage));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter saturationFilter = new Filter();
        saturationFilter.addSubFilter(new SaturationSubfilter(saturation));
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888,true);
        filterPhotoView.setImageBitmap(saturationFilter.processFilter(filteredImage));
    }

    @Override
    public void onContrastChanged(float contrast) {
        contrastFinal = contrast;
        Filter contrastFilter = new Filter();
        contrastFilter.addSubFilter(new ContrastSubFilter(contrast));
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888,true);
        filterPhotoView.setImageBitmap(contrastFilter.processFilter(filteredImage));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        final Bitmap imageBitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
        Filter editFilter = new Filter();
        editFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        editFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        editFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        finalImage = editFilter.processFilter(imageBitmap);
    }

    private void resetControls() {
        if (editPhotoFragment != null) {
            editPhotoFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    @Override
    public void onFilterSelected(Filter filter) {
        // Function to add filtering changes to original clicked image
        resetControls();
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        filterPhotoView.setImageBitmap(filter.processFilter(filteredImage));
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_IMAGE);
        }
        // error message where activity is not supported
        catch (ActivityNotFoundException e) {
            // display an error message
            String errorMessage = e.getMessage();
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


//    @Override
//    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//        // When the given tab is selected, switch to the corresponding page in
//        // the ViewPager.
//        mViewPager.setCurrentItem(tab.getPosition());
//    }
//
//    @Override
//    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//    }
//
//    @Override
//    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch(position){
                case 0: Fragment filterListFragment = new FilterListFragment();
                    Bundle args = new Bundle();
                    args.putByteArray("photo",bitmapArray);
                    filterListFragment.setArguments(args);
                    if(!fragmentHashMap.containsKey(position)) {
                        fragmentHashMap.put(position, filterListFragment);
                    }
                    return filterListFragment;
                case 1: Fragment editPhotoFragment = new EditPhotoFragment();
                    Bundle edit_args = new Bundle();
                    edit_args.putByteArray("photo",bitmapArray);
                    editPhotoFragment.setArguments(edit_args);
                    if(!fragmentHashMap.containsKey(position)) {
                        fragmentHashMap.put(position, editPhotoFragment);
                    }
                    return editPhotoFragment;
                default: return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.filter_fragment_title);
                case 1:
                    return getString(R.string.edit_fragment_title);
            }
            return null;
        }
    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static PhotoViewPager.ErrorDialog newInstance(String message) {
            PhotoViewPager.ErrorDialog dialog = new PhotoViewPager.ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }
    }

    /**
     * Shows OK/Cancel confirmation dialog for accessing storage permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_write_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

}
