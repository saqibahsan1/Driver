package com.akhdmny.driver.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;

import com.akhdmny.driver.Utils.Show_Chat_Conversation_Data_Items;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class Chat extends AppCompatActivity {
    private static final String TAG = "saqib test" ;

    Button sendButton;
    EditText messageArea;
    SimpleDateFormat sdf;
    Uri imageUri ;
    public static final int READ_EXTERNAL_STORAGE = 0,MULTIPLE_PERMISSIONS = 10,REQUEST_WRITE_PERMISSION = 21;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
Uri mImageUri = Uri.EMPTY;
    String id ="",userId = "";
    String[] permissions= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    public LinearLayoutManager mLinearLayoutManager;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.no_chat_text)
    TextView no_chat;

    @BindView(R.id.no_data_available_image)
    ImageView no_data_available_image;

    @BindView(R.id.addImages)
    ImageView addImages;
    private FirebaseRecyclerAdapter<Show_Chat_Conversation_Data_Items, Chat_Conversation_ViewHolder> mFirebaseAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef,myRef2;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        id = String.valueOf(preferences.getInt("id",0));
        userId = NetworkConsume.getInstance().getDefaults("userId",Chat.this);
//        setSupportActionBar(toolbar);
//        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // tvTitle.setText("Chat");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + getString(R.string.chat) + "</font>"));
        }
        sdf = new SimpleDateFormat("EEE, MMM d 'AT' HH:mm a");

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(id).child(userId);
        myRef.keepSynced(true);

        myRef2 = FirebaseDatabase.getInstance().getReference().child("Chat").
                child(userId).child(id);
        myRef2.keepSynced(true);

//        layout = (LinearLayout) findViewById(R.id.layout1);
//        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                sendButton.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(Chat.this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mLinearLayoutManager.setStackFromEnd(true);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    String currentDateandTime = sdf.format(new Date());
                    ArrayMap<String, String> map = new ArrayMap<>();
//                    map.put("message", messageText);
                    map.put("sender", id);
                    map.put("body", messageText);
                    map.put("id", userId);
                    map.put("isDelivered", "true");
                    map.put("isRead", "true");
                    map.put("senderId", id);
                    map.put("senderImage", "true");
                    map.put("senderName", "id");
                    map.put("time", currentDateandTime);
                    map.put("type", "1");
                    myRef.push().setValue(map);
                    myRef2.push().setValue(map);
                    messageArea.setText("");
                    sendButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightA));
                    sendButton.setTextColor(getResources().getColor(R.color.counter_text_bg));
                    recyclerView.postDelayed(new Runnable() {
                        @Override public void run()
                        {
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);

                        }
                    }, 500);
                }
            }
        });
        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(Chat.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_PERMISSION);
            }
        });

    }

    private void onPickPhoto() {
        EasyImage.openChooserWithGallery(this,"Pick source",0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onPickPhoto();
                return;

            case REQUEST_WRITE_PERMISSION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    onPickPhoto();
                }
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("LOGGED", "InSIDE onActivityResult : ");
        Log.d("LOGGED", " requestCode : " + requestCode+" resultCode : " + resultCode+" DATA "+data);

            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    //Some error handling
                    e.printStackTrace();
                }

                @Override
                public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
