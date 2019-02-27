package com.akhdmny.driver.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akhdmny.driver.ApiResponse.TransactionPojo.Transaction;
import com.akhdmny.driver.R;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Transaction> orderArrayList;
    public TransactionAdapter(Context context, ArrayList<Transaction> arrayList){
            this.context = context;
            this.orderArrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.transaction_items, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Transaction transaction = orderArrayList.get(position);
            holder.TxtViewDate.setText(transaction.getCreatedAt());
            holder.textViewDetails.setText(transaction.getDriver().getName());
            holder.TvAmount.setText(String.valueOf(transaction.getTransactionAmount()));
        if(position %2 == 0)
        {
            holder.transaction_layout.setBackgroundColor(context.getResources().getColor(R.color.cardBg));

        }
        else
        {
            holder.transaction_layout.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));

        }
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDetails, TxtViewDate,TvAmount;
        LinearLayout transaction_layout;
        public ViewHolder(View itemView) {
            super(itemView);
            TxtViewDate = itemView.findViewById(R.id.date_time);
            textViewDetails = itemView.findViewById(R.id.details);
            TvAmount = itemView.findViewById(R.id.finalAmountTv);
            transaction_layout = itemView.findViewById(R.id.transaction_layout);
        }
    }
}
