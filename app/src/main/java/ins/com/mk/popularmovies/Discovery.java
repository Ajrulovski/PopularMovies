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

import org.json.JSONObject;

import java.util.ArrayList;

import ins.com.mk.popularmovies.helper.GridViewAdapter;
import ins.com.mk.popularmovies.sync.MovieAPIAsyncTask;

public class Discovery extends ActionBarActivity {
    ArrayList<JSONObject> metadataByItem = new ArrayList<JSONObject>();

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

        // fetch the movies info, by default show popularity
        startWebServiceTask("popularity.desc");

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

    // run the async task
    private void startWebServiceTask(String sortCriteria) {
        MovieAPIAsyncTask webServiceTask = new MovieAPIAsyncTask();
        webServiceTask.execute(sortCriteria, this);
    }

    // bind the GridView with the adapter
    public void populateGridWithPosters(ArrayList<String> poster,ArrayList<JSONObject> metadata){
        GridView gv = (GridView) findViewById(R.id.gridView);
        gv.setAdapter(new GridViewAdapter(this,poster));
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
