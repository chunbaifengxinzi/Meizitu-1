package com.makerwei.meizitu;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.makerwei.meizitu.adapter.ImgsAdapter;
import com.makerwei.meizitu.model.Meizi;
import com.makerwei.meizitu.net.MyRetrofit;


import java.io.IOException;

import okhttp3.ResponseBody;

import retrofit2.Call;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity{
    private MyRetrofit myRetrofit;
    private int groupId = 1;
    private Meizi meizi;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private Toolbar toolbar;
    private ImgsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRetrofit = new MyRetrofit(this);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        initToolbar();
        new LoadImageAsyncTask().execute(groupId);


    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRecyclerView() throws IOException {
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.rc_imgs);
        adapter = new ImgsAdapter(MainActivity.this, meizi);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(getOnBottomListener(layoutManager));
    }


    RecyclerView.OnScrollListener getOnBottomListener(final StaggeredGridLayoutManager layoutManager) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isBottom = layoutManager.findLastCompletelyVisibleItemPositions(
                        new int[2])[1] >= adapter.getItemCount() - 6;

                if (isBottom) {
                    groupId += 1;
                    myRetrofit.getMoreData(groupId).enqueue(new Callback<Meizi>() {
                        @Override
                        public void onResponse(Call<Meizi> call, Response<Meizi> response) {
                            meizi.getResults().addAll(response.body().getResults());
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<Meizi> call, Throwable t) {

                        }
                    });
                }
            }
        };
    }

    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    public class LoadImageAsyncTask extends AsyncTask<Integer, Void, Meizi> {

        @Override
        protected Meizi doInBackground(Integer... params) {
            try {
                meizi = myRetrofit.getData(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return meizi;
        }


        @Override
        protected void onPostExecute(Meizi meizi) {
            super.onPostExecute(meizi);
            try {
                initRecyclerView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
