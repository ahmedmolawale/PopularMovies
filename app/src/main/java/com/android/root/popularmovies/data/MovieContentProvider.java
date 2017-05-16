package com.android.root.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by root on 5/14/17.
 */

public class MovieContentProvider extends ContentProvider {


    MovieDbHelper mMovieDbHelper;

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;
    public static final int TRAILERS = 200;
    public static final int TRAILER_WITH_ID = 201;
    public static final int REVIEWS = 300;
    public static final int REVIEW_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
         */
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_TRAILERS + "/#", TRAILER_WITH_ID);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS + "/#", REVIEW_WITH_ID);
        return uriMatcher;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase sqLiteDatabase = mMovieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        String movieId;
        String mSelection;
        String[] mSelectionArgs;
        switch (match) {
            case MOVIES:
                retCursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_WITH_ID:
                movieId = uri.getLastPathSegment();
                mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
                mSelectionArgs = new String[]{movieId};
                retCursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;
            case TRAILER_WITH_ID:
                movieId = uri.getLastPathSegment();
                mSelection = MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " =?";
                mSelectionArgs = new String[]{movieId};
                retCursor = sqLiteDatabase.query(MovieContract.TrailerEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;
            case REVIEW_WITH_ID:
                movieId = uri.getLastPathSegment();
                mSelection = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " =?";
                mSelectionArgs = new String[]{movieId};
                retCursor = sqLiteDatabase.query(MovieContract.ReviewEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;
            default:
                throw new SQLiteException("Unable to fetch for uri " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned
        long id;
        switch (match) {
            case MOVIES:
                // Insert new values into the database
                // Inserting values into movies table
                id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        SQLiteDatabase mSqLiteDatabase = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int retNum = 0;

        switch (match) {

            case TRAILERS:
                try {
                    mSqLiteDatabase.beginTransaction();
                    for (ContentValues contentValues : values) {
                        long id = mSqLiteDatabase.insert(MovieContract.TrailerEntry.TABLE_NAME, null, contentValues);
                        if (id != -1) {
                            retNum++;
                        }
                    }
                    mSqLiteDatabase.setTransactionSuccessful();
                } finally {
                    mSqLiteDatabase.endTransaction();
                }
                break;
            case REVIEWS:
                try {
                    mSqLiteDatabase.beginTransaction();
                    for (ContentValues contentValues : values) {
                        long id = mSqLiteDatabase.insert(MovieContract.ReviewEntry.TABLE_NAME, null, contentValues);
                        if (id != -1) {
                            retNum++;
                        }
                    }
                    mSqLiteDatabase.setTransactionSuccessful();
                } finally {
                    mSqLiteDatabase.endTransaction();
                }
                break;
            default:
                super.bulkInsert(uri, null);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return retNum;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase sqLiteDatabase = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int retValue;
        switch (match) {
            case MOVIE_WITH_ID:
                String movieId = uri.getLastPathSegment();
                String mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
                String[] mSelectionArgs = {movieId};
                retValue = sqLiteDatabase.update(MovieContract.MovieEntry.TABLE_NAME, values, mSelection, mSelectionArgs);
                break;
            default:
                throw new SQLiteException("Unable to update for uri " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retValue;
    }
}
