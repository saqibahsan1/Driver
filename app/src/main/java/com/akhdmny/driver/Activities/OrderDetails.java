package com.akhdmny.driver.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.akhdmny.driver.ApiResponse.MyOrderDetails.Order;
import com.akhdmny.driver.ApiResponse.MyOrderDetails.OrderItem;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class OrderDetails extends AppCompatActivity {

    @BindView(R.id.titleBar)
    TextView title;

    @BindView(R.id.statusColorImg)
    CircleImageView statusColorImg;

    @BindView(R.id.order_type)
    TextView order_type;

    @BindView(R.id.category)
    TextView category;

    @BindView(R.id.total_distance)
    TextView total_distance;

    @BindView(R.id.ratingTxt)
    TextView ratingTxt;

    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.tip)
    TextView tip;
    @BindView(R.id.total_pr)
    TextView total_pr;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    TextView tvTitle;

    @BindView(R.id.rating_bar)
    com.fuzzproductions.ratingbar.RatingBar rating_bar;

    SharedPreferences prefs;
    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);
        ButterKnife.bind(this);
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        setSupportActionBar(toolbar);
        tvTitle = toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Gson gson = new Gson();
        category.setText("Restaurant");
        String order = NetworkConsume.getInstance().getDefaults("order",OrderDetails.this);
        Order orderJson = gson.fromJson(order,Order.class);
        ratingTxt.setText("Not Bad");

//        String order_details = NetworkConsume.getInstance().getDefaults("order_details",OrderDetails.this);
//        OrderItem orderDetailsJson = gson.fromJson(order_details,OrderItem.class);
        price.setText(new DecimalFormat("##").format(orderJson.getAmount()));
        total_pr.setText(new DecimalFormat("##").format(orderJson.getAmount()));
        if (orderJson.getTip() != null) {
            tip.setText(new DecimalFormat("##").format(orderJson.getTip()));
        }
        total_distance.setText("2.23 Km");
        order_type.setText(orderJson.getType());
        tvTitle.setText("Order Number: " + orderJson.getId());
        String status = orderJson.getStatus();
        if (status.equals("2")) {
            title.setText(getString(R.string.deliverd));
            statusColorImg.setBackground(getResources().getDrawable(R.drawable.green_dot));
        }else if (status.equals("3")){
            title.setText(getString(R.string.pending));
            statusColorImg.setBackground(getResources().getDrawable(R.drawable.reddot_dash));
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
