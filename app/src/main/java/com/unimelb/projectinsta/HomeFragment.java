package com.unimelb.projectinsta;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unimelb.projectinsta.model.UserFeed;
import com.unimelb.projectinsta.model.UserPojo;
import com.unimelb.projectinsta.util.CommonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public List<DocumentReference> mFollowingList;
    public List<UserFeed> feeds = new ArrayList<>();
    public List<DocumentReference> comments;
    public UserPojo loggedInUser = new UserPojo();
    public RecyclerView mUserFeedRecyclerView;
    private TextView nofeedsTextView;
    public UserFeedAdapter mAdapter;
    public FirebaseFirestore instadb = FirebaseFirestore.getInstance();
    public static Boolean changeDate = Boolean.TRUE;
    public static Boolean changeLocation = Boolean.TRUE;
    private static Location myLocation = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "haha";

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    private static final int REQUEST_ENABLE_BT = 2;

    BluetoothAdapter bluetoothAdapter;

    private static UUID MY_UUID = null;

    private ServerThread serverThread;

    private volatile boolean isServerRunning = false;

    private Location tempLocation1 = new Location(LocationManager.NETWORK_PROVIDER);
    private Location tempLocation2 = new Location(LocationManager.NETWORK_PROVIDER);

    private static final int MESSAGE_RECEIVED = 0;
    private static final int CONNECTION_SUCCESSFUL = 1;

    InputStream ServerInStream = null;
    OutputStream ServerOutStream = null;

    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private static final int LOCATION_REQUEST_CODE = 123;
    // Declaring a Location Manager
    protected LocationManager locationManager;

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


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        if (myLocation == null){
            myLocation = getLocation();
        }
        startBluetoothSensor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loggedInUser = CommonUtil.getInstance().getLoggedInUser();
        if(loggedInUser != null) {
            getFeeds();
        } else {
            queryUserFeeds();
        }
        View homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        mUserFeedRecyclerView = (RecyclerView) homeFragmentView.findViewById(R.id.fragment_userfeed_recycler);
        mUserFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUserFeedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        nofeedsTextView = (TextView) homeFragmentView.findViewById(R.id.no_feed_display_text);
        mAdapter = new UserFeedAdapter(getContext(),feeds);
        mUserFeedRecyclerView.setAdapter(mAdapter);
        return homeFragmentView;
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

    public void queryUserFeeds(){
        //fetches user doc, followed by users following list and the feeds which have those users to display in home view
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        CollectionReference userDocuments = instadb.collection("users");
        userDocuments.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //retrieve user Details
                    DocumentSnapshot document = task.getResult();
                    Log.i("DB Success", document.getId() + " => " + document.getData());
                    loggedInUser = document.toObject(UserPojo.class);
                    getFeeds();
                }
            }
        });
    }

    public void getFeeds() {
        final Boolean[] hasFeeds = {false};

        List<String> followingUserIds = loggedInUser.getFollowingList();
        followingUserIds.add(loggedInUser.getUserId()); //Adding current user's post too.
        if (followingUserIds.size() > 0) {
            //query feeds
            feeds.clear();
            for(String followingId : followingUserIds) {
                Query feedQuery = instadb.collection("feeds").whereEqualTo("userId",followingId);
                feedQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot userFeedDocument : task.getResult()) {
                                UserFeed userFeed = userFeedDocument.toObject(UserFeed.class);
                                feeds.add(userFeed);
                                if(!hasFeeds[0]) {
                                    hasFeeds[0] = true;
                                }
                            }
                        }
                        if(hasFeeds[0]) {
                            nofeedsTextView.setVisibility(View.INVISIBLE);
                        } else {
                            nofeedsTextView.setVisibility(View.VISIBLE);
                        }
                        if(changeDate){
                            Collections.sort(feeds, new Comparator<UserFeed>() {
                                public int compare(UserFeed o1, UserFeed o2) {
                                    if (o1.getDate() == null || o2.getDate() == null)
                                        return 0;
                                    return o1.getDate().compareTo(o2.getDate());
                                }
                            });
                        }else {
                            Collections.sort(feeds);
                        }

//                        if(changeLocation){
//                            Collections.sort(feeds, new Comparator<UserFeed>() {
//                                public int compare(UserFeed o1, UserFeed o2) {
//                                    tempLocation1.setLatitude(o1.getLatitude());
//                                    tempLocation1.setLongitude(o1.getLongitude());
//                                    tempLocation2.setLatitude(o2.getLatitude());
//                                    tempLocation2.setLongitude(o2.getLongitude());
//                                    if (tempLocation1.distanceTo(myLocation) > tempLocation2.distanceTo(myLocation))
//                                        return 1;
//                                    else if (tempLocation1.distanceTo(myLocation) < tempLocation2.distanceTo(myLocation))
//                                        return -1;
//                                    else
//                                        return 0;
//                                }
//                            });
//
//                            System.out.println("comparator");
//
//                        }else {
//                            Collections.sort(feeds, new Comparator<UserFeed>() {
//                                public int compare(UserFeed o1, UserFeed o2) {
//                                    tempLocation1.setLatitude(o1.getLatitude());
//                                    tempLocation1.setLongitude(o1.getLongitude());
//                                    tempLocation2.setLatitude(o2.getLatitude());
//                                    tempLocation2.setLongitude(o2.getLongitude());
//                                    if (tempLocation1.distanceTo(myLocation) > tempLocation2.distanceTo(myLocation))
//                                        return -1;
//                                    else if (tempLocation1.distanceTo(myLocation) < tempLocation2.distanceTo(myLocation))
//                                        return 1;
//                                    else
//                                        return 0;
//                                }
//                            });

//                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Error in fetching feed documents",Toast.LENGTH_SHORT);
                    }
                });
            }
            followingUserIds.remove(loggedInUser.getUserId());
        } else {
            nofeedsTextView.setVisibility(View.VISIBLE);
        }

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getContext()
                    .getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                    }else {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                /*if (locationManager != null){
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }*/
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
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

        // start server
        becomeServer();
    }


    // be a server

    public void becomeServer() {

        Log.d(TAG, "152");
        serverThread = new ServerThread();
        serverThread.start();
    }

    private class ServerThread extends Thread{
        private final BluetoothServerSocket serverSocket;

        private ServerThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("myServer", MY_UUID);
                Log.d(TAG, "@ UUID "+MY_UUID);
            } catch (IOException e) {
                Log.d(TAG, "Server establishing failed");
            }
            serverSocket = tmp;
        }

        public void sendData(String data) {
            StringBuffer sb = new StringBuffer();
            sb.append(data);
            sb.append("\n");
            if (ServerOutStream != null) {
                try {
                    ServerOutStream.write(sb.toString().getBytes());
                    ServerOutStream.flush();
                    Log.d(TAG,"@ send data " + sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"@ Server sending fail");
                }
            }
        }
        public void closeServer(){
            this.interrupt();
        }


        public void run() {
            Log.d(TAG, "start server.");
            BluetoothSocket connectSocket = null;
            String line = "";

            isServerRunning = true;

            // Keep listening until exception occurs or a socket is returned.
            while (isServerRunning) {
                try {
                    Log.d(TAG, "waiting for connection");
                    connectSocket = serverSocket.accept();
                } catch (IOException e) {
                    Log.d(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (connectSocket != null) {
                    // A connection was accepted. Perform work later.
                    break;
                }

                if(Thread.currentThread().isInterrupted())
                {

                    try {
                        Log.d(TAG, "quit during waiting for connection");
                        serverSocket.close();
                    } catch (IOException e) {
                        Log.d(TAG, "error quit during waiting", e);
                        break;
                    }

                    isServerRunning = false;
                    break;
                }
            }

            while (isServerRunning) {
                try {
                    Log.d(TAG, "do work with connection");
                    serverSocket.close();

                    if (connectSocket != null) {
                        // A connection was accepted. Perform work here

                        bluetoothAdapter.cancelDiscovery();
                        Log.d(TAG, "@ Connection Successfully");
                        // send a message to the UI thread
                        Message message = new Message();
                        message.what=CONNECTION_SUCCESSFUL;
                        handler_process.sendMessage(message);
                    }
                    else {
                        isServerRunning = false;
                        break;
                    }

                } catch (IOException e) {
                    Log.d(TAG, "Connection failed");
                    break;
                }
                try {
                    Log.d(TAG, "@ line1"+line);

                    if(ServerInStream == null) {
                        InputStream tmpIn = null;
                        // Get the input and output streams; using temp objects because
                        // member streams are final.
                        try {
                            tmpIn = connectSocket.getInputStream();
                        } catch (Exception e) {
                            Log.e(TAG, "Error occurred when creating input stream", e);
                        }
                        ServerInStream = tmpIn;
                    }

                    if(ServerOutStream == null) {
                        OutputStream tmpOut = null;
                        // Get the input and output streams; using temp objects because
                        // member streams are final.
                        try {
                            tmpOut = connectSocket.getOutputStream();
                        } catch (Exception e) {
                            Log.e(TAG, "Error occurred when creating output stream", e);
                        }
                        ServerOutStream = tmpOut;
                    }


                    // send data to client
                    sendData("Server: Hello!");

                    Log.d(TAG, "254");

                    while (isServerRunning &&ServerInStream != null) {

                        BufferedReader br = new BufferedReader(new InputStreamReader(ServerInStream));
                        // readLine() read and delete one line
                        while ((line = br.readLine()) != null) {
                            Log.d(TAG, "@message " + line);
                            // send a message to the UI thread
                            Message message = new Message();
                            message.what = MESSAGE_RECEIVED;
                            message.obj = line;
                            handler_process.sendMessage(message);

                        }

                        // send data to client
                        sendData("Server: Hello!");

                        if(Thread.currentThread().isInterrupted())
                        {
                            connectSocket.close();
                            Log.d(TAG, "quit from connection");
                            isServerRunning = false;
                            break;
                        }

                    }

                    if(Thread.currentThread().isInterrupted())
                    {
                        connectSocket.close();
                        Log.d(TAG, "quit from connection");
                        isServerRunning = false;
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();


                    Log.d(TAG, "quit from connection");

                    try {
                        connectSocket.close();
                    } catch (Exception e2) {
                        Log.e(TAG, "Error occurred in an exception of an exception", e2);
                    }
                    isServerRunning = false;

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
                break;
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d("haha", "one or more permission denied");
                            return;
                        }
                    }
                    Log.d("haha", "all permissions granted");

                }
                break;


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

    @Override
    public void onLocationChanged(Location location) {
    }


    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public void onProviderEnabled(String provider) {
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
