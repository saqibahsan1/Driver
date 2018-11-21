package com.akhdmny.driver.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akhdmny.driver.Activities.Chat;
import com.akhdmny.driver.Adapter.DriverDetailedAdapter;
import com.akhdmny.driver.ApiResponse.CartInsideResponse;
import com.akhdmny.driver.ApiResponse.DeliverOrderPkg.DeliverOrderApi;
import com.akhdmny.driver.ApiResponse.UserAcceptedResponse.DriverAwardedResp;
import com.akhdmny.driver.ApiResponse.cancelOrder.CancelOrderResponse;
import com.akhdmny.driver.Authenticate.login;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Service.TrackerService;
import com.akhdmny.driver.Utils.GPSActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverOrders extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {
    private static final int PERMISSION_CODE = 99;
    private static final int PERMISSION_CALL = 22;
    private GoogleMap mMap;
    MapView mMapView;
    private GoogleApiClient mGoogleApiClient;
    private double longitude;
    private double latitude;
    ArrayList<com.akhdmny.driver.ApiResponse.UserAcceptedResponse.CartItem> list;
    ArrayList<CartInsideResponse> listResponse;
    private GoogleApiClient googleApiClient;
    private String TAG = "gps";
    public static final int REQUEST_CHECK_SETTINGS = 123;
    LocationRequest mLocationRequest;
    int INTERVAL = 1000;
    int FASTEST_INTERVAL = 500;
    @BindView(R.id.toggleButtons)
    ToggleSwitch toogleButtons;
    SharedPreferences prefs;

    @BindView(R.id.map_layout)
    FrameLayout map_layout;

    @BindView(R.id.callChatBtns)
    LinearLayout callChatBtns;

    @BindView(R.id.RV_MyOrders)
    RecyclerView recyclerView;

    @BindView(R.id.deliver)
    Button deliver;
    @BindView(R.id.driverChat)
    Button driverChat;
    @BindView(R.id.driverCall)
    Button driverCall;

    @BindView(R.id.CancelOrder)
    Button CancelOrder;

    @BindView(R.id.total_amount)
    TextView total_amount;

    @BindView(R.id.tip)
    TextView tip;

    @BindView(R.id.final_amount)
    TextView final_amount;

    SpotsDialog dialog;

    @BindView(R.id.OrdersLayout)
    LinearLayout OrdersLayout;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView tvTitle;

    String UserNumber = "";
    String id;;
    public DriverOrders() {

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText(getString(R.string.Home));
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = String.valueOf(prefs.getInt("id",0));
        list = new ArrayList<>();
        listResponse = new ArrayList<>();

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        toogleButtons.setCheckedPosition(0);
        toogleButtons.setOnChangeListener(new ToggleSwitch.OnChangeListener() {
            @Override
            public void onToggleSwitchChanged(int i) {
                if (i == 0) {
                    scrollView.setVisibility(View.GONE);
                    map_layout.setVisibility(View.VISIBLE);
                    callChatBtns.setVisibility(View.VISIBLE);
                } else {
                    scrollView.setVisibility(View.VISIBLE);
                    map_layout.setVisibility(View.GONE);
                    callChatBtns.setVisibility(View.GONE);
                }
            }
        });

        mMapView.getMapAsync(this);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(DriverOrders.this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleApiClient = new GoogleApiClient.Builder(DriverOrders.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
//        mLocationRequest.setNumUpdates(1);
//        mLocationRequest.setExpirationTime(6000);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(DriverOrders.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(DriverOrders.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_CODE);
        }


        startTrackerService();

        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliverApi();
            }
        });

        CancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelOrderApi();
            }
        });

        driverChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DriverOrders.this, Chat.class));
            }
        });

        driverCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEvent();
            }
        });
    }
    private void requestPermissionForCall() {

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL);
        }

    }
    private void callEvent(){
        if (checkCallPermission()) {
            String uri = "tel:" + UserNumber;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }else {
            requestPermissionForCall();
        }

    }



    private void startTrackerService() {
        startService(new Intent(this, TrackerService.class));
        //finish();
    }
    private void CancelOrderApi(){
        NetworkConsume.getInstance().ShowProgress(DriverOrders.this);
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        String orderID = NetworkConsume.getInstance().getDefaults("orderId",DriverOrders.this);
        NetworkConsume.getInstance().getAuthAPI().CancelOrderApi(Integer.parseInt(orderID)).enqueue(new Callback<CancelOrderResponse>() {
            @Override
            public void onResponse(Call<CancelOrderResponse> call, Response<CancelOrderResponse> response) {
                if (response.isSuccessful()){
                    CancelOrderResponse cancelOrderResponse = response.body();
                    try {


                    if (cancelOrderResponse.getStatus()){
                        NetworkConsume.getInstance().setDefaults("yes",null,DriverOrders.this);
                        startActivity(new Intent(DriverOrders.this,MainActivity.class));
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").
                                child("Driver").child(String.valueOf(prefs.getInt("id",1)));
                        ref.child("status").setValue(6);

                        finish();
                        NetworkConsume.getInstance().HideProgress();
                    }
                    }catch (Exception e){
                        NetworkConsume.getInstance().HideProgress();
                    }

                }else {
                    NetworkConsume.getInstance().HideProgress();
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(DriverOrders.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CancelOrderResponse> call, Throwable t) {
                Toast.makeText(DriverOrders.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                NetworkConsume.getInstance().HideProgress();
            }
        });
    }

    private void deliverApi(){
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        NetworkConsume.getInstance().ShowProgress(DriverOrders.this);
        String orderID = NetworkConsume.getInstance().getDefaults("orderId",DriverOrders.this);
        NetworkConsume.getInstance().getAuthAPI().DeliverOrder(Integer.parseInt(orderID)).enqueue(new Callback<DeliverOrderApi>() {
            @Override
            public void onResponse(Call<DeliverOrderApi> call, Response<DeliverOrderApi> response) {
                if (response.isSuccessful()){
                    try {
                        DeliverOrderApi api = response.body();
                    if (api.getStatus()){
                       DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").
                                child("Driver").child(String.valueOf(prefs.getInt("id",1)));
                        ref.child("status").setValue(6);
                        NetworkConsume.getInstance().setDefaults("yes",null,DriverOrders.this);
                        startActivity(new Intent(DriverOrders.this,MainActivity.class));
                        finish();
                        NetworkConsume.getInstance().HideProgress();
                    }
                    }catch (Exception e){
                        NetworkConsume.getInstance().HideProgress();

                        }


                }else {
                    NetworkConsume.getInstance().HideProgress();
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(DriverOrders.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<DeliverOrderApi> call, Throwable t) {
                Toast.makeText(DriverOrders.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                NetworkConsume.getInstance().HideProgress();
            }
        });
    }
    protected Marker createMarker(double latitude, double longitude, String title,int driver) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(driver))
                .draggable(true));
    }
    private void GetOrderDetails(){
        try {
            NetworkConsume.getInstance().ShowProgress(DriverOrders.this);
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        String orderID = NetworkConsume.getInstance().getDefaults("orderId",DriverOrders.this);
        NetworkConsume.getInstance().getAuthAPI().getOrderDetails(Integer.valueOf(orderID)).
                enqueue(new Callback<DriverAwardedResp>() {
                    @Override
                    public void onResponse(Call<DriverAwardedResp> call, Response<DriverAwardedResp> response) {
                        if (response.isSuccessful()) {
                            DriverAwardedResp cartOrder = response.body();
                            for (int i=0; i< cartOrder.getResponse().getOrderDetails().getCartItems().size(); i++){
                                if (cartOrder.getResponse().getOrderDetails().getCartItems().size() == 0)
                                {

                                }else {
                                    list.add(cartOrder.getResponse().getOrderDetails().getCartItems().get(i));
                                    createMarker(cartOrder.getResponse().getOrderDetails().getCartItems().get(i).getLat(),
                                            cartOrder.getResponse().getOrderDetails().getCartItems().get(i).getLong(),
                                            cartOrder.getResponse().getOrderDetails().getCartItems().get(i).getAddress(),R.drawable.market_marker);
                                }
                            }
                            try {
                            total_amount.setText(new DecimalFormat("##").format(cartOrder.getResponse().getOrderDetails().getAmount())+" "+cartOrder.getResponse().
                            getOrderDetails().getCurrency());
                            tip.setText(new DecimalFormat("##").format(cartOrder.getResponse().getOrderDetails().getTip()));
                            final_amount.setText(new DecimalFormat("##").format(cartOrder.getResponse().getOrderDetails().getFinalAmount())+" "+cartOrder.getResponse().
                                    getOrderDetails().getCurrency());
                            UserNumber = cartOrder.getResponse().getUserInfo().getPhone();
                            NetworkConsume.getInstance().setDefaults("yes",cartOrder.getResponse().getUserInfo().getFirstName(),DriverOrders.this);
                            NetworkConsume.getInstance().setDefaults("userId", String.valueOf(cartOrder.getResponse().getUserInfo().getId()),DriverOrders.this);
//                            createMarker(cartOrder.getResponse().getDriverInfo().getLat(),cartOrder.getResponse().getDriverInfo().getLong(),
//                                    cartOrder.getResponse().getDriverInfo().getAddress());
                            createMarker(cartOrder.getResponse().getUserInfo().getLat(),cartOrder.getResponse().getUserInfo().getLong(),
                                    cartOrder.getResponse().getUserInfo().getAddress(),R.drawable.user_marker);
                            heading(cartOrder.getResponse().getUserInfo().getLat(),cartOrder.getResponse().getUserInfo().getLong());
                            DriverDetailedAdapter myAdapter = new DriverDetailedAdapter(DriverOrders.this,list);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DriverOrders.this);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(myAdapter);
                            NetworkConsume.getInstance().HideProgress();

                            }catch (Exception e){
                                NetworkConsume.getInstance().HideProgress();
                                Toast.makeText(DriverOrders.this, "Something Went Wrong!! Details Missing...", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            NetworkConsume.getInstance().HideProgress();
                            Gson gson = new Gson();
                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                            Toast.makeText(DriverOrders.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                            if (message.getError().getMessage().get(0).equals("Unauthorized access_token")){
                                SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                                prefs.edit().putString("access_token", "")
                                        .putString("avatar","")
                                        .putString("login","").commit();

                                Intent intent = new Intent(DriverOrders.this,  login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<DriverAwardedResp> call, Throwable t) {
                        NetworkConsume.getInstance().HideProgress();
                        Toast.makeText(DriverOrders.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        }catch (Exception e){
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void heading(double UserLat, double UserLong){
        GPSActivity activity = new GPSActivity(DriverOrders.this);
        LatLng current = new LatLng(activity.getLatitude(),activity.getLongitude());
        LatLng UserLoc = new LatLng(UserLat,UserLong);
        double heading = SphericalUtil.computeHeading(current, UserLoc);
        showDistance(current,UserLoc);
    }
    private void showDistance(LatLng a,LatLng b) {
        double distance = SphericalUtil.computeDistanceBetween(a, b);
        Log.w("The markers are " , formatNumber(distance) + " apart.");
    }
    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
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
                                DriverOrders.this,
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
                  //  boolean coarselocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (finelocation) {

                        if (checkPermission())
                            buildGoogleApiClient();

    //                        getCurrentLocation();
//                        Toast.makeText(DriverOrders.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DriverOrders.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        requestPermission();

                    }
                }

                break;

            case PERMISSION_CALL:
                if (grantResults.length>0){
                    boolean CallResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (CallResult){
                        if (checkCallPermission())
                            callEvent();
                    }else {
                        requestPermissionForCall();
                    }
                }
                break;
        }

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
            moveMap();
            startTrackerService();
        }
    }

    private void moveMap() {


    //        getCurrentLocation();
        LatLng latLng = new LatLng(latitude, longitude);
        //Adding marker to map
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng) //setting position
//                .draggable(true) //Making the marker draggable
//                /*.icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_marker))*/
//                .title("Current Location")); //Adding a title
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
//                .anchor(0.5f, 0.5f)
//                .title("current position")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon))
//                .draggable(true));
        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(9));

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(DriverOrders.this)
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();



    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Clearing all the markers
        mMap.clear();

        //Adding a new marker to the current pressed position we are also making the draggable true
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
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
            mMap.setMyLocationEnabled(true);
//            getCurrentLocation();
            GetOrderDetails();
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

        ActivityCompat.requestPermissions(DriverOrders.this, new String[]
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_CODE);

    }
    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(DriverOrders.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(DriverOrders.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;

    }
    public boolean checkCallPermission(){
        int callPerm = ContextCompat.checkSelfPermission(DriverOrders.this,Manifest.permission.CALL_PHONE);

        return callPerm == PackageManager.PERMISSION_GRANTED;
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
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
