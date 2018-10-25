package com.akhdmny.driver.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.akhdmny.driver.ApiResponse.LoginInsideResponse;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Requests.ProfileResponse;
import com.google.gson.Gson;
import com.raywenderlich.android.validatetor.ValidateTor;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {
    @BindView(R.id.user_profile_Image)
    CircleImageView user_profile_Image;

    @BindView(R.id.Et_firstName)
    EditText Et_firstName;

    @BindView(R.id.profileLayout)
    LinearLayout profileLayout;

    @BindView(R.id.et_lastName)
    EditText et_lastName;

    @BindView(R.id.et_Mobile)
    EditText et_Mobile;

    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.et_email)
    EditText et_email;

    @BindView(R.id.et_Confirm_password)
    EditText et_Confirm_password;

    @BindView(R.id.et_address)
    EditText et_address;

    @BindView(R.id.et_country)
    EditText et_country;

    @BindView(R.id.et_gender)
    EditText et_gender;

    @BindView(R.id.Btn_save)
    Button Btn_save;
    SharedPreferences prefs;
    SpotsDialog dialog;

    private ArrayList<File> photos = new ArrayList<>();

    //    @BindView(R.id.ccp_getFullNumber)
//    CountryCodePicker ccp_getFullNumber;
    ValidateTor validateTor = new ValidateTor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.profile);
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        Btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();
            }
        });
        user_profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto();
            }
        });

        try {
            Gson gson = new Gson();
            String jsonLoin = NetworkConsume.getInstance().getDefaults("login",Profile.this);
            LoginInsideResponse response = gson.fromJson(jsonLoin,LoginInsideResponse.class);
          //  Picasso.get().load(response.getAvatar()).into(user_profile_Image);
            String splitter = response.getName();
            String[] strings = splitter.split(" ");

            Et_firstName.setText(strings[0]);
            et_email.setText(response.getEmail());
            et_lastName.setText(strings[1]);
            et_Mobile.setText(response.getPhone());
            et_address.setText(response.getCountry());
            et_country.setText(response.getCountry());
        }catch (Exception e){}
    }

    private void Validate(){
        View focusView = null;
        Boolean cancel = false;
        if (validateTor.isEmpty(Et_firstName.getText().toString())){
            Et_firstName.setError("Please enter your First name");
            focusView = Et_firstName;
            cancel = true;

        }if (validateTor.isEmpty(et_lastName.getText().toString())){
            et_lastName.setError("Please enter your last name");
            focusView = et_lastName;
            cancel = true;
        }if (validateTor.isEmpty(et_Mobile.getText().toString())){
            et_Mobile.setError("Please enter your number");
            focusView = et_Mobile;
            cancel = true;
        }if (validateTor.isEmpty(et_gender.getText().toString())){
            et_gender.setError("Please select your gender");
            focusView = et_gender;
            cancel = true;
        }
        if (validateTor.isEmpty(et_address.getText().toString())){
            et_address.setError("Please select your address");
            focusView = et_address;
            cancel = true;
        }
        if (validateTor.isAtleastLength(et_password.getText().toString(),6)
                && et_password.getText().toString().equals(et_Confirm_password.getText().toString()) )
        {
            cancel = false;

        }else {
            cancel = true;
            focusView = et_password;
            et_password.setError("Password did not matched");
        }
        if (et_password.getText().toString().equals(et_Confirm_password.getText().toString())){
            Toast.makeText(this, "Password Matched", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Password did not matched", Toast.LENGTH_SHORT).show();
            cancel = true;
        }
        if (cancel){
            if (focusView!=null){
                focusView.requestFocus();
            }
        }else {
            dialog = new SpotsDialog(this,"Please wait...");
            dialog.show();
            UpdateProfileApi();
        }
    }
    private void onPickPhoto() {
        EasyImage.openChooserWithGallery(Profile.this,"Pick source",0);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(Profile.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }
    private void onPhotosReturned(List<File> returnedPhotos) {
        photos.addAll(returnedPhotos);
        Picasso.get()
                .load(photos.get(photos.size()-1))
                .fit()
                .into(user_profile_Image);
    }

    private void UpdateProfileApi(){

        MultipartBody.Part[] ImagesParts = new MultipartBody.Part[photos.size()];
        for (int i = 0; i<photos.size();i++) {
            File addImageFile = new File(photos.get(i).getPath());
            RequestBody ImageBody = RequestBody.create(MediaType.parse("image/*"), addImageFile);
            ImagesParts[i] = MultipartBody.Part.createFormData("avatar", addImageFile.getName(), ImageBody);
        }
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().UpdateProfile(Et_firstName.getText().toString(),et_lastName.getText().toString(),
                et_password.getText().toString(),et_email.getText().toString(),et_address.getText().toString(),et_gender.getText().toString(),
                ImagesParts).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()){
                    ProfileResponse response1 = response.body();
                    NetworkConsume.getInstance().SnackBarSucccess(profileLayout,Profile.this, R.string.success);
                    dialog.hide();
                }else {
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    NetworkConsume.getInstance().SnackBarError(profileLayout,Profile.this,R.string.error);
                    dialog.hide();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(Profile.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.hide();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
