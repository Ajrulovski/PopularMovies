package ins.com.mk.popularmovies.helper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Gazmend on 8/12/2015.
 */
public class MovieResult implements Parcelable {
    public ArrayList<String> poster;
    //public ArrayList<JSONObject> metadata;
    public String metadata;

    public MovieResult(ArrayList<String> poster, String metadata) {
        this.poster = poster;
        this.metadata = metadata;
    }

    private MovieResult(Parcel in) {
        poster = in.readArrayList(String.class.getClassLoader());
        metadata = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "STRING_VALUE";
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeList(poster);
        out.writeString(metadata);
    }

    public static final Parcelable.Creator<MovieResult> CREATOR = new Parcelable.Creator<MovieResult>() {
        public MovieResult createFromParcel(Parcel in) {
            return new MovieResult(in);
        }

        public MovieResult[] newArray(int size) {
            return new MovieResult[size];
        }
    };
}
