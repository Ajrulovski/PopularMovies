package ins.com.mk.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class DetailView extends ActionBarActivity implements DetailFragment.OnFragmentInteractionListener{
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

        ((TextView)v.findViewById(R.id.title)).setText(this.getTitle());
        this.getSupportActionBar().setCustomView(v);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            // get fragment manager
            FragmentManager fm = getFragmentManager();

            // add
            DetailFragment fragment = new DetailFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.movie_detail_container, fragment);

            // maybe I would add it with a tag later on smth like this
            // ft.add(R.id.your_placehodler, new YourFragment(), "detail");
            ft.commit();

        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
