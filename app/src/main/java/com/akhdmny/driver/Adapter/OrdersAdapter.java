package com.akhdmny.driver.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akhdmny.driver.ApiResponse.MyOrderDetails.Order;
import com.akhdmny.driver.R;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

     private Context context;
     private ArrayList<Order> orderArrayList;
    public  OrdersAdapter(Context context, ArrayList<Order> list){
        this.context = context;
        this.orderArrayList = list;
    }
    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_order_items, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
        Order order = orderArrayList.get(position);

        holder.textViewId.setText(order.getId().toString());
        holder.TxtViewDate.setText(order.getUpdatedAt());

        if(position %2 == 0)
        {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.cardBg));

        }
        else
        {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));

        }
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewId, TxtViewDate;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewParent);
            textViewId = itemView.findViewById(R.id.OrderId);
            TxtViewDate = itemView.findViewById(R.id.Date);
        }
    }
}
