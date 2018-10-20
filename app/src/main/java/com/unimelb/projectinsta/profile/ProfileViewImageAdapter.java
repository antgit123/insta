package com.unimelb.projectinsta.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.unimelb.projectinsta.MainActivity;
import com.unimelb.projectinsta.R;
import com.unimelb.projectinsta.profile.EnlargedPostViewFragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ProfileViewImageAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> imageList;

    ProfileViewImageAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public long getItemId(int position) {
        return imageList.get(position).hashCode();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View grid = convertView;

        if(grid == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.custom_profile_view_images_layout, null);
        }

        ImageView image = (ImageView) grid.findViewById(R.id.myPostedImage);
//        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
//        ByteArrayOutputStream stream=new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//        final byte[] imagebytes=stream.toByteArray();

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(context)
                .load(imageList.get(position))
                .apply(options)
                .into(image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnlargedPostViewFragment enlargedPostView  = new EnlargedPostViewFragment();
                MainActivity mainActivity = (MainActivity)context;
                Bundle postBundle = new Bundle();
                postBundle.putString("imageUrl",imageList.get(position));
                enlargedPostView.setArguments(postBundle);
                mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, enlargedPostView).addToBackStack(null).commit();
            }
        });

        return grid;
    }
}
