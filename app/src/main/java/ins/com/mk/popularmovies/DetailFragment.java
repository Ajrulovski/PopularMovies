package ins.com.mk.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    SharedPreferences prefs;
    //MenuItem favMenu;
    private Menu mainmenu;
    String movieid;
    String detailValues;
    String[] jsonTrailerValues;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //Bundle arguments = getArguments();
        //if (arguments != null) {
        //    movieValues = arguments.getParcelable(DetailFragment.DETAIL_URI);
        //}



        //open the shared preferences for favs handling
        prefs = getActivity().getSharedPreferences("mk.com.ins.popularmovies", Context.MODE_PRIVATE);

        Discovery dd = new Discovery();
        if(dd.mTwoPane)
        {
            //Log.i("PARAM!",mParam1);
            Bundle arguments = getArguments();
            if (arguments != null) {
                //mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
                detailValues = arguments.getString("param1");
            }
        }
        else {
            Bundle b = getActivity().getIntent().getExtras();
            if (b != null) {
                detailValues = b.getString("details");
            }
        }

             if(detailValues!=null)
             {
                // decode the JSON object and set the values for the views
                try {
                    JSONObject row = new JSONObject(detailValues);
                    Log.i("JSONSTRING",row.getString("id") );
                    movieid = row.getString("id");

                    TextView tvTitle = (TextView) rootView.findViewById(R.id.title);
                    tvTitle.setText(row.getString("title"));

                    ImageView posterView = (ImageView) rootView.findViewById(R.id.poster);

                    //new ImageLoaderAsyncTask(posterView).execute("http://image.tmdb.org/t/p/w185" + row.getString("poster"));
                    Picasso.with(getActivity().getApplicationContext()).load("http://image.tmdb.org/t/p/w185" + row.getString("poster")).into(posterView);

                    TextView tvPlot = (TextView) rootView.findViewById(R.id.plot);
                    tvPlot.setText(row.getString("plot"));

                    TextView tvRating = (TextView) rootView.findViewById(R.id.userrating);
                    tvRating.setText("User rating: " + row.getString("rating"));

                    TextView tvReleaseDate = (TextView) rootView.findViewById(R.id.releasedate);
                    tvReleaseDate.setText(row.getString("releasedate"));

                    // fetch the review and trailer data
                    startReviewTask(movieid);
                    startTrailerTask(movieid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ListView listView = (ListView) rootView.findViewById(R.id.listview_trailers);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            JSONObject row = new JSONObject(jsonTrailerValues[position]);
                            String videokey = row.getString("key");
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videokey)));
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", e.getMessage(), e);
                            e.printStackTrace();
                        }

                    }
                });
             }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_view, menu);
        this.mainmenu = menu;

        //handle the icon drawable in the case that the movie is already faved
        if(isFaved(movieid)) {
            mainmenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.favourite_full));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(getActivity(), About.class);
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
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No network connection. Please check your internet connection and try again.", Toast.LENGTH_SHORT);
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
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No network connection. Please check your internet connection and try again.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            ReviewListAdapter rla = new ReviewListAdapter(getActivity(), jsonValues);
            ListView lv = (ListView) getActivity().findViewById(R.id.listview_reviews);
            lv.setAdapter(rla);

            // having a listview inside of a scrollview makes the listview show only one item
            // this function fixes that
            Utility.setListViewHeightBasedOnChildren(lv);
        }
        else
        {
            TextView reviewTitleText = (TextView) getActivity().findViewById(R.id.reviews_title);
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
            TrailerListAdapter rla = new TrailerListAdapter(getActivity(), jsonTrailerValues);
            ListView lv = (ListView) getActivity().findViewById(R.id.listview_trailers);
            lv.setAdapter(rla);

            // having a listview inside of a scrollview makes the listview show only one item
            // this function fixes that
            Utility.setTrailerListViewHeightBasedOnChildren(lv);
        }
        else
        {
            TextView trailerTitleText = (TextView) getActivity().findViewById(R.id.trailers_title);
            trailerTitleText.setText(R.string.no_trailers_title);
        }
    }

    public boolean isFaved(String title){
        //first read the complete string with the favourited movie titles
        boolean found = false;

        if(title != null) {
            String idstring = prefs.getString("ids", null);
            //check if there is any movie in the fav list already
            if (idstring != null) {
                //there are some movies in the list, so check if there is such a movie in the list already
                found = idstring.indexOf(title) > 0;
            } else {
                found = false;
            }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
