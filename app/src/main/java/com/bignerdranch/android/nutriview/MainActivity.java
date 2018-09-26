package com.bignerdranch.android.nutriview;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import Utility.UpdateType;
import Utility.Values;
import model.ClientModelRoot;
import model.Food;


// Right now our main class doesn't really do much regarding the fragment interaction listener
// But we could have it do a lot eventually!
// We can send messages back to the activity this way!
public class MainActivity extends AppCompatActivity implements Observer,
        SearchFragment.OnFragmentInteractionListener,
        FoodDetailFragment.OnFragmentInteractionListener,
        FoodVisualFragment.OnFragmentInteractionListener,
        BrowseFragment.OnFragmentInteractionListener,
        FavoritesFragment.OnFragmentInteractionListener,
        EditFoodDialog.OnFragmentInteractionListener {


    /* CONSTANTS */
    private static final String TAG = "MAIN_ACTIVITY";

    /* DATA MEMBERS */
    Fragment mSearchFragment;
    Fragment mBrowseFragment;
    Fragment mFavoritesFragment;
    Fragment mCurrentFragment;

    FoodDetailFragment mCurrentFoodDetail;

    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load some data in the model!
        // We might want a parent function for this setup stuff, if it starts to become verbose
        ClientModelRoot.SINGLETON.loadStapleFoods(getBaseContext());
        ClientModelRoot.SINGLETON.loadFavoriteFoods(getBaseContext());
        Values.SINGLETON.loadAPIKey(getBaseContext());

        // I want to disable the animations tho sooo....
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            Log.d(TAG, "Enabling custom toolbar!");
            // Enable overriding of the default actionbar
            ab.setDisplayShowCustomEnabled(true);
        }


        // Add ourselves as an observer to the client model root
        ClientModelRoot.SINGLETON.addObserver(this);


        // Lets do some fragment stuff!
        FragmentManager fm = getSupportFragmentManager();
        mBrowseFragment = BrowseFragment.newInstance();

        // Add the search fragment to start things off
        fm.beginTransaction()
                .add(R.id.fragment_container_main, mBrowseFragment)
                .commit();

        // Update the current main fragment
        mCurrentFragment = mBrowseFragment;

        initBottomNav();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart!");
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume!");
    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause!");
    }

    // good idea to save data to files in this method
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop!");

        Log.d(TAG, "Attempting to have CMR save favorites");
        // Save stuff in the client model root!
        ClientModelRoot.SINGLETON.saveFavorites(this.getBaseContext());
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume!");
    }

    // This callback is called only when there is a saved instance previously saved using
    // onSaveInstanceState(). We restore some state in onCreate() while we can optionally restore
    // other state here, possibly usable after onStart() has completed.
    // The savedInstanceState Bundle is same as the one used in onCreate().
    // TODO: acutally make these guys work correctly with saved instance states
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState!");
//        super.onRestoreInstanceState(savedInstanceState);
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState!");

        Log.d(TAG, "We're not calling the super method!!");
        // call superclass to save any view hierarchy
