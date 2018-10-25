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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akhdmny.driver.Adapter.ImageAdapterCart;
import com.akhdmny.driver.Adapter.MyCartAdapter;
import com.akhdmny.driver.ApiResponse.BidResp.SubmitBidResp;
import com.akhdmny.driver.ApiResponse.OrdersResponse.CartItem;
import com.akhdmny.driver.ApiResponse.OrdersResponse.GetOrderItemsResp;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.Fragments.FargmentService;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceptOrderActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.total_amount)
    TextView total_amount;

    @BindView(R.id.OrderBidScreen)
    LinearLayout OrderBidScreen;
    @BindView(R.id.et_bid)
    EditText et_bid;
    @BindView(R.id.CancelOrder)
    Button CancelOrder;
    @BindView(R.id.confirmOrder)
    Button confirmOrder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView tvTitle;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    private Handler mHandler;
    private Runnable mRunnable;
    private ArrayList<String> photos = new ArrayList<>();
    ArrayList<CartItem> list = new ArrayList<>();
    SharedPreferences prefs;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_items);
        ButterKnife.bind(this);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        tvTitle.setText(getString(R.string.order_req));
        getOrdersApi();
        itemTouchLister();
    }

    private void getOrdersApi(){
        try {


        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        String orderID = NetworkConsume.getInstance().getDefaults("orderId",AcceptOrderActivity.this);


        NetworkConsume.getInstance().getAuthAPI().getOrderItems(Integer.valueOf(orderID)).enqueue(new Callback<GetOrderItemsResp>() {
            @Override
            public void onResponse(Call<GetOrderItemsResp> call, Response<GetOrderItemsResp> response) {
                    if (response.isSuccessful()){
                        GetOrderItemsResp getOrderItemsResp = response.body();
                        for (int i = 0; i<getOrderItemsResp.getResponse().getOrderDetails().getCartItems().size(); i++){
                            if (getOrderItemsResp.getResponse().getOrderDetails().getCartItems().size() == 0)
                            {

                            }else {
                                list.add(getOrderItemsResp.getResponse().getOrderDetails().getCartItems().get(i));
                                MyCartAdapter myAdapter = new MyCartAdapter(AcceptOrderActivity.this,list);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(myAdapter);

                                total_amount.setText(new DecimalFormat("##").format(getOrderItemsResp.getResponse().getOrderDetails().getAmount()));
                            }

                        }
                    }else {
                        Gson gson = new Gson();
                        LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                        Toast.makeText(AcceptOrderActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onFailure(Call<GetOrderItemsResp> call, Throwable t) {
                Toast.makeText(AcceptOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        }catch (Exception e){}
    }

    private void bidSubmitApi(){
        String orderID = NetworkConsume.getInstance().getDefaults("orderId",AcceptOrderActivity.this);
        NetworkConsume.getInstance().getAuthAPI().SubmitBid(Integer.parseInt(orderID), Integer.parseInt(et_bid.getText().toString())).enqueue(new Callback<SubmitBidResp>() {
            @Override
            public void onResponse(Call<SubmitBidResp> call, Response<SubmitBidResp> response) {
                if (response.isSuccessful()){
                    NetworkConsume.getInstance().SnackBarSucccess(OrderBidScreen,AcceptOrderActivity.this,R.string.successBid);
                    startActivity(new Intent(AcceptOrderActivity.this,MainActivity.class));
                    finish();
                }else {
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(AcceptOrderActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubmitBidResp> call, Throwable t) {

            }
        });
    }
    private void itemTouchLister() {

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_bid.getText().toString().equals("")){
                    Toast.makeText(AcceptOrderActivity.this, "Please enter the Bid", Toast.LENGTH_SHORT).show();
                }else {
                    bidSubmitApi();
                }
            }
        });

        recyclerView.addOnItemTouchListener(new CategoryDetailActivity.RecyclerTouchListener(this, recyclerView,
                new FargmentService.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        final AlertDialog.Builder ADD_Cart = new AlertDialog.Builder(AcceptOrderActivity.this);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View viewCart = inflater.inflate(R.layout.remove_cart, null);
                        ImageView imageView = viewCart.findViewById(R.id.img_Resturaunt);
                        ImageView PlayAudio = viewCart.findViewById(R.id.PlayAudio);
                        seekBar = viewCart.findViewById(R.id.seek_bar);
                        TextView textViewTitle = viewCart.findViewById(R.id.Tv_title);
                        RecyclerView recyclerViewPopup = viewCart.findViewById(R.id.recycler_view);
                        recyclerViewPopup.setLayoutManager(new LinearLayoutManager(AcceptOrderActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerViewPopup.setHasFixedSize(true);
//                        for (int i =0; i<list.get(position).getImage().size()-1;i++) {
                            photos.add(list.get(position).getImage());

                        //}
                        ImageAdapterCart imagesAdapter = new ImageAdapterCart(AcceptOrderActivity.this, photos);

                        recyclerViewPopup.setAdapter(imagesAdapter);
                        Picasso.get().load(list.get(position).getImage()).error(R.drawable.place_holder).into(imageView);
                        PlayAudio.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (list.get(position).getVoice() == null){
                                    Toast.makeText(AcceptOrderActivity.this, "No Voice Found", Toast.LENGTH_SHORT).show();
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
                              //  RemoveCartOrderApi(list.get(position).getId());
                                alertDialog.dismiss();
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


    @Override
    public void onCompletion(MediaPlayer mp) {
            stopPlaying();
    }
}
