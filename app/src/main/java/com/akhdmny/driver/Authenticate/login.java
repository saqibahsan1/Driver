package com.akhdmny.driver.Authenticate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.akhdmny.driver.ApiResponse.LoginApiResponse;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Requests.LoginRequest;
import com.akhdmny.driver.Utils.UserDetails;
import com.akhdmny.driver.Utils.Validator;
import com.google.gson.Gson;
import com.victor.loading.rotate.RotateLoading;

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
    String token = "YWhsYW0tYXBwLWFuZHJvaWQ6NGQxNjNlZTgtMzJiZi00M2U2LWFlMzgtY2E1YmMwZjA0N2Nk";
    SpotsDialog dialog;
    private Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        ClickEvent();

    }

    private void ClickEvent(){
        mActivity = this;

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
        dialog = new SpotsDialog(this,"Please wait...");
        dialog.show();
        Validator validator = new Validator(login.this, true);

        validator
                .setRules(Validator.Rules.REQUIRED, Validator.Rules.MIN)
                .validate(et_Mobile.getText().toString(), et_Mobile, 3)
                .validate(et_password.getText().toString(), et_password, 3);

        if (validator.fails()) {
            dialog.hide();
            return;
        }
        // driver login 923138834882
        NetworkConsume.getInstance().setAccessKey("Basic "+token);
        LoginRequest request = new LoginRequest();
        request.setPhone(et_Mobile.getText().toString());
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
                    dialog.hide();

                }else {
                    // rotateLoading.stop();
                    dialog.hide();
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(mActivity, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginApiResponse> call, Throwable t) {
                dialog.hide();
                Toast.makeText(login.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
