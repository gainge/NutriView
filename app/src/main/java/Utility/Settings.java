package Utility;

/**
 * Created by Gibson on 10/22/17.
 */

public class Settings {
    private static final Settings ourInstance = new Settings();

    public static final int BROWSE_ORDER_NUTRIENT_ASCENDING = 1;
    public static final int BROWSE_ORDER_NUTRIENT_DESCENDING = -1;

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
        // Initalize some of our default stuff
        browseNutrientValue = Values.NUTRIENT_CALORIES;
        browseSortOrder = BROWSE_ORDER_NUTRIENT_DESCENDING;
        browseDataSource = Values.SUBSET_FULL;
        browseMaxItems = Values.MAX_ITEMS_LARGE;
        searchMaxItems = Values.MAX_ITEMS_LARGE;
        shouldSortByName = true;
        maxSearchResultsForRequery = 10;
    }


    private static String browseNutrientValue;
    private static String browseDataSource;
    private static int browseSortOrder;
    private static String browseMaxItems;
    private static String searchMaxItems;
    private static int maxSearchResultsForRequery;

    private static boolean shouldSortByName;


    // Need to generate getters and setters


    public static String getBrowseNutrientValue() {
        return browseNutrientValue;
    }

    public static void setBrowseNutrientValue(String browseNutrientValue) {
        Settings.browseNutrientValue = browseNutrientValue;
    }

    public static String getBrowseDataSource() {
        return browseDataSource;
    }

    public static void setBrowseDataSource(String browseDataSource) {
        Settings.browseDataSource = browseDataSource;
    }

    public static int getBrowseSortOrder() {
        return browseSortOrder;
    }

    public static void setBrowseSortOrder(int browseSortOrder) {
        Settings.browseSortOrder = browseSortOrder;
    }

    public static String getBrowseMaxItems() {
        return browseMaxItems;
    }

    public static void setBrowseMaxItems(String browseMaxItems) {
        Settings.browseMaxItems = browseMaxItems;
    }

    public static boolean isShouldSortByName() {
        return shouldSortByName;
    }

    public static void setShouldSortByName(boolean shouldSortByName) {
        Settings.shouldSortByName = shouldSortByName;
    }

    public static int getMaxSearchResultsForRequery() {
        return maxSearchResultsForRequery;
    }

    public static void setMaxSearchResultsForRequery(int maxSearchResultsForRequery) {
        Settings.maxSearchResultsForRequery = maxSearchResultsForRequery;
    }

    public static String getSearchMaxItems() {
        return searchMaxItems;
    }

    public static void setSearchMaxItems(String searchMaxItems) {
        Settings.searchMaxItems = searchMaxItems;
    }
}
