package ins.com.mk.popularmovies.helper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Gazmend on 10/1/2015.
 */
public class Utility {
    public ArrayList<String> allurls = new ArrayList<String>();
    public ArrayList<JSONObject> metadata = new ArrayList<JSONObject>();

    public void getMovieDataFromJsonString(String movieJsonStr) throws JSONException {

        // Now we have a String representing the complete most popular movies in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        // Location information
        //String full_result = null;

        // Dude, maybe you should move these to a separate file named constants, it would be easier to configure if needed
        final String OMD_TITLE = "title";
        final String OMD_POSTER = "poster";
        final String OMD_PLOT = "plot";
        final String OMD_RATING = "rating";
        final String OMD_RELEASEDATE = "releasedate";

        final String OMD_MOVIE_ID = "id";

        final String OMD_RESULTS = "results";

        try {
            //JSONObject movieJson = new JSONObject(movieJsonStr);
            //JSONArray resultsArray = movieJson.getJSONArray(OMD_RESULTS);

            JSONArray resultsArray = new JSONArray(movieJsonStr);

            String title;
            String poster;
            String plot;
            String rating;
            String releasedate;
            String id;

            for (int i = 0; i < resultsArray.length()-1; i++) {
                JSONObject row = resultsArray.getJSONObject(i);
                title = row.getString(OMD_TITLE);
                poster = row.getString(OMD_POSTER);
                plot = row.getString(OMD_PLOT);
                rating = row.getString(OMD_RATING);
                releasedate = row.getString(OMD_RELEASEDATE);
                id = row.getString(OMD_MOVIE_ID);

                //generate the metadata JSON object
                JSONObject movieObject = new JSONObject();
                movieObject.put("title", title);
                movieObject.put("poster", poster);
                movieObject.put("plot", plot);
                movieObject.put("rating", rating);
                movieObject.put("releasedate", releasedate);
                movieObject.put("id", id);

                metadata.add(i, movieObject);
                //full_result = full_result+title+","+poster+","+plot+","+rating+","+releasedate+";";
                allurls.add(i,"http://image.tmdb.org/t/p/w185"+poster);
            }

        } catch (JSONException e) {
            Log.e("UTILITY_ERROR", e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            // *checkpoint* DUDE, FIX THIS TO RETURN SOME INDICATIVE STRING!!!
        }
    }
}
