package com.akhdmny.driver.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.akhdmny.driver.Activities.CategoryDetailActivity;
import com.akhdmny.driver.Activities.ParcelActivity;
import com.akhdmny.driver.Adapter.RecyclerViewAdapter;
import com.akhdmny.driver.ApiResponse.CatInsideResponse;
import com.akhdmny.driver.ApiResponse.CategoriesResponse;
import com.akhdmny.driver.ErrorHandling.LoginApiError;
import com.akhdmny.driver.MainActivity;
import com.akhdmny.driver.NetworkManager.NetworkConsume;
import com.akhdmny.driver.R;
import com.google.gson.Gson;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FargmentService extends Fragment {

    ArrayList<CatInsideResponse> categoriesNames;
    @BindView(R.id.recyclerview_id)
    RecyclerView recyclerview;
    @BindView(R.id.rotateloading)
    RotateLoading rotateLoading;
    RecyclerViewAdapter myAdapter;
    SharedPreferences prefs;
    public FargmentService(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.services, container, false);
        ButterKnife.bind(this,view);
        prefs = getActivity().getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        categoriesNames = new ArrayList<>();
        CategoryApi();
        ItemClick();

        return view;
    }

    private void CategoryApi(){
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().CategoryApi().enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful()) {
                CategoriesResponse categoriesResponse = response.body();
                for (int i=0; i< categoriesResponse.getResponse().size(); i++){
                    categoriesNames.add(categoriesResponse.getResponse().get(i));
                }
                   // categoriesNames = new ArrayList((categoriesResponse.getResponse()));
                    myAdapter = new RecyclerViewAdapter(getActivity(),categoriesNames);
                    recyclerview.setLayoutManager(new GridLayoutManager(getActivity(),3));
                    recyclerview.setAdapter(myAdapter);
                    rotateLoading.stop();


                }else {
                    rotateLoading.stop();
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(getActivity(), message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private void ItemClick(){
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerview, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                NetworkConsume.getInstance().setDefaults("id",categoriesNames.get(position).getId().toString(),getActivity());
                NetworkConsume.getInstance().setDefaults("image",categoriesNames.get(position).getIcon(),getActivity());
                NetworkConsume.getInstance().setDefaults("title",categoriesNames.get(position).getTitle(),getActivity());
                NetworkConsume.getInstance().setDefaults("colour",categoriesNames.get(position).getColor(),getActivity());

                if (categoriesNames.get(position).getTitle().trim().equals("Parcels")){
                    startActivity(new Intent(getActivity(), ParcelActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                }else {
                    startActivity(new Intent(getActivity(), CategoryDetailActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                }


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }


}
