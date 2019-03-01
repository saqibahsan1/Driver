package com.akhdmny.driver.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akhdmny.driver.Adapter.TransactionAdapter;
import com.akhdmny.driver.ApiResponse.TransactionPojo.DriverRewards;
import com.akhdmny.driver.ApiResponse.TransactionPojo.Reward;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionFragment extends Fragment {

    public TransactionFragment(){}

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.totalAmountTv)
    TextView totalAmountTv;

    TransactionAdapter transactionAdapter;
    SharedPreferences prefs;
    ArrayList<Reward> transactionArrayList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_fragment, container, false);
        ButterKnife.bind(this,view);
        transactionArrayList = new ArrayList<>();
        prefs = getActivity().getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);

        transactionAdapter = new TransactionAdapter(getActivity(),transactionArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(transactionAdapter);
        transactionApi();
        return view;
    }

    private void transactionApi(){
        NetworkConsume.getInstance().ShowProgress(getActivity());
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().Transactions().enqueue(new Callback<DriverRewards>() {
            @Override
            public void onResponse(Call<DriverRewards> call, Response<DriverRewards> response) {
                if (response.isSuccessful()){
                    NetworkConsume.getInstance().HideProgress();
                    DriverRewards model = response.body();
                    double sum = 0;
                    if (model != null) {
                        if (model.getResponse().getRewards().size() >0){
                            transactionArrayList.addAll(model.getResponse().getRewards());
                            transactionAdapter.notifyDataSetChanged();
                            for (int i=0; i<model.getResponse().getRewards().size();i++) {
                                    sum += model.getResponse().getRewards().get(i).getAmount();
                            }
                            totalAmountTv.setText(String.valueOf(sum));
                        }else {
                            NetworkConsume.getInstance().HideProgress();
                            Toast.makeText(getActivity(), "No Transactions found!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    NetworkConsume.getInstance().HideProgress();
                    Toast.makeText(getActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<DriverRewards> call, Throwable t) {
                NetworkConsume.getInstance().HideProgress();
                Toast.makeText(getActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
