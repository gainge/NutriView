package com.bignerdranch.android.nutriview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.andreilisun.swipedismissdialog.SwipeDismissDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utility.Settings;
import Utility.Values;
import model.ClientModelRoot;
import model.Food;
import server.SearchRequest;
import server.SearchResult;
import server.ServerProxy;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "SEARCH_FRAGMENT";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean requeryAttempted;

    EditText mSearchField;
    RecyclerView mSearchResultsRecyclerView;
    SearchAdapter mSearchAdapter;
    ImageView mSettingsImageView;

    private ProgressBar mProgressBar;
    private TextView mNoResults;

    Toolbar mSearchToolbar;

    private OnFragmentInteractionListener mListener;


    private class SearchTask extends AsyncTask<Object, Void, Object> {
        private boolean emptyResults = false;

        @Override
        protected void onPreExecute() {
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if (mNoResults != null) {
                mNoResults.setVisibility(View.INVISIBLE);
            }
        }

        // this is the method that actually interacts with the server!
        @Override
        protected Object doInBackground(Object... objects) {
            Log.d(TAG, "Starting search do in background!");
            if (objects[0] == null) {
                Log.e(TAG, "Search Task Parameter was null!");
                return null;    // Return null so onPostExecute knows what was up
            }

            // Retrieve the request
            SearchRequest searchRequest = (SearchRequest) objects[0];

            // Initialize a dummy result
            SearchResult searchResult = new SearchResult();
            searchResult.addError("Internal Server Error", "Unknown");

            try {
                searchResult = ServerProxy.SINGLETON.search(searchRequest);

            } catch (Exception e) {
                Log.e(TAG, "Exception thrown in SearchTask doInBackground!!");
                Log.e(TAG, e.toString());


                if (e instanceof java.lang.IllegalStateException) {
                    emptyResults = true;
                }

                SearchResult temp = new SearchResult();
                temp.addError("Empty List!", "rip");
                return temp;
            }

            return searchResult;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.d(TAG, "SearchTask onPostExecute!");
            if (o == null) {
                Toast.makeText(getActivity().getBaseContext(), "Failure!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "SearchTask PostExecute Fail!");

                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
                if (mNoResults != null) {
                    mNoResults.setVisibility(View.INVISIBLE);
                }
                return;
            }

//            if (o instanceof String) {
//                // This is when the results were empty!
//                if (requeryAttempted && emptyResults) {
//                    if (mNoResults != null) {
//                        mNoResults.setVisibility(View.VISIBLE);
//                    }
//
//                    emptyResults = false;
//
//                    return;
//                }
//            }

            SearchResult searchResult = (SearchResult) o;

//            Log.d(TAG, "Printing Search Result");
//            Log.d(TAG, searchResult.toString());

            // Then do some stuff with the search result, I'm assuming
            // Probably will have to notify our class to do a fragment thing

            if (!searchResult.hasErrors() || requeryAttempted) {
                // Set the results in our model?
                ArrayList<Food> foods = searchResult.getResultAsFoods();
                ClientModelRoot.SINGLETON.addSearchResults(foods);

                // Check if the results were puny enough to requery
                boolean shouldRequery = foods.size() <= Settings.getMaxSearchResultsForRequery();

                if (!requeryAttempted && shouldRequery) {
                    Log.d(TAG, "Attempting Requery with string: " + mSearchField.getText().toString());
                    requeryAttempted = true;
                    SearchRequest request = new SearchRequest(mSearchField.getText().toString());
                    request.setDataSource(Values.SOURCE_BRANDED);
                    request.setMaxItems(Settings.getSearchMaxItems());

                    new SearchTask().execute(request);
                }
                else {
                    // Then, do we have to notify the recycler view or something, I guess?
                    List<Food> filteredSearchResults = ClientModelRoot.SINGLETON.getFilteredSearchResults();

                    if (filteredSearchResults.size() > 0) {
                        mSearchAdapter = new SearchAdapter(ClientModelRoot.SINGLETON.getFilteredSearchResults());
                        mSearchResultsRecyclerView.setAdapter(mSearchAdapter);
                        if (mNoResults != null) {
                            mNoResults.setVisibility(View.INVISIBLE);
                        }
                    }
                    else {
                        Log.d(TAG, "Search results were empty!!");
                        if (mNoResults != null) {
                            mNoResults.setVisibility(View.VISIBLE);
                        }
                    }

                    mListener.onSearchExecuted(); // Let the listener know what's up
                }

            }
            else {
                // This means the result was empty

                if (!requeryAttempted) {
                    requeryAttempted = true;
                    SearchRequest request = new SearchRequest(mSearchField.getText().toString());
                    request.setDataSource(Values.SOURCE_BRANDED);
                    request.setMaxItems(Settings.getSearchMaxItems());

                    new SearchTask().execute(request);
                }
                else {
                    if (mNoResults != null) {
                        mNoResults.setVisibility(View.VISIBLE);
                    }
                }
            }

            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public SearchFragment() {
        // Required empty public constructor
        requeryAttempted = false;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Have to do something here with an options menu?
        // Or like, specifying the custom toolbar?
        // Hmmmm... I'll have to read up on this
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

//        initBottomNav(view);

        initRecyclerView(view);

        initSearchField(view);

        initToolbar(view);

        initSettingsIcon(view);

        initProgressBar(view);

        initNoResults(view);

        List<Food> filteredSearchResults = ClientModelRoot.SINGLETON.getFilteredSearchResults();

        if (filteredSearchResults.size() > 0) {
            mSearchAdapter = new SearchAdapter(ClientModelRoot.SINGLETON.getFilteredSearchResults());
            if (mSearchResultsRecyclerView != null) {
                mSearchResultsRecyclerView.setAdapter(mSearchAdapter);
            }
        }


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSearchAdapter != null) {
            mSearchAdapter.notifyDataSetChanged();
        }
    }

    private void initNoResults(View v) {
        mNoResults = (TextView) v.findViewById(R.id.label_no_results);

        if (mNoResults != null) {
            mNoResults.setVisibility(View.INVISIBLE);
        }
    }


    private void initProgressBar(View v) {
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar_browse);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void initSettingsIcon(View v) {
        mSettingsImageView = (ImageView) v.findViewById(R.id.action_settings_home);

        mSettingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Let's make another cool swipable view!
                makeIntroDialog();
            }
        });
    }

    private void makeIntroDialog() {
        View dialog = LayoutInflater.from(getActivity().getBaseContext())
                .inflate(R.layout.dialog_serving_size, null);

        SwipeDismissDialog swipeDismissDialog = new SwipeDismissDialog.Builder(getActivity())
                .setFlingVelocity(0.06f)
                .setView(dialog)
                .build()
                .show();

        ImageView servingSizes = (ImageView) dialog.findViewById(R.id.image_serving_size);

        Bitmap bitmap = null;
        String path = getActivity().getBaseContext().getString(R.string.dialog_info_blurb_image_path);

        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getBaseContext().getAssets().open(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            servingSizes.setImageBitmap(bitmap);
        }
    }



    private void initRecyclerView(View v) {
        mSearchResultsRecyclerView = (RecyclerView) v.findViewById(R.id.search_results_recyclerview);
        mSearchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
    }

    private void initSearchField(View v) {
        mSearchField = (EditText) v.findViewById(R.id.edit_text_search);


        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Probably intentionally left blank
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Definitely fill this out!
            }
        });

        mSearchField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mListener.onSearchEntered();
                }
                else {
//                    mListener.onSearchExecuted();
                }
            }
        });

        // Override action stuff?
        mSearchField.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        mSearchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    ClientModelRoot.SINGLETON.clearSearchResults(); // Start from a blank slate
                    requeryAttempted = false;
                    // I'm not sure if this will work, honestly
                    SearchRequest searchRequest = new SearchRequest(mSearchField.getText().toString());
                    // Then try to do the actual querying part
                    new SearchTask().execute(searchRequest);

                    /* CODE BORROWED FROM ONLINE */
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(mSearchField
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    /* END CUT-AND-PASTED CODE */

                    mListener.onSearchExecuted();

                    return true;
                }
                else if (keyCode == KeyEvent.KEYCODE_BACK) { // This doesn't work right now!!
                    // IN order for this to do the correct thing, we
                    mListener.onSearchExecuted();
                    return true;
                }
                else {
                    return false;
                }
            }
        });

    }

    private void initToolbar(View v) {
        mSearchToolbar = (Toolbar) v.findViewById(R.id.search_toolbar);
        if (mSearchToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mSearchToolbar);
        }

