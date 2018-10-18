package com.unimelb.projectinsta;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationCaptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationCaptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationCaptionFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location mLocation;
    private  double latitude, longitude;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String address;

    private OnFragmentInteractionListener mListener;

    public LocationCaptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationCaptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationCaptionFragment newInstance(String param1, String param2) {
        LocationCaptionFragment fragment = new LocationCaptionFragment();
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
        //super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_caption, container, false);
        Button shareButton = (Button) view.findViewById(R.id.button_share);
        shareButton.setOnClickListener(this);
        EditText text = view.findViewById(R.id.caption);
        String caption = text.getText().toString();
        TextView locationDetail = view.findViewById(R.id.location_info);
        locationDetail.setText(address);
        return inflater.inflate(R.layout.fragment_location_caption, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            address = ((PhotoViewPager) mListener).address;

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

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.button_share:
                //redirect to home page and save details
                /*fragment = new LocationCaptionFragment();
                replaceFragment(fragment);*/
                final EditText captionTextView = view.findViewById(R.id.caption);
                String caption = captionTextView.getText().toString();
                Log.d("caption", "onClick: "+ caption);
                break;
        }
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
        String onFragmentInteraction();
    }

}

