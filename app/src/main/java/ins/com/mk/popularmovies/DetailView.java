package ins.com.mk.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ins.com.mk.popularmovies.data.ReviewListAdapter;
import ins.com.mk.popularmovies.data.TrailerListAdapter;
import ins.com.mk.popularmovies.helper.Utility;
import ins.com.mk.popularmovies.sync.ReviewAPIAsyncTask;
import ins.com.mk.popularmovies.sync.TrailerAPIAsyncTask;

public class DetailView extends ActionBarActivity {
    SharedPreferences prefs;
    //MenuItem favMenu;
    private Menu mainmenu;
    String movieid;
    String detailValues;
    String[] jsonTrailerValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        // some aesthetics, change the actionbar title font
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        //open the shared preferences for favs handling
        prefs = getSharedPreferences("mk.com.ins.popularmovies", Context.MODE_PRIVATE);

        ((TextView)v.findViewById(R.id.title)).setText(this.getTitle());
        this.getSupportActionBar().setCustomView(v);

        // get the item JSONified metadata
        Bundle b = getIntent().getExtras();
        detailValues = b.getString("details");

        // decode the JSON object and set the values for the views
        try {
            JSONObject row = new JSONObject(detailValues);

            movieid = row.getString("id");

            TextView tvTitle = (TextView) findViewById(R.id.title);
            tvTitle.setText(row.getString("title"));

            ImageView posterView = (ImageView) findViewById(R.id.poster);
            //new ImageLoaderAsyncTask(posterView).execute("http://image.tmdb.org/t/p/w185" + row.getString("poster"));
            Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w185" + row.getString("poster")).into(posterView);

            TextView tvPlot = (TextView) findViewById(R.id.plot);
            tvPlot.setText(row.getString("plot"));

            TextView tvRating = (TextView) findViewById(R.id.userrating);
            tvRating.setText("User rating: "+row.getString("rating"));

            TextView tvReleaseDate = (TextView) findViewById(R.id.releasedate);
            tvReleaseDate.setText(row.getString("releasedate"));

            // fetch the review and trailer data
            startReviewTask(movieid);
            startTrailerTask(movieid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView listView = (ListView) findViewById(R.id.listview_trailers);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),
                //        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                //        .show();

                // get the videokey
                //JSONObject row = resultsArray.getJSONObject(i);
                try {
                    JSONObject row = new JSONObject(jsonTrailerValues[position]);
                    String videokey = row.getString("key");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+videokey)));
                }
                catch (JSONException e) {
                    Log.e("JSON_ERROR", e.getMessage(), e);
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_view, menu);
        this.mainmenu = menu;

        //handle the icon drawable in the case that the movie is already faved
        if(isFaved(movieid)) {
            mainmenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.favourite_full));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(DetailView.this, About.class);
            startActivity(intent);
        }
        if (id == R.id.action_favourite) {
            //Toast toast = Toast.makeText(getApplicationContext(), "Added to favs.", Toast.LENGTH_SHORT);
            //toast.show();
            //handleMovieFavs(prefs, movieid);

            handleMovieFavs(prefs, detailValues);
        }

        return super.onOptionsItemSelected(item);
    }

    private void startReviewTask(String movieid) {
        if (isNetworkAvailable()) {
            ReviewAPIAsyncTask webServiceTask = new ReviewAPIAsyncTask();
            webServiceTask.execute(movieid, this);
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), "No network connection. Please check your internet connection and try again.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void startTrailerTask(String movieid) {
        if (isNetworkAvailable()) {
            TrailerAPIAsyncTask webServiceTask = new TrailerAPIAsyncTask();
            webServiceTask.execute(movieid, this);
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), "No network connection. Please check your internet connection and try again.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // bind the GridView with the adapter
    public void populateReviewList(ArrayList<JSONObject> metadata){
        //prepare the review layout
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.review_list_item, metadata);
        //String jsonValues = metadata.toString();
        String[] jsonValues = new String[metadata.size()];
        for (int i = 0; i < metadata.size(); i++) {
            jsonValues[i] = metadata.get(i).toString();
        }

        if (jsonValues.length > 0) {
            //JSONArray resultsArray = metadata.toString();
            ReviewListAdapter rla = new ReviewListAdapter(this, jsonValues);
            ListView lv = (ListView) findViewById(R.id.listview_reviews);
            lv.setAdapter(rla);

            // having a listview inside of a scrollview makes the listview show only one item
            // this function fixes that
            Utility.setListViewHeightBasedOnChildren(lv);
        }
        else
        {
            TextView reviewTitleText = (TextView) findViewById(R.id.reviews_title);
            reviewTitleText.setText(R.string.no_reviews_title);
        }
    }

    // bind the GridView with the adapter
    public void populateTrailerList(ArrayList<JSONObject> metadata){
        //prepare the review layout
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.review_list_item, metadata);
        //String jsonValues = metadata.toString();
        jsonTrailerValues = new String[metadata.size()];
        for (int i = 0; i < metadata.size(); i++) {
            jsonTrailerValues[i] = metadata.get(i).toString();
        }

        if (jsonTrailerValues.length > 0) {
            //JSONArray resultsArray = metadata.toString();
            TrailerListAdapter rla = new TrailerListAdapter(this, jsonTrailerValues);
            ListView lv = (ListView) findViewById(R.id.listview_trailers);
            lv.setAdapter(rla);

            // having a listview inside of a scrollview makes the listview show only one item
            // this function fixes that
            Utility.setTrailerListViewHeightBasedOnChildren(lv);
        }
        else
        {
            TextView trailerTitleText = (TextView) findViewById(R.id.trailers_title);
            trailerTitleText.setText(R.string.no_trailers_title);
        }
    }

    public boolean isFaved(String title){
        //first read the complete string with the favourited movie titles
        String idstring = prefs.getString("ids", null);

        boolean found;
        //check if there is any movie in the fav list already
        if(idstring != null){
            //there are some movies in the list, so check if there is such a movie in the list already
            found = idstring.indexOf(title) > 0;
        }
        else{
            found = false;
        }
        return found;
    }

    public String addMovieToFavs(String movieidlist, String movieid){
        String newlist = movieidlist+movieid+",";
        mainmenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.favourite_full));
        return newlist;
    }

    public String removeMovieFromFavs(String movieidlist, String movieid){
        String newlist = movieidlist.replace(movieid+",", "");
        mainmenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.favourite_empty));
        return newlist;
    }

    public void handleMovieFavs(SharedPreferences prefs, String movieid){
        //first read the complete string with the favourited movie titles
        String idstring = prefs.getString("ids", null);

        String newlist;
        //check if there is any movie in the fav list already
        if(idstring != null){
            //there are some movies in the list, so check if there is such a movie in the list already
            boolean found = idstring.indexOf(movieid) > 0;
            if (found)
            {
                // as the movie is in the list already this means that we need to pull it out of the list
                newlist = removeMovieFromFavs(idstring,movieid);
            }
            else{
                // add the movie to the list
                newlist = addMovieToFavs(idstring,movieid);
            }
        }
        else{
            // no movies found in the list, so add this one
            newlist = movieid+",";;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ids", newlist);
        editor.commit();

    }
}
