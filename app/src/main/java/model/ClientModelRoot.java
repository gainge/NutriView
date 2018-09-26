package model;

/**
 * Created by Gibson on 10/6/17.
 */


import android.content.Context;
import android.util.Log;

import com.bignerdranch.android.nutriview.MainActivity;
import com.bignerdranch.android.nutriview.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import Utility.Settings;
import Utility.UpdateType;
import model.*;
import server.NutrientReportRequest;

public class ClientModelRoot extends Observable {

    // Singleton Party!
    public static final ClientModelRoot SINGLETON = new ClientModelRoot();

    /* CONSTANTS */
    private static final String TAG = "CLIENT_MODEL_ROOT";
    private static final int NUM_STAPLES = 5;



    /* DATA MEMBERS */
    private ArrayList<Food> searchResults;
    private ArrayList<ArrayList<Food>> mStapleFoods;
    private ArrayList<Food> mFavoriteFoods;
    private Map<String, List<Food>> mBrowseListMap;
    private Food selectedFood;

    private String browseFoodGroupID;

    private ArrayList<String> mStapleFoodIDs;
    private ArrayList<String> mStapleFoodPaths;


    // We should also have some constant stuff for reference
    //      Like the list of food groups, the reference foods
    //      As well as the image asset ids for all of these things

    public ClientModelRoot() {
        // Default Constructor
        this.searchResults = new ArrayList<>();
        mStapleFoods = new ArrayList<>();
        mFavoriteFoods = new ArrayList<>();
        mStapleFoodIDs = new ArrayList<>();
        mStapleFoodPaths = new ArrayList<>();
        mBrowseListMap = new HashMap<>();
    }

    public NutrientReportRequest getBrowseNutrientReportRequest() {
        // Returns a nutrient report request object with the given settings
        NutrientReportRequest request = new NutrientReportRequest();

        request.setFoodGroup(browseFoodGroupID);
        request.setSubSet(Settings.getBrowseDataSource());
        request.setMaxItems(Settings.getBrowseMaxItems());
        request.addNutrient(Settings.getBrowseNutrientValue());

        return request;
    }

    /**
     * Loads the staple food nutritional info, as well as the staple food resources
     * @param context
     * @return
     */
    public boolean loadStapleFoods(Context context) {
        boolean wasSuccess = true;

        wasSuccess = wasSuccess & loadStapleData(context);

        wasSuccess = wasSuccess & loadStapleResources(context);

        return wasSuccess;
    }

    public boolean loadFavoriteFoods(Context context) {
        boolean wasSuccess = true;

        Gson gson = new Gson();

        String path = context.getResources().getString(R.string.favorites_path);

        try {
            File file = new File(context.getFilesDir(), path);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileInputStream fis = context.openFileInput("favorites.json");
            InputStreamReader isr = new InputStreamReader(fis);

//            Reader reader = new InputStreamReader(context.getAssets().open(path));
            Reader reader = isr;

            mFavoriteFoods = gson.fromJson(reader, new TypeToken<ArrayList<Food>>() {}.getType());
            Log.d(TAG, "Loaded file \'" + path + "\' successfully!");

            if (mFavoriteFoods == null) {
                mFavoriteFoods = new ArrayList<>();
            }

        } catch (IOException e) {
            Log.d(TAG, "IOException Thrown in ClientModelRoot loadFavoriteFoods!");
            e.printStackTrace();
            wasSuccess = false;
        }

        if (wasSuccess) {
            Log.d(TAG, "Loaded [" + mFavoriteFoods.size() + "] foods successfully!");
        }

        if (mFavoriteFoods.size() == 0) {
            try {
                Reader backup_reader = new InputStreamReader(context.getAssets().open(path));
                mFavoriteFoods = gson.fromJson(backup_reader, new TypeToken<ArrayList<Food>>() {}.getType());
                Log.d(TAG, "Loaded some files from backup sucessfully!");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "IOException Thrown in ClientModelRoot loadFavoriteFoods!");
                wasSuccess = false;
            }


        }


