package com.akhdmny.driver.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akhdmny.driver.ApiResponse.UserAcceptedResponse.CartItem;
import com.akhdmny.driver.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DriverDetailedAdapter extends RecyclerView.Adapter<DriverDetailedAdapter.ProductViewHolder> {
    private Context mCtx;

    //we are storing all the products in a list
    private ArrayList<CartItem> productList;

    @NonNull
    @Override
    public DriverDetailedAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.category_details_items, null);
        return new ProductViewHolder(view);
    }
    public DriverDetailedAdapter(Context mCtx, ArrayList<CartItem> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }


    @Override
    public void onBindViewHolder(@NonNull DriverDetailedAdapter.ProductViewHolder holder, int position) {
       CartItem product = productList.get(position);

        try {
            if (product == null){
                Log.i("product","is null");
            }else {
                holder.textViewTitle.setText(product.getTitle());
                holder.TxtViewAddress.setText(String.valueOf(product.getAddress()));
                holder.textViewPrice.setText(new DecimalFormat("##").format(product.getAmount()));
                if (product.getImage() == null){
                    Picasso.get().load(R.drawable.place_holder).into(holder.imageView);
                }else {
                    Picasso.get().load(product.getImage()).into(holder.imageView);
                }
            }
        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, TxtViewAddress, textViewPrice;
        ImageView imageView;
        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            TxtViewAddress = itemView.findViewById(R.id.TVAddress);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imgResturaunt);
        }
    }
}
