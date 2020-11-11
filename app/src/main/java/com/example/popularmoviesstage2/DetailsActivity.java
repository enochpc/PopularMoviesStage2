package com.example.popularmoviesstage2;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.popularmoviesstage2.Adapters.CustomCastAdapter;
import com.example.popularmoviesstage2.Adapters.CustomReviewsAdapter;
import com.example.popularmoviesstage2.Adapters.CustomTrailerAdapter;
import com.example.popularmoviesstage2.Database.MovieDatabase;
import com.example.popularmoviesstage2.Interface.MovieInterface;
import com.example.popularmoviesstage2.Models.Cast;
import com.example.popularmoviesstage2.Models.Movie;
import com.example.popularmoviesstage2.Models.MovieCredits;
import com.example.popularmoviesstage2.Models.MovieReviews;
import com.example.popularmoviesstage2.Models.MovieTrailer;
import com.example.popularmoviesstage2.Models.Reviews;
import com.example.popularmoviesstage2.Models.Trailer;
import com.example.popularmoviesstage2.Network.APIClient;
import com.example.popularmoviesstage2.Utils.AppExecutors;
import com.example.popularmoviesstage2.Utils.MovieUtils;


import java.util.List;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;



import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.popularmoviesstage2.Utils.Constants.FAV_MOVIE_KEY;
import static com.example.popularmoviesstage2.Utils.Constants.SELECTED_MOVIE_TO_SEE_DETAILS;


