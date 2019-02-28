package com.akhdmny.driver.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akhdmny.driver.Models.Reason;
import com.akhdmny.driver.R;

import java.util.ArrayList;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.ProductViewHolder> {

    private Context mCtx;
    private ArrayList<Reason> reasonArrayList;
    DetectReasonSelected detectReasonSelected;
    public BottomSheetAdapter(Context context, ArrayList<Reason> list, DetectReasonSelected selected){
        this.mCtx = context;
        this.reasonArrayList = list;
        this.detectReasonSelected =selected;

    }
    public interface DetectReasonSelected{
        void onSelection(String reason);
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.cancel_item_fragment, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            String title = reasonArrayList.get(position).getReasonEn();
            holder.title.setText(title);
            holder.title.setOnClickListener(v -> detectReasonSelected.onSelection(title));
    }

    @Override
    public int getItemCount() {
        return reasonArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public ProductViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
        }
    }
}
