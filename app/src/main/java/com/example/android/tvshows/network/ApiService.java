package com.example.android.tvshows.network;

import com.example.android.tvshows.responses.TVShowDetailResponse;
import com.example.android.tvshows.responses.TVShowRespons;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("most-popular")
    Call<TVShowRespons> getMostPopularTvShows(@Query("page") int page);

    @GET("show-details")
    Call<TVShowDetailResponse> getTVShowDetails(@Query("q") String tvShowId);
}
