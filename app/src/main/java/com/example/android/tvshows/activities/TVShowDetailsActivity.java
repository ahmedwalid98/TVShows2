package com.example.android.tvshows.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.tvshows.R;
import com.example.android.tvshows.adapters.EpisodesAdapter;
import com.example.android.tvshows.adapters.ImageSliderAdapter;
import com.example.android.tvshows.databinding.ActivityTVShowDetailsBinding;
import com.example.android.tvshows.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.android.tvshows.models.TVShow;
import com.example.android.tvshows.viewmodels.TVShowDetailsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {
    ActivityTVShowDetailsBinding activityTVShowDetailsBinding;
    TVShowDetailsViewModel tvShowDetailsViewModel;
    BottomSheetDialog bottomSheetDialog;
    LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    TVShow tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTVShowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_t_v_show_details);
        doInitialization();
    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTVShowDetailsBinding.imageBack.setOnClickListener(view -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        getTvShowDetail();
    }

    private void getTvShowDetail() {
        activityTVShowDetailsBinding.setIsLoading(true);
        String showId = String.valueOf(tvShow.getId());
        tvShowDetailsViewModel.getTvShowDetails(showId).observe(this, s -> {
            activityTVShowDetailsBinding.setIsLoading(false);
            if (s.getTvShowDetails() != null) {
                if (s.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(s.getTvShowDetails().getPictures());
                }
                activityTVShowDetailsBinding.setImageURL(s.getTvShowDetails().getImagePath());
                activityTVShowDetailsBinding.imageTVShow.setVisibility(View.VISIBLE);

                activityTVShowDetailsBinding.setDescription(
                        String.valueOf(
                                HtmlCompat.fromHtml(
                                        s.getTvShowDetails().getDescription(),
                                        HtmlCompat.FROM_HTML_MODE_LEGACY
                                )
                        )
                );
                activityTVShowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.readMore.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.readMore.setOnClickListener(view -> {
                    if (activityTVShowDetailsBinding.readMore.getText().toString().equals("Read More")) {
                        activityTVShowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                        activityTVShowDetailsBinding.textDescription.setEllipsize(null);
                        activityTVShowDetailsBinding.readMore.setText("Read Less");
                    } else {
                        activityTVShowDetailsBinding.textDescription.setMaxLines(4);
                        activityTVShowDetailsBinding.textDescription.setEllipsize(null);
                        activityTVShowDetailsBinding.readMore.setText("Read More");
                    }
                });
                activityTVShowDetailsBinding.setRating(
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                Double.parseDouble(s.getTvShowDetails().getRating())
                        )
                );
                if (s.getTvShowDetails().getGenres() != null) {
                    activityTVShowDetailsBinding.setGenre(s.getTvShowDetails().getGenres()[0]);
                } else {
                    activityTVShowDetailsBinding.setGenre("N/A");
                }
                activityTVShowDetailsBinding.setRuntime(s.getTvShowDetails().getRuntime() + " Min");
                activityTVShowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.buttonWebsite.setOnClickListener(view -> {
                    Intent in = new Intent(Intent.ACTION_VIEW);
                    in.setData(Uri.parse(s.getTvShowDetails().getUrl()));
                    startActivity(in);
                });
                activityTVShowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.buttonEpisodes.setOnClickListener(view -> {
                    if (bottomSheetDialog == null) {
                        bottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                        layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                LayoutInflater.from(TVShowDetailsActivity.this),
                                R.layout.layout_episodes_bottom_sheet,
                                findViewById(R.id.episodesContainer),
                                false
                        );
                        bottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                        layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(
                                new EpisodesAdapter(s.getTvShowDetails().getEpisodes())
                        );
                        layoutEpisodesBottomSheetBinding.textTitle.setText(
                                String.format("Episodes | %s", tvShow.getName())
                        );
                        layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
                    }
                    FrameLayout frameLayout = bottomSheetDialog.findViewById(
                            com.google.android.material.R.id.design_bottom_sheet
                    );
                    if (frameLayout != null) {
                        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                        bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    }

                    bottomSheetDialog.show();
                });
                activityTVShowDetailsBinding.imageWatchList.setOnClickListener(view ->
                        new CompositeDisposable().add(tvShowDetailsViewModel.addToWatchlist(tvShow)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    activityTVShowDetailsBinding.imageWatchList.setImageResource(R.drawable.ic_added);
                                    Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                                })
                        ));
                activityTVShowDetailsBinding.imageWatchList.setVisibility(View.VISIBLE);
                loadBasicTVShowDetails();
            }
        });


    }

    private void loadImageSlider(String[] images) {
        activityTVShowDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTVShowDetailsBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(images));
        activityTVShowDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.ViewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicators(images.length);
        activityTVShowDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setupCurrentSliderIndicators(position);
            }
        });
    }

    private void setupSliderIndicators(int count) {
        ImageView[] imageViews = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView(getApplicationContext());
            imageViews[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive));
            imageViews[i].setLayoutParams(layoutParams);
            activityTVShowDetailsBinding.layoutSliderIndicators.addView(imageViews[i]);
        }
        activityTVShowDetailsBinding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setupCurrentSliderIndicators(0);
    }

    private void setupCurrentSliderIndicators(int position) {
        int currentCount = activityTVShowDetailsBinding.layoutSliderIndicators.getChildCount();
        for (int i = 0; i < currentCount; i++) {
            ImageView imageView = (ImageView) activityTVShowDetailsBinding.layoutSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.backgorund_slider_indicator_active)
                );
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive));
            }
        }
    }

    private void loadBasicTVShowDetails() {
        activityTVShowDetailsBinding.setTvShowName(tvShow.getName());
        activityTVShowDetailsBinding.textShowName.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.setNetworkCountry(tvShow.getNetwork() + " (" + tvShow.getCountry() + ")");
        activityTVShowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.setStatus(tvShow.getStatus());
        activityTVShowDetailsBinding.status.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.setStartedDate(tvShow.getStartDate());
        activityTVShowDetailsBinding.textStarted.setVisibility(View.VISIBLE);
    }
}