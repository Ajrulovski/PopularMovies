package ins.com.mk.popularmovies.helper;

/**
 * Created by Gazmend on 7/22/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ins.com.mk.popularmovies.R;

public final class GridViewAdapter extends BaseAdapter {
    private final Context context;
    private List<String> urls = new ArrayList<String>();
    private LayoutInflater inflater;

    public GridViewAdapter(Context context, List<String> urlist) {
        this.context = context;
        urls = urlist;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        ImageView view = (ImageView) convertView;
        if (view == null) {
            view = new ImageView(context);
        }
        view.setAdjustViewBounds(true);

        // Get the image URL for the current position.
        String url = getItem(position);
        // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(context) //
                .load(url) //
                .placeholder(R.drawable.placeholder) //
                .error(R.drawable.picasso_error) //
                .into(view);

        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}
