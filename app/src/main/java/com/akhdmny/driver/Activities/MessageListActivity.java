package com.akhdmny.driver.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import com.akhdmny.driver.Adapter.MessageListAdapter;
import com.akhdmny.driver.ApiResponse.AcceptModel.Driver;
import com.akhdmny.driver.ApiResponse.AcceptModel.User;
import com.akhdmny.driver.Models.Message;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Singletons.CurrentOrder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MessageListActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, MediaPlayer.OnCompletionListener {

    private EditText editText;
    private MessageListAdapter mMessageAdapter;
    private RecyclerView mMessageRecycler;
    private List<Message> messageList = new ArrayList<>();
    private DatabaseReference ref;
    private SimpleDateFormat formatter;
    private Driver user = CurrentOrder.getInstance().driver;
    private int orderId = CurrentOrder.getInstance().orderId;
    private ValueEventListener valueEventListner;
    private GoogleApiClient mGoogleApiClient;
    Place place;

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this,
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList, "31");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMessageRecycler.setLayoutManager(linearLayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);


        editText = findViewById(R.id.edittext_chatbox);
        Button sendButton = findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(view -> {
            Message message = new Message();
            message.setBody(editText.getText().toString());
            message.setType(1);
            sendMessage(message);
            editText.setText("");
        });

        setupButtons();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        ref = FirebaseDatabase.getInstance().getReference().child("Chat").child(orderId + "").child("messages");
        getFirebaseMessage();

    }

    private void getFirebaseMessage() {
        valueEventListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Boolean isRead = Boolean.parseBoolean(Objects.requireNonNull(snapshot.child("isRead").getValue()).toString());
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        message.setRead(isRead);
                    }
                    messageList.add(message);
                    if (user != null) {
                        if (message != null) {
                            if (!message.getSenderId().equals(user.getId() + "")) {
                                if (message.getRead() == null) {
                                    message.setRead(false);
                                }
                                ref.child(message.getId()).child("isRead").setValue(true);
                            }
                        }
                    }
                }
                mMessageAdapter.notifyDataSetChanged();
                mMessageRecycler.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addValueEventListener(valueEventListner);
    }

    private void setupButtons() {

        Button camera_button = findViewById(R.id.camera_button);
        Button audio_button = findViewById(R.id.audio_button);
        Button location_button = findViewById(R.id.location_button);

        audio_button.setOnClickListener(view -> {
            if (checkPermission()) {
                AndroidAudioRecorder.with(MessageListActivity.this)
                        // Required
                        .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/recorded_audio1.wav")
                        .setColor(ContextCompat.getColor(MessageListActivity.this, R.color.colorPrimary))
                        .setRequestCode(0)
                        // Optional
                        .setSource(AudioSource.MIC)
                        .setChannel(AudioChannel.STEREO)
                        .setSampleRate(AudioSampleRate.HZ_48000)
                        .setAutoStart(false)
                        .setKeepDisplayOn(true)
                        // Start recording
                        .record();
            } else {
                ActivityCompat.requestPermissions(this, new
                        String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, 0);
            }

        });
        camera_button.setOnClickListener(view -> {
            EasyImage.openChooserWithGallery(MessageListActivity.this, "Pick source", 0);
        });
        location_button.setOnClickListener(view -> {

            NetworkConsume.getInstance().ShowProgress(MessageListActivity.this);
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(MessageListActivity.this), 1);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        });

    }

    private void sendMessage(int type) {

        switch (type) {
            case 1:

                break;

            case 2:
                break;

            case 3:
                break;

            case 4:
                break;
        }
    }

    private void sendMessage(Message message) {
        String key = ref.push().getKey();
        if (user != null) {
            ArrayMap<String, Object> map = new ArrayMap<>();
            map.put("body", message.getBody());
            map.put("isDelivered", true);
            map.put("isRead", false);
            map.put("senderId", user.getId() + "");
            map.put("senderImage", user.getAvatar());
            map.put("senderName", user.getName());
            map.put("time", formatter.format(new Date()));
            map.put("type", message.getType());
            map.put("id", key);
            if (key != null) {
                ref.child(key).setValue(map);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (valueEventListner != null) {
            ref.removeEventListener(valueEventListner);
        }
        finish();
        return;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);
                StringBuilder stBuilder = new StringBuilder();
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);

                Message message = new Message();
                message.setBody(latitude + "," + longitude);
                message.setType(2);
                sendMessage(message);
                NetworkConsume.getInstance().HideProgress();
            }
        }

        if (requestCode == 0){
            if (resultCode == RESULT_OK) {

                String url = Environment.getExternalStorageDirectory().getPath() + "/recorded_audio1.wav";
                Uri imageUri = Uri.fromFile(new File(url));
                String key = ref.push().getKey() + imageUri.getLastPathSegment();
                StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Orders").child(orderId  + "").child("audios").child(key);
                UploadTask uploadTask = filePath.putFile(imageUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            if (downloadUri != null) {
                                String url = downloadUri.toString();
                                Message message = new Message();
                                message.setType(4);
                                message.setBody(url);
                                sendMessage(message);
                            }
                        }
                    }
                });

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
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                for (File imageFile : imageFiles) {
                    Uri imageUri = Uri.fromFile(imageFiles.get(0));
                    String key = ref.push().getKey() + imageUri.getLastPathSegment();
                    StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Orders").child(orderId  + "").child("images").child(key);
                    UploadTask uploadTask = filePath.putFile(imageUri);
                    Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                if (downloadUri != null) {
                                    String url = downloadUri.toString();
                                    Message message = new Message();
                                    message.setType(3);
                                    message.setBody(url);
                                    sendMessage(message);
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MessageListActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }


}