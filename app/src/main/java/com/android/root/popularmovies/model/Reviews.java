
package com.android.root.popularmovies.model;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reviews implements Parcelable
{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("results")
    @Expose
    private ArrayList<Review> reviews = null;
    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("total_results")
    @Expose
    private int totalResults;
    public final static Creator<Reviews> CREATOR = new Creator<Reviews>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Reviews createFromParcel(Parcel in) {
            Reviews instance = new Reviews();
            instance.id = ((int) in.readValue((int.class.getClassLoader())));
            instance.page = ((int) in.readValue((int.class.getClassLoader())));
            in.readList(instance.reviews, (Review.class.getClassLoader()));
            instance.totalPages = ((int) in.readValue((int.class.getClassLoader())));
            instance.totalResults = ((int) in.readValue((int.class.getClassLoader())));
            return instance;
        }

        public Reviews[] newArray(int size) {
            return (new Reviews[size]);
        }
    }
    ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(page);
        dest.writeList(reviews);
        dest.writeValue(totalPages);
        dest.writeValue(totalResults);
    }

    public int describeContents() {
        return  0;
    }

}
