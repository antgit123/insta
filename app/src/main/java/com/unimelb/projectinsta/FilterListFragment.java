/*
    The purpose of this java class is to implement the fragment functionality for photo
    filters present under filter tab in Edit photo screen
 */

package com.unimelb.projectinsta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.unimelb.projectinsta.util.ThumbnailFormatter;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FilterListFragment extends Fragment implements  ThumbnailAdapter.ThumbnailImageListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private FiltersListFragmentListener mListener;
    RecyclerView thumbNailListView;
    ThumbnailAdapter mAdapter;
    List<ThumbnailItem> thumbnailItemList;
    FiltersListFragmentListener listener;
    public byte[] imageBytes;
    public Bitmap imageBitmap;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FilterListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FilterListFragment newInstance(int columnCount) {
        FilterListFragment fragment = new FilterListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    /*
        On create view method that sets adapter to list of image thumbnail
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thumbnail_list, container, false);
        // Set the adapter
        thumbnailItemList = new ArrayList<>();
        thumbNailListView = view.findViewById(R.id.filter_list);
        mAdapter = new ThumbnailAdapter(getContext(), thumbnailItemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        thumbNailListView.setLayoutManager(mLayoutManager);
        thumbNailListView.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        thumbNailListView.addItemDecoration(new ThumbnailFormatter(space));
        imageBytes = (byte []) getArguments().get("photo");
        imageBitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        prepareThumbnail(imageBitmap);
        thumbNailListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FiltersListFragmentListener) {
            listener = (FiltersListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FiltersListFragmentListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(List<String> frag);
    }

    /*
        Util method to retrieve the list of filters and prepare thumbnail image
     */
    public void prepareThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage;
                thumbImage = bitmap;
                if(bitmap == null){
                    thumbImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                }

                if (thumbImage == null)
                    return;

                ThumbnailsManager.clearThumbs();
                if(thumbnailItemList != null) {
                    thumbnailItemList.clear();
                }else{
                    thumbnailItemList = new ArrayList<>();
                }

                // add normal bitmap first
                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImage;
                thumbnailItem.filterName = getString(R.string.filter_normal);
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters = FilterPack.getFilterPack(getContext());

                for (Filter filter : filters) {
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumbImage;
                    tI.filter = filter;
                    tI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }

                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(getContext()));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        new Thread(r).start();
    }

    /*
        Interface which defines the onFilter selected interaction which gets triggered on
        touching a thumbnail filter item
     */
    @Override
    public void onFilterSelected(Filter filter) {
        if (listener != null)
            listener.onFilterSelected(filter);
    }

    public interface FiltersListFragmentListener {
        void onFilterSelected(Filter filter);
    }
}
