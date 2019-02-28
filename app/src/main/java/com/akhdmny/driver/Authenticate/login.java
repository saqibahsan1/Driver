package com.akhdmny.driver.Authenticate;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akhdmny.driver.ApiResponse.LoginApiResponse;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Requests.LoginRequest;
import com.akhdmny.driver.Service.TrackerService;
import com.akhdmny.driver.Utils.UserDetails;
import com.akhdmny.driver.Utils.Validator;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.victor.loading.rotate.RotateLoading;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity {

    @BindView(R.id.et_Mobile)
    EditText et_Mobile;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.btn_register)
    Button btn_register;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.rotateloading)
    RotateLoading rotateLoading;
    @BindView(R.id.btn_forgot_password)
    Button btn_forgot_password;
    @BindView(R.id.btn_skip)
    Button btn_skip;
    @BindView(R.id.eye_icon)
    ImageView eye_icon;
    @BindView(R.id.ccp_getFullNumber)
    CountryCodePicker ccp_getFullNumber;
    boolean show = true;
    String token = "YWhsYW0tYXBwLWFuZHJvaWQ6NGQxNjNlZTgtMzJiZi00M2U2LWFlMzgtY2E1YmMwZjA0N2Nk";
    private Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        ClickEvent();


    }

    private void stopService(){

        Intent intent = new Intent(login.this, TrackerService.class);
        Objects.requireNonNull(login.this).stopService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void ClickEvent(){
        mActivity = this;
        if (isMyServiceRunning(TrackerService.class)){
            stopService();
        }
        ccp_getFullNumber.registerCarrierNumberEditText(et_Mobile);
        eye_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (show){
                    eye_icon.setBackgroundResource(android.R.color.transparent);
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    et_password.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                    show = false;
                }else {
                    eye_icon.setBackgroundResource(android.R.color.transparent);
//                    eye_icon.setBackgroundResource(R.drawable.eye);
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_password.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password
                    show=true;
                }

            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,Registration.class));

            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }

    private void Login(){
        // rotateLoading.start();
        NetworkConsume.getInstance().ShowProgress(login.this);
        Validator validator = new Validator(login.this, true);

        validator
                .setRules(Validator.Rules.REQUIRED, Validator.Rules.MIN)
                .validate(ccp_getFullNumber.getFullNumberWithPlus(), et_Mobile, 3)
                .validate(et_password.getText().toString(), et_password, 3);

        if (validator.fails()) {
           NetworkConsume.getInstance().HideProgress();
            return;
        }
        // driver login 923138834882
        NetworkConsume.getInstance().setAccessKey("Basic "+token);
        LoginRequest request = new LoginRequest();
        request.setPhone(ccp_getFullNumber.getFullNumberWithPlus());
        request.setPassword(et_password.getText().toString());

        NetworkConsume.getInstance().getAuthAPI().LoginApi(request).enqueue(new Callback<LoginApiResponse>() {
            @Override
            public void onResponse(Call<LoginApiResponse> call, Response<LoginApiResponse> response) {
                if (response.isSuccessful()) {
                    LoginApiResponse apiResponse = response.body();
                    UserDetails.username = apiResponse.getResponse().getId();
                    Gson gson = new Gson();
                    String json = gson.toJson(apiResponse.getResponse());
                    NetworkConsume.getInstance().setDefaults("login",json,login.this);
                    SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                    prefs.edit().putString("access_token", apiResponse.getResponse().getAccessToken())
                            .putInt("id",apiResponse.getResponse().getId())
                            .putString("avatar",apiResponse.getResponse().getAvatar()).commit();
                    startActivity(new Intent(login.this,MainActivity.class));
//                    rotateLoading.stop();

                    finish();
                    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                    NetworkConsume.getInstance().HideProgress();

                }else {
                    // rotateLoading.stop();
                    NetworkConsume.getInstance().HideProgress();
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(mActivity, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginApiResponse> call, Throwable t) {
                NetworkConsume.getInstance().HideProgress();
                Toast.makeText(login.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
