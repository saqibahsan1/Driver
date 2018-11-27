package com.akhdmny.driver.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akhdmny.driver.LocaleHelper;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Service.TrackerService;
import com.akhdmny.driver.Singletons.OrderManager;
import com.akhdmny.driver.Utils.StatusModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ar-android on 15/10/2015.
 */
public class FragmentHome extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {
    private static final int PERMISSION_CODE = 99;
    private GoogleMap mMap;
    MapView mMapView;
    private GoogleApiClient mGoogleApiClient;
    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;
    private String TAG = "gps";
    public static final int REQUEST_CHECK_SETTINGS = 123;
    LocationRequest mLocationRequest;
    int INTERVAL = 1000;
    int FASTEST_INTERVAL = 500;
    @BindView(R.id.buttonStatus)
    Button buttonStatus;

    @BindView(R.id.topBar)
    LinearLayout topBar;

    @BindView(R.id.titleBar)
    TextView titleBar;
    @BindView(R.id.statusColorImg)
    CircleImageView statusColorImg;
    private FragmentActivity myContext;
    private BottomSheetBehavior mBottomSheetBehaviour;
    boolean status = true;
    SharedPreferences preferences;
    String id;
    DatabaseReference myRef;

    public FragmentHome() {

    }

    private LinkedHashMap<String, StatusModel> adminModelLArrayList = new LinkedHashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        preferences = getActivity().getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        id = String.valueOf(preferences.getInt("id", 0));
        myRef = FirebaseDatabase.getInstance().getReference().child("Token").child(id).child("status");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object status = dataSnapshot.getValue();
                if (status != null) {
                    switch (status.toString()) {
                        case "0":
                            buttonStatus.setBackgroundColor(getResources().getColor(R.color.Red));
                            buttonStatus.setText(R.string.Bz);
                            titleBar.setText(R.string.available);
                            statusColorImg.setBackground(getResources().getDrawable(R.drawable.green_dot));
                            startTrackerService();
                            break;
                        case "1":
                            buttonStatus.setBackgroundColor(getResources().getColor(R.color.green));
                            buttonStatus.setText(R.string.online);
                            titleBar.setText(R.string.busy);
                            statusColorImg.setBackground(getResources().getDrawable(R.drawable.reddot_dash));
                            OrderManager.getInstance().stopObservingOrder();
                            break;
                        case "2":
                            startTrackerService();
                            break;
                        case "3":
                            buttonStatus.setVisibility(View.INVISIBLE);
                            titleBar.setText(R.string.blocked);
                            statusColorImg.setBackground(getResources().getDrawable(R.drawable.reddot_dash));
                            OrderManager.getInstance().stopObservingOrder();
                            break;
                        default:

                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        myRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                try {
//                    if (Objects.equals(dataSnapshot.getKey(), "status") && Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("0")) {
//
//
//                        buttonStatus.setBackgroundColor(getResources().getColor(R.color.Red));
//                        buttonStatus.setText(R.string.Bz);
//                        titleBar.setText(R.string.available);
//                        statusColorImg.setBackground(getResources().getDrawable(R.drawable.green_dot));
//
//
//                    } else if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("1")) {
//                        buttonStatus.setBackgroundColor(getResources().getColor(R.color.green));
//                        buttonStatus.setText(R.string.online);
//                        titleBar.setText(R.string.busy);
//                        statusColorImg.setBackground(getResources().getDrawable(R.drawable.reddot_dash));
//
//                    }
//
//                } catch (Exception e) {
//                    Log.e("Firebase db err:", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                try {
//
//
//                    if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("0")) {
//
//                        buttonStatus.setBackgroundColor(getResources().getColor(R.color.Red));
//                        buttonStatus.setText(R.string.Bz);
//                        titleBar.setText(R.string.available);
//                        statusColorImg.setBackground(getResources().getDrawable(R.drawable.green_dot));
//
//
//                    } else {
//                        buttonStatus.setBackgroundColor(getResources().getColor(R.color.green));
//                        buttonStatus.setText(R.string.online);
//                        titleBar.setText(R.string.busy);
//                        statusColorImg.setBackground(getResources().getDrawable(R.drawable.reddot_dash));
//
//                    }
//
//
//                } catch (Exception e) {
//                    Log.e("Firebase db err:", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        buttonStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status) {
                    buttonStatus.setBackgroundColor(getResources().getColor(R.color.Red));
                    buttonStatus.setText(R.string.Bz);
                    titleBar.setText(R.string.available);
                    UpdateToken(Integer.valueOf(id), 0);
                    statusColorImg.setBackground(getResources().getDrawable(R.drawable.green_dot));

                    status = false;
                } else {
                    buttonStatus.setBackgroundColor(getResources().getColor(R.color.green));
                    buttonStatus.setText(R.string.online);
                    titleBar.setText(R.string.busy);
                    UpdateToken(Integer.valueOf(id), 1);
                    statusColorImg.setBackground(getResources().getDrawable(R.drawable.reddot_dash));
                    status = true;
                }
            }
        });
        mMapView.getMapAsync(this);
