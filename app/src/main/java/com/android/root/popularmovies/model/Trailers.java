
package com.android.root.popularmovies.model;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trailers implements Parcelable
{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("results")
    @Expose
    private ArrayList<Trailer> trailers = null;
    public final static Creator<Trailers> CREATOR = new Creator<Trailers>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Trailers createFromParcel(Parcel in) {
            Trailers instance = new Trailers();
            instance.id = ((int) in.readValue((int.class.getClassLoader())));
            in.readList(instance.trailers, (Trailer.class.getClassLoader()));
            return instance;
        }
        public Trailers[] newArray(int size) {
            return (new Trailers[size]);
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(trailers);
    }

    public int describeContents() {
        return  0;
    }
}