//                    onPhotosReturned(imageFiles);
                    File img = imageFiles.get(imageFiles.size()-1);
                    imageUri = Uri.fromFile(img);
                    StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Chat_Images").child(imageUri.getLastPathSegment());
                     Log.d("LOGGED", "ImageURI : " +mImageUri);

                    NetworkConsume.getInstance().ShowProgress(Chat.this);

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 Uri downloadUri = taskSnapshot.getUploadSessionUri();
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    String currentDateandTime = sdf.format(new Date());
                    Uri downloadUrl = urlTask.getResult();
                    ArrayMap<String, String> map = new ArrayMap<>();
                    map.put("message", downloadUrl.toString());
                    map.put("sender", id);
                    map.put("body", downloadUrl.toString());
                    map.put("id", userId);
                    map.put("isDelivered", "true");
                    map.put("isRead", "true");
                    map.put("senderId", id);
                    map.put("senderImage", "true");
                    map.put("senderName", "id");
                    map.put("time", currentDateandTime);
                    map.put("type", "1");
                    myRef.push().setValue(map);
                    myRef2.push().setValue(map);
                    NetworkConsume.getInstance().HideProgress();
                }
            });

                }

                @Override
                public void onCanceled(EasyImage.ImageSource source, int type) {
                    //Cancel handling, you might wanna remove taken photo if it was canceled
                    if (source == EasyImage.ImageSource.CAMERA) {
                        File photoFile = EasyImage.lastlyTakenButCanceledPhoto(Chat.this);
                        if (photoFile != null) photoFile.delete();
                    }
                }
            });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Log.d("LOGGED", "On Start : " );
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Show_Chat_Conversation_Data_Items,
                Chat_Conversation_ViewHolder>(Show_Chat_Conversation_Data_Items.class,
                R.layout.show_chat_conversation_single_item, Chat_Conversation_ViewHolder.class, myRef) {

            @Override
            public Chat_Conversation_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return super.onCreateViewHolder(parent, viewType);
            }

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }

            public void populateViewHolder(final Chat_Conversation_ViewHolder viewHolder, Show_Chat_Conversation_Data_Items model, final int position) {

                viewHolder.getSender(model.getSender(), id, userId,model.getTime(),Chat.this);
                viewHolder.getMessage(model.getBody(),model.getTime(), userId);
                //Log.d("LOGGED", "Sender : " + model.getSender());
                //Log.d("LOGGED", "Message : " + model.getBody());




                viewHolder.mView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {

                        final DatabaseReference ref = mFirebaseAdapter.getRef(position);
                        ref.keepSynced(true);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                String retrieve_image_url = dataSnapshot.child("body").getValue(String.class);
                                if(retrieve_image_url.startsWith("https"))
                                {
                                    //Toast.makeText(ChatConversationActivity.this, "URL : " + retrieve_image_url, Toast.LENGTH_SHORT).show();
                                    Intent intent = (new Intent(Chat.this,EnlargeImageView.class));
                                    intent.putExtra("url",retrieve_image_url);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };
        Log.d("LOGGED", "Set Layout : " );
        recyclerView.setAdapter(mFirebaseAdapter);





        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren())
                {
                    //Log.d("LOGGED", "Data SnapShot : " +dataSnapshot.toString());
                   // progressBar.setVisibility(ProgressBar.GONE);
                    NetworkConsume.getInstance().HideProgress();
                    recyclerView.setVisibility(View.VISIBLE);
                    no_data_available_image.setVisibility(View.GONE);
                    no_chat.setVisibility(View.GONE);
                    recyclerView.postDelayed(new Runnable() {
                        @Override public void run()
                        {
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                        }
                    }, 500);
                    recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v,
                                                   int left, int top, int right, int bottom,
                                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            if (bottom < oldBottom) {
                                recyclerView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                    }
                                }, 100);
                            }
                        }
                    });
                }
                else {
                    //Log.d("LOGGED", "NO Data SnapShot : " +dataSnapshot.toString());
                    NetworkConsume.getInstance().HideProgress();
                    recyclerView.setVisibility(View.VISIBLE);
                    no_data_available_image.setVisibility(View.GONE);
                    no_chat.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermission();
//            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]
                {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, MULTIPLE_PERMISSIONS);

    }

    public static class Chat_Conversation_ViewHolder extends RecyclerView.ViewHolder {
        private final TextView message, sender,timeChat;
        private final ImageView chat_image_incoming,chat_image_outgoing;
        private final CircleImageView leftImage,RightImage;
        View mView;
        final LinearLayout.LayoutParams params,text_params;
        LinearLayout layout;




        public Chat_Conversation_ViewHolder(final View itemView) {
            super(itemView);
            //Log.d("LOGGED", "ON Chat_Conversation_ViewHolder : " );
            mView = itemView;
            message = (TextView) mView.findViewById(R.id.fetch_chat_messgae);
            sender = (TextView) mView.findViewById(R.id.fetch_chat_sender);
            timeChat = (TextView) mView.findViewById(R.id.timeChat);
            leftImage =  mView.findViewById(R.id.msg_image_avatar_left);
            RightImage =  mView.findViewById(R.id.msg_image_avatar_right);

            chat_image_incoming = (ImageView) mView.findViewById(R.id.chat_uploaded_image_incoming);
            chat_image_outgoing = (ImageView) mView.findViewById(R.id.chat_uploaded_image_outgoing);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            text_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout = (LinearLayout) mView.findViewById(R.id.chat_linear_layout);
        }



        private void getSender(String title, String id,String DriverId,String time, Context context) {

            try {


            if(title.equals(id))
            {
                //Log.d("LOGGED", "getSender: ");
                params.setMargins((MainActivity.Device_Width/3),5,10,10);
                text_params.setMargins(15,2,0,5);
                sender.setLayoutParams(text_params);
                mView.setLayoutParams(params);
//                mView.setBackgroundResource(R.drawable.text_in);
                sender.setText("YOU");
//                userImage.setLayoutParams(params);
                timeChat.setText(time);
                chat_image_outgoing.setVisibility(View.VISIBLE);
                chat_image_incoming.setVisibility(View.GONE);
                RightImage.setVisibility(View.VISIBLE);
                leftImage.setVisibility(View.GONE);

            }
            else
            {
                params.setMargins(10,0,(MainActivity.Device_Width/3),10);
                sender.setGravity(Gravity.START);
                text_params.setMargins(40,0,0,5);
                sender.setVisibility(View.GONE);
                mView.setLayoutParams(params);
                sender.setTextColor(context.getResources().getColor(R.color.black));
                timeChat.setTextColor(context.getResources().getColor(R.color.black));
                message.setTextColor(context.getResources().getColor(R.color.black));

                layout.setBackgroundResource(R.drawable.left_bubble);
//                userImage.setLayoutParams(params);
                RightImage.setVisibility(View.GONE);
                leftImage.setVisibility(View.VISIBLE);

                sender.setText(DriverId);
                timeChat.setText(time);
                chat_image_incoming.setScaleType(ImageView.ScaleType.FIT_XY);
                chat_image_outgoing.setVisibility(View.GONE);
                chat_image_incoming.setVisibility(View.VISIBLE);

            }
            }catch (Exception e){}
        }

        private void getMessage(String title,String time,String id) {
            try {



            if(!title.startsWith("https"))
            {

                if(!sender.getText().equals(id))
                {
                    text_params.setMargins(15,2,22,15);
                    message.setTextColor(Color.parseColor("#FFFFFF"));
                }
                else
                {
                    text_params.setMargins(15,2,22,15);
                    message.setTextColor(Color.parseColor("#000000"));
                }

                message.setLayoutParams(text_params);
                sender.setLayoutParams(text_params);

                message.setText(title);

                message.setVisibility(View.VISIBLE);
                timeChat.setText(time);
                chat_image_incoming.setVisibility(View.GONE);
                chat_image_outgoing.setVisibility(View.GONE);
            }
            else
            {
                if (chat_image_outgoing.getVisibility()==View.VISIBLE && chat_image_incoming.getVisibility()==View.GONE)
                {
                    chat_image_outgoing.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    Glide.with(itemView.getContext())
                            .load(title)
                            .crossFade()
                            .fitCenter()
                            .placeholder(R.drawable.place_holder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(chat_image_outgoing);
                }
                else
                {
                    chat_image_incoming.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    Glide.with(itemView.getContext())
                            .load(title)
                            .crossFade()
                            .fitCenter()
                            .placeholder(R.drawable.place_holder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(chat_image_incoming);
                }
            }

            }catch (Exception e){

            }
        }

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
