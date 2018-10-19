package com.unimelb.projectinsta;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unimelb.projectinsta.model.UserPojo;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscoverUsersAdapter extends ArrayAdapter<UserPojo> {
    private LayoutInflater mInflator;
    private List<UserPojo> mUsers = null;
    private int layoutResource;

    private Context mContext;

    public DiscoverUsersAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<UserPojo> objects) {

        super(context, resource, objects);
        mContext = context;
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
    }
    private static class ViewHolder {
        TextView username_d, displayname;
        CircleImageView profileimage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null)
        {
            convertView = mInflator.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.username_d = (TextView) convertView.findViewById(R.id.username);
            holder.displayname = (TextView) convertView.findViewById(R.id.display_name);
            holder.profileimage = (CircleImageView) convertView.findViewById(R.id.profile_image);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.username_d.setText(getItem(position).getUserName());
        holder.displayname.setText(getItem(position).getUserRealName());
        Glide.with(getContext())
                .load(getItem(position).getProfilePhoto())
                .into(holder.profileimage);

        return convertView;



    }
}
