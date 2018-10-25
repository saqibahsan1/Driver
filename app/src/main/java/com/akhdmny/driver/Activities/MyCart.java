package com.akhdmny.driver.Activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akhdmny.driver.Adapter.ImageAdapterCart;
import com.akhdmny.driver.Adapter.MyCartAdapter;
import com.akhdmny.driver.ApiResponse.CartInsideResponse;
import com.akhdmny.driver.ApiResponse.CartOrder;
import com.akhdmny.driver.ApiResponse.Cartitem;
import com.akhdmny.driver.ApiResponse.OrderConfirmation;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.Fragments.FargmentService;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.akhdmny.driver.Utils.GPSActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCart extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    ArrayList<Cartitem> list;
    ArrayList<CartInsideResponse> listResponse;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.MyCartLayout)
    LinearLayout MyCartLayout;
    @BindView(R.id.cart_sendBtn)
    Button cart_sendBtn;
    SharedPreferences prefs;
    AlertDialog alertDialog;
    GPSActivity gpsActivity;
    SpotsDialog dialog;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView tvTitle;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    private Handler mHandler;
    private Runnable mRunnable;
    private ArrayList<String> photos = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_cart);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText("Cart");
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cart");
        gpsActivity = new GPSActivity(this);
        list = new ArrayList<>();
        listResponse = new ArrayList<>();
        CartApi();
        itemTouchLister();
    }

    private void CartApi(){
        dialog = new SpotsDialog(this,"Please wait...");
        dialog.show();
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().CartOrders().
                enqueue(new Callback<CartOrder>() {
                    @Override
                    public void onResponse(Call<CartOrder> call, Response<CartOrder> response) {
                        if (response.isSuccessful()) {
                            CartOrder cartOrder = response.body();
                            for (int i=0; i< cartOrder.getResponse().getCartitems().size(); i++){
                                if (cartOrder.getResponse().getCartitems().size() == 0)
                                {

                                }else {
                                    list.add(cartOrder.getResponse().getCartitems().get(i));
                                    listResponse.add(cartOrder.getResponse());
                                }
                            }
//                            MyCartAdapter myAdapter = new MyCartAdapter(MyCart.this,list);
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                            recyclerView.setLayoutManager(mLayoutManager);
//                            recyclerView.setItemAnimator(new DefaultItemAnimator());
//                            recyclerView.setAdapter(myAdapter);
                            dialog.hide();

                        }else {
                            dialog.hide();
                            Gson gson = new Gson();
                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                            Toast.makeText(MyCart.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CartOrder> call, Throwable t) {
                        dialog.hide();
                        Toast.makeText(MyCart.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        }
    private void itemTouchLister() {
        cart_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder order_popup = new AlertDialog.Builder(MyCart.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewOrder = inflater.inflate(R.layout.confirm_order, null);
                Button btnDone = viewOrder.findViewById(R.id.OrderDone);
                Button btnCancel = viewOrder.findViewById(R.id.CancelOrder);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                    }
                });
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    OrderConfirmationApi();
                    }
                });

                order_popup.setView(viewOrder);
                order_popup.setCancelable(true);
                alertDialog = order_popup.create();
                alertDialog.show();
            }
        });

        recyclerView.addOnItemTouchListener(new CategoryDetailActivity.RecyclerTouchListener(this, recyclerView,
                new FargmentService.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final AlertDialog.Builder ADD_Cart = new AlertDialog.Builder(MyCart.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewCart = inflater.inflate(R.layout.remove_cart, null);
                ImageView imageView = viewCart.findViewById(R.id.img_Resturaunt);
                ImageView PlayAudio = viewCart.findViewById(R.id.PlayAudio);
                seekBar = viewCart.findViewById(R.id.seek_bar);
                TextView textViewTitle = viewCart.findViewById(R.id.Tv_title);
                RecyclerView recyclerViewPopup = viewCart.findViewById(R.id.recycler_view);
                recyclerViewPopup.setLayoutManager(new LinearLayoutManager(MyCart.this, LinearLayoutManager.HORIZONTAL, false));
                recyclerViewPopup.setHasFixedSize(true);
                for (int i =0; i<list.get(position).getImages().size()-1;i++)
                {
                    photos.add(list.get(i).getImages().get(i));

                }
                ImageAdapterCart imagesAdapter = new ImageAdapterCart(MyCart.this, photos);

                recyclerViewPopup.setAdapter(imagesAdapter);
                Picasso.get().load(list.get(position).getServiceImage()).into(imageView);
                PlayAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.get(position).getVoice() == null){
                            Toast.makeText(MyCart.this, "No Voice Found", Toast.LENGTH_SHORT).show();
                        }else {
                            if(isPlaying()){
                                stopPlaying();
                            } else {
                                startPlaying(list.get(position).getVoice());
                            }
                        }
                    }
                });

                Button btnRemove = viewCart.findViewById(R.id.buttonRemoveItem);
                TextView textDialogMsg = viewCart.findViewById(R.id.textDialog);
                TextView textViewAddress = viewCart.findViewById(R.id.TV_Address);
                TextView textViewprice = viewCart.findViewById(R.id.textView_Price);
                textViewTitle.setText(list.get(position).getTitle());
                textViewAddress.setText(list.get(position).getAddress());
                textViewprice.setText(list.get(position).getAmount().toString());
                textDialogMsg.setText(list.get(position).getDescription());
                ADD_Cart.setView(viewCart);
                ADD_Cart.setCancelable(true);
                alertDialog = ADD_Cart.create();
                alertDialog.show();

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RemoveCartOrderApi(list.get(position).getId());
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
    private boolean isPlaying(){
        try
        {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (Exception e){
            return false;
        }
    }
    private void startPlaying(String source){
        try {
            seekBar.setVisibility(View.VISIBLE);
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnCompletionListener(this);

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
                seekBar.setProgress(0);
                seekBar.setVisibility(View.GONE);

                if(mHandler!=null){
                    mHandler.removeCallbacks(mRunnable);
                }
            } catch (Exception e){ }
        }

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

    private void OrderConfirmationApi(){
        dialog = new SpotsDialog(this,"Confirming your order please wait...");
        dialog.show();
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().OrderRequest(gpsActivity.getLatitude(),gpsActivity.getLongitude())
                .enqueue(new Callback<OrderConfirmation>() {
                    @Override
                    public void onResponse(Call<OrderConfirmation> call, Response<OrderConfirmation> response) {
                        if (response.isSuccessful()){
                            OrderConfirmation orderConfirmation = response.body();
                            dialog.hide();

                            Gson gson = new Gson();
                            String json = gson.toJson(orderConfirmation);
                            NetworkConsume.getInstance().setDefaults("myObject",json,MyCart.this);

                            Intent intent = new Intent(MyCart.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }else {
                            dialog.hide();
                            Gson gson = new Gson();
                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                            Toast.makeText(MyCart.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderConfirmation> call, Throwable t) {

                    }
                });
    }
    private void RemoveCartOrderApi(int id){
        dialog = new SpotsDialog(this,"Removing item please wait...",R.style.AppTheme);
        dialog.show();
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().RemoveCartOrders(id,gpsActivity.getLatitude(),gpsActivity.getLongitude()).
                enqueue(new Callback<CartOrder>() {
                    @Override
                    public void onResponse(Call<CartOrder> call, Response<CartOrder> response) {
                        if (response.isSuccessful()) {
                            listResponse = new ArrayList<>();
                            list = new ArrayList<>();
                            alertDialog.hide();
                            CartOrder cartOrder = response.body();
                            for (int i=0; i< cartOrder.getResponse().getCartitems().size(); i++){
                                if (cartOrder.getResponse().getCartitems().size() == 0)
                                {

                                }else {
                                    list.add(cartOrder.getResponse().getCartitems().get(i));
                                    listResponse.add(cartOrder.getResponse());
                                }
                            }
//                            MyCartAdapter myAdapter = new MyCartAdapter(MyCart.this,list);
//                            dialog.hide();
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                            recyclerView.setLayoutManager(mLayoutManager);
//                            recyclerView.setItemAnimator(new DefaultItemAnimator());
//                            recyclerView.setAdapter(myAdapter);
//                            myAdapter.notifyDataSetChanged();
//                            NetworkConsume.getInstance().SnackBarSucccess(MyCartLayout,MyCart.this,R.string.remove_item);

                        }else {
                            dialog.hide();
                            Gson gson = new Gson();
                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                            Toast.makeText(MyCart.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CartOrder> call, Throwable t) {
                        dialog.hide();
                        Toast.makeText(MyCart.this, t.getMessage(), Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlaying();
    }
}
