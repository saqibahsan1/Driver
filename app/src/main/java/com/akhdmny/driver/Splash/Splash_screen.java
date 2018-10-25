package com.akhdmny.driver.Splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.akhdmny.driver.Authenticate.login;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.R;

public class Splash_screen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    private static String SPLASH_TAG = "SPLASH";
    SharedPreferences prefs;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        try{
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if(getIntent()!=null){
                        if (!prefs.getString("access_token","").equals("")) {

                            Intent i = new Intent(Splash_screen.this, MainActivity.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                        }
                       else {
                            Intent i = new Intent(Splash_screen.this, login.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                        }
                    }
                }

            }, SPLASH_TIME_OUT);

        }catch (Exception e){
            Log.e(SPLASH_TAG, e.getLocalizedMessage());
        }
    }
}
