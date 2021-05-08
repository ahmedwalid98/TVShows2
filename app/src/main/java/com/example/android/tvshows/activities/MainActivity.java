package com.example.android.tvshows.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.android.tvshows.R;
import com.example.android.tvshows.adapters.TVShowsAdapter;
import com.example.android.tvshows.databinding.ActivityMainBinding;
import com.example.android.tvshows.listeners.TVShowListener;
import com.example.android.tvshows.models.TVShow;
import com.example.android.tvshows.responses.TVShowRespons;
import com.example.android.tvshows.viewmodels.MostPopularTVShowsViewModel;
import com.example.android.tvshows.viewmodels.TVShowDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowListener {
    MostPopularTVShowsViewModel viewModel;
    ActivityMainBinding activityMainBinding;
    List<TVShow> tvShows = new ArrayList<>();
    TVShowsAdapter adapter;
    int currentPage = 1;
    int totalPages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        doInitialization();
    }

    private void doInitialization() {
        activityMainBinding.tvShowsRecyclerView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        adapter = new TVShowsAdapter(tvShows,this);
        activityMainBinding.tvShowsRecyclerView.setAdapter(adapter);
        activityMainBinding.tvShowsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!activityMainBinding.tvShowsRecyclerView.canScrollVertically(1)){
                    if(currentPage<= totalPages){
                        currentPage+=1;
                        getMostPopularTvShows();
                    }
                }
            }
        });
        getMostPopularTvShows();
    }
    private void getMostPopularTvShows() {
       toggleLoading();
        viewModel.getMostPopularTvShows(currentPage).observe(this, s ->{
            toggleLoading();
            if(s!=null){
                totalPages = s.getTotalPages();
                if(s.getTvShows()!=null){
                    int oldCount = tvShows.size();
                    tvShows.addAll(s.getTvShows());
                    adapter.notifyItemRangeInserted(oldCount,tvShows.size());
                }
            }
        }
        );
    }

    private void toggleLoading(){
        if(currentPage == 1){
            if(activityMainBinding.getIsLoading() != null && activityMainBinding.getIsLoading()){
                activityMainBinding.setIsLoading(false);
            }else {
                activityMainBinding.setIsLoading(true);
            }
        }else {
            if(activityMainBinding.getIsLoadingMore() != null && activityMainBinding.getIsLoadingMore()){
                activityMainBinding.setIsLoadingMore(false);
            }else {
                activityMainBinding.setIsLoadingMore(true);
            }
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent in = new Intent(MainActivity.this, TVShowDetailsActivity.class);
        in.putExtra("tvShow", tvShow);
        startActivity(in);
    }
}