//        final ActionBar ab = getSupportActionBar();
//        ab.setDisplayShowHomeEnabled(false);    // Show or hide the default home button
//        ab.setDisplayHomeAsUpEnabled(false);
//        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
//        ab.setDisplayShowTitleEnabled(false); // disable the default title element
    }



    // Private view holder class for our recycler view
    private class SearchHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        /* DATA MEMBERS */
        private Food mFood;

        /* WIDGETS */
        TextView mMainText;
        // Visual components that we'll need to give values



        public SearchHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mMainText = (TextView) itemView.findViewById(R.id.label_plain_food_title);

            this.mFood = null;
        }

        public void bindFood(Food food) {
            mFood = food;
            mMainText.setText(mFood.getName());
        }

        public void onClick(View v) {
            // We need to start a new activity, eventually
            // But for now we can just diplay a toast
            if (mFood != null) {
//                Toast.makeText(getBaseContext(), mFood.getName(), Toast.LENGTH_SHORT).show();
                ClientModelRoot.SINGLETON.setSelectedFood(mFood);   // Hopefully this will notify stuff!
//                Intent intent = FoodDetailActivity.newIntent(getBaseContext(), mFood.getID());
//                startActivity(intent);
            }
            else {
                Toast.makeText(getActivity().getBaseContext(), "This is no bueno!!", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {
        private ArrayList<Food> mFoods;

        public SearchAdapter(ArrayList<Food> foods) {
            mFoods = foods;
        }

        @Override
        public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity().getBaseContext());
            View view = layoutInflater.inflate(R.layout.list_item_plain, parent, false);
            return new SearchHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchHolder holder, int position) {
            holder.bindFood(mFoods.get(position));
        }

        @Override
        public int getItemCount() {
            return mFoods.size();
        }
    }





    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);

        // So this is where we can add our methods with things that we want to send back to the activity?
        // Hmmm... Cool
        // Let's leave this blank for now, and make the main activity implement it
        // And maybe mess around with it later
        public void onSearchEntered();

        public void onSearchExecuted();
    }
}
