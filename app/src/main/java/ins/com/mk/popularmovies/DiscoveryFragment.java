package ins.com.mk.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ins.com.mk.popularmovies.helper.GridViewAdapter;
import ins.com.mk.popularmovies.helper.MovieResult;
import ins.com.mk.popularmovies.helper.Utility;
import ins.com.mk.popularmovies.sync.MovieAPIAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiscoveryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiscoveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoveryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<JSONObject> metadataByItem = new ArrayList<JSONObject>();
    //ArrayList<String> allurls = new ArrayList<String>();

    ArrayList<MovieResult> list;
    MovieResult savedMovieResult;
    SharedPreferences prefs;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoveryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoveryFragment newInstance(String param1, String param2) {
        DiscoveryFragment fragment = new DiscoveryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DiscoveryFragment() {
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
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);
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
            Utility u = new Utility();

            try {
                u.getMovieDataFromJsonString(savedMovieResult.metadata);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            populateGridWithPosters(u.allurls,u.metadata);
            //populateGridWithPosters(savedMovieResult.poster,metadataByItem);
        }

        // handle gridview onItemClick
        GridView gv = (GridView) rootView.findViewById(R.id.gridView);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Discovery disc = (Discovery)getActivity();
                if(!disc.mTwoPane)
                {
                    Intent intent = new Intent(getActivity(), DetailView.class);

                    Bundle b = new Bundle();
                    b.putString("details", metadataByItem.get(position).toString()); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    startActivity(intent);
                }
                else
                {

                    Bundle arguments = new Bundle();
                    String p = metadataByItem.get(position).toString();

                    arguments.putString("param1", p);

                    DetailFragment fragment = new DetailFragment();
                    fragment.setArguments(arguments);


                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    //ft.remove()
                    ft.replace(R.id.movie_detail_container, fragment);

                    ft.commit();

                }

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    // run the async task
    private void startWebServiceTask(String sortCriteria) {
        if (isNetworkAvailable()) {
            MovieAPIAsyncTask webServiceTask = new MovieAPIAsyncTask();
            webServiceTask.execute(sortCriteria, this);
        }
        else
        {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No network connection. Please check your internet connection and try again.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void populateDetailViewDefault(ArrayList<JSONObject> metadata)
    {
        Discovery disc = (Discovery)getActivity();
        if(disc.mTwoPane){
            Bundle arguments = new Bundle();
            String p = metadata.get(0).toString();

            arguments.putString("param1", p);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);


        //Log.i("POPULATING_DETAIL",metadata.toString());
        // initialise the detail fragment


            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            //ft.remove()
            ft.replace(R.id.movie_detail_container, fragment);

            ft.commit();
        }
    }

    // bind the GridView with the adapter
    public void populateGridWithPosters(ArrayList<String> poster,ArrayList<JSONObject> metadata){
        // bind the gridview with the adapter
        GridView gv = (GridView) getActivity().findViewById(R.id.gridView);
        gv.setAdapter(new GridViewAdapter(getActivity(), poster));

        // fill the parcelable
        savedMovieResult.poster = poster;

        // serialize the arraylist of JSONObject and set it in the parcelable as a string
        String serializedList = metadata.toString();
        savedMovieResult.metadata = serializedList;

        // fill the arraylist variable with the metadata used for the gridview clicks
        metadataByItem = metadata;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_discovery, menu);
        //inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Sorted movies by popularity.", Toast.LENGTH_SHORT);
            toast.show();
            startWebServiceTask("popularity.desc");
            return true;
        }

        if (id == R.id.action_rated) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Sorted movies by rating.", Toast.LENGTH_SHORT);
            toast.show();
            startWebServiceTask("vote_average.desc");
            return true;
        }

        if (id == R.id.action_favs) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Showing favourite movies.", Toast.LENGTH_SHORT);
            toast.show();
            //startWebServiceTask("vote_average.desc");
            prefs = getActivity().getSharedPreferences("mk.com.ins.popularmovies", Context.MODE_PRIVATE);
            String movieJsonListString = "["+prefs.getString("ids", null)+"]";
            Utility u = new Utility();
            try {
                u.getMovieDataFromJsonString(movieJsonListString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            populateGridWithPosters(u.allurls,u.metadata);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(getActivity(), About.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        //public void onItemSelected(Uri dateUri);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
