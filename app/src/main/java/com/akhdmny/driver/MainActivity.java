package com.akhdmny.driver;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akhdmny.driver.Activities.AcceptOrderActivity;
import com.akhdmny.driver.Activities.MyCart;
import com.akhdmny.driver.Activities.Profile;
import com.akhdmny.driver.ApiResponse.LoginInsideResponse;
import com.akhdmny.driver.ApiResponse.OrderConfirmation;
import com.akhdmny.driver.ApiResponse.UpdateTokenResponse;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.FireBaseNotification.FCM_service;
import com.akhdmny.driver.Fragments.FragmentComplaints;
import com.akhdmny.driver.Fragments.FragmentContact;
import com.akhdmny.driver.Fragments.DriverOrders;
import com.akhdmny.driver.Fragments.FragmentNotification;
import com.akhdmny.driver.Fragments.FragmentOrder;
import com.akhdmny.driver.Fragments.FragmentHome;
import com.akhdmny.driver.Fragments.FragmentSettings;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String AUTH_PREF_KEY = "com.android.akhdmny.authKey";
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.MLocation)
    Button MLocation;
    TextView tvTitle;

    public static int Device_Width;


    private static final int PERMISSION_CODE = 99;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private ActionBarDrawerToggle toggle;
    private FragmentNotification fragmentNotification = new FragmentNotification();
    private FragmentHome fragmentHome = new FragmentHome();
    private DriverOrders fragmentDriverOrders = new DriverOrders();
    private FragmentOrder fragmentOrder = new FragmentOrder();
    private FragmentSettings fragmentSettings = new FragmentSettings();
    private FragmentComplaints fragmentComplaints = new FragmentComplaints();
    private FragmentContact fragmentContact = new FragmentContact();

    private int activeMenu;
    private Handler handlerSaveId = new Handler();
    private long DRAWER_CLOSE_DELAY = 350;
    private String ID_MENU_ACTIVE = "IdMenuActive";
    Button cartButton;
    SharedPreferences prefs;
    String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lang = LocaleHelper.getInstance().getLanguage();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        try {

            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
            Device_Width = metrics.widthPixels;
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                final String path = "Token/" + prefs.getInt("id",0);
                String lang = LocaleHelper.languageSwitcher.getCurrentLocale().getLanguage();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);

                ref.child("lang").setValue(lang);
                UpdateToken(prefs.getInt("id",0),newToken);

            });

        }catch (Exception e){}

        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        String yes = NetworkConsume.getInstance().getDefaults("yes",MainActivity.this);
//        if (yes != null){
//            startActivity(new Intent(MainActivity.this,DriverOrders.class));
//            finish();
//        }
        if(null == savedInstanceState){
            activeMenu = R.id.home;
            setFragment(fragmentHome);


            tvTitle.setText(R.string.Home);


        }else{
            activeMenu = savedInstanceState.getInt(ID_MENU_ACTIVE);
        }
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);


        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Profile.class));
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        try {
            Gson gson = new Gson();

            CircleImageView imageView = headerView.findViewById(R.id.user_profile_Image);
            TextView userName = headerView.findViewById(R.id.userName);
            TextView emailText = headerView.findViewById(R.id.email);
            String jsonLoin = NetworkConsume.getInstance().getDefaults("login",MainActivity.this);
            LoginInsideResponse response = gson.fromJson(jsonLoin,LoginInsideResponse.class);
          //  Picasso.get().load(response.getAvatar()).into(imageView);
            userName.setText(response.getName());
            emailText.setText(response.getEmail());
        }catch (Exception e){}

        cartButton = toolbar.findViewById(R.id.btn_MyCart);

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyCart.class));
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });

        drawerLayout.setDrawerListener(toggle);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset == 0) {
                    // drawer closed
                    // invalidateOptionsMenu();
//                    if (activeMenu == R.id.home) {
//                        btn_layout.setVisibility(View.VISIBLE);
//                    }else {
//                        btn_layout.setVisibility(View.GONE);
//                    }


                } else if (slideOffset != 0)
                {
                   // btn_layout.setVisibility(View.GONE);
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
       toggle.syncState();
        clickLIstner();

    }


    @Override
    protected void onResume() {
        super.onResume();
//        listner();
        setFragment(fragmentHome);
        tvTitle.setText(getString(R.string.Home));
    }
