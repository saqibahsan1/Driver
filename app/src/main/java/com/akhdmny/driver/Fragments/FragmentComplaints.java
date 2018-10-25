package com.akhdmny.driver.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akhdmny.driver.Adapter.HistoryAdapter;
import com.akhdmny.driver.Adapter.ImagesAdapter;
import com.akhdmny.driver.ApiResponse.AddComplaintResponse;
import com.akhdmny.driver.ApiResponse.ComplaintHistoryInsideResponse;
import com.akhdmny.driver.ApiResponse.ComplaintHistoryResponse;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Utils.Validator;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.google.gson.Gson;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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

public class FragmentComplaints extends Fragment implements MediaPlayer.OnCompletionListener {
    public static final int RequestPermissionCode = 0;
    private static final String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/recorded_audio.wav";

    MediaPlayer mediaPlayer;

    @BindView(R.id.recordAudio)
    ImageView recordAudio;

    @BindView(R.id.PlayAudio)
    ImageView PlayAudio;

    @BindView(R.id.getCapturedImage)
    ImageView getCapturedImage;

    @BindView(R.id.et_textMessage)
    EditText et_textMessage;

    @BindView(R.id.et_title)
    EditText et_title;

    @BindView(R.id.ComplaintLayout)
    LinearLayout ComplaintLayout;

    @BindView(R.id.historyLayout)
    LinearLayout historyLayout;

