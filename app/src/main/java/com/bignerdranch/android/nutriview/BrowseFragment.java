package com.bignerdranch.android.nutriview;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Utility.Settings;
import model.ClientModelRoot;
import model.Food;
import server.NutrientReportRequest;
import server.NutrientReportResult;
import server.ServerProxy;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BrowseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BrowseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "BROWSE_FRAGMENT";



    // Here's where we'll define our nutrient report task!
    // On selection from the browse menu
    //      We start a task to get a report
    //      On the task finish:
    //          We set the action bar as up enabled
    //          We instantiate a category adapter from the result
    //          Update the recycler view's adapter

    private class BrowseCategoryTask extends AsyncTask<Object, Void, Object> {
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            taskStarted();
            startProgressDialog();
        }

        @Override
        protected Object doInBackground(Object... objects) {
            if (objects[0] == null) {
                Log.e(TAG, "Browse Category Task Parameter was null!");
                return null;    // Return null so onPostExecute knows what was up
            }

            // Grab our request
            NutrientReportRequest request = (NutrientReportRequest) objects[0];

            // Initialize a dummy result
            NutrientReportResult result = new NutrientReportResult();
            result.addError("Internal Server Error", "Unknown");

            try {
                result = ServerProxy.SINGLETON.nutrientReport(request);
            } catch (Exception e) {
                Log.e(TAG, "Exception thrown in BrowseCategoryTask doInBackground!!");
                Log.e(TAG, e.toString());
            }

            return result;
        }


        @Override
        protected void onPostExecute(Object o) {
            if (o == null) {
                Toast.makeText(getActivity().getBaseContext(), "Failure!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "BrowseCategoryTask PostExecute Fail!");

                // Make sure to clean up shop when we fail, too
                taskEnded();
                endProgressDialog();
                return;
            }

            NutrientReportResult reportResult = (NutrientReportResult) o;

            if (!reportResult.hasErrors()) {
                // Now we can update the data!
                // Grab the foods from the result!
                ArrayList<Food> foods = reportResult.getResultAsFoodArrayList();

                if (Settings.isShouldSortByName()) {
                    Log.d(TAG, "Sorting foods by name!");
                    Collections.sort(foods);
                }
                else if (Settings.getBrowseSortOrder() == Settings.BROWSE_ORDER_NUTRIENT_ASCENDING) {
                    Collections.reverse(foods);
                }

                ClientModelRoot.SINGLETON.addBrowseList(currentFoodGroupID, foods);

                handleSwitchToCategoryAdapter(foods);
            }

            taskEnded();
            endProgressDialog();

        }

        private void startProgressDialog() {
            Log.d(TAG, "Creating a Progress Dialog!");
            mProgressDialog = new ProgressDialog(getActivity(), R.style.AppCompatProgressDialogStyle);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Loading Foods. Please wait...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

        }

        private void endProgressDialog() {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }


        private void taskStarted() {
            isLoading = true;
        }

        private void taskEnded() {
            isLoading = false;
        }
    }



    /* DATA MEMBERS */
    private String[] mFoodGroupIconFiles;
    private String[] mFoodGroupNames;
    private String[] mFoodGroupIDs;
    private boolean isLoading = false;



    /* WIDGETS */
    private RecyclerView mBrowseRecyclerView;
    private BrowseAdapter mBrowseAdapter;
    private BrowseCategoryAdapter mBrowseCategoryAdapter;
    private Toolbar mMainToolbar;
    private Toolbar mPlainToolbar;
    private Toolbar mBacknavToolbar;
    private FrameLayout mBacknavButton;
    private SearchView mSearchView;

    private String currentFoodGroupID;

    private OnFragmentInteractionListener mListener;

    public BrowseFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment BrowseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance() {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "BrowseFragment onCreate!");

        // Initialize our data structures!
        mFoodGroupIconFiles = getActivity().getResources().getStringArray(R.array.food_group_icon_files);
        mFoodGroupNames = getActivity().getResources().getStringArray(R.array.food_group_names);
        mFoodGroupIDs = getActivity().getResources().getStringArray(R.array.food_group_ids);
        Log.d(TAG, "Loaded Arrays!");
        Log.d(TAG, "Sizes: " + mFoodGroupIconFiles.length + " " + mFoodGroupNames.length + " " + mFoodGroupIDs.length);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_browse, container, false);

        initRecyclerView(view);

        initToolbar(view);

        initSearchView();

        initBacknav();

        return view;
    }

    private void initSearchView() {
        if (mBacknavToolbar == null) return;

        mSearchView = (SearchView) mBacknavToolbar.findViewById(R.id.searchview_toolbar_backnav);

        if (mSearchView != null) {
            // Hook up our x button?
            ImageView closeButton = (ImageView)mSearchView.findViewById(R.id.search_close_btn);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Find EditText view
                    EditText et = (EditText) mSearchView.findViewById(R.id.search_src_text);

                    //Clear the text from EditText view
                    et.setText("");

                    //Clear query
                    mSearchView.setQuery("", false);
                    //Collapse the action view
                    mSearchView.onActionViewCollapsed();
                    //Collapse the search widget
                    mSearchView.setIconified(true);
                }
            });


            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    // idk what this is really doing rn, lol
                    // I could use this to make sure the bottom nav shows up again tho
                    // Actually, I don't even need to hide it in the first place, come to think of it
                    mSearchView.clearFocus();
                    return true;
                }
            });

            mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.d(TAG, "onFocusChange in searchView");
                    if (mBacknavToolbar == null) {
                        Log.d(TAG, "Backnav Toolbar was null on focus change");
                        return;
                    }
                    TextView title = (TextView) mBacknavToolbar.findViewById(R.id.label_toolbar_navback_title);

                    if (title == null) {
                        Log.d(TAG, "Title was null on search view focus change");
                    }

                    if (hasFocus) {
                        // Hide the title
                        title.setVisibility(View.INVISIBLE);
                    }
                    else {
                        // Show the title
                        title.setVisibility(View.VISIBLE);
                    }
                }
            });


            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchView.clearFocus();
                    return true;    // Handled
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (mBrowseCategoryAdapter != null) {
                        mBrowseCategoryAdapter.getFilter().filter(newText);
                        return true;
                    }
                    else {
                        return false;
                    }

                }
            });

            mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // Uhhhh... I can explain
                }
            });

            int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = (TextView) mSearchView.findViewById(id);
            if (textView != null) {
                textView.setTextColor(Color.WHITE);
            }


        }
    }


    /**
     * Initializes the two toolbars we'll use for this fragment.  the plain toolbar, and the navback toolbar
     * These toolbars will be instantiated here, and the navback will be hidden by default
     * @param v
     */
    private void initToolbar(View v) {
        Log.d(TAG, "Initializing Browse Fragment Toolbar!");
        mPlainToolbar = (Toolbar) v.findViewById(R.id.toolbar_plain_title);
        mBacknavToolbar = (Toolbar) v.findViewById(R.id.toolbar_plain_backnav);
        mBacknavToolbar.setVisibility(View.INVISIBLE);


        if (mPlainToolbar == null) Log.d(TAG, "Plain toolbar is null!");
        else Log.d(TAG, "Plain toolbar is good to go!");
        if (mBacknavToolbar == null) Log.d(TAG, "Backnav toolbar is null!");
        else Log.d(TAG, "Backnav toolbar is good to go!");

        // Set the main toolbar
        mMainToolbar = mPlainToolbar;
        // Enable in our activity?  Override it?
        ((AppCompatActivity)getActivity()).setSupportActionBar(mMainToolbar);
        Log.d(TAG, "Set our activity's support action bar!");

        // Now set some values in our toolbar
        // Set the text
        TextView titleText = (TextView)mPlainToolbar.findViewById(R.id.label_toolbar_plain_title);
        if (titleText != null) {
            titleText.setText("Browse");
            Log.d(TAG, "Set action bar title text!");
        }

    }

    /**
     * Sets up the image view on the backnav toolbar
     * Also sets up the click listener for this image view
     */
    private void initBacknav() {
        mBacknavButton = (FrameLayout) mBacknavToolbar.findViewById(R.id.btn_toolbar_navback);

        if (mBacknavButton != null) {
            mBacknavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // This is where we reset the adapter and swap the toolbars
                    resetAdapter();
                    if (mMainToolbar == mBacknavToolbar) {
                        swapToolbar();
                    }
                    resetSearchView();
                }
            });
        }

    }

    private void resetSearchView() {
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
        mSearchView.setIconified(true);
    }


    private void swapToolbar() {
        if (mMainToolbar.equals(mPlainToolbar)) {
            disableToolbar(mPlainToolbar);          // Disable the current
            enableToolbar(mBacknavToolbar);         // Enable the other
            mMainToolbar = mBacknavToolbar;         // Swap the current
            mBacknavButton.setEnabled(true);        // enable the backnav
        }
        else {
            disableToolbar(mBacknavToolbar);
            enableToolbar(mPlainToolbar);
            mMainToolbar = mPlainToolbar;
            mBacknavButton.setEnabled(false);
        }
        ((AppCompatActivity)getActivity()).setSupportActionBar(mMainToolbar);
    }

    private void disableToolbar(Toolbar tb) {
        tb.setVisibility(View.INVISIBLE);
        tb.setEnabled(false);
    }

    private void enableToolbar(Toolbar tb) {
        tb.setVisibility(View.VISIBLE);
        tb.setEnabled(true);
    }

    public void resetToolbar() {
        if (!mMainToolbar.equals(mPlainToolbar)) {
            disableToolbar(mBacknavToolbar);
            enableToolbar(mPlainToolbar);
            mMainToolbar = mPlainToolbar;
            mBacknavButton.setEnabled(false);
            ((AppCompatActivity)getActivity()).setSupportActionBar(mMainToolbar);
        }
    }




    private void initRecyclerView(View v) {
        mBrowseRecyclerView = (RecyclerView) v.findViewById(R.id.browse_fragment_recyclerview);
        mBrowseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        mBrowseAdapter = new BrowseAdapter();

        mBrowseRecyclerView.setAdapter(mBrowseAdapter);
    }




    // Custom Viewholder class!
    private class BrowseHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        /* DATA MEMBERS */
        private String iconFile;
        private String foodGroupName;
        private String foodGroupID;

        /* WIDGETS */
        private ImageView mFoodGroupIcon;
        private TextView mFoodGoupTitle;

        public BrowseHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mFoodGroupIcon = (ImageView) itemView.findViewById(R.id.image_view_food_group);
            mFoodGoupTitle = (TextView) itemView.findViewById(R.id.label_food_group_title);
        }


        public void bindFoodGroup(String iconFile, String foodGroupName, String foodGroupID) {
            this.iconFile = iconFile;
            this.foodGroupName = foodGroupName;
            this.foodGroupID = foodGroupID;

            // Magic drawable code
            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getBaseContext().getAssets().open(iconFile));
            } catch (IOException e) {
                // Crap
                Log.e(TAG, "Error thrown in initImageView!!");
                e.printStackTrace();
            }

            if (bitmap != null) {
                mFoodGroupIcon.setImageBitmap(bitmap);
            }

            mFoodGoupTitle.setText(foodGroupName);
        }

        // How to handle clicks?
        @Override
        public void onClick(View v) {
            if (isLoading) return;

            Log.d(TAG, "BrowseHolder clicked with data: " + this.foodGroupName + " " + this.foodGroupID);
            // Create a request!
            currentFoodGroupID = this.foodGroupID;
            ClientModelRoot.SINGLETON.setBrowseFoodGroupID(this.foodGroupID);   // Set the ID so requests can be made

            List<Food> categoryFoods = ClientModelRoot.SINGLETON.getBrowseList(foodGroupID);


            if (categoryFoods == null) {
                NutrientReportRequest request = ClientModelRoot.SINGLETON.getBrowseNutrientReportRequest();
                // Create a task and execute!
                new BrowseCategoryTask().execute(request);
            }
            else {
                handleSwitchToCategoryAdapter(categoryFoods);
            }

            // Update the navback toolbar's title
            TextView titleText = (TextView)mBacknavToolbar.findViewById(R.id.label_toolbar_navback_title);
            if (titleText != null) {
                titleText.setText(this.foodGroupName);
            }

            // Make sure the search thing is empty?
            if (mSearchView != null) {
                mSearchView.setIconified(true);
            }


        }
    }

    private void handleSwitchToCategoryAdapter(List<Food> foods) {
        if (mBrowseCategoryAdapter == null) {
            mBrowseCategoryAdapter = new BrowseCategoryAdapter();
        }

        mBrowseCategoryAdapter.setFoods(foods);
        mBrowseCategoryAdapter.notifyDataSetChanged();
        mBrowseRecyclerView.setAdapter(mBrowseCategoryAdapter);
        if (mMainToolbar != mBacknavToolbar) {
            swapToolbar();
        }
    }

    private class BrowseCategoryHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        /* DATA MEMBERS */
        private Food mFood;

        /* WIDGETS */
        // I think all we need is a text view for now
        TextView mFoodNameTextView;

        public BrowseCategoryHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            // Wire up our widgets
            mFoodNameTextView = (TextView) itemView.findViewById(R.id.label_plain_food_title);
        }

        public void bindFood(String foodID, String foodName) {
            // Hmmm...
            // Idk if we'll ever really use this lmao
        }

        public void bindFood(Food food) {
            mFood = food;
            mFoodNameTextView.setText(mFood.getName());

        }


        @Override
        public void onClick(View v) {
            // Do the same stuff as search
            if (mFood != null) {
                if (mSearchView != null) {
                    mSearchView.clearFocus();
                }
                ClientModelRoot.SINGLETON.setSelectedFood(mFood);   // Hopefully this will notify stuff!
            }
            else {
                Toast.makeText(getActivity().getBaseContext(), "This is no bueno!!", Toast.LENGTH_SHORT).show();
            }
        }
    }




    // Our own custom adapter class!
    private class BrowseAdapter extends RecyclerView.Adapter<BrowseHolder> {
        /* DATA MEMBERS */
        // Maybe none, because it's all held in the class

        @Override
        public BrowseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity().getBaseContext());
            View view = layoutInflater.inflate(R.layout.list_item_basic, parent, false);
            return new BrowseHolder(view);
        }

        @Override
        public void onBindViewHolder(BrowseHolder holder, int position) {
            holder.bindFoodGroup(mFoodGroupIconFiles[position],
                                 mFoodGroupNames[position],
                                 mFoodGroupIDs[position]);
        }

        @Override
        public int getItemCount() {
            return mFoodGroupNames.length;
        }

    }


    private class BrowseCategoryAdapter extends RecyclerView.Adapter<BrowseCategoryHolder> implements Filterable {
        private List<Food> mFoods;
        private List<Food> mWorkingFoods;
        private BrowseResultsFilter mBrowseFilter;

        @Override
        public BrowseCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity().getBaseContext());
            View view = layoutInflater.inflate(R.layout.list_item_plain, parent, false);
            return new BrowseCategoryHolder(view);
        }

        @Override
        public void onBindViewHolder(BrowseCategoryHolder holder, int position) {
            holder.bindFood(mWorkingFoods.get(position));
            if (position == mWorkingFoods.size() - 1) {
                holder.itemView.findViewById(R.id.list_item_plain_bottom_bar).setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mWorkingFoods.size();
        }

        public void setFoodsWithArray(Food[] foods) {
            // Clear the stuff
            mFoods = new ArrayList<>();

            // Add each of the passed foods!
            for (int i = 0; i < foods.length; i++) {
                mFoods.add(foods[i]);
            }

            mWorkingFoods = mFoods; // Be sure to copy what we like
        }

        public void setFoods(List<Food> foods) {
            mFoods = foods;
            mWorkingFoods = mFoods; // Start working with a fresh list
        }

        /* FILTRATION STUFF */
        @Override
        public Filter getFilter() {
            // return a filter that filters data based on a constraint

            if (mBrowseFilter == null) {
                mBrowseFilter = new BrowseResultsFilter();
            }

            return mBrowseFilter;
        }


        private class BrowseResultsFilter extends Filter {

            /**
             * Actually do the filtering itself!
             * @param constraint
             * @return
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Create a FilterResults object
                FilterResults results = new FilterResults();

                // If the constraint (search string/pattern) is null
                // or its length is 0, i.e., its empty then
                // we just set the `values` property to the
                // original contacts list which contains all of them
                if (constraint == null || constraint.length() == 0) {
                    results.values = mFoods;
                    results.count = mFoods.size();
                }
                else {
                    // Time to actually filter the stuff!
                    ArrayList<Food> filteredFoods = new ArrayList<>();

                    for (Food f : mFoods) {
                        if (f.getName().toLowerCase().contains(constraint)) {
                            filteredFoods.add(f);
                        }
                    }

                    results.values = filteredFoods;
                    results.count = filteredFoods.size();
                }


                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mWorkingFoods = (List<Food>) results.values;    // Update our list of working foods
                notifyDataSetChanged();                         // Tell the adapter what's up
            }
        }


    }

    public void resetAdapter() {
        if (mBrowseRecyclerView != null && mBrowseAdapter != null) {
            mBrowseRecyclerView.setAdapter(mBrowseAdapter); // Reset the adapter
        }
        resetSearchView();
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
//        public void onBrowseUpPress(Fragment fragment);
    }
}
