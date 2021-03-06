/*
The purpose of this class file is to implement the view pager which displays the fragments
for displaying image filters and editing filters.
The image filters are filters which change the look of the image by adding effects to it.
The edit filters are used for changing the brightness, contrast and saturation of the image clicked
*/


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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
        EditPhotoFragment.EditPhotoFragmentListener,ActivityCompat.OnRequestPermissionsResultCallback{

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
        RequestOptions options = new RequestOptions();
        Glide.with(this)
                .asBitmap()
                .load(imageBitmap).apply(options)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        filterPhotoView.setImageBitmap(resource);
                    }
                });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*
    util method to request permission to write image to storage
     */
    public void requestWritePermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new PhotoViewPager.ConfirmationDialog().show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }

    /*
        util method which gets called based on user input on dialog permission
   */
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
        // select image from another gallery on click of button
        if (id ==R.id.select_gallery_photo){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            return true;
        }
        // process crop functionality on click of crop button
        if (id == R.id.crop_existing_photo){
            performCrop(clickedImageUri);
            return true;
        }
        if(id == R.id.next_edit_action){
            //send final filtered image to share Activity on click of next
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            finalImage.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] byteArray = stream.toByteArray();
            Intent shareActivity = new Intent(this,LocationActivity.class);
            shareActivity.putExtra("shareImage",byteArray);
            startActivity(shareActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        Util method which gets called on performing cropping functionality or selecting image
        from gallery
        Cropping functionality returns the processed crop photo and select image from gallery
        returns the gallery from image
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (requestCode == PICK_IMAGE || requestCode == CROP_IMAGE)) {
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
                Glide.with(this)
                        .asBitmap()
                        .load(selectedImage)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                filterPhotoView.setImageBitmap(resource);
                            }
                        });
                FilterListFragment filterListFragment = (FilterListFragment) fragmentHashMap.get(0);
                filterListFragment.prepareThumbnail(originalImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        Util method to change brightness of selected image
     */
    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter brightnessFilter = new Filter();
        brightnessFilter.addSubFilter(new BrightnessSubFilter(brightness));
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888,true);
        filterPhotoView.setImageBitmap(brightnessFilter.processFilter(filteredImage));
    }

    /*
        Util method to change saturation of selected image
     */
    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter saturationFilter = new Filter();
        saturationFilter.addSubFilter(new SaturationSubfilter(saturation));
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888,true);
        filterPhotoView.setImageBitmap(saturationFilter.processFilter(filteredImage));
    }

    /*
        Util method to change contrast of selected image
     */
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

    /*
        Util method to apply all edit filter changes and convert filtered image to final image
     */
    @Override
    public void onEditCompleted() {
        final Bitmap imageBitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
        Filter editFilter = new Filter();
        editFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        editFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        editFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        finalImage = editFilter.processFilter(imageBitmap);
    }

    /*
        Util method to reset controls of edit filter seekbars
     */
    private void resetControls() {
        if (editPhotoFragment != null) {
            editPhotoFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    /*
        Util method to apply filtering changes
     */
    @Override
    public void onFilterSelected(Filter filter) {
        // Function to add filtering changes to original clicked image
        resetControls();
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        filterPhotoView.setImageBitmap(filter.processFilter(filteredImage));
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    /*
        Util method to open android app cropping functionality
    */
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