        return wasSuccess;
    }

    private boolean loadStapleData(Context context) {
        boolean wasSuccess = true;

        String[] paths = context.getResources().getStringArray(R.array.staple_paths);

        if (paths.length != NUM_STAPLES) return false;

        Gson gson = new Gson();

        for (int i = 0; i < paths.length; i++) {
            // Define an array list to work with!
            ArrayList<Food> currentStapleList;

            File currentFile = context.getFileStreamPath(paths[i]);

            try {
                // Define a reader to use!
                Reader reader = new InputStreamReader(context.getAssets().open(paths[i]));

                // Do some Gson magic
                currentStapleList = gson.fromJson(reader, new TypeToken<ArrayList<Food>>() {}.getType());
                Log.d(TAG, "Loaded file \'" + paths[i] + "\' successfully!");

                // Add it to the overall list!
                mStapleFoods.add(currentStapleList);
            } catch (IOException e) {
                Log.e(TAG, "IOException in Load Staple Foods!");
                e.printStackTrace();
                wasSuccess = false;
            }

        }

        if (wasSuccess) {
            Log.d(TAG, "Loaded [" + mStapleFoods.size() + "] staples!");
            StringBuilder str = new StringBuilder();
            for (ArrayList<Food> list : mStapleFoods) {
                str.append(list.size() + " ");
            }
            Log.d(TAG, "Sizes of Staple Lists:\n\t" + str.toString());
        }

        return wasSuccess;
    }

    private boolean loadStapleResources(Context context) {
        boolean wasSuccess = true;

        // Initialize the IDs
        String[] ids = context.getResources().getStringArray(R.array.staple_food_ids);

        for (int i = 0; i < ids.length; i++) {
            mStapleFoodIDs.add(ids[i]);
        }

        // Initialize the file paths
        String[] paths = context.getResources().getStringArray(R.array.staple_food_images);

        for (int i = 0; i < paths.length; i++) {
            mStapleFoodPaths.add(paths[i]);
        }

        return wasSuccess;
    }

    public String getPathForFoodID(String foodID) {
        int idIndex = mStapleFoodIDs.indexOf(foodID);

        if (idIndex < 0 || idIndex >= mStapleFoodPaths.size()) {
            return "blueEfron.jpg"; // Crap this is bad
        }

        return mStapleFoodPaths.get(idIndex);
    }

    public ArrayList<Food> getFavoriteFoods() {
        return mFavoriteFoods;
    }

    public Food getFavoriteWithID(String foodID) {
        int index = favoritesContainsFoodWithID(foodID);

        if (index == -1) return null;
        return mFavoriteFoods.get(index);
    }

    public void addFavoriteFood(Food newFav) {
        int foodIndex = favoritesContainsFoodWithID(newFav.getID());
        if (foodIndex >= 0) {
            // Just update the food's information
            updateFavoriteAtIndex(foodIndex, newFav);
        }
        else {
            // Just add it normally
            mFavoriteFoods.add(newFav);
            // Notify a food detail fragment what's up
//            setChanged();
//            notifyObservers(newFav);
        }
    }

    public void removeFavorite(Food badFood) {
        removeFavorite(badFood.getID());
    }


    public void removeFavorite(String badFoodID) {
        // If present, remove the element with this ID from our list
        int removalIndex = -1;

        for (int i = 0; i < mFavoriteFoods.size(); i++) {
            if (mFavoriteFoods.get(i).getID().equals(badFoodID)) {
                removalIndex = i;
                break;
            }
        }

        if (removalIndex >= 0) {
            mFavoriteFoods.remove(removalIndex);
        }
    }

    public void updateFavoriteWithID(Food updatedFav, String currentID) {
        int index = favoritesContainsFoodWithID(currentID);

        if (index < 0)  return; // Do nothing

        // Otherwise update the food and notify the observer fragment
        updateFavoriteAtIndex(index, updatedFav);
//        setChanged();
//        notifyObservers(updatedFav);
    }

    public void updateFavoriteFood(Food updatedFav) {
        // Add favorite food does updating as well, if it's already contained
        addFavoriteFood(updatedFav);
        // Notify a food detail fragment what's up
//        setChanged();
//        notifyObservers(updatedFav);
    }

    public boolean containsFavorite(Food food) {
        return containsFavorite(food.getID());
    }

    public boolean containsFavorite(String foodID) {
        return (favoritesContainsFoodWithID(foodID) >= 0);
    }

    public ArrayList<ArrayList<Food>> getStapleFoods() {
        return mStapleFoods;
    }

    public ArrayList<Food> getSearchResults() {
        return searchResults;
    }

    public void clearSearchResults() {
        searchResults.clear();
    }

    public void addSearchResults(List<Food> additionalResults) {
        Collections.sort(additionalResults);
        searchResults.addAll(additionalResults);
    }

    public void setSearchResults(ArrayList<Food> searchResults) {
        this.searchResults = searchResults;
        // I think I want to sort these based on food title length
        // I implemented comparable in the food class, so this should work now
        Collections.sort(this.searchResults);
    }

    public ArrayList<Food> getFilteredSearchResults() {
        // Do some filtering logic here eventually

        // then just return our foods
        return searchResults;
    }

    public Food getSelectedFood() {
        return selectedFood;
    }

    public void setSelectedFood(Food selectedFood) {
        this.selectedFood = selectedFood;
        setChanged();
        notifyObservers(UpdateType.SelectedFoodChanged);
    }

    public String getBrowseFoodGroupID() {
        return browseFoodGroupID;
    }

    public void setBrowseFoodGroupID(String browseFoodGroupID) {
        this.browseFoodGroupID = browseFoodGroupID;
    }

    public void addBrowseList(String foodGroupID, List<Food> foods) {
        mBrowseListMap.put(foodGroupID, foods);
    }

    public List<Food> getBrowseList(String foodGroupID) {
        if (mBrowseListMap.containsKey(foodGroupID)) {
            return mBrowseListMap.get(foodGroupID);
        }
        else {
            return null;
        }
    }

    /* Helper Functions */
    private int favoritesContainsFoodWithID(String foodID) {
        int indexOfFood = -1;

        for (int i = 0; i < mFavoriteFoods.size(); i++) {
            if (mFavoriteFoods.get(i).getID().equals(foodID)) {
                indexOfFood = i;
                break;
            }
        }

        return indexOfFood;
    }


    private void updateFavoriteAtIndex(int updateIndex, Food updatedFav) {
        mFavoriteFoods.set(updateIndex, updatedFav);
    }

    public void saveFavorites(Context context) {
        Log.d(TAG, "Attempting to save favorites!");
        // Save the favorites to a file?
        Gson gson = new Gson();

        String path = context.getResources().getString(R.string.favorites_path);


        try {
            File file = new File(context.getFilesDir(), path);
            if (!file.exists()) {
                file.createNewFile();
            }

//            FileOutputStream fileOutputStream = context.openFileOutput(path, Context.MODE_PRIVATE);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Writer writer = new OutputStreamWriter(fileOutputStream);

            gson.toJson(mFavoriteFoods, writer);
            writer.close();
            Log.d(TAG, "Saved Favorites Sucessfully!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "FileNotFoundException!");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Generic IO Exception!");
        }





//        try {
//            context.getAssets().
//            Writer writer = new OutputStreamWriter(context.getAssets().open(path));
//            gson.toJson(mFavoriteFoods, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//
//
//        }
//
//
//        // Write gson to a file
//        try (Writer writer = new FileWriter("Output.json")) {
//            gson.toJson(users, writer);
//        }
    }






    // We should have lots of cool methods here for accessing global data in the application
    //      For example, getting food group names from IDs, and getting IDs from names.
}
