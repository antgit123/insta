package com.unimelb.projectinsta;

import android.app.Activity;
import android.app.ActionBar;
//import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.util.List;

public class PhotoViewPager extends AppCompatActivity implements FilterListFragment.OnListFragmentInteractionListener,EditPhotoFragment.OnFragmentInteractionListener {

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
    FilterListFragment filterListFragment;
    EditPhotoFragment editPhotoFragment;
    int brightnessFinal = 0;
    float saturationFinal, contrastFinal = 1.0f;
    ImageView editPhotoView,filterPhotoView;

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

//        editPhotoView = findViewById(R.id.edit_photo_image_view);
        filterPhotoView = findViewById(R.id.image_clicked);
        imageBitmap = BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
        filterPhotoView.setImageBitmap(imageBitmap);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(List<String> frag) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

//    @Override
//    public void onBrightnessChanged(int brightness) {
//        brightnessFinal = brightness;
//        Filter brightnessFilter = new Filter();
//        brightnessFilter.addSubFilter(new BrightnessSubFilter(brightness));
//        editPhotoView.setImageBitmap(brightnessFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
//    }
//
//    @Override
//    public void onSaturationChanged(float saturation) {
//        saturationFinal = saturation;
//        Filter saturationFilter = new Filter();
//        saturationFilter.addSubFilter(new SaturationSubfilter(saturation));
//        editPhotoView.setImageBitmap(saturationFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
//    }
//
//    @Override
//    public void onContrastChanged(float contrast) {
//        contrastFinal = contrast;
//        Filter contrastFilter = new Filter();
//        contrastFilter.addSubFilter(new ContrastSubFilter(contrast));
//        editPhotoView.setImageBitmap(contrastFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888,true)));
//    }
//
//    @Override
//    public void onEditStarted() {
//
//    }
//
//    @Override
//    public void onEditCompleted() {
//        final Bitmap imageBitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
//        Filter editFilter = new Filter();
//        editFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
//        editFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
//        editFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
//        finalImage = editFilter.processFilter(imageBitmap);
//    }
//
//    private void resetControls() {
//        if (editPhotoFragment != null) {
//            editPhotoFragment.resetControls();
//        }
//        brightnessFinal = 0;
//        saturationFinal = 1.0f;
//        contrastFinal = 1.0f;
//    }
//
//    @Override
//    public void onFilterSelected(Filter filter) {
//        // reset image controls
//        resetControls();
//        // applying the selected filter
//        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
//        // preview filtered image
//        filterPhotoView.setImageBitmap(filter.processFilter(filteredImage));
//        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
//    }

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
                        return filterListFragment;
                case 1: Fragment editPhotoFragment = new EditPhotoFragment();
                        Bundle edit_args = new Bundle();
                        edit_args.putByteArray("photo",bitmapArray);
                        editPhotoFragment.setArguments(edit_args);
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
}
