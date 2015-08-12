package ins.com.mk.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ins.com.mk.popularmovies.sync.ImageLoaderAsyncTask;

public class DetailView extends ActionBarActivity {

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

        // get the item JSONified metadata
        Bundle b = getIntent().getExtras();
        String detailValues = b.getString("details");

        // decode the JSON object and set the values for the views
        try {
            JSONObject row = new JSONObject(detailValues);

            TextView tvTitle = (TextView) findViewById(R.id.title);
            tvTitle.setText(row.getString("title"));

            ImageView posterView = (ImageView) findViewById(R.id.poster);
            new ImageLoaderAsyncTask(posterView).execute("http://image.tmdb.org/t/p/w185" + row.getString("poster"));

            TextView tvPlot = (TextView) findViewById(R.id.plot);
            tvPlot.setText(row.getString("plot"));

            TextView tvRating = (TextView) findViewById(R.id.userrating);
            tvRating.setText("User rating: "+row.getString("rating"));

            TextView tvReleaseDate = (TextView) findViewById(R.id.releasedate);
            tvReleaseDate.setText(row.getString("releasedate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_view, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
