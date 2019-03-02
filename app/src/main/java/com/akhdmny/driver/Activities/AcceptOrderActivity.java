package com.akhdmny.driver.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akhdmny.driver.Adapter.BottomSheetAdapter;
import com.akhdmny.driver.Adapter.ImageAdapterCart;
import com.akhdmny.driver.Adapter.MyCartAdapter;
import com.akhdmny.driver.ApiResponse.BidResp.SubmitBidResp;
import com.akhdmny.driver.ApiResponse.OrdersResponse.CartItem;
import com.akhdmny.driver.ApiResponse.OrdersResponse.GetOrderItemsResp;
import com.akhdmny.driver.ApiResponse.TimeOut.OrderTimeOut;
import com.akhdmny.driver.ApiResponse.UpdateFbmodel;
import com.akhdmny.driver.Authenticate.login;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.Fragments.CancelFragment;
import com.akhdmny.driver.Fragments.DriverOrders;
import com.akhdmny.driver.Fragments.FargmentService;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.Network;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Singletons.CurrentOrder;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceptOrderActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.total_amount)
    TextView total_amount;

    @BindView(R.id.OrderBidScreen)
    LinearLayout OrderBidScreen;
    @BindView(R.id.et_bid)
    EditText et_bid;
    @BindView(R.id.CancelOrder)
    Button CancelOrder;
    @BindView(R.id.confirmOrder)
    Button confirmOrder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.toggleButtons)
    ToggleSwitch toogleButtons;
    TextView tvTitle;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;

    @BindView(R.id.map_layout)
    FrameLayout map_layout;
    private static final int PERMISSION_CODE = 99;
    private static final int PERMISSION_CALL = 22;
    private GoogleMap mMap;
    MapView mMapView;
    private GoogleApiClient mGoogleApiClient;
    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;
    private String TAG = "gps";
    public static final int REQUEST_CHECK_SETTINGS = 123;
    LocationRequest mLocationRequest;
    private Handler mHandler;
    private Runnable mRunnable;
    private ArrayList<String> photos = new ArrayList<>();
    ArrayList<CartItem> list = new ArrayList<>();
    SharedPreferences prefs;
    AlertDialog alertDialog;
    int INTERVAL = 1000;
    int FASTEST_INTERVAL = 500;
    GPSActivity gpsActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_items);
        ButterKnife.bind(this);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        tvTitle.setText(getString(R.string.order_req));
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        toogleButtons.setCheckedPosition(0);
        gpsActivity = new GPSActivity(AcceptOrderActivity.this);
        toogleButtons.setOnChangeListener(new ToggleSwitch.OnChangeListener() {
            @Override
            public void onToggleSwitchChanged(int i) {
                if (i == 0) {
                    scrollView.setVisibility(View.GONE);
                    map_layout.setVisibility(View.VISIBLE);
                } else {
                    scrollView.setVisibility(View.VISIBLE);
                    map_layout.setVisibility(View.GONE);
                }
            }
        });

        CancelOrder.setOnClickListener(v -> {
            CancelFragment cancelFragment = new CancelFragment(new BottomSheetAdapter.DetectReasonSelected() {
                @Override
                public void onSelection(String reason) {
                    CancelOrder(reason);
                }
            });
            FragmentManager fm =getSupportFragmentManager();
            cancelFragment.show(fm,cancelFragment.getTag());
//            finish();
        });

        mMapView.getMapAsync(this);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(AcceptOrderActivity.this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleApiClient = new GoogleApiClient.Builder(AcceptOrderActivity.this)
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
            ActivityCompat.requestPermissions(AcceptOrderActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(AcceptOrderActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_CODE);
        }

        itemTouchLister();
    }


    private void getOrdersApi(){
        try {

            NetworkConsume.getInstance().ShowProgress(AcceptOrderActivity.this);
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        String orderID = NetworkConsume.getInstance().getDefaults("orderId",AcceptOrderActivity.this);


        NetworkConsume.getInstance().getAuthAPI().getOrderItems(Integer.valueOf(orderID)).enqueue(new Callback<GetOrderItemsResp>() {
            @Override
            public void onResponse(Call<GetOrderItemsResp> call, Response<GetOrderItemsResp> response) {
                    if (response.isSuccessful()){
                        GetOrderItemsResp getOrderItemsResp = response.body();
                        for (int i = 0; i<getOrderItemsResp.getResponse().getOrderDetails().getCartItems().size(); i++){
                            if (getOrderItemsResp.getResponse().getOrderDetails().getCartItems().size() != 0)
                            {
                                if (getOrderItemsResp.getResponse().getOrderDetails().getIsBid() ==1){
                                    et_bid.setVisibility(View.GONE);
                                    CurrentOrder.getInstance().finalAmount = Integer.parseInt(new DecimalFormat("##").format(getOrderItemsResp.getResponse().getOrderDetails().getAmount()));
                                }else {
                                    et_bid.setVisibility(View.VISIBLE);
                                }
                                list.add(getOrderItemsResp.getResponse().getOrderDetails().getCartItems().get(i));
                                createMarker(getOrderItemsResp.getResponse().getOrderDetails().getCartItems().get(i).getLat(),
                                        getOrderItemsResp.getResponse().getOrderDetails().getCartItems().get(i).getLong(),
                                        getOrderItemsResp.getResponse().getOrderDetails().getCartItems().get(i).getAddress(),R.drawable.market_marker);
                                MyCartAdapter myAdapter = new MyCartAdapter(AcceptOrderActivity.this,list);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(myAdapter);
                                NetworkConsume.getInstance().HideProgress();
                                total_amount.setText(new DecimalFormat("##").format(getOrderItemsResp.getResponse().getOrderDetails().getAmount()));
                            }

                        }

                    }else {
                        Gson gson = new Gson();
                        NetworkConsume.getInstance().HideProgress();
                        LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                        Toast.makeText(AcceptOrderActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                        if (message.getError().getMessage().get(0).equals("Unauthorized access_token")){
                            Intent intent = new Intent(AcceptOrderActivity.this,  login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                            prefs.edit().putString("access_token", "")
                                    .putString("avatar","")
                                    .putString("login","").commit();
                            startActivity(intent);
                        }
                    }
            }

            @Override
            public void onFailure(Call<GetOrderItemsResp> call, Throwable t) {
                NetworkConsume.getInstance().HideProgress();
                Toast.makeText(AcceptOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        }catch (Exception e){}
    }

    @Override
    public void onBackPressed() {

    }
    private void CancelOrder(String reason) {
        NetworkConsume.getInstance().ShowProgress(AcceptOrderActivity.this);

        NetworkConsume.getInstance().setAccessKey("Bearer " + prefs.getString("access_token", "12"));
        String orderId = NetworkConsume.getInstance().getDefaults("orderId", AcceptOrderActivity.this);
        NetworkConsume.getInstance().getAuthAPI().cancelOrderApi(orderId,reason).enqueue(new Callback<OrderTimeOut>() {
            @Override
            public void onResponse(Call<OrderTimeOut> call, Response<OrderTimeOut> response) {
                if (response.isSuccessful()) {
                    OrderTimeOut timeOut = response.body();
                    NetworkConsume.getInstance()
                            .SnackBarSucccess(OrderBidScreen,AcceptOrderActivity.this,R.string.order_cancel);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").
                            child("Driver").child(String.valueOf(prefs.getInt("id",1)));
                    ref.child("status").setValue(6);
                    startActivity(new Intent(AcceptOrderActivity.this,MainActivity.class));
                    finish();
                    NetworkConsume.getInstance().HideProgress();
                } else {
                    NetworkConsume.getInstance().HideProgress();
                    Gson gson = new Gson();
                    LoginApiError message = gson.fromJson(response.errorBody().charStream(), LoginApiError.class);
                    Toast.makeText(AcceptOrderActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderTimeOut> call, Throwable t) {
                Toast.makeText(AcceptOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                NetworkConsume.getInstance().HideProgress();
            }
        });
    }


    protected Marker createMarker(double latitude, double longitude, String title, int driver) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(driver))
                .draggable(true));
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(AcceptOrderActivity.this, new String[]
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_CODE);

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
                                AcceptOrderActivity.this,
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
//                        Toast.makeText(AcceptOrderActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AcceptOrderActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        requestPermission();

                    }
                }

                break;


        }

    }
    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(AcceptOrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(AcceptOrderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;

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
        mGoogleApiClient = new GoogleApiClient.Builder(AcceptOrderActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    private void bidSubmitApi(String bid){
        NetworkConsume.getInstance().ShowProgress(AcceptOrderActivity.this);
        String orderID = NetworkConsume.getInstance().getDefaults("orderId",AcceptOrderActivity.this);
        NetworkConsume.getInstance().getAuthAPI().SubmitBid(Integer.parseInt(orderID), bid,gpsActivity.getLatitude(),gpsActivity.getLongitude()).enqueue(new Callback<SubmitBidResp>() {
            @Override
            public void onResponse(Call<SubmitBidResp> call, Response<SubmitBidResp> response) {
                if (response.isSuccessful()){
                    NetworkConsume.getInstance().SnackBarSucccess(OrderBidScreen,AcceptOrderActivity.this,R.string.successBid);
                    startActivity(new Intent(AcceptOrderActivity.this,MainActivity.class));
                    finish();
                    NetworkConsume.getInstance().HideProgress();
                }else {
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(AcceptOrderActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                    NetworkConsume.getInstance().HideProgress();
                }
            }

            @Override
            public void onFailure(Call<SubmitBidResp> call, Throwable t) {
                NetworkConsume.getInstance().HideProgress();

            }
        });
    }
    private void itemTouchLister() {

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_bid.getVisibility() == View.VISIBLE){
                    if (et_bid.getText().toString().equals("")) {
                    Toast.makeText(AcceptOrderActivity.this, "Please enter the Bid", Toast.LENGTH_SHORT).show();
                    }else {
                        bidSubmitApi(et_bid.getText().toString());
                    }
                }else {
                    bidSubmitApi(String.valueOf(CurrentOrder.getInstance().finalAmount));
                }
            }
        });

        recyclerView.addOnItemTouchListener(new CategoryDetailActivity.RecyclerTouchListener(this, recyclerView,
                new FargmentService.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        final AlertDialog.Builder ADD_Cart = new AlertDialog.Builder(AcceptOrderActivity.this);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View viewCart = inflater.inflate(R.layout.remove_cart, null);
                        ImageView imageView = viewCart.findViewById(R.id.img_Resturaunt);
                        ImageView PlayAudio = viewCart.findViewById(R.id.PlayAudio);
                        seekBar = viewCart.findViewById(R.id.seek_bar);
                        TextView textViewTitle = viewCart.findViewById(R.id.Tv_title);
                        RecyclerView recyclerViewPopup = viewCart.findViewById(R.id.recycler_view);
                        recyclerViewPopup.setLayoutManager(new LinearLayoutManager(AcceptOrderActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerViewPopup.setHasFixedSize(true);
                        if (list.get(position).getImage()!= null){
                           // for (int i =0; i<list.get(position).getImage().size()-1;i++) {
                                photos.add(list.get(position).getImage());
                                //}
                                ImageAdapterCart imagesAdapter = new ImageAdapterCart(AcceptOrderActivity.this, photos);
                            recyclerViewPopup.setAdapter(imagesAdapter);
                        }else {
                            recyclerViewPopup.setVisibility(View.GONE);
                        }
//


                        Picasso.get().load(list.get(position).getImage()).error(R.drawable.place_holder).into(imageView);
                        if (list.get(position).getVoice() == null){
                            PlayAudio.setVisibility(View.GONE);
                        }
                        PlayAudio.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (list.get(position).getVoice() == null){
                                    Toast.makeText(AcceptOrderActivity.this, "No Voice Found", Toast.LENGTH_SHORT).show();
                                }else {
                                    if(isPlaying()){
                                        stopPlaying();
                                    } else {
                                        startPlaying(list.get(position).getVoice());
                                    }
                                }
                            }
                        });

                        Button btnRemove = viewCart.findViewById(R.id.buttonRemoveItem);
                        TextView textDialogMsg = viewCart.findViewById(R.id.textDialog);
                        TextView textViewAddress = viewCart.findViewById(R.id.TV_Address);
                        TextView textViewprice = viewCart.findViewById(R.id.textView_Price);
                        textViewTitle.setText(list.get(position).getTitle());
                        textViewAddress.setText(list.get(position).getAddress());
                        textViewprice.setText(list.get(position).getAmount().toString());
                        textDialogMsg.setText(list.get(position).getDescription());
                        ADD_Cart.setView(viewCart);
                        ADD_Cart.setCancelable(true);
                        alertDialog = ADD_Cart.create();
                        alertDialog.show();

                        btnRemove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getToken();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                alertDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
    }
    private void getToken(){

        String orderId = NetworkConsume.getInstance().getDefaults("orderId", AcceptOrderActivity.this);
        String id = String.valueOf(prefs.getInt("id",1));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Token").child(id).child("token");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            Log.e("TAG", "" + dataSnapshot.getValue());
                            UpdateFBApi(""+dataSnapshot.getValue(),orderId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("TAG", " it's null.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void UpdateFBApi(String token,String orderId){
        NetworkConsume.getInstance().ShowProgress(AcceptOrderActivity.this);
        Network.getInstance().getAuthAPINew().updateFirebase(token,orderId,"Item purchased","has been purchased by your driver").enqueue(new Callback<UpdateFbmodel>() {
            @Override
            public void onResponse(Call<UpdateFbmodel> call, Response<UpdateFbmodel> response) {
                if (response.isSuccessful()){
                    Toast.makeText(AcceptOrderActivity.this, "Order has been done!!", Toast.LENGTH_SHORT).show();
                    NetworkConsume.getInstance().HideProgress();
                }
                else {
                    Toast.makeText(AcceptOrderActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    NetworkConsume.getInstance().HideProgress();
                }
            }

            @Override
            public void onFailure(Call<UpdateFbmodel> call, Throwable t) {
                Toast.makeText(AcceptOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                NetworkConsume.getInstance().HideProgress();
            }
        });
    }

    private boolean isPlaying(){
        try
        {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (Exception e){
            return false;
        }
    }
    private void startPlaying(String source){
        try {
            seekBar.setVisibility(View.VISIBLE);
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnCompletionListener(this);

            try {
                mediaPlayer.setDataSource(source);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.start();
            initializeSeekBar();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void stopPlaying(){

        if(mediaPlayer != null){
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                seekBar.setProgress(0);
                seekBar.setVisibility(View.GONE);

                if(mHandler!=null){
                    mHandler.removeCallbacks(mRunnable);
                }
            } catch (Exception e){ }
        }

    }
    protected void initializeSeekBar(){
        seekBar.setMax(mediaPlayer.getDuration());

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
                mHandler.postDelayed(mRunnable,50);
            }
        };
        mHandler.postDelayed(mRunnable,50);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
            stopPlaying();
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

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

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
            getOrdersApi();
//            getCurrentLocation();

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
