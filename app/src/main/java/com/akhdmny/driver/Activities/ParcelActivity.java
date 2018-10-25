package com.akhdmny.driver.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akhdmny.driver.Adapter.ImagesAdapter;
import com.akhdmny.driver.ApiResponse.ParcelApiResponse;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ParcelActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, MediaPlayer.OnCompletionListener {

    @BindView(R.id.FromLayout)
    RelativeLayout fromLayout;
    @BindView(R.id.parcelLayout)
    LinearLayout parcelLayout;
    @BindView(R.id.seek_barLayout)
    LinearLayout seek_barLayout;
    @BindView(R.id.ToLayout)
    RelativeLayout ToLayout;
    @BindView(R.id.parcelSendBtn)
    Button parcelSendBtn;
    @BindView(R.id.TVFromAddress)
    TextView TVFromAddress;
    @BindView(R.id.TvToAddress)
    TextView TvToAddress;
    @BindView(R.id.total_distance)
    TextView total_distance;
    @BindView(R.id.et_msg)
    EditText et_msg;
    @BindView(R.id.recordAudio)
    ImageView recordAudio;
    @BindView(R.id.PlayAudio)
    ImageView PlayAudio;
    @BindView(R.id.addImages)
    ImageView addImages;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    TextView tvTitle;
    MediaPlayer mediaPlayer;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private int PLACE_PICKER_ToRequest = 2;
    private ImagesAdapter imagesAdapter;
    private Handler mHandler;
    private Runnable mRunnable;
    Place place,place2;
    SharedPreferences prefs;
    String dist;
    SpotsDialog dialog;
    Boolean trigger = false;
    public static final int RequestPermissionCode = 0;
    private static final String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/recorded_audio1.wav";

    private ArrayList<File> photos = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parcel);
        ButterKnife.bind(this);
        mHandler = new Handler();
        setSupportActionBar(toolbar);
        tvTitle = toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText("Parcel");
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        fromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(ParcelActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        ToLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(ParcelActivity.this), PLACE_PICKER_ToRequest);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto();
            }
        });
        parcelSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_msg.getText().toString().equals("")){
                    NetworkConsume.getInstance().SnackBarError(parcelLayout,ParcelActivity.this,R.string.error_text);
                }else {
                    ParcelApi();
                }
            }
        });
        recordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()) {


                    AndroidAudioRecorder.with(ParcelActivity.this)
                            // Required
                            .setFilePath(AUDIO_FILE_PATH)
                            .setColor(ContextCompat.getColor(ParcelActivity.this, R.color.colorPrimary))
                            .setRequestCode(RequestPermissionCode)

                            // Optional
                            .setSource(AudioSource.MIC)
                            .setChannel(AudioChannel.STEREO)
                            .setSampleRate(AudioSampleRate.HZ_48000)
                            .setAutoStart(false)
                            .setKeepDisplayOn(true)

                            // Start recording
                            .record();



                }else {
                    requestPermission();
                }
            }
        });
        PlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){

                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    private void ParcelApi(){
        dialog = new SpotsDialog(this,"Please wait...");
        dialog.show();
        File AudioFile = new File(AUDIO_FILE_PATH);
        RequestBody propertyImage = RequestBody.create(MediaType.parse("audio/*"), AudioFile);
        MultipartBody.Part voicePart = MultipartBody.Part.createFormData("voice", AudioFile.getName(), propertyImage);

        MultipartBody.Part[] ImagesParts = new MultipartBody.Part[photos.size()];
        for (int i = 0; i<photos.size();i++) {
            File addImageFile = new File(photos.get(i).getPath());
            RequestBody ImageBody = RequestBody.create(MediaType.parse("image/*"), addImageFile);
            ImagesParts[i] = MultipartBody.Part.createFormData("images["+i+"]",addImageFile.getName(),ImageBody);
        }
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().ParcelApi(place.getLatLng().latitude,place.getLatLng().longitude,
                place2.getLatLng().latitude,place2.getLatLng().longitude,et_msg.getText().toString(),20,dist,ImagesParts,voicePart).
                enqueue(new Callback<ParcelApiResponse>() {
            @Override
            public void onResponse(Call<ParcelApiResponse> call, Response<ParcelApiResponse> response) {
                if (response.isSuccessful()){
                    dialog.hide();
                    NetworkConsume.getInstance().SnackBarError(parcelLayout,ParcelActivity.this,R.string.successParcel);
                }else {
                    dialog.hide();
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(ParcelActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<ParcelApiResponse> call, Throwable t) {
                dialog.hide();
                Toast.makeText(ParcelActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void bottomSheet(){
        BottomSheetMenuDialog dialog = new BottomSheetBuilder(ParcelActivity.this, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setMenu(R.menu.bottom_menu)
                .addDividerItem()
                .setItemBackground(R.drawable.drawer_item_bg)
                .setItemTextColorResource(R.color.colorAccent)
                .setBackgroundColorResource(R.color.colorPrimary)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        if (item.getTitle().equals("Listen")){
                            if(isPlaying()){
                                stopPlaying();
                            } else {
                                startPlaying(AUDIO_FILE_PATH);
                            }
                        }
                        if (item.getTitle().equals("Remove")){
                            recordAudio.setVisibility(View.VISIBLE);
                            PlayAudio.setVisibility(View.GONE);
                            seekBar.setProgress(0);
                            seek_barLayout.setVisibility(View.GONE);
                        }
                        if (item.getTitle().equals("Record")){
                            if(checkPermission()) {


                                AndroidAudioRecorder.with(ParcelActivity.this)
                                        // Required
                                        .setFilePath(AUDIO_FILE_PATH)
                                        .setColor(ContextCompat.getColor(ParcelActivity.this, R.color.colorPrimary))
                                        .setRequestCode(RequestPermissionCode)

                                        // Optional
                                        .setSource(AudioSource.MIC)
                                        .setChannel(AudioChannel.STEREO)
                                        .setSampleRate(AudioSampleRate.HZ_48000)
                                        .setAutoStart(false)
                                        .setKeepDisplayOn(true)

                                        // Start recording
                                        .record();
                            }else {
                                requestPermission();
                            }
                        }
                    }
                })
                .createDialog();

        dialog.show();
    }
    protected void initializeSeekBar(){
        seekBar.setMax(mediaPlayer.getDuration());

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
                mHandler.postDelayed(mRunnable,50);
            }
        };
        mHandler.postDelayed(mRunnable,50);
    }

    private void startPlaying(String source){
        try {

            mediaPlayer = new MediaPlayer();
            seek_barLayout.setVisibility(View.VISIBLE);
            mediaPlayer.setOnCompletionListener(ParcelActivity.this);

            try {
                mediaPlayer.setDataSource(source);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.start();
            initializeSeekBar();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void stopPlaying(){
        PlayAudio.setImageResource(R.drawable.ic_play);


        if(mediaPlayer != null){
            try {
                seekBar.setProgress(0);
                mediaPlayer.stop();
                mediaPlayer.reset();
                seek_barLayout.setVisibility(View.GONE);
                if(mHandler!=null){
                    mHandler.removeCallbacks(mRunnable);
                }
            } catch (Exception e){ }
        }

    }
    private boolean isPlaying(){
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (Exception e){
            return false;
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlaying();
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this,
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    private void onPickPhoto() {
        EasyImage.openChooserWithGallery(ParcelActivity.this,"Pick source",0);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        NetworkConsume.getInstance().SnackBarErrorHistory(parcelLayout,this,connectionResult.getErrorMessage());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                trigger = true;
                place = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);
                TVFromAddress.setText(stBuilder.toString());
            }
        }
        if (requestCode == PLACE_PICKER_ToRequest) {
            if (resultCode == RESULT_OK) {
                trigger = true;
                place2 = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place2.getName());
                String latitude = String.valueOf(place2.getLatLng().latitude);
                String longitude = String.valueOf(place2.getLatLng().longitude);
                String address = String.format("%s", place2.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("Address: ");
                stBuilder.append("\n");
                stBuilder.append(address);
                TvToAddress.setText(stBuilder.toString());
            }
            try {
                if (trigger){
                    double distance  = CalculationByDistance(place.getLatLng(),place2.getLatLng());
                    dist = new DecimalFormat("##.##").format(distance)+" km" ;
                    //total_distance.setText(String.valueOf(distance));
                    total_distance.setText(dist);

                }
            }catch (Exception e){}

        }
        if (requestCode == 0){
            if (resultCode == RESULT_OK) {
                // Great! User has recorded and saved the audio file
                recordAudio.setVisibility(View.GONE);
                PlayAudio.setVisibility(View.VISIBLE);
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
            }
        }
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(ParcelActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }
    private void onPhotosReturned(List<File> returnedPhotos) {
        photos.addAll(returnedPhotos);
        recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler_view.setHasFixedSize(true);
        imagesAdapter = new ImagesAdapter(this, photos);

        recycler_view.setAdapter(imagesAdapter);
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