public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private static Retrofit retrofit;
    private static String API_KEY;
    public List<Trailer> trailers;
    public List<Reviews> reviews;
    public List<Cast> cast;
    private boolean isFavorite;
    private int movieId;
    private Movie movie;


    @BindView(R.id.iv_details_moviePoster)
    ImageView moviePoster;

    @BindView(R.id.tv_details_MovieTitle)
    TextView movieTitle;

    @BindView(R.id.tv_details_Language)
    TextView movieLanguage;

    @BindView(R.id.tv_details_plot)
    TextView moviePlot;

    @BindView(R.id.tv_details_releaseDate)
    TextView movieReleaseDate;

    @BindView(R.id.tv_details_voteAverage)
    TextView movieVoteAverage;

    @BindView(R.id.rv_trailer)
    public RecyclerView rvTrailer;

    @BindView(R.id.rv_reviews)
    public RecyclerView rvReviews;

    @BindView(R.id.rv_cast)
    public RecyclerView rvCast;

    @BindView(R.id.tv_cast_not_available)
    TextView castNotAvailable;

    @BindView(R.id.tv_trailers_not_available)
    TextView trailersNotAvailable;

    @BindView(R.id.tv_reviews_not_available)
    TextView reviewsNotAvailable;

    @BindView(R.id.iv_fav_btn)
    ImageView favBtn;

    private DetailsActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        API_KEY = "put your key here";

        ButterKnife.bind(this);
        MovieInterface movieService = APIClient.getRetrofitInstance().create(MovieInterface.class);
        viewModel = ViewModelProviders.of(this).get(DetailsActivityViewModel.class);

        favBtn.setOnClickListener(v -> onFavButtonClicked());

        if (getIntent() != null){
            if (getIntent().hasExtra(SELECTED_MOVIE_TO_SEE_DETAILS)){
                movie = getIntent().getParcelableExtra(SELECTED_MOVIE_TO_SEE_DETAILS);
                movieId = movie.getMovieId();
                AppExecutors.getExecutorInstance().getDiskIO().execute(() -> {
                    isFavorite = viewModel.isFavorite(movieId);
                    if (isFavorite) {
                        movie = MovieDatabase.getInstance(this).movieDao().getMovie(movieId);
                        runOnUiThread(() -> favBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.favorite_selected)));
                    } else {
                        runOnUiThread(() -> favBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.favorite_unsel)));
                    }
                });

            }

        }

        getSelectedMovieDetails(movieService);
    }
    private void getSelectedMovieDetails(MovieInterface client){

        if (movieId != 0){
            Call<Movie> detailResultsCall = client.getMovieDetails(movieId, API_KEY);
            detailResultsCall.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                    if(response.body() == null){
                        return;
                    }

                    movieTitle.setText(response.body().getTitle());
                    movieLanguage.setText(new StringBuilder("Language:").append(response.body().getOriginalLanguage()));
                    if(response.body().getOverview() != null && !response.body().getOverview().isEmpty()){
                        moviePlot.setText(response.body().getOverview());
                    } else {
                        moviePlot.setText("Plot synopsis not available");
                    }
                    movieReleaseDate.setText(new StringBuilder("Release Date:").append(response.body().getReleaseDate()));
                    movieVoteAverage.setText(new StringBuilder("Rating:").append(response.body().getVoteAverage()));

                    Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
                    builder.downloader(new OkHttp3Downloader(getApplicationContext()));
                    builder.build().load(getResources().getString(R.string.IMAGE_BASE_URL) + response.body().getBackdropPath())
                            .placeholder((R.drawable.gradient_background))
                            .error(R.drawable.ic_launcher_foreground)
                            .into(moviePoster);

                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {
                    if (movie != null) {
                        Log.d(TAG, "Movie is already set by favorites");
                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong, Try again later", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            getMovieTrailers(movieId);
            getMovieReviews(movieId);
            getMovieCast(movieId);

        }
    }

    private void onFavButtonClicked() {
        AppExecutors.getExecutorInstance().getDiskIO().execute(() -> {
            boolean isFavorite = viewModel.isFavorite(movieId);
            if (isFavorite) {
                viewModel.removeMovieFromFavorites(movie);
                runOnUiThread(() -> {
                    favBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.favorite_unsel));
                    Toast.makeText(this, getResources().getString(R.string.Favorite_Removed), Toast.LENGTH_SHORT).show();

                });
            } else {
                viewModel.addMovieToFavorites(movie);
                runOnUiThread(() -> {
                    Toast.makeText(this, getResources().getString(R.string.Favorite_Added), Toast.LENGTH_SHORT).show();
                    favBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.favorite_selected));
                });
            }
            viewModel.updateFavoriteMovie(movieId, !isFavorite);
            finish();

        });
    }
    private void navigateToFavoritesMovieScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(FAV_MOVIE_KEY, true);
        startActivity(intent);
    }

    private void getMovieCast(Integer id){
        if (MovieUtils.getInstance().isNetworkAvailable(this)){
            if (retrofit == null){
                retrofit = APIClient.getRetrofitInstance();
            }
            MovieInterface movieService = retrofit.create(MovieInterface.class);
            Call<MovieCredits> call = movieService.getMovieCredits(id, API_KEY);
            call.enqueue(new Callback<MovieCredits>() {
                @Override
                public void onResponse(@NonNull Call<MovieCredits> call, @NonNull Response<MovieCredits> response) {
                    if (response.isSuccessful() && response.body() != null){
                        cast = response.body().getCast();
                        if (cast != null && !cast.isEmpty()){
                            rvCast.setVisibility(View.VISIBLE);
                            castNotAvailable.setVisibility(View.GONE);
                            generateCreditsList(cast);
                        }else {
                            rvCast.setVisibility(View.GONE);
                            castNotAvailable.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MovieCredits> call, Throwable t) {
                    Toast.makeText(DetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(DetailsActivity.this, "Network Connection not Available", Toast.LENGTH_SHORT).show();
        }
    }

    private void getMovieReviews(Integer id){
        if (MovieUtils.getInstance().isNetworkAvailable(this)){
            if (retrofit == null){
                retrofit = APIClient.getRetrofitInstance();
            }
            MovieInterface movieService = retrofit.create(MovieInterface.class);
            Call<MovieReviews> call = movieService.getMovieReviews(id, API_KEY, 1);
            call.enqueue(new Callback<MovieReviews>() {
                @Override
                public void onResponse(@NonNull Call<MovieReviews> call, @NonNull Response<MovieReviews> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        reviews = response.body().getReviewList();
                        if (reviews != null && !reviews.isEmpty()){
                            rvReviews.setVisibility(View.VISIBLE);
                            reviewsNotAvailable.setVisibility(View.GONE);
                            generateReviewList(reviews);
                        }else {
                            rvReviews.setVisibility(View.GONE);
                            reviewsNotAvailable.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MovieReviews> call, Throwable t) {
                    Toast.makeText(DetailsActivity.this, "Something went wrong",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(DetailsActivity.this, "Network Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    private void getMovieTrailers(Integer id){
        if (MovieUtils.getInstance().isNetworkAvailable(this)){
            if(retrofit == null){
                retrofit = APIClient.getRetrofitInstance();
            }
            MovieInterface movieService = retrofit.create(MovieInterface.class);
            Call<MovieTrailer> call = movieService.getMovieTrailers(id, API_KEY);
            call.enqueue(new Callback<MovieTrailer>() {
                @Override
                public void onResponse(@NonNull Call<MovieTrailer> call, @NonNull Response<MovieTrailer> response) {
                    if (response.isSuccessful() && response.body() != null){
                        trailers = response.body().getTrailers();
                        if(trailers != null && !trailers.isEmpty()){
                            rvTrailer.setVisibility(View.VISIBLE);
                            trailersNotAvailable.setVisibility(View.GONE);
                            generateTrailerList(trailers);
                        }else {
                            rvTrailer.setVisibility(View.GONE);
                            trailersNotAvailable.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MovieTrailer> call, Throwable t) {
                    Toast.makeText(DetailsActivity.this, "Something went wrong",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(DetailsActivity.this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateCreditsList(List<Cast> cast){
        CustomCastAdapter adapter = new CustomCastAdapter(this, cast);
        initCastAdapter(adapter);
    }

    private void generateReviewList(final List<Reviews> reviews){
        CustomReviewsAdapter adapter = new CustomReviewsAdapter(this, reviews);
        initReviewsAdapter(adapter);
    }

    private void generateTrailerList(final List<Trailer> trailers){
        CustomTrailerAdapter adapter = new CustomTrailerAdapter(this,trailers);
        initTrailerAdapter(adapter);
    }

    private void initCastAdapter(CustomCastAdapter adapter){
        rvCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCast.setAdapter(adapter);
    }

    private void initReviewsAdapter(CustomReviewsAdapter adapter){
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(adapter);
    }

    private void initTrailerAdapter(CustomTrailerAdapter adapter){
        rvTrailer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTrailer.setAdapter(adapter);
    }
}