//    private void listner(){
//        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").child("Driver").child(String.valueOf(prefs.getInt("id",1)));
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    if (dataSnapshot1.getKey().equals("orderId")) {
//                        NetworkConsume.getInstance().setDefaults("orderId", dataSnapshot1.getValue().toString(), MainActivity.this);
//                    }
//                    if (dataSnapshot1.getKey().equals("status") && dataSnapshot1.getValue().toString().equals("0")) {
//                        Intent start = new Intent(MainActivity.this, AcceptOrderActivity.class);
//                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(start);
//                    }
//                    if (dataSnapshot1.getKey().equals("status") && dataSnapshot1.getValue().toString().equals("2")) {
//                        Intent start = new Intent(MainActivity.this, DriverOrders.class);
//                        startActivity(start);
////                        requestLocationUpdatesOnRoute();
//                    }
////                    if (dataSnapshot1.getKey().equals("status") && dataSnapshot1.getValue().toString().equals("6")) {
////                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
////                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                        startActivity(intent);
////                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
////        ref.addChildEventListener(new ChildEventListener() {
////            @Override
////            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
////                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
////            }
////
////            @Override
////            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
////                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
////                if (dataSnapshot.getKey().equals("orderId")){
////                    NetworkConsume.getInstance().setDefaults("orderId",dataSnapshot.getValue().toString(),TrackerService.this);
////                }
////                if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("0")){
////                    Intent start = new Intent(TrackerService.this,AcceptOrderActivity.class);
////                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                   startActivity(start);
////                }
////                if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("2")){
////                    Intent start = new Intent(TrackerService.this,DriverOrders.class);
////                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    startActivity(start);
////                    requestLocationUpdatesOnRoute();
////                }
////
////            }
////
////            @Override
////            public void onChildRemoved(DataSnapshot dataSnapshot) {
////                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
////            }
////
////            @Override
////            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
////                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////                Log.w(TAG, "onCancelled", databaseError.toException());
////            }
////        });
//    }


    private void UpdateToken(int id, String token){
        final String path = "Token/" + id;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.child("token").setValue(token);
    }

    private void clickLIstner(){
        MLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeMenu == R.id.home) {
                   fragmentHome.getCurrentLocation();
                }else {
                    Log.i("Hide","true");
                }
            }
        });
    }


    private void switchFragment(int activeMenu) {
        switch (activeMenu){
            case R.id.home:
                setFragment(fragmentHome);
                tvTitle.setText(R.string.Home);
               // btn_layout.setVisibility(View.VISIBLE);
                cartButton.setVisibility(View.GONE);
//                String json = NetworkConsume.getInstance().getDefaults("myObject",MainActivity.this);

                break;
            case R.id.orders:
                setFragment(fragmentOrder);
                tvTitle.setText(R.string.Orders);
                cartButton.setVisibility(View.GONE);
                break;
            case R.id.settings:
                setFragment(fragmentSettings);
                tvTitle.setText(R.string.settings);

                cartButton.setVisibility(View.GONE);
                break;
            case R.id.complaints:
                setFragment(fragmentComplaints);
                tvTitle.setText(R.string.complaint);
                cartButton.setVisibility(View.GONE);
                break;
            case R.id.notifications:
                setFragment(fragmentNotification);
                tvTitle.setText(R.string.notifications);
                cartButton.setVisibility(View.GONE);
                break;
            case R.id.contact:
                setFragment(fragmentContact);
                tvTitle.setText(R.string.contact);
                cartButton.setVisibility(View.GONE);
                break;
            default:
                //Default
                break;
        }
    }
    private void setFragment(Fragment fragment){
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        activeMenu = menuItem.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);
        handlerSaveId.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchFragment(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY);

        return true;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    /**
     * to case active id menu
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ID_MENU_ACTIVE, activeMenu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        switch (requestCode) {
//
//            case PERMISSION_CODE:
//
//                if (grantResults.length > 0) {
//
//                    boolean finelocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean coarselocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//
//                    if (finelocation && coarselocation) {
//
//                        if (checkPermission())
//                            buildGoogleApiClient();
//                        mMap.setMyLocationEnabled(true);
//
//
//                        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
//
//                    }
//                }
//
//                break;
//        }

    }

}
