package com.akhdmny.driver.Authenticate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.Requests.VerificationReguest;
import com.akhdmny.driver.Utils.Validator;
import com.akhdmny.driver.ApiResponse.LoginApiResponse;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Requests.VerificationReguest;
import com.akhdmny.driver.Utils.Validator;
import com.google.gson.Gson;
import com.poovam.pinedittextfield.PinField;
import com.poovam.pinedittextfield.SquarePinField;
import com.victor.loading.rotate.RotateLoading;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Verification extends AppCompatActivity {
    @BindView(R.id.squareField)
    SquarePinField squareField;
    @BindView(R.id.rotateloading)
    RotateLoading rotateLoading;
    SharedPreferences prefs;
    String token = "YWhsYW0tYXBwLWFuZHJvaWQ6NGQxNjNlZTgtMzJiZi00M2U2LWFlMzgtY2E1YmMwZjA0N2Nk";
    SpotsDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification);
        ButterKnife.bind(this);
         prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        squareField.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public void onTextComplete(@NotNull String enteredText) {
                Toast.makeText(Verification.this,enteredText,Toast.LENGTH_SHORT).show();
                Login(enteredText);
            }
        });
    }
    private void Login(String Code){
        dialog = new SpotsDialog(this,"Verifying...");
        Validator validator = new Validator(Verification.this, true);

        validator
                .setRules(Validator.Rules.REQUIRED, Validator.Rules.MIN)
                .validate(Code, squareField, 4);


        if (validator.fails()) {
            rotateLoading.stop();
            return;
        }
        dialog.show();
        NetworkConsume.getInstance().setAccessKey("Basic "+token);
        VerificationReguest request = new VerificationReguest();
        request.setCode(Integer.valueOf(Code));
        String number = prefs.getString("number","+923313037613");
        request.setPhone(number);

        NetworkConsume.getInstance().getAuthAPI().VerificationApi(request).enqueue(new Callback<LoginApiResponse>() {
            @Override
            public void onResponse(Call<LoginApiResponse> call, Response<LoginApiResponse> response) {
                if (response.isSuccessful()) {

                    LoginApiResponse apiResponse = response.body();
                    Toast.makeText(Verification.this, apiResponse.getStatus().toString(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Verification.this,MainActivity.class));
                    dialog.hide();
                    finish();

                }else {
                    dialog.hide();
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(Verification.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginApiResponse> call, Throwable t) {
                dialog.hide();
                Toast.makeText(Verification.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}
