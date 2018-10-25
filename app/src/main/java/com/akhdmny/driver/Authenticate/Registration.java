package com.akhdmny.driver.Authenticate;

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

import com.akhdmny.driver.ApiResponse.RegisterResponse;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Requests.SignInRequest;
import com.akhdmny.driver.Utils.Validator;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.victor.loading.rotate.RotateLoading;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity{

    @BindView(R.id.et_country)
    EditText et_country;
    @BindView(R.id.et_phone_number)
    EditText et_phone_number;
    @BindView(R.id.et_Password)
    EditText et_Password;
    @BindView(R.id.rotateloading)
    RotateLoading rotateLoading;
    @BindView(R.id.ccp_getFullNumber)
    CountryCodePicker ccp_getFullNumber;
    @BindView(R.id.btn_register)
    Button btn_register;
    String token = "YWhsYW0tYXBwLWFuZHJvaWQ6NGQxNjNlZTgtMzJiZi00M2U2LWFlMzgtY2E1YmMwZjA0N2Nk";
    SpotsDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ButterKnife.bind(this);
        onClick();

    }
    private void onClick(){

        ccp_getFullNumber.registerCarrierNumberEditText(et_phone_number);
        ccp_getFullNumber.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidNumber) {
                    Toast.makeText(Registration.this, "Valid number", Toast.LENGTH_SHORT).show();
                } else {
                    //  et_phone_number.setError("Invalid Number");
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void Register(){
        dialog = new SpotsDialog(this,"Please wait...");
        Validator validator = new Validator(Registration.this, true);

        validator
                .setRules(Validator.Rules.REQUIRED, Validator.Rules.MIN)
                .validate(ccp_getFullNumber.getSelectedCountryName(), et_country, 2)
                .validate(ccp_getFullNumber.getFullNumberWithPlus(), et_phone_number, 3)
                .validate(et_Password.getText().toString(), et_Password, 3);

        if (validator.fails()) {
//            rotateLoading.stop();
            return;
        }
        dialog.show();
        SignInRequest request = new SignInRequest();
        request.setCountry(ccp_getFullNumber.getSelectedCountryName());
        request.setPhone(ccp_getFullNumber.getFullNumberWithPlus());
        request.setPassword(et_Password.getText().toString());
        NetworkConsume.getInstance().setAccessKey("Basic "+token);
        NetworkConsume.getInstance().getAuthAPI().register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {

                    SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                    prefs.edit().putString("number", ccp_getFullNumber.getFullNumberWithPlus()).commit();
                    startActivity(new Intent(Registration.this,Verification.class));
//                    rotateLoading.stop();
                    dialog.hide();
                    finish();


                }else {
                    dialog.hide();
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(Registration.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                dialog.hide();
                Toast.makeText(Registration.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
