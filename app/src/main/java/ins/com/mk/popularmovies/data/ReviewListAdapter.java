package ins.com.mk.popularmovies.data;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ins.com.mk.popularmovies.R;

/**
 * Created by Gazmend on 10/2/2015.
 */
public class ReviewListAdapter extends ArrayAdapter<String> {
private final Activity context;
private final String[] reviews;

static class ViewHolder {
    public TextView textAuthor;
    public TextView textReview;
}

    public ReviewListAdapter(Activity context, String[] reviews) {
        super(context, R.layout.review_list_item, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.review_list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textAuthor = (TextView) rowView.findViewById(R.id.Author);
            viewHolder.textReview = (TextView) rowView.findViewById(R.id.Comment);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String JSONstring = reviews[position];
        try
        {
            JSONObject revJSON = new JSONObject(JSONstring);
            holder.textAuthor.setText(revJSON.getString("author"));
            holder.textReview.setText(revJSON.getString("content"));
        } catch (JSONException e) {
            Log.e("JSON_ERROR", e.getMessage(), e);
            e.printStackTrace();
        }

        return rowView;
    }
}