//        super.onSaveInstanceState(outState);
    }




    private void initBottomNav() {
        // Grab our cool bottom nav view!
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Set some of it's listeners and crap
        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Remove any cheeky favorite food views we might have
                        popFragmentBackStack();
                        switch(item.getItemId()) {
                            case R.id.action_browse:
                                // Aww snap, fragment stuff
                                if (mBrowseFragment == null) {
                                    Log.d(TAG, "Created new BrowseFragment Instance!");
                                    mBrowseFragment = BrowseFragment.newInstance();
                                }

                                if (mSearchFragment != null) {
                                    removeMainFragment(mSearchFragment);
                                }
                                if (mFavoritesFragment != null) {
                                    removeMainFragment(mFavoritesFragment);
                                }

                                mCurrentFragment = mBrowseFragment;
                                ((BrowseFragment)mBrowseFragment).resetAdapter();
                                ((BrowseFragment)mBrowseFragment).resetToolbar();

                                break;
                            case R.id.action_search:
                                if (mSearchFragment == null) {
                                    mSearchFragment = SearchFragment.newInstance();
                                }

                                // Remove favorites, if present
                                if (mFavoritesFragment != null) {
                                    removeMainFragment(mFavoritesFragment);
                                }

                                // Add the fragment?  Eh?
                                addMainFragment(mSearchFragment);

                                mCurrentFragment = mSearchFragment;

                                break;
                            case R.id.action_favorites:
                                if (mFavoritesFragment == null) {
                                    mFavoritesFragment = FavoritesFragment.newInstance();
                                }

                                // Remove search, if present
                                if (mSearchFragment != null) {
                                    removeMainFragment(mSearchFragment);
                                }

                                addMainFragment(mFavoritesFragment);

                                mCurrentFragment = mFavoritesFragment;

                                break;
                        }

                        return true;
                    }
                }
        );
    }

    private void popFragmentBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Pop all the extra stuff off the back stack?
        int backStackCount = fragmentManager.getBackStackEntryCount();
        if (backStackCount > 0) {
            for (int i = 0; i < backStackCount; i++) {
                fragmentManager.popBackStack();
            }
        }

        // Reassign current food detail fragment to be null
        mCurrentFoodDetail = null;
    }

    private void addMainFragment(Fragment fragment) {
        if (mCurrentFragment.equals(fragment)) return;

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.fragment_container_main, fragment)
                .commit();
    }

    private void removeMainFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .remove(fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        // Be a good boy
        super.onBackPressed();
    }



    /* INTERFACE METHODS */

    public void onSearchEntered() {
        // Hide the bottom nav?
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mBottomNavigationView.setEnabled(false);
    }

    public void onSearchExecuted() {
        // Show the bottom nav
        mBottomNavigationView.setVisibility(View.VISIBLE);
        mBottomNavigationView.setEnabled(true);
    }

    // This method is called when the up button is pressed on a food detail fragment
    // We'll remove it using the fragment manager?
    public void onFoodDetailUpPress(Fragment fragment) {
        if (mCurrentFragment instanceof FavoritesFragment) {
            ((FavoritesFragment)mFavoritesFragment).favoritesChanged();
        }

        Log.d(TAG, "Removing Fragment!");

        // Now remove the thing
        FragmentManager fm = getSupportFragmentManager();
        Log.d(TAG, "Back stack size: " + fm.getBackStackEntryCount());
        fm.beginTransaction()
                .remove(fragment)
                .commit();
        fm.popBackStack();

        Log.d(TAG, "Removed a fragment (I think...)");
        Log.d(TAG, "Back stack size: " + fm.getBackStackEntryCount());

        // I'm not sure if we'll always want to do that, but it's useful for now
    }

    public void onFoodDetailEditPress(Food food) {
        // Display the dialog
        EditFoodDialog dialog = EditFoodDialog.newInstance(food);

        dialog.show(getSupportFragmentManager(), "EditFoodDialog");
        dialog.setOldFood(food);
    }

    public void onDialogFoodEdited(String newFoodID) {
        // Ok...
        // We need to go through the fragment stack, if there
        // And update a food detail fragment's stuff?
        // Here we know for a fact that a food has been changed + added to the favorites
        // Let's get the food then!
        Food newFavorite = ClientModelRoot.SINGLETON.getFavoriteWithID(newFoodID);

        if (mCurrentFoodDetail.isFavorited()) {
            mCurrentFoodDetail.setFood(newFavorite);
            mCurrentFoodDetail.initWithFoodMember();
            mCurrentFoodDetail.updateViewPager();       // This might be really bad
        }

    }



    public void update(Observable o, Object arg) {
        // Check argument
        if (arg instanceof UpdateType) {
            UpdateType updateType = (UpdateType) arg;
            if (updateType != UpdateType.SelectedFoodChanged) return;
        }
        // Otherwise we're good to go


        // So this on is called whenever the notify observers is called in the client model root
        Log.d(TAG, "Observation made, update() executing");

        if (ClientModelRoot.SINGLETON.getSelectedFood() != null) {
            String foodID = ClientModelRoot.SINGLETON.getSelectedFood().getID();
            Fragment foodDetailFragment;

            if (ClientModelRoot.SINGLETON.containsFavorite(foodID)) {
                // Init from a blank foodID, and set the food manually
                foodDetailFragment = FoodDetailFragment.newInstance(ClientModelRoot.SINGLETON.getFavoriteWithID(foodID));
            }
            else {
                // Initialize from a foodID
                foodDetailFragment = FoodDetailFragment.newInstance(foodID);
            }

            // Then add the fragment manager to the stack?
            if (!isFinishing()) { // Holy crap this thing worked
                FragmentManager fm =  getSupportFragmentManager();
                fm.beginTransaction()
                        .add(R.id.fragment_container_main, foodDetailFragment)
                        // The following line allows us to implement back navigation?
                        .addToBackStack(null)
                        .commitAllowingStateLoss();     // This is a pretty hacky way to do it...

                // Assign this as our current Food detail fragment
                mCurrentFoodDetail = (FoodDetailFragment) foodDetailFragment;
            }

        }
    }

}
