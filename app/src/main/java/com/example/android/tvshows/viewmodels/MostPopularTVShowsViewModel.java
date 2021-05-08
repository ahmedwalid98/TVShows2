package com.example.android.tvshows.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.tvshows.repositories.MostPopularTVShowRepository;
import com.example.android.tvshows.responses.TVShowRespons;

public class MostPopularTVShowsViewModel extends ViewModel {
    MostPopularTVShowRepository mostPopularTVShowRepository;

    public MostPopularTVShowsViewModel() {
        mostPopularTVShowRepository = new MostPopularTVShowRepository();
    }

    public LiveData<TVShowRespons> getMostPopularTvShows(int page){
        return mostPopularTVShowRepository.getMostPopularTvShows(page);
    }
}
