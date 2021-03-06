package ins.com.mk.popularmovies.sync;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ins.com.mk.popularmovies.DetailFragment;

/**
 * Created by Gazmend on 7/21/2015.
 */

public class TrailerAPIAsyncTask extends AsyncTask<Object, Boolean, String> {

    DetailFragment callerActivity;
    public final String LOG_TAG = TrailerAPIAsyncTask.class.getSimpleName();
    ArrayList<JSONObject> metadata = new ArrayList<JSONObject>();

    @Override
    protected String doInBackground(Object... params) {
        String id = (String) params[0];
        callerActivity = (DetailFragment) params[1];
        String apikey = "API_KEY";

        String res = null;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;



        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            // Construct the URL to get the most popular movies from themoviedb
            // http://api.themoviedb.org/3/movie/id/reviews

            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL+id+"/videos").buildUpon()
                    //.appendQueryParameter(SORT_PARAM, sortby)
                    .appendQueryParameter(APIKEY_PARAM, apikey)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return "";
            }
            moviesJsonStr = buffer.toString();
            res = getTrailerDataFromJson(moviesJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            return "";
        }
    }

    private String getTrailerDataFromJson(String movieJsonStr)
            throws JSONException {

        // Now we have a String representing the complete most popular movies in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        // Location information
        //String full_result = null;

        // Dude, maybe you should move these to a separate file named constants, it would be easier to configure if needed
        final String OMD_KEY = "key";
        final String OMD_NAME = "name";

        final String OMD_RESULTS = "results";

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray resultsArray = movieJson.getJSONArray(OMD_RESULTS);

            String videokey;
            String trailername;

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject row = resultsArray.getJSONObject(i);
                videokey = row.getString(OMD_KEY);
                trailername = row.getString(OMD_NAME);

                //generate the metadata JSON object
                JSONObject movieObject = new JSONObject();
                movieObject.put("key", videokey);
                movieObject.put("name", trailername);

                metadata.add(i, movieObject);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            // *checkpoint* DUDE, FIX THIS TO RETURN SOME INDICATIVE STRING!!!
            return "";
        }
    }


    @Override
    protected void onPostExecute(String response) {
        try
        {
            callerActivity.populateTrailerList(metadata);
        }
        catch(Exception e)
        {
            Log.d("Error: ", " " + e.getMessage());
        }
        super.onPostExecute(response);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }



}
