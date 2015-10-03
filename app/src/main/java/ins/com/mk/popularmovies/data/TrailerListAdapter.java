package ins.com.mk.popularmovies.data;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import ins.com.mk.popularmovies.R;

/**
 * Created by Gazmend on 10/2/2015.
 */
public class TrailerListAdapter extends ArrayAdapter<String> {
private final Activity context;
private final String[] trailers;

static class ViewHolder {
    public ImageView thumb;
    public TextView trailername;
}

    public TrailerListAdapter(Activity context, String[] trailers) {
        super(context, R.layout.review_list_item, trailers);
        this.context = context;
        this.trailers = trailers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.trailer_list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            //ImageView posterView = (ImageView) findViewById(R.id.poster);
            //Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w185" + row.getString("poster")).into(posterView);
            viewHolder.thumb = (ImageView) rowView.findViewById(R.id.thumb);
            viewHolder.trailername = (TextView) rowView.findViewById(R.id.trailername);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String JSONstring = trailers[position];
        try
        {
            JSONObject revJSON = new JSONObject(JSONstring);


            Picasso.with(context).load("http://img.youtube.com/vi/" + revJSON.getString("key") + "/0.jpg")
                    .placeholder(R.drawable.placeholder) //
                    .error(R.drawable.picasso_error)
                    .into(holder.thumb);
            //holder.textAuthor.setText(revJSON.getString("author"));
            holder.trailername.setText(revJSON.getString("name"));
            holder.thumb.setAdjustViewBounds(true);
        } catch (JSONException e) {
            Log.e("JSON_ERROR", e.getMessage(), e);
            e.printStackTrace();
        }

        return rowView;
    }
}
