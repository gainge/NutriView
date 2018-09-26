package Utility;

import android.content.Context;

import com.bignerdranch.android.nutriview.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Gibson on 10/7/17.
 */

public class Values {

    public static final Values SINGLETON = new Values();

    private Values() {
        // Default singleton constructor
    }

    /* Instance Methods */
    public void loadAPIKey(Context context) {
        API_KEY = context.getResources().getString(R.string.api_key);
    }

    /* CONSTANTS */
    public static final String DEFAULT_SEARCH_TERMS = "50";
    public static String API_KEY = "";


    /* NUTRIENT IDS */
    public static final String NUTRIENT_CALORIES = "208";
    public static final String NUTRIENT_SUGAR = "269";
    public static final String NUTRIENT_PROTEIN = "203";
    public static final String NUTRIENT_FAT = "204";
    public static final String NUTRIENT_CARBS = "205";

    /* REQUEST FIELD STRINGS */
    public static final String FIELD_API = "api_key=";
    public static final String FIELD_FOOD_NUMBER = "ndbno=";
    public static final String FIELD_NUTRIENTS = "nutrients=";
    public static final String FIELD_SUBSET = "subset=";
    public static final String FIELD_TYPE = "type=";
    public static final String FIELD_FORMAT = "format=";
    public static final String FIELD_DATA_SOURCE = "ds=";
    public static final String FIELD_FOOD_GROUP= "fg=";
    public static final String FIELD_SEARCH_TERMS = "q=";
    public static final String FIELD_LIST_TYPE = "lt=";
    public static final String FIELD_MAX_ITEMS = "max=";
    public static final String FIELD_OFFSET = "offset=";
    public static final String FIELD_SORT_ORDER = "sort=";

    public static final String SORT_ORDER_DEFAULT = "r";
    public static final String SORT_ORDER_NAME = "n";
    public static final String SORT_ORDER_RELEVANCE = "r";
    public static final String SORT_ORDER_CONTENT = "c";
    public static final String SORT_ORDER_FOODNAME = "f";

    public static final String SUBSET_ABRIDGED = "1";
    public static final String SUBSET_FULL = "0";

    public static final String MAX_ITEMS_DEFAULT = "50";
    public static final String MAX_ITEMS_LARGE = "1000";
    public static final String OFFSET_DEFAULT = "0";

    public static final String SOURCE_BRANDED = "Branded Food Products";
    public static final String SOURCE_STANDARD = "Standard Reference";
}
