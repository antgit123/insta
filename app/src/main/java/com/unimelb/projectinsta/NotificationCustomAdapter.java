package com.unimelb.projectinsta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class NotificationCustomAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> followingList;

    public NotificationCustomAdapter(Context context, ArrayList<String> followingNotificationList) {
        this.context = context;
        this.followingList = followingNotificationList;
    }

    @Override
    public int getCount() {
        return followingList.size();
    }

    @Override
    public Object getItem(int position) {
        return followingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return followingList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;

        if(listView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listView = inflater.inflate(R.layout.following_list_view_item, null);
        }

        ImageView image = (ImageView) listView.findViewById(R.id.myListImage);

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(context)
                .load(followingList.get(position))
                .apply(options)
                .into(image);
//        image.setImageURI(imageList.get(position));
        return listView;
    }
}
