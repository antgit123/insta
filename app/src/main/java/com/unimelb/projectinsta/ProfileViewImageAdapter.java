package com.unimelb.projectinsta;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

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
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View grid = convertView;

        if(grid == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.custom_profile_view_images_layout, null);
        }

        ImageView image = (ImageView) grid.findViewById(R.id.myPostedImage);
//        image.setImageURI(imageList.get(position));
        return grid;
    }
}
