package com.akhdmny.driver.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akhdmny.driver.ApiResponse.CatInsideResponse;
import com.akhdmny.driver.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext ;
    private ArrayList<CatInsideResponse> mData ;


    public RecyclerViewAdapter(Context mContext, ArrayList<CatInsideResponse> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardveiw_item_book,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.bar.setVisibility(View.VISIBLE);
        holder.tv_book_title.setText(mData.get(position).getTitle());
        try {
            holder.img_book_thumbnail.setBackgroundColor(Color.parseColor(mData.get(position).getColor()));
            holder.tv_book_title.setBackgroundColor(Color.parseColor(mData.get(position).getColor()));
        }catch (Exception e){
            Log.i("exc",e.getMessage());
        }
        if(mData.get(position).getIcon() != null) {      //  holder.img_book_thumbnail.setImageResource(mData.get(position).getId());
            Picasso.get().load(mData.get(position).getIcon()).into(holder.img_book_thumbnail, new Callback() {
                @Override
                public void onSuccess() {
                    holder.bar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    holder.bar.setVisibility(View.GONE);
                }
            });
        }else {
            holder.bar.setVisibility(View.GONE);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                Intent intent = new Intent(mContext,Book_Activity.class);
//
//                // passing data to the book activity
//                intent.putExtra("Title",mData.get(position).getTitle());
//                intent.putExtra("Description",mData.get(position).getDescription());
//                intent.putExtra("Thumbnail",mData.get(position).getThumbnail());
//                // start the activity
//                mContext.startActivity(intent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_book_title;
        ImageView img_book_thumbnail;
        CardView cardView ;
        ProgressBar bar;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_book_title = (TextView) itemView.findViewById(R.id.book_title_id) ;
            img_book_thumbnail = (ImageView) itemView.findViewById(R.id.book_img_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
            bar = itemView.findViewById(R.id.progress);


        }
    }


}
