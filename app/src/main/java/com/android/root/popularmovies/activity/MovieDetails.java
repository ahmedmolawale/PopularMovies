package com.android.root.popularmovies.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.root.popularmovies.R;
import com.android.root.popularmovies.data.MovieContract;
import com.android.root.popularmovies.fragment.MoreFragment;
import com.android.root.popularmovies.fragment.ReviewFragment;
import com.android.root.popularmovies.fragment.TrailerFragment;
import com.android.root.popularmovies.model.Movie;
import com.android.root.popularmovies.model.Review;
import com.android.root.popularmovies.model.Trailer;
import com.android.root.popularmovies.utility.ImageUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MovieDetails extends AppCompatActivity {

    @InjectView(R.id.poster_image)
    ImageView mMovieImage;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @InjectView(R.id.tabs)
    TabLayout mTabLayout;
    @InjectView(R.id.viewpager)
    ViewPager mViewPager;
    Movie movie;

    private ArrayList<Review> reviews;
    private ArrayList<Trailer> trailers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //injecting view with the ButterKnife Library, its so sweet
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT) ) {
            movie = intent.getParcelableExtra(Intent.EXTRA_TEXT);

            if(intent.hasExtra(MainScreen.IMAGE_POSTER_BYTE_ARRAY)) {
                //offline view
                byte[] image = intent.getByteArrayExtra(MainScreen.IMAGE_POSTER_BYTE_ARRAY);
                setUp(movie, image);

            }else{
                setUp(movie,null);
            }
        }

        if (intent != null && intent.hasExtra(MainScreen.REVIEWS_ID)) {
            reviews = intent.getParcelableArrayListExtra(MainScreen.REVIEWS_ID);
        }
        if (intent != null && intent.hasExtra(MainScreen.TRAILERS_ID)) {
            trailers = intent.getParcelableArrayListExtra(MainScreen.TRAILERS_ID);
        }
        dynamicToolbarColor();
        toolbarTextAppernce();

        setUpViewPager();
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_details_menu, menu);
        //if movie is a favourite, set icon to shaded one
        MenuItem item = menu.findItem(R.id.action_favourite);
        if (checkWhetherMovieIsFavourite(movie.getId())) {

            item.setIcon(R.drawable.ic_favourite_on);
        } else
            item.setIcon(R.drawable.ic_favorite_off);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_share) {
            String mimeType = "text/plain";
            String title;
            String message;
            //check whether movie has at least one trailer
            //if so, get the first trailer
            if(trailers != null && trailers.size()>0){
                title = "Share Movie Trailer";
                String link = "https://www.youtube.com/watch?v=" + trailers.get(0).getKey();
                message = "Hey Buddy, Here is a movie you need to see. Watch the trailer here: " + link;
            }else{

                title = "Share Movie";
                //just append the movie title
                message = "Hey Buddy, Here is a movie you need to see: " + movie.getOriginalTitle();
            }


            ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(this);
            intentBuilder.setType(mimeType);
            intentBuilder.setChooserTitle(title);
            intentBuilder.setText(message);
            intentBuilder.startChooser();
        } else if (itemId == R.id.action_favourite) {
            if (checkWhetherMovieIsFavourite(movie.getId())) {
                //update movie and its trailers and reviews
                //updateMovieDetails();
                item.setIcon(R.drawable.ic_favorite_off);

            } else {
                insertMovieDetails();
                item.setIcon(R.drawable.ic_favourite_on);
                Toast toast = Toast.makeText(getApplicationContext(), "Added to favourites", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkWhetherMovieIsFavourite(int movieId) {
        ContentResolver contentResolver = getContentResolver();
        String id = String.valueOf(movieId);
        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    private void insertMovieDetails() {
        //get the poster
        Drawable drawable = mMovieImage.getDrawable();
        byte[] image = ImageUtility.getImageBytes(drawable);
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, String.valueOf(movie.getVoteAverage()));
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASED_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, image);

        contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        //also bulk insert the trailers
        if (trailers != null && trailers.size() > 0) {
            ContentValues[] trailersContentValues = new ContentValues[trailers.size()];
            int i = 0;
            for (Trailer trailer : trailers) {
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movie.getId());
                contentValues1.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, trailer.getName());
                contentValues1.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, trailer.getKey());
                trailersContentValues[i] = contentValues1;
                i += 1;
            }
            contentResolver.bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, trailersContentValues);
        }

        //also bulk insert the reviews
        if (reviews != null && reviews.size() > 0) {
            ContentValues[] reviewsContentValues = new ContentValues[reviews.size()];
            int j = 0;
            for (Review review : reviews) {
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movie.getId());
                contentValues1.put(MovieContract.ReviewEntry.COLUMN_AUTHOR_NAME, review.getAuthor());
                contentValues1.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, review.getContent());
                reviewsContentValues[j] = contentValues1;
                j += 1;
            }
            contentResolver.bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, reviewsContentValues);
        }
    }

    private void dynamicToolbarColor() {

        //Dynamic Toolbar colouring
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_movie);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                mCollapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(getResources().getColor(R.color.colorPrimary)));
                mCollapsingToolbarLayout.setStatusBarScrimColor(palette.getMutedColor(getResources().getColor(R.color.colorPrimaryDark)));
            }
        });

    }

    private void toolbarTextAppernce() {

        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);

    }

    private void setUpViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MoreFragment(), "Synopsis");
        adapter.addFragment(new TrailerFragment(), "Trailers");
        adapter.addFragment(new ReviewFragment(), "Reviews");
        mViewPager.setAdapter(adapter);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MoreFragment();

                case 1:
                    return new TrailerFragment();

                case 2:
                    return new ReviewFragment();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    private void setUp(Movie movie,byte[] image) {
        mCollapsingToolbarLayout.setTitle(movie.getOriginalTitle());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String preference = sharedPreferences.getString(MainScreen.SORT_KEY, MainScreen.POPULAR);

        if (preference.equals(MainScreen.FAVOURITE)) {
            //load locally
            Bitmap bitmap = ImageUtility.getImage(image);
            mMovieImage.setImageBitmap(bitmap);
            return;
        }

        String BASE_URL = "http://image.tmdb.org/t/p/";
        String IMAGE_SIZE = "w185/";
        String POSTER_PATH = movie.getPosterPath();
        if (POSTER_PATH != null) {
            Context context = MovieDetails.this;
            String imageUrl = BASE_URL.concat(IMAGE_SIZE).concat(POSTER_PATH);
            Picasso.with(context).load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.action_error)
                    .into(mMovieImage);
        }

    }

}
