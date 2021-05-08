package com.example.android.tvshows.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.android.tvshows.database.TVShowsDatabase;
import com.example.android.tvshows.models.TVShow;
import com.example.android.tvshows.repositories.TVShowDetailsRepository;
import com.example.android.tvshows.responses.TVShowDetailResponse;

import io.reactivex.Completable;

public class TVShowDetailsViewModel extends AndroidViewModel {
    TVShowDetailsRepository tvShowDetailsRepository;
    TVShowsDatabase tvShowsDatabase;

    public TVShowDetailsViewModel(@NonNull Application application) {
        super(application);
        tvShowDetailsRepository = new TVShowDetailsRepository();
        tvShowsDatabase = TVShowsDatabase.getTvShowsDatabase(application);
    }

    public LiveData<TVShowDetailResponse> getTvShowDetails(String showId){
        return tvShowDetailsRepository.getTvShowDetails(showId);
    }

    public Completable addToWatchlist(TVShow tvShow){
        return tvShowsDatabase.tvShowDao().addToWatchlist(tvShow);
    }
}
