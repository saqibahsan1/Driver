package com.akhdmny.driver.NetworkManager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akhdmny.driver.R;
import com.akhdmny.driver.Service.AuthService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    private static final String API_ENDPOINT = "https://us-central1-akhdmny-4d778.cloudfunctions.net/";
    private String _accessToken = null;

    private Retrofit _retrofit;
    private Retrofit.Builder _retrofitBuilder = null;
    private HttpLoggingInterceptor _interceptor;
    private OkHttpClient.Builder _client;

    public Network() {
        _interceptor = new HttpLoggingInterceptor();
        _interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        rebuild();
    }

    public void rebuild() {
        _client = new OkHttpClient.Builder()
                .addInterceptor(_interceptor);

        if (this._accessToken != null) {
            _client.connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS);
            _client.addInterceptor(chain -> {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Content-Type","application/json");
                        /*.header("Authorization", _accessToken)
                        .header("client-id","akhdmny-app-android");*/
                // <-- this is the important line

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });
        }

        if (_retrofitBuilder == null) {
            _retrofitBuilder = new Retrofit.Builder();
        } else {
            _retrofitBuilder = _retrofit.newBuilder();
        }

        _retrofit = _retrofitBuilder.baseUrl(API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(_client.build())
                .build();
    }
    public static final Network getInstance() {
        return Network.SingletonHolder._instance;
    }

    private static final class SingletonHolder {
        protected static final Network _instance = new Network();
    }

    public AuthService getAuthAPINew() {
        return _retrofit.create(AuthService.class);
    }
    public void setAccessKey(String accessToken) {
        this._accessToken = accessToken;

        rebuild();
    }
    public boolean checkPermission(Context context) {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;

    }
    public void SnackBarSucccess(LinearLayout layout, Context context, int message){
        Snackbar snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        FrameLayout.LayoutParams params=(FrameLayout.LayoutParams)snackbarLayout.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarLayout.setLayoutParams(params);
        snackbarLayout.setBackgroundColor(context.getResources().getColor(R.color.green));
        TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick, 0, 0, 0);
        textView.setCompoundDrawablePadding(context.getResources().getDimensionPixelOffset(R.dimen.snackBar));
        snackbar.show();

    }
    public void SnackBarSucccessComplaint(LinearLayout layout,Context context,int message){
        Snackbar snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)snackbarLayout.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarLayout.setLayoutParams(params);
        snackbarLayout.setBackgroundColor(context.getResources().getColor(R.color.green));
        TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick, 0, 0, 0);
        textView.setCompoundDrawablePadding(context.getResources().getDimensionPixelOffset(R.dimen.snackBar));
        snackbar.show();

    }
    public void SnackBarError(LinearLayout layout,Context context,int Message){
        Snackbar snackbar = Snackbar.make(layout, Message, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        FrameLayout.LayoutParams params=(FrameLayout.LayoutParams)snackbarLayout.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarLayout.setLayoutParams(params);
        snackbarLayout.setBackgroundColor(context.getResources().getColor(R.color.Red));
        TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cross, 0, 0, 0);
        textView.setCompoundDrawablePadding(context.getResources().getDimensionPixelOffset(R.dimen.snackBar));
        snackbar.show();

    }
    public void SnackBarErrorHistory(LinearLayout layout,Context context,String Message){
        Snackbar snackbar = Snackbar.make(layout, Message, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)snackbarLayout.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarLayout.setLayoutParams(params);
        snackbarLayout.setBackgroundColor(context.getResources().getColor(R.color.Red));
        TextView textView = (TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cross, 0, 0, 0);
        textView.setCompoundDrawablePadding(context.getResources().getDimensionPixelOffset(R.dimen.snackBar));
        snackbar.show();

    }
    public void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
}
