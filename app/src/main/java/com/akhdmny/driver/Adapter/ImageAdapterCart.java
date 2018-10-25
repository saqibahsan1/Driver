package com.akhdmny.driver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.akhdmny.driver.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapterCart   extends RecyclerView.Adapter<ImageAdapterCart.ViewHolder> {

    private Context context;
    private List<String> imagesFiles;

    public ImageAdapterCart(Context context, List<String> imagesFiles) {
        this.context = context;
        this.imagesFiles = imagesFiles;
    }

    @Override
    public ImageAdapterCart.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ImageAdapterCart.ViewHolder(inflater.inflate(R.layout.image_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageAdapterCart.ViewHolder holder, int position) {

        if (imagesFiles== null){
            holder.imageView.setVisibility(View.GONE);
        }else {
            Picasso.get()
                    .load(imagesFiles.get(position))
                    .fit()
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imagesFiles.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_circle);
        }


    }
}