//        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
//
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setNumUpdates(1);
//        mLocationRequest.setExpirationTime(6000);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_CODE);
        }

        return view;
    }

    private void UpdateToken(int id, Integer status) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(), instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.e("newToken", newToken);

            final String path = "Token/" + id;

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);


            ref.child("status").setValue(status);
        });
    }

//    private void startTrackerService() {
//        getActivity().startService(new Intent(getActivity(), TrackerService.class));
//        //finish();
//    }

    private void startTrackerService() {
        if (!OrderManager.getInstance().isObserverRunning()){
            Intent intent = new Intent(getActivity(), TrackerService.class);
            getActivity().startService(intent);
        }
    }

    private ResultCallback<LocationSettingsResult> mResultCallbackFromSettings = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(LocationSettingsResult result) {
            final Status status = result.getStatus();
            //final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                                getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.e(TAG, "Settings change unavailable. We have no way to fix the settings so we won't show the dialog.");
                    break;
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_CODE:

                if (grantResults.length > 0) {

                    boolean finelocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarselocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (finelocation && coarselocation) {

                        if (checkPermission())
                            buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);


                        //Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        requestPermission();

                    }
                }
                break;
        }

    }

    protected Marker createMarker(double latitude, double longitude, String title) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .draggable(true));
    }

    public void getCurrentLocation() {
        Location location = null;
        if (checkPermission()) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            mMap.setMyLocationEnabled(true);
            //moving the map to location
            moveMap();
        }
    }

    private void moveMap() {


        LatLng latLng = new LatLng(latitude, longitude);
        //Adding marker to map
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng) //setting position
//                .draggable(true) //Making the marker draggable
//                .title("Current Location")); //Adding a title
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
//                .anchor(0.5f, 0.5f)
//                .title("current position")
//                /*.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon))*/
//                .draggable(true));
        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(9));

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // For showing a move to my location button
        //Setting onMarkerDragListener to track the marker drag
        mMap.setOnMarkerDragListener(this);
        //Adding a long click listener to the map
        mMap.setOnMapLongClickListener(this);

        if (checkPermission()) {
            buildGoogleApiClient();
            if (Build.VERSION.SDK_INT < 23) {
                LatLng sydney = new LatLng(21.4858, 39.1925);
                mMap.addMarker(new MarkerOptions().position(sydney));
            }
            mMap.setMyLocationEnabled(true);
//            startTrackerService();
            // Check the location settings of the user and create the callback to react to the different possibilities
            LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequestBuilder.build());
            result.setResultCallback(mResultCallbackFromSettings);

        } else {
            requestPermission();
        }

    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        moveMap();

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        //Clearing all the markers
        mMap.clear();

        //Adding a new marker to the current pressed position we are also making the draggable true
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .draggable(true));

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
