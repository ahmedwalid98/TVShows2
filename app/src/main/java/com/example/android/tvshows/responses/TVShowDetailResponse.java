package com.example.android.tvshows.responses;

import com.example.android.tvshows.models.TVShowDetails;
import com.google.gson.annotations.SerializedName;

public class TVShowDetailResponse {
    @SerializedName("tvShow")
    private TVShowDetails tvShowDetails;

    public TVShowDetails getTvShowDetails() {
        return tvShowDetails;
    }
}
