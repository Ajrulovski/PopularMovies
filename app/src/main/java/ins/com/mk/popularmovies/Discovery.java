package ins.com.mk.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ins.com.mk.popularmovies.helper.GridViewAdapter;
import ins.com.mk.popularmovies.helper.MovieResult;
import ins.com.mk.popularmovies.sync.MovieAPIAsyncTask;

public class Discovery extends ActionBarActivity {
    ArrayList<JSONObject> metadataByItem = new ArrayList<JSONObject>();
    //ArrayList<String> allurls = new ArrayList<String>();

    ArrayList<MovieResult> list;
    MovieResult savedMovieResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        // some aesthetics, change the actionbar title font
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);

        ((TextView)v.findViewById(R.id.title)).setText(this.getTitle());
        this.getSupportActionBar().setCustomView(v);

        //check if we have already fetched the data from the query and we are not just rotating the view
        if(savedInstanceState == null || !savedInstanceState.containsKey("object")) {
            // fetch the movies info, by default show popularity
            savedMovieResult = new MovieResult(null,null);
            startWebServiceTask("popularity.desc");

        }
        else {
            // if we have an instance state then read that and not call the API
            // read the data from parcelable
            savedMovieResult = savedInstanceState.getParcelable("object");

            try {
                // deserialize the string from the parcelable into an JSONArray
                // and then loop through the array to fill an arraylist of JSONObjects
                // as we need that arraylist to send an item of it towards the detailView activity
                JSONArray jsonActList = new JSONArray(savedMovieResult.metadata);
                metadataByItem.clear();
                for (int i = 0; i < jsonActList.length(); i++) {
                    JSONObject temp = jsonActList.getJSONObject(i);
                    metadataByItem.add(temp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            populateGridWithPosters(savedMovieResult.poster,metadataByItem);
        }

        // handle gridview onItemClick
        GridView gv = (GridView) findViewById(R.id.gridView);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(Discovery.this, DetailView.class);
                Bundle b = new Bundle();
                b.putString("details", metadataByItem.get(position).toString()); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("object", savedMovieResult);
        super.onSaveInstanceState(outState);
    }

    // run the async task
    private void startWebServiceTask(String sortCriteria) {
        MovieAPIAsyncTask webServiceTask = new MovieAPIAsyncTask();
        webServiceTask.execute(sortCriteria, this);
    }

    // bind the GridView with the adapter
    public void populateGridWithPosters(ArrayList<String> poster,ArrayList<JSONObject> metadata){
        // bind the gridview with the adapter
        GridView gv = (GridView) findViewById(R.id.gridView);
        gv.setAdapter(new GridViewAdapter(this, poster));

        // fill the parcelable
        savedMovieResult.poster = poster;

        // serialize the arraylist of JSONObject and set it in the parcelable as a string
        String serializedList = metadata.toString();
        savedMovieResult.metadata = serializedList;

        // fill the arraylist variable with the metadata used for the gridview clicks
        metadataByItem = metadata;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_discovery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            Toast toast = Toast.makeText(getApplicationContext(), "Sorted movies by popularity.", Toast.LENGTH_SHORT);
            toast.show();
            startWebServiceTask("popularity.desc");
            return true;
        }

        if (id == R.id.action_rated) {
            Toast toast = Toast.makeText(getApplicationContext(), "Sorted movies by rating.", Toast.LENGTH_SHORT);
            toast.show();
            startWebServiceTask("vote_average.desc");
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(Discovery.this, About.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
