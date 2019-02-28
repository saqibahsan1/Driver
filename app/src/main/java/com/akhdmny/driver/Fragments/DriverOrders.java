package com.akhdmny.driver.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akhdmny.driver.Activities.AcceptOrderActivity;
import com.akhdmny.driver.Activities.CategoryDetailActivity;
import com.akhdmny.driver.Activities.Chat;
import com.akhdmny.driver.Activities.MessageListActivity;
import com.akhdmny.driver.Adapter.DriverDetailedAdapter;
import com.akhdmny.driver.Adapter.ImageAdapterCart;
import com.akhdmny.driver.ApiResponse.AcceptModel.Order;
import com.akhdmny.driver.ApiResponse.AcceptModel.User;
import com.akhdmny.driver.ApiResponse.CartInsideResponse;
import com.akhdmny.driver.ApiResponse.DeliverOrderPkg.DeliverOrderApi;
import com.akhdmny.driver.ApiResponse.MyOrderDetails.OrderDetail;
import com.akhdmny.driver.ApiResponse.UpdateFbmodel;
import com.akhdmny.driver.ApiResponse.UserAcceptedResponse.CartItem;
import com.akhdmny.driver.ApiResponse.UserAcceptedResponse.DriverAwardedResp;
import com.akhdmny.driver.ApiResponse.cancelOrder.CancelOrderResponse;
import com.akhdmny.driver.Authenticate.login;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
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
import com.google.maps.android.SphericalUtil;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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
        GoogleMap.OnMapLongClickListener,
        MediaPlayer.OnCompletionListener {
    private static final int PERMISSION_CODE = 99;
    private static final int PERMISSION_CALL = 22;
    private GoogleMap mMap;
    MapView mMapView;
    private GoogleApiClient mGoogleApiClient;
    private double longitude;
    private double latitude;
    ArrayList<com.akhdmny.driver.ApiResponse.UserAcceptedResponse.CartItem> list;
    ArrayList<CartInsideResponse> listResponse;
    private ArrayList<String> photos = new ArrayList<>();
    private GoogleApiClient googleApiClient;
    private String TAG = "gps";
    public static final int REQUEST_CHECK_SETTINGS = 123;
    AlertDialog alertDialog;
    LocationRequest mLocationRequest;
    private Runnable mRunnable;
    private Handler mHandler;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    int INTERVAL = 1000;
    int FASTEST_INTERVAL = 500;
    @BindView(R.id.toggleButtons)
    ToggleSwitch toogleButtons;
    SharedPreferences prefs;

    @BindView(R.id.map_layout)
    FrameLayout map_layout;

    @BindView(R.id.callChatBtns)
    ConstraintLayout callChatBtns;

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
    String id;
    ;

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
        id = String.valueOf(prefs.getInt("id", 0));
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
                startActivity(new Intent(DriverOrders.this, MessageListActivity.class));
            }
        });

        driverCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEvent();
            }
        });

        recyclerView.addOnItemTouchListener(new CategoryDetailActivity.RecyclerTouchListener(this, recyclerView,
                new FargmentService.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        final AlertDialog.Builder ADD_Cart = new AlertDialog.Builder(DriverOrders.this);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View viewCart = inflater.inflate(R.layout.remove_cart, null);
                        ImageView imageView = viewCart.findViewById(R.id.img_Resturaunt);
                        ImageView PlayAudio = viewCart.findViewById(R.id.PlayAudio);
                        seekBar = viewCart.findViewById(R.id.seek_bar);
                        TextView textViewTitle = viewCart.findViewById(R.id.Tv_title);
                        RecyclerView recyclerViewPopup = viewCart.findViewById(R.id.recycler_view);
                        recyclerViewPopup.setLayoutManager(new LinearLayoutManager(DriverOrders.this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerViewPopup.setHasFixedSize(true);
//                        for (int i =0; i<list.get(position).getImage().size()-1;i++) {
                        photos.add(list.get(position).getImage());

                        //}
                        ImageAdapterCart imagesAdapter = new ImageAdapterCart(DriverOrders.this, photos);

                        recyclerViewPopup.setAdapter(imagesAdapter);
                        Picasso.get().load(list.get(position).getImage()).error(R.drawable.place_holder).into(imageView);
                        PlayAudio.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (list.get(position).getVoice() == null){
                                    Toast.makeText(DriverOrders.this, "No Voice Found", Toast.LENGTH_SHORT).show();
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
                                getToken();
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
        String orderId = NetworkConsume.getInstance().getDefaults("orderId", DriverOrders.this);
        String id = NetworkConsume.getInstance().getDefaults("id", DriverOrders.this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Token").child(orderId).child("token");
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
        Network.getInstance().getAuthAPINew().updateFirebase(token,orderId,"Item purchased","has been purchased by your driver").enqueue(new Callback<UpdateFbmodel>() {
            @Override
            public void onResponse(Call<UpdateFbmodel> call, Response<UpdateFbmodel> response) {
                if (response.isSuccessful()){
                    Toast.makeText(DriverOrders.this, "Order has been done!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateFbmodel> call, Throwable t) {

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

    private void requestPermissionForCall() {

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL);
        }

    }

    private void callEvent() {
        if (checkCallPermission()) {
            String uri = "tel:" + UserNumber;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        } else {
            requestPermissionForCall();
        }

    }


    private void startTrackerService() {
//        startService(new Intent(this, TrackerService.class));
        //finish();
    }

    private void CancelOrderApi() {
        OrderDetail order = CurrentOrder.getInstance().order;
        if (order != null) {
            int orderID = CurrentOrder.getInstance().orderId;
            NetworkConsume.getInstance().ShowProgress(DriverOrders.this);
            NetworkConsume.getInstance().setAccessKey("Bearer " + prefs.getString("access_token", "12"));
            NetworkConsume.getInstance().getAuthAPI().CancelOrderApi(orderID).enqueue(new Callback<CancelOrderResponse>() {
                @Override
                public void onResponse(@NonNull Call<CancelOrderResponse> call, @NonNull Response<CancelOrderResponse> response) {
                    if (response.isSuccessful()) {
                        CancelOrderResponse cancelOrderResponse = response.body();
                        NetworkConsume.getInstance().HideProgress();
                        try {
                            if (cancelOrderResponse != null) {
                                if (cancelOrderResponse.getStatus()) {
                                    CurrentOrder.shared = null;
                                    Intent start = new Intent(DriverOrders.this, MainActivity.class);
                                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(start);
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").
                                            child("Driver").child(String.valueOf(prefs.getInt("id", 1)));
                                    ref.child("status").setValue(6);
                                    finish();
                                }
                            }
                        } catch (Exception e) {
                            NetworkConsume.getInstance().HideProgress();
                        }

                    } else {
                        NetworkConsume.getInstance().HideProgress();
                        Gson gson = new Gson();
                        LoginApiError message = gson.fromJson(response.errorBody().charStream(), LoginApiError.class);
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
    }

    private void deliverApi() {
        OrderDetail order = CurrentOrder.getInstance().order;
        if (order != null) {
            int orderID = CurrentOrder.getInstance().orderId;
            NetworkConsume.getInstance().setAccessKey("Bearer " + prefs.getString("access_token", "12"));
            NetworkConsume.getInstance().ShowProgress(DriverOrders.this);
            NetworkConsume.getInstance().getAuthAPI().DeliverOrder(orderID).enqueue(new Callback<DeliverOrderApi>() {
                @Override
                public void onResponse(@NonNull Call<DeliverOrderApi> call, @NonNull Response<DeliverOrderApi> response) {
                    if (response.isSuccessful()) {
                        NetworkConsume.getInstance().HideProgress();
                        try {
                            DeliverOrderApi api = response.body();
                            if (api != null && api.getStatus()) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").
                                        child("Driver").child(String.valueOf(prefs.getInt("id", 1)));
                                ref.child("status").setValue(6);
                                Intent start = new Intent(DriverOrders.this, MainActivity.class);
                                start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(start);
                                finish();
                            }
                        } catch (Exception e) {
                            NetworkConsume.getInstance().HideProgress();

                        }


                    } else {
                        NetworkConsume.getInstance().HideProgress();
                        Gson gson = new Gson();
                        LoginApiError message = gson.fromJson(response.errorBody().charStream(), LoginApiError.class);
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
    }

    protected Marker createMarker(double latitude, double longitude, String title, int driver) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(driver))
                .draggable(true));
    }

    private void setupOrder() {
        OrderDetail order = CurrentOrder.getInstance().order;
        if (order != null) {
            for (CartItem cartItem : order.getCartItems()) {
                createMarker(cartItem.getLat(), cartItem.getLong(), cartItem.getTitle(), R.drawable.market_marker);
                list.add(cartItem);
            }
            total_amount.setText("" + (float) order.getAmount());
            tip.setText("" + (float) order.getTip());
            final_amount.setText("" + (float) order.getFinalAmount());
            User user = CurrentOrder.getInstance().user;
            if (user != null) {
                UserNumber = user.getPhone();
            }
            createMarker(order.getLat(), order.getLongField(), "drop off", R.drawable.user_marker);
            heading(order.getLat(), order.getLongField());
            DriverDetailedAdapter myAdapter = new DriverDetailedAdapter(DriverOrders.this, list);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DriverOrders.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(myAdapter);
        }
    }

//    private void GetOrderDetails(){
//        try {
//            NetworkConsume.getInstance().ShowProgress(DriverOrders.this);
//        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
//        String orderID = NetworkConsume.getInstance().getDefaults("orderId",DriverOrders.this);
//        NetworkConsume.getInstance().getAuthAPI().getOrderDetails(Integer.valueOf(orderID)).
//                enqueue(new Callback<DriverAwardedResp>() {
//                    @Override
//                    public void onResponse(Call<DriverAwardedResp> call, Response<DriverAwardedResp> response) {
//                        if (response.isSuccessful()) {
//                            DriverAwardedResp cartOrder = response.body();
//                            for (int i=0; i< cartOrder.getResponse().getOrderDetails().getCartItems().size(); i++){
//                                if (cartOrder.getResponse().getOrderDetails().getCartItems().size() == 0)
//                                {
//
//                                }else {
//                                    list.add(cartOrder.getResponse().getOrderDetails().getCartItems().get(i));
//                                    createMarker(cartOrder.getResponse().getOrderDetails().getCartItems().get(i).getLat(),
//                                            cartOrder.getResponse().getOrderDetails().getCartItems().get(i).getLong(),
//                                            cartOrder.getResponse().getOrderDetails().getCartItems().get(i).getAddress(),R.drawable.market_marker);
//                                }
//                            }
//                            try {
//                            total_amount.setText(new DecimalFormat("##").format(cartOrder.getResponse().getOrderDetails().getAmount())+" "+cartOrder.getResponse().
//                            getOrderDetails().getCurrency());
//                            tip.setText(new DecimalFormat("##").format(cartOrder.getResponse().getOrderDetails().getTip()));
//                            final_amount.setText(new DecimalFormat("##").format(cartOrder.getResponse().getOrderDetails().getFinalAmount())+" "+cartOrder.getResponse().
//                                    getOrderDetails().getCurrency());
//                            UserNumber = cartOrder.getResponse().getUserInfo().getPhone();
//                            NetworkConsume.getInstance().setDefaults("yes",cartOrder.getResponse().getUserInfo().getFirstName(),DriverOrders.this);
//                            NetworkConsume.getInstance().setDefaults("userId", String.valueOf(cartOrder.getResponse().getUserInfo().getId()),DriverOrders.this);
////                            createMarker(cartOrder.getResponse().getDriverInfo().getLat(),cartOrder.getResponse().getDriverInfo().getLong(),
////                                    cartOrder.getResponse().getDriverInfo().getAddress());
//                            createMarker(cartOrder.getResponse().getUserInfo().getLat(),cartOrder.getResponse().getUserInfo().getLong(),
//                                    cartOrder.getResponse().getUserInfo().getAddress(),R.drawable.user_marker);
//                            heading(cartOrder.getResponse().getUserInfo().getLat(),cartOrder.getResponse().getUserInfo().getLong());
//                            DriverDetailedAdapter myAdapter = new DriverDetailedAdapter(DriverOrders.this,list);
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DriverOrders.this);
//                            recyclerView.setLayoutManager(mLayoutManager);
//                            recyclerView.setItemAnimator(new DefaultItemAnimator());
//                            recyclerView.setAdapter(myAdapter);
//                            NetworkConsume.getInstance().HideProgress();
//
//                            }catch (Exception e){
//                                NetworkConsume.getInstance().HideProgress();
//                                Toast.makeText(DriverOrders.this, "Something Went Wrong!! Details Missing...", Toast.LENGTH_SHORT).show();
//                            }
//                        }else {
//                            NetworkConsume.getInstance().HideProgress();
//                            Gson gson = new Gson();
//                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
//                            Toast.makeText(DriverOrders.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
//                            if (message.getError().getMessage().get(0).equals("Unauthorized access_token")){
//                                SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
//                                prefs.edit().putString("access_token", "")
//                                        .putString("avatar","")
//                                        .putString("login","").commit();
//
//                                Intent intent = new Intent(DriverOrders.this,  login.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                            }
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<DriverAwardedResp> call, Throwable t) {
//                        NetworkConsume.getInstance().HideProgress();
//                        Toast.makeText(DriverOrders.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//        }catch (Exception e){
//            dialog.dismiss();
//        }
//    }

    @Override
    public void onBackPressed() {

    }

    private void heading(double UserLat, double UserLong) {
        GPSActivity activity = new GPSActivity(DriverOrders.this);
        LatLng current = new LatLng(activity.getLatitude(), activity.getLongitude());
        LatLng UserLoc = new LatLng(UserLat, UserLong);
        double heading = SphericalUtil.computeHeading(current, UserLoc);
        showDistance(current, UserLoc);
    }

    private void showDistance(LatLng a, LatLng b) {
        double distance = SphericalUtil.computeDistanceBetween(a, b);
        Log.w("The markers are ", formatNumber(distance) + " apart.");
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
                if (grantResults.length > 0) {
                    boolean CallResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (CallResult) {
                        if (checkCallPermission())
                            callEvent();
                    } else {
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

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
//            GetOrderDetails();
            setupOrder();
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

    public boolean checkCallPermission() {
        int callPerm = ContextCompat.checkSelfPermission(DriverOrders.this, Manifest.permission.CALL_PHONE);

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

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopPlaying();
    }
}
