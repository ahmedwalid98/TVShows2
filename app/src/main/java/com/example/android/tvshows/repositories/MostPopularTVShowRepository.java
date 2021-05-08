package com.example.android.tvshows.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.tvshows.network.ApiClient;
import com.example.android.tvshows.network.ApiService;
import com.example.android.tvshows.responses.TVShowRespons;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostPopularTVShowRepository {
    private ApiService apiService;

    public MostPopularTVShowRepository() {
        apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<TVShowRespons> getMostPopularTvShows(int page){
        MutableLiveData<TVShowRespons> mutableLiveData = new MutableLiveData<>();
        apiService.getMostPopularTvShows(page).enqueue(new Callback<TVShowRespons>() {
            @Override
            public void onResponse(@NonNull Call<TVShowRespons> call,@NonNull Response<TVShowRespons> response) {
                mutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<TVShowRespons> call,@NonNull Throwable t) {
                mutableLiveData.setValue(null);
            }
        });

        return mutableLiveData;
    }
}
