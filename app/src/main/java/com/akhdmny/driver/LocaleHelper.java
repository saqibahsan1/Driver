package com.akhdmny.driver;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;


import com.ahmedjazzar.rosetta.LanguageSwitcher;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.HashSet;
import java.util.Locale;

public class LocaleHelper extends Application{

    private Locale firstLaunchLocale;
    private HashSet<Locale> supportedLocales;
    public static LanguageSwitcher languageSwitcher;
    private static LocaleHelper mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        init();
        logUser();
    }

    private void init(){
        mInstance = this;
        AutomatedSupportedLocales();
        manualSupportedLocales();

        languageSwitcher = new LanguageSwitcher(this, firstLaunchLocale);
        languageSwitcher.setSupportedLocales(supportedLocales);
    }

    public static synchronized LocaleHelper getInstance() {
        return mInstance;
    }

    private void AutomatedSupportedLocales() {

    }

    private void manualSupportedLocales() {
        // This is the locale that you wanna your app to launch with.
        firstLaunchLocale = new Locale("en");

        // You can use a HashSet<String> instead and call 'setSupportedStringLocales()' :)
        supportedLocales = new HashSet<>();
        supportedLocales.add(Locale.US);
        supportedLocales.add(Locale.CHINA);
        supportedLocales.add(firstLaunchLocale);
    }

    public void setLanguage(String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString("lang", language).commit();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public String getLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("lang", "en");
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("Test User");
    }

}
