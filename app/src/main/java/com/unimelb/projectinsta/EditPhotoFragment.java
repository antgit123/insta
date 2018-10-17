package com.unimelb.projectinsta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.nio.channels.SeekableByteChannel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPhotoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditPhotoFragmentListener photoListener;
    public byte[] bitmapArray;
    public Bitmap bitmapImage;
    public SeekBar brightnessSeekbar;
    public SeekBar contrastSeekbar;
    public SeekBar saturationSeekbar;
    ImageView editphotoView;

    public EditPhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditPhotoFragment newInstance(String param1, String param2) {
        EditPhotoFragment fragment = new EditPhotoFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_photo_fragment,container,false);
//        editphotoView = view.findViewById(R.id.edit_photo_image_view);
        bitmapArray = (byte []) getArguments().get("photo");
        if(bitmapArray != null) {
            bitmapImage = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        }
//        brightnessSeekbar.setMax(200);
//        brightnessSeekbar.setProgress(100);
//        contrastSeekbar.setMax(20);
//        contrastSeekbar.setProgress(0);
//        saturationSeekbar.setMax(30);
//        saturationSeekbar.setProgress(10);

//        brightnessSeekbar.setOnSeekBarChangeListener(this);
//        contrastSeekbar.setOnSeekBarChangeListener(this);
//        saturationSeekbar.setOnSeekBarChangeListener(this);
//        editphotoView.setImageBitmap(bitmapImage);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditPhotoFragmentListener) {
            photoListener = (EditPhotoFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EditPhotoFragmentListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
//        if (photoListener != null) {
//
//            if (seekBar.getId() == R.id.seekbar_brightness) {
//                // brightness values are b/w -100 to +100
//                photoListener.onBrightnessChanged(progress - 100);
//            }
//
//            if (seekBar.getId() == R.id.seekbar_contrast) {
//                // converting int value to float
//                // contrast values are b/w 1.0f - 3.0f
//                // progress = progress > 10 ? progress : 10;
//                progress += 10;
//                float floatVal = .10f * progress;
//                photoListener.onContrastChanged(floatVal);
//            }
//
//            if (seekBar.getId() == R.id.seekbar_saturation) {
//                // converting int value to float
//                // saturation values are b/w 0.0f - 3.0f
//                float floatVal = .10f * progress;
//                photoListener.onSaturationChanged(floatVal);
//            }
//        }
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//        if (photoListener != null)
//            photoListener.onEditStarted();
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        if (photoListener != null)
//            photoListener.onEditCompleted();
//    }
//
    public void resetControls() {
        brightnessSeekbar.setProgress(100);
        contrastSeekbar.setProgress(0);
        saturationSeekbar.setProgress(10);
    }

    public interface EditPhotoFragmentListener {
        void onBrightnessChanged(int brightness);

        void onSaturationChanged(float saturation);

        void onContrastChanged(float contrast);

        void onEditStarted();

        void onEditCompleted();
    }
}