    @BindView(R.id.addImages)
    ImageView addImages;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.RV_historyList)
    RecyclerView RV_historyList;

    @BindView(R.id.toggleButtons)
    ToggleSwitch toogleButtons;

    @BindView(R.id.btn_addComplaint)
    Button btn_addComplaint;

    @BindView(R.id.seek_bar)
    SeekBar seek_bar;

    SeekBar seek_barHistory;


    private boolean trigger = false;
    AlertDialog levelDialog;
    private ImagesAdapter imagesAdapter;
    private ArrayList<File> photos = new ArrayList<>();
    SharedPreferences prefs;
    SpotsDialog dialog;
    ArrayList<ComplaintHistoryInsideResponse> list;
    android.app.AlertDialog alertDialog;
    View historyItemView;
    ImageView Audio;
    private Handler mHandler;
    private Runnable mRunnable;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.complaint_fragment, container, false);
        ButterKnife.bind(this,view);
        ClickEvents();
        return view;
    }
    private void ClickEvents(){
        mHandler = new Handler();
        dialog = new SpotsDialog(getActivity(),"Registering Your Complain");

        prefs = getActivity().getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);

        toogleButtons.setCheckedPosition(0);
        toogleButtons.setOnChangeListener(new ToggleSwitch.OnChangeListener() {
            @Override
            public void onToggleSwitchChanged(int i) {
                if (i==0){
                    historyLayout.setVisibility(View.GONE);
                    ComplaintLayout.setVisibility(View.VISIBLE);
                }else {
                    list = new ArrayList<>();
                    historyLayout.setVisibility(View.VISIBLE);
                    ComplaintLayout.setVisibility(View.GONE);
                    apiHistory();


                }
            }
        });
        RV_historyList.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), RV_historyList, new FargmentService.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final android.app.AlertDialog.Builder history = new android.app.AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                historyItemView = inflater.inflate(R.layout.history_popup, null);
                TextView textViewTitle = historyItemView.findViewById(R.id.tv_title);
                seek_barHistory = historyItemView.findViewById(R.id.seek_bar);
                ProgressBar progressBar = historyItemView.findViewById(R.id.progress);
                progressBar.setVisibility(View.VISIBLE);
                TextView msg = historyItemView.findViewById(R.id.Tv_msg);
                textViewTitle.setText(list.get(position).getTitle());
                ImageView imageView = historyItemView.findViewById(R.id.HistoryImage);
                Audio = historyItemView.findViewById(R.id.PlayAudio);
                seek_barHistory.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                Audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.get(position).getVoice() != null) {
                            if (isPlaying()) {
                                stopPlaying();
                            } else {
                                startPlaying(list.get(position).getVoice());
                            }
                        }else {
                            Toast.makeText(getActivity(), "No voice found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                msg.setText(list.get(position).getDescription());
                if (list.get(position).getImage() != null) {
                    Picasso.get().load(list.get(position).getImage()).into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
                history.setView(historyItemView);
                history.setCancelable(true);
                alertDialog = history.create();
                alertDialog.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recycler_view.setHasFixedSize(true);
        recordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()) {
                        trigger = true;

                    AndroidAudioRecorder.with(getActivity())
                            // Required
                            .setFilePath(AUDIO_FILE_PATH)
                            .setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
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
        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto();
            }
        });
        btn_addComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataTOApi();
            }
        });
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
    private void bottomSheet(){
        BottomSheetMenuDialog dialog = new BottomSheetBuilder(getActivity(), R.style.AppTheme_BottomSheetDialog)
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
                            seek_bar.setProgress(0);
                            seek_bar.setVisibility(View.GONE);
                        }
                        if (item.getTitle().equals("Record")){
                            if(checkPermission()) {
                                trigger = true;

                                AndroidAudioRecorder.with(getActivity())
                                        // Required
                                        .setFilePath(AUDIO_FILE_PATH)
                                        .setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
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
        seek_bar.setMax(mediaPlayer.getDuration());
        seek_barHistory.setMax(mediaPlayer.getDuration());

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seek_bar.setProgress(mediaPlayer.getCurrentPosition());
                    seek_barHistory.setProgress(mediaPlayer.getCurrentPosition());
                }
                mHandler.postDelayed(mRunnable,50);
            }
        };
        mHandler.postDelayed(mRunnable,50);
    }
    private void apiHistory(){
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().History().enqueue(new Callback<ComplaintHistoryResponse>() {
            @Override
            public void onResponse(Call<ComplaintHistoryResponse> call, Response<ComplaintHistoryResponse> response) {
                if (response.isSuccessful()) {
                    RV_historyList.setHasFixedSize(true);
                    RV_historyList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    ComplaintHistoryResponse complaintHistoryResponse = response.body();
                    if (complaintHistoryResponse.getResponse().size() != 0) {
                        for (int i = 0; i < complaintHistoryResponse.getResponse().size(); i++) {
                            list.add(complaintHistoryResponse.getResponse().get(i));
                        }

                        HistoryAdapter myAdapter = new HistoryAdapter(getActivity(), list);
                        RV_historyList.setAdapter(myAdapter);
                    }else {
                        try {
                            NetworkConsume.getInstance().SnackBarErrorHistory(historyLayout,getActivity(),"No History was found");
                        }catch (Exception e){

                        }
                    }


                }else {

                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(getActivity(), message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ComplaintHistoryResponse> call, Throwable t) {
               // dialog.hide();
            }
        });
    }
    private void onPickPhoto() {
        EasyImage.openChooserWithGallery(FragmentComplaints.this,"Pick source",0);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (trigger){
            recordAudio.setVisibility(View.GONE);
            PlayAudio.setVisibility(View.VISIBLE);
            trigger = false;
            Toast.makeText(getActivity(), "Audio recorded successfully!", Toast.LENGTH_SHORT).show();


        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }
    private void onPhotosReturned(List<File> returnedPhotos) {
        photos.addAll(returnedPhotos);
        //imagesAdapter.notifyDataSetChanged();
        getCapturedImage.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(photos.get(photos.size()-1))
                .fit()
                .into(getCapturedImage);
        imagesAdapter = new ImagesAdapter(getActivity(), photos);

        recycler_view.setAdapter(imagesAdapter);
       // recycler_view.scrollToPosition(photos.size() - 1);
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
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
                        Toast.makeText(getActivity(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    private void startPlaying(String source){
        try {
            seek_bar.setVisibility(View.VISIBLE);
            seek_barHistory.setVisibility(View.VISIBLE);
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnCompletionListener(FragmentComplaints.this);

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

        if(mediaPlayer != null){
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                seek_bar.setProgress(0);
                seek_barHistory.setProgress(0);
                seek_bar.setVisibility(View.GONE);
                seek_barHistory.setVisibility(View.GONE);
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
    private void addDataTOApi(){
        dialog.show();
        File AudioFile = new File(AUDIO_FILE_PATH);
        RequestBody propertyImage = RequestBody.create(MediaType.parse("audio/*"), AudioFile);
        MultipartBody.Part voicePart = MultipartBody.Part.createFormData("voice", AudioFile.getName(), propertyImage);

        MultipartBody.Part[] ImagesParts = new MultipartBody.Part[photos.size()];
        for (int i = 0; i<photos.size();i++) {
            File addImageFile = new File(photos.get(i).getPath());
            RequestBody ImageBody = RequestBody.create(MediaType.parse("image/*"), addImageFile);
            ImagesParts[i] = MultipartBody.Part.createFormData("image",addImageFile.getName(),ImageBody);
        }
            Validator validator = new Validator(getActivity(), true);

            validator
                    .setRules(Validator.Rules.REQUIRED, Validator.Rules.MIN)
                    .validate(et_title.getText().toString(), et_title, 3)
                    .validate(et_textMessage.getText().toString(), et_textMessage, 3);

            if (validator.fails()) {
                dialog.dismiss();
                return;
            }

            NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"),et_title.getText().toString());
        RequestBody Description = RequestBody.create(MediaType.parse("text/plain"),et_textMessage.getText().toString());
        NetworkConsume.getInstance().getAuthAPI().AddComplaint(Description,title,ImagesParts,voicePart)
                .enqueue(new Callback<AddComplaintResponse>() {
                @Override
                public void onResponse(Call<AddComplaintResponse> call, Response<AddComplaintResponse> response) {
                    if (response.isSuccessful()) {
                        AddComplaintResponse categoriesResponse = response.body();
                        photos = new ArrayList<>();
                        photos.clear();
                        imagesAdapter = new ImagesAdapter(getActivity(), photos);
                        recycler_view.setAdapter(imagesAdapter);
                        et_title.setText("");
                        et_textMessage.setText("");
                        et_title.setHint(R.string.title);
                        Picasso.get()
                                .load(R.drawable.image_caputure)
                                .fit()
                                .into(addImages);
                        et_textMessage.setHint(R.string.Message);
                        recordAudio.setVisibility(View.VISIBLE);
                        PlayAudio.setVisibility(View.GONE);
                    try {
                        NetworkConsume.getInstance().SnackBarSucccessComplaint(ComplaintLayout,getActivity(),R.string.success);
                        getCapturedImage.setVisibility(View.GONE);

                    }catch (Exception e){

                    }
                        dialog.dismiss();
                    }else {

                        Gson gson = new Gson();
                        LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                        NetworkConsume.getInstance().SnackBarErrorHistory(ComplaintLayout,getActivity(),message.getError().getMessage().get(0));

                       // Toast.makeText(getActivity(), message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                }

                @Override
                public void onFailure(Call<AddComplaintResponse> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FargmentService.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FargmentService.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }
}
