package com.akhdmny.driver.Service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.akhdmny.driver.Activities.AcceptOrderActivity;
import com.akhdmny.driver.ApiResponse.UpdateDriverLoc;
import com.akhdmny.driver.Fragments.DriverOrders;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.Network;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Utils.UserDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    SharedPreferences prefs;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestLocationUpdates();
        listner();
        String yes = NetworkConsume.getInstance().getDefaults("yes",TrackerService.this);
        if (yes != null){
            requestLocationUpdatesOnRoute();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Toast.makeText(this, " MyService Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }
    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };
    private void listner(){
         prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").child("Driver").child(String.valueOf(prefs.getInt("id",1)));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                if (dataSnapshot.getKey().equals("orderId")){
                    NetworkConsume.getInstance().setDefaults("orderId",dataSnapshot.getValue().toString(),TrackerService.this);
                }
                if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("0")){
                    Intent start = new Intent(TrackerService.this,AcceptOrderActivity.class);
                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(start);
                }
                if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("2")){
                    Intent start = new Intent(TrackerService.this,DriverOrders.class);
                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(start);
                    requestLocationUpdatesOnRoute();
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }


    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(50000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
       // final String path = "Tracking/" + prefs.getInt("id",1);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);

            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                 //   DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        UpdateLoc(prefs.getInt("id",1),location.getLatitude(),location.getLongitude());
                        Map<String, Double> map = new HashMap<String, Double>();
                        map.put("latitude", location.getLatitude());
                        map.put("longitude", location.getLongitude());
                        map.put("heading", location.getAltitude());
                        map.put("status", Double.valueOf("0"));
                        Log.d(TAG, "location update " + location);
                       // ref.setValue(map);
                    }
                }
            }, null);
        }
    }
    private void requestLocationUpdatesOnRoute() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(50000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = "Tracking/" + prefs.getInt("id",1);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);

            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        //UpdateLoc(prefs.getInt("id",1),location.getLatitude(),location.getLongitude());
                         Map<String, Double> map = new HashMap<String, Double>();
                        map.put("latitude", location.getLatitude());
                        map.put("longitude", location.getLongitude());
                        map.put("heading", location.getAltitude());
//                        map.put("status", Double.valueOf("0"));
                        Log.d(TAG, "location update " + location);
                       ref.setValue(map);
                    }
                }
            }, null);
        }
    }

    private void UpdateLoc(int id,double lat, double longitude){

        Network.getInstance().getAuthAPINew().DriverLoc(id,lat,longitude).enqueue(new Callback<UpdateDriverLoc>() {
            @Override
            public void onResponse(Call<UpdateDriverLoc> call, Response<UpdateDriverLoc> response) {
                if (response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<UpdateDriverLoc> call, Throwable t) {

            }
        });

    }
}
