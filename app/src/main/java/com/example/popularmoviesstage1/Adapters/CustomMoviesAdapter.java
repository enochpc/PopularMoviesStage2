package com.example.popularmoviesstage1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmoviesstage1.Models.Movie;
import com.example.popularmoviesstage1.R;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//class to extend the recyclerview for our purposes using the Movie class
//as time allows I intend to replace the deprecated Butterknife with bind

public class CustomMoviesAdapter extends RecyclerView.Adapter<CustomMoviesAdapter.CustomMovieViewHolder> {

    private List<Movie> dataList;
    private Context context;
    final private MovieItemClickListener mOnMovieItemClickListener;

    public interface MovieItemClickListener {
        void onMovieItemClick(int clickedItemIndex);
    }

    public CustomMoviesAdapter(Context context, List<Movie> dataList, MovieItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.mOnMovieItemClickListener = listener;
    }

    @Override
    public CustomMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_movie_item, parent, false);
        return new CustomMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomMovieViewHolder holder, int position) {
        holder.bindMovie(dataList.get(position));
    }

    //returns dataList from Movie object
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //class to extend Viewholder each CustomMovieViewholder becomes a grid item
    class CustomMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context mContext;

        @BindView(R.id.tvMovieTitle)
        TextView movieTitle;
        @BindView(R.id.ivMovieImage)
        ImageView coverImage;
        @BindView(R.id.tvReleaseDate)
        TextView releaseDate;

        CustomMovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        //builds the movie object for each grid
        void bindMovie(Movie movie) {
            StringBuilder releaseText = new StringBuilder().append("Release Date: ");
            releaseText.append(movie.getReleaseDate());

            movieTitle.setText(movie.getOriginalTitle());
            releaseDate.setText(releaseText);
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.downloader(new OkHttp3Downloader(context));
            builder.build().load(context.getResources().getString(R.string.IMAGE_BASE_URL) + movie.getPosterPath())
                    .placeholder((R.drawable.gradient_background))
                    .error(R.drawable.ic_launcher_background)
                    .into(coverImage);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnMovieItemClickListener.onMovieItemClick(clickedPosition);
        }
    }
}

