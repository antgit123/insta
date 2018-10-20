package com.unimelb.projectinsta.profile;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.unimelb.projectinsta.DeviceUuidFactory;
import com.unimelb.projectinsta.OnSwipeTouchListener;
import com.unimelb.projectinsta.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EnlargedPostViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EnlargedPostViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnlargedPostViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "haha";

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    private static final int REQUEST_ENABLE_BT = 2;

    BluetoothAdapter bluetoothAdapter;

    private static UUID MY_UUID = null;

    private ClientThread clientThread;

    private volatile boolean isClientRunning = false;

    private static final int MESSAGE_RECEIVED = 0;
    private static final int CONNECTION_SUCCESSFUL = 1;

    InputStream ClientInStream = null;
    OutputStream ClientOutStream = null;

    private static android.os.Handler handler_process = new android.os.Handler(){
        public void handleMessage(Message msg){
            if (msg.what==MESSAGE_RECEIVED){
                Log.d(TAG, msg.obj.toString());
            }else if (msg.what==CONNECTION_SUCCESSFUL){
                Log.d(TAG, "CONNECTION_SUCCESSFUL");
            }
        }
    };

    private IntentFilter filter;

    private static final String[] BLUE_PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public EnlargedPostViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnlargedPostViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnlargedPostViewFragment newInstance(String param1, String param2) {
        EnlargedPostViewFragment fragment = new EnlargedPostViewFragment();
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
        MY_UUID = new DeviceUuidFactory(getContext()).getDeviceUuid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enlarged_self_posts, container, false);
        ImageView enlargedImage = (ImageView) view.findViewById(R.id.enlarged_post);
        Bundle bundle = getArguments();
        String image = bundle.getString("imageUrl");

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(getContext())
                .load(image)
                .apply(options)
                .into(enlargedImage);
        //enlargedImage.buildDrawingCache();
        //Bitmap bitmap = enlargedImage.getDrawingCache();
        //Bitmap bitmap = ((BitmapDrawable)enlargedImage.getDrawable()).getBitmap();

        enlargedImage.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeRight() {
                Toast.makeText(getActivity(), "swipe right!!!", Toast.LENGTH_SHORT).show();
                startBluetoothSensor();
            }
        });
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public void startBluetoothSensor(){

        // This only targets API 23+
        // check permission using a thousand lines (Google is naive!)

        if (!hasPermissionsGranted(BLUE_PERMISSIONS)) {
            requestBluePermissions(BLUE_PERMISSIONS);
            return;
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null)
        {
            Log.d(TAG, "Device has no bluetooth");
            return;
        }

        // ask users to open bluetooth
        if (bluetoothAdapter.isEnabled()==false){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }



        //to start scanning whether there are any other Bluetooth devices
        bluetoothAdapter.startDiscovery();

        //register the BroadcastReceiver to broadcast discovered devices
        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(receiver, filter);

        //return paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, "@ paired devices: "+device.getAddress());
            }
        }
    }

    //Create a BroadcastRecevier for ACTION_FOUND
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //when discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "@ discovered devices: "+device.getAddress());

                // call becomeClient
                // use your server device Bluetooth address
                //
                if (device.getAddress().equals("64:A2:F9:7C:78:F4"))
                {
                    becomeClient(device);
                }

            }
        }
    };

    // be a client

    public void becomeClient(BluetoothDevice device) {
        clientThread = new ClientThread(device);
        clientThread.start();
    }

    private class ClientThread extends Thread{

        private final BluetoothSocket clientSocket;
        private final BluetoothDevice clientDevice;

        private ClientThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            clientDevice = device;
            clientSocket = tmp;
        }

        public void sendData(String data) {
            StringBuffer sb = new StringBuffer();
            sb.append(data);
            sb.append("\n");
            if (ClientOutStream != null) {
                try {
                    ClientOutStream.write(sb.toString().getBytes());
                    ClientOutStream.flush();
                    Log.d(TAG,"@ send data " + sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"@ Client sending fail");
                }
            }
        }

        public void closeClient(){
            this.interrupt();
        }


        public void run() {
            Log.d(TAG, "start client.");

            isClientRunning = true;

            bluetoothAdapter.cancelDiscovery();

            String line = "";

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                clientSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    clientSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // do work with connection

            while (isClientRunning) {

                Log.d(TAG, "do work with connection");

                try {
                    Log.d(TAG, "@ line1"+line);

                    if(ClientInStream == null) {
                        InputStream tmpIn = null;
                        // Get the input and output streams; using temp objects because
                        // member streams are final.
                        try {
                            tmpIn = clientSocket.getInputStream();
                        } catch (Exception e) {
                            Log.e(TAG, "Error occurred when creating input stream", e);
                        }
                        ClientInStream = tmpIn;
                    }

                    if(ClientOutStream == null) {
                        OutputStream tmpOut = null;
                        // Get the input and output streams; using temp objects because
                        // member streams are final.
                        try {
                            tmpOut = clientSocket.getOutputStream();
                        } catch (Exception e) {
                            Log.e(TAG, "Error occurred when creating output stream", e);
                        }
                        ClientOutStream = tmpOut;
                    }

                    // send data to server
                    sendData("Client: Hello!");

                    Log.d(TAG, "254");

                    // listen to server
                    while (isClientRunning &&ClientInStream != null) {

                        Log.d(TAG, "257");

                        BufferedReader br = new BufferedReader(new InputStreamReader(ClientInStream));
                        // readLine() read and delete one line
                        while ((line = br.readLine()) != null) {
                            Log.d(TAG, "@message " + line);
                            // send a message to the UI thread
                            Message message = new Message();
                            message.what = MESSAGE_RECEIVED;
                            message.obj = line;
                            handler_process.sendMessage(message);

                        }


                        if(Thread.currentThread().isInterrupted())
                        {
                            clientSocket.close();
                            Log.d(TAG, "quit from connection");
                            isClientRunning = false;
                            break;
                        }

                    }

                    if(Thread.currentThread().isInterrupted())
                    {
                        clientSocket.close();
                        Log.d(TAG, "quit from connection");
                        isClientRunning = false;
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "@ exception");

                    Log.d(TAG, "quit from connection");

                    try {
                        clientSocket.close();
                    } catch (Exception e2) {
                        Log.e(TAG, "Error occurred in an exception of an exception", e2);
                    }
                    isClientRunning = false;

                    break;
                }

            }

        }

    }



    // check if app has a list of permissions, then request not-granted ones

    public void requestBluePermissions(String[] permissions) {
        Log.d(TAG, "line 376");
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_BLUETOOTH_PERMISSIONS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_BLUETOOTH_PERMISSIONS:
                Log.d(TAG, "line 394");

                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d("haha", "one or more permission denied");
                            return;
                        }
                    }
                    Log.d("haha", "all permissions granted");

                }

        }
    }


    private boolean hasPermissionsGranted(String[] permissions) {
        Log.d(TAG, "411");
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

}
