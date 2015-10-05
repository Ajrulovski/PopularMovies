package ins.com.mk.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import ins.com.mk.popularmovies.helper.MovieResult;

public class Discovery extends ActionBarActivity implements DiscoveryFragment.OnFragmentInteractionListener, DetailFragment.OnFragmentInteractionListener{
    ArrayList<JSONObject> metadataByItem = new ArrayList<JSONObject>();
    //ArrayList<String> allurls = new ArrayList<String>();

    ArrayList<MovieResult> list;
    MovieResult savedMovieResult;
    SharedPreferences prefs;
    public static boolean mTwoPane;

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
        // check if we're using the app on phone or tablet
        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                FragmentManager fm = getFragmentManager();

                // add
                DetailFragment fragment = new DetailFragment();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.movie_detail_container, fragment);

                // maybe I would add it with a tag later on smth like this
                // ft.add(R.id.your_placehodler, new YourFragment(), "detail");
                ft.commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    //@Override
    //protected void onSaveInstanceState(Bundle outState) {
    //    outState.putParcelable("object", savedMovieResult);
    //    super.onSaveInstanceState(outState);
    //}

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
