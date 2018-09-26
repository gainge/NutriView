package com.bignerdranch.android.nutriview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.andreilisun.swipedismissdialog.SwipeDismissDialog;

import com.github.andreilisun.swipedismissdialog.SwipeDismissDialog;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;

import Utility.UpdateType;
import Utility.Values;
import model.ClientModelRoot;
import model.Food;
import model.Nutrient;
import server.NutrientReportRequest;
import server.NutrientReportResult;
import server.ServerProxy;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FoodDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FoodDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodDetailFragment extends Fragment implements Observer {

    private static final String FOOD_ID = "com.bignerdranch.android.nutriview.food_id";
    private static final String TAG = "FOOD_DETAIL_FRAGMENT";

    private static final double TRIM_WINDOW = 0.15;

    private static final DecimalFormat df2 = new DecimalFormat(".##");


    /* DATA MEMBERS */
    private Food mFood; // What this whole view will be centered around
    private String mFoodID;
    private NutrientButtonClickListener mNutrientButtonClickListener;
    private ArrayList<Button> mButtonList;
    private ArrayList<Integer> mButtonIDList;
    private ArrayList<Integer> mButtonColorList;
    private final int startingIndex = 0;
    private int currentNutrientIndex;
    private boolean isFavorited = false;
    private int servingFactor = 1;



    /* WIDGETS */
    private Button mCalorieButton;
    private Button mSugarButton;
    private Button mFatButton;
    private Button mCarbButton;
    private Button mProteinButton;
    private ViewPager mFoodVisualPager;
    private FoodVisualPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;

    private TextView mServingSizeTextView;
    private TextView mNutrientEquivalentTextView;
    private TextView mNutrientMeasureTextView;

    // We need a spinner here eventually!

    private TextView mToolbarTextView;

    private Toolbar mFoodDetailToolbar;
    private FrameLayout mBacknavButton;
    private FrameLayout mEditButton;
    private FrameLayout mFavoriteButton;
    private LinearLayout mServingInfoContainer;

    private ImageView mLeftButton;
    private ImageView mRightButton;

    private Fragment mDetailFragment;

    // A listener for our momma activity!
    private OnFragmentInteractionListener mListener;


    /**
     * This is literally the most stupid class I've ever written, in all my years of coding
     * What a sight to behold, this monstrosity is
     *
     * It exists solely because of my own ignorance
     */
    private class DummyTask extends AsyncTask<Object, Void, Object> {
        @Override
        protected Void doInBackground(Object... objects) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "Evil Exception thrown in dummytask!");
            }


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (mFood != null) {
                initViewPager(getView());
                updateViewPager();
                initWithFoodMember();
            }
        }

    }

    // Time to code up that yung async task!
    private class FoodDetailTask extends AsyncTask<Object, Void, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            Log.d(TAG, "FoodDetailTask doInBackground!");
            if (objects[0] == null) {
                Log.e(TAG, "Food Detail Task Parameter was null!");
                return null;    // Return null so onPostExecute knows what was up
            }

            NutrientReportRequest request = (NutrientReportRequest) objects[0];

            // Initialize a dummy result
            NutrientReportResult result = new NutrientReportResult();
            result.addError("Internal Server Error", "Unknown");

            try {
                result = ServerProxy.SINGLETON.nutrientReport(request);
            } catch (Exception e) {
                Log.e(TAG, "Exception thrown in FoodDetailTask doInBackground!!");
                Log.e(TAG, e.toString());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.d(TAG, "FoodDetailTask onPostExecute!");
            if (o == null) {
                Toast.makeText(getActivity().getBaseContext(), "Failure!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "FoodDetailTask PostExecute Fail!");
                return;
            }

            NutrientReportResult reportResult = (NutrientReportResult) o;

            if (!reportResult.hasErrors()) {
                // Now we can populate our fragment with data!
                mFood = reportResult.getResultAsFood();

                // This might kill everything
                initViewPager(getView());
                updateViewPager();

                // Do our initialization now that we have a food to work with
                initWithFoodMember();

            }
        }
    }

    public FoodDetailFragment() {
        // Required empty public constructor
        // Can we add things in here?
        mButtonList = new ArrayList<>();

        // Add things manually for now, eventually I'd like to consolidate this stuff to a list
        // Maybe something using xml, I suppose
        mButtonIDList = new ArrayList<>();
        mButtonIDList.add(R.id.button_calories);
        mButtonIDList.add(R.id.button_sugar);
        mButtonIDList.add(R.id.button_protein);
        mButtonIDList.add(R.id.button_fat);
        mButtonIDList.add(R.id.button_carbs);


        mButtonColorList = new ArrayList<>();
        mButtonColorList.add(R.color.calories);
        mButtonColorList.add(R.color.sugar);
        mButtonColorList.add(R.color.protein);
        mButtonColorList.add(R.color.fat);
        mButtonColorList.add(R.color.carbs);


        currentNutrientIndex = startingIndex;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FoodDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FoodDetailFragment newInstance(String foodID) {
        // Maybe we could do something cool with the constructor...
        // Maybe do the async task in the constructor
        FoodDetailFragment fragment = new FoodDetailFragment();

        Bundle args = new Bundle();

        args.putString(FOOD_ID, foodID);

        fragment.setArguments(args);

        return fragment;
    }

    public static FoodDetailFragment newInstance(Food food) {
        FoodDetailFragment fragment = new FoodDetailFragment();

        fragment.setFood(food);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "FoodDetailView onCreate!");

        mDetailFragment = this;

        if (getArguments() != null) {
            mFoodID = getArguments().getString(FOOD_ID);

            // If we have a string arg, continue like normal
            if (mFoodID != null && !mFoodID.equals("")) {
                loadFoodData();
            }
        }
        else if (mFood != null) {
            mFoodID = mFood.getID();
        }
        else {
            mFoodID = "Crap!!";
        }

    }

    private void loadFoodData() {
        // Create a nutrient report request and start our task!
        NutrientReportRequest request = new NutrientReportRequest();
        // Set the food ID
        request.setFoodID(mFoodID);
        // Set the nutrient IDs
        request.addNutrient(Values.NUTRIENT_CALORIES);
        request.addNutrient(Values.NUTRIENT_SUGAR);
        request.addNutrient(Values.NUTRIENT_PROTEIN);
        request.addNutrient(Values.NUTRIENT_FAT);
        request.addNutrient(Values.NUTRIENT_CARBS);
        // Send it off to the races!
        new FoodDetailTask().execute(request);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "FoodDetailView onCreateView!");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_detail, container, false);

        // Initialize our Buttons and crap
        initButtons(view);

        initToolbar(view);

        initToolbarButtons();

        initTouchables(view);

        initTextViews(view);

        initViewPager(view);


        if (mFood != null) {
            mFoodID = mFood.getID();
            if (ClientModelRoot.SINGLETON.containsFavorite(mFoodID)) {
                doUIForFavorite();
                isFavorited = true;
            }
            new DummyTask().execute();
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    public void setFood(Food food) {
        mFood = food;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    private void populateViewLabels() {
        // Set the serving size text
        if (mServingSizeTextView != null) {
            mServingSizeTextView.setText(mFood.getMeasure());
        }

        // Set the title in the toolbar
        if (mToolbarTextView != null) {
            mToolbarTextView.setText(mFood.getName());
        }
    }

    public void initWithFoodMember() {
        // Set some text in our view
        populateViewLabels();

        // Load up the nutritional info from our food, and populate data members
        loadNutrientInfo(startingIndex);
        refreshEquivalenceLabel(0);
    }

    private void initToolbar(View v) {
        Log.d(TAG, "Initializing Food Detail Fragment Toolbar!");
        mFoodDetailToolbar = (Toolbar) v.findViewById(R.id.toolbar_food_detail);
        mToolbarTextView = (TextView) mFoodDetailToolbar.findViewById(R.id.label_toolbar_favorite_title);

        // Set the toolbar as the activity's toolbar
        ((AppCompatActivity)getActivity()).setSupportActionBar(mFoodDetailToolbar);
    }


    /**
     * Initializes the buttons accessible through the toolbar
     */
    private void initToolbarButtons() {
        initBacknav();
        initFavorite();
        initEdit();
    }

    /**
     * Sets up the image view on the backnav toolbar
     * Also sets up the click listener for this image view
     */
    private void initBacknav() {
        mBacknavButton = (FrameLayout) mFoodDetailToolbar.findViewById(R.id.btn_toolbar_navback);

        if (mBacknavButton != null) {
            mBacknavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // We need to tell our activity to kick us off!
                    mListener.onFoodDetailUpPress(mDetailFragment);
                }
            });
        }

    }

    private void initFavorite() {
        mFavoriteButton = (FrameLayout) mFoodDetailToolbar.findViewById(R.id.btn_toolbar_favorite);

        if (mFavoriteButton != null) {
            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle the favorite
                    toggleFavorite();
                    if (isFavorited) {
                        ClientModelRoot.SINGLETON.addFavoriteFood(mFood);
                    }
                    else {
                        ClientModelRoot.SINGLETON.removeFavorite(mFood);
                    }
                }
            });
        }
    }

    private void initEdit() {
        mEditButton = (FrameLayout) mFoodDetailToolbar.findViewById(R.id.btn_toolbar_edit);

        if (mEditButton != null) {
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Let our activity know what's up!
                    if (mFood != null) {
                        ((MainActivity)getActivity()).onFoodDetailEditPress(mFood);
                    }

                }
            });
        }
    }

    private void initTouchables(View v) {
        mServingInfoContainer = (LinearLayout) v.findViewById(R.id.serving_info_container);

        mServingInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeServingSizeDialog();
            }
        });

        mLeftButton = (ImageView) v.findViewById(R.id.button_pager_left);
        mRightButton = (ImageView) v.findViewById(R.id.button_pager_right);

        mLeftButton.setVisibility(View.INVISIBLE);  // Start as invisible, obvi

        // Wire up the click listeners
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFoodVisualPager != null) {
                    if (mFoodVisualPager.getCurrentItem() != 0) {
                        // Scroll the pager one left
                        mFoodVisualPager.setCurrentItem(mFoodVisualPager.getCurrentItem() - 1);
                    }
                }
            }
        });

        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFoodVisualPager != null) {
                    if (mFoodVisualPager.getCurrentItem() != mPagerAdapter.mFoodList.size() - 1) {
                        // Scroll the pager one left
                        mFoodVisualPager.setCurrentItem(mFoodVisualPager.getCurrentItem() + 1);
                    }
                }
            }
        });
    }


    private void toggleFavorite() {
        // toggle the data member
        isFavorited = !isFavorited;

        // Update the views
        if (isFavorited) {
            // Handle switch to fav
            doUIForFavorite();
        }
        else {
            // Handle the switch from fav
            doUIForNotFavorite();
        }
    }

    private void doUIForFavorite() {
        ImageView starImage = (ImageView) mFavoriteButton.findViewById(R.id.image_toolbar_favorite_star);

        if (starImage != null) {
            // Update the star icon
            starImage.setBackgroundResource(R.drawable.ic_favorite_gold);
            starImage.setImageResource(R.drawable.ic_favorite_gold);
        }

        // Make the edit icon visible and interactive
        if (mEditButton != null) {
            mEditButton.setVisibility(View.VISIBLE);
            mEditButton.setEnabled(true);
        }


    }

    private void doUIForNotFavorite() {
        ImageView starImage = (ImageView) mFavoriteButton.findViewById(R.id.image_toolbar_favorite_star);

        if (starImage != null) {
            // Update the star icon
            starImage.setBackgroundResource(R.drawable.ic_favorite_white);
            starImage.setImageResource(R.drawable.ic_favorite_white);
        }

        // Make the edit icon visible and interactive
        if (mEditButton != null) {
            mEditButton.setVisibility(View.INVISIBLE);
            mEditButton.setEnabled(false);
        }
    }


    private void initViewPager(View v) {
        mFoodVisualPager = (ViewPager) v.findViewById(R.id.pager_food_visual);

        if (mPagerAdapter != null) {
            mFoodVisualPager.setAdapter(mPagerAdapter);
        }

        // Set up the fancy dots
        mTabLayout = (TabLayout) v.findViewById(R.id.tabDots);
        mTabLayout.setupWithViewPager(mFoodVisualPager, true);

        // Might want to override some methods here
        mFoodVisualPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Refresh the lower label's information based on what page we've landed on
                refreshEquivalenceLabel(position);

                if (position == 0) {
                    mLeftButton.setVisibility(View.INVISIBLE);
                }
                else if (position == mPagerAdapter.mFoodList.size() - 1) {
                    mRightButton.setVisibility(View.INVISIBLE);
                }
                else {
                    mLeftButton.setVisibility(View.VISIBLE);
                    mRightButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initTextViews(View v) {
        mServingSizeTextView = (TextView) v.findViewById(R.id.label_serving_size);
        mServingSizeTextView.setText(mFoodID);
        mNutrientMeasureTextView = (TextView) v.findViewById(R.id.label_nutrient_measure);
        mNutrientEquivalentTextView = (TextView) v.findViewById(R.id.label_equivalent_measure);
    }

    private void makeServingSizeDialog() {
        View dialog = LayoutInflater.from(getActivity().getBaseContext())
                .inflate(R.layout.dialog_serving_size, null);

        SwipeDismissDialog swipeDismissDialog = new SwipeDismissDialog.Builder(getActivity())
                .setFlingVelocity(0.06f)
                .setView(dialog)
                .build()
                .show();

        ImageView servingSizes = (ImageView) dialog.findViewById(R.id.image_serving_size);

        Bitmap bitmap = null;
        String path = getActivity().getBaseContext().getString(R.string.dialog_serving_size_image_path);

        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getBaseContext().getAssets().open(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            servingSizes.setImageBitmap(bitmap);
        }
    }

    private void initButtons(View v) {
        mNutrientButtonClickListener = new NutrientButtonClickListener();

        mCalorieButton = (Button) v.findViewById(R.id.button_calories);
        mProteinButton = (Button) v.findViewById(R.id.button_protein);
        mSugarButton = (Button) v.findViewById(R.id.button_sugar);
        mFatButton = (Button) v.findViewById(R.id.button_fat);
        mCarbButton = (Button) v.findViewById(R.id.button_carbs);

        // Add the buttons to our data structure
        mButtonList.add(mCalorieButton);
        mButtonList.add(mSugarButton);
        mButtonList.add(mProteinButton);
        mButtonList.add(mFatButton);
        mButtonList.add(mCarbButton);

        // Set up the listeners to just display a toast for now
        for (Button b : mButtonList) {
            b.setOnClickListener(mNutrientButtonClickListener);
        }

        // Gray out all the buttons
        gray_buttons();
        // Then assign the starting button its resource
        mButtonList.get(startingIndex).setBackgroundColor(mButtonColorList.get(startingIndex));
        mCalorieButton.setBackgroundColor(getResources().getColor(R.color.calories));
    }


    // Basic listener, could do crazier stuff eventually
    // Like resetting the backgrounds of the buttons that weren't clicked
    private class NutrientButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            gray_buttons();
            int index = mButtonIDList.indexOf(new Integer(v.getId()));
            mButtonList.get(index).setBackgroundResource(mButtonColorList.get(index));

            if (currentNutrientIndex != index) {
                // Set the current button pressed
                currentNutrientIndex = index;

                // Now update the view pager?
                updateViewPager();
            }

            // Now, update the nutrient measure text accordingly
            Log.d(TAG, "Button pressed at index: " + index);
            loadNutrientInfo(index);

        }

    }

    // I wonder if we have to remove the active fragments or something?
    // I guess we'll find out
    // I'm not sure how I feel about just grabbing the new fragment manager willy-nilly like this
    public void updateViewPager() {
        Log.d(TAG, "Creating a new adapter instance in updateViewPager()");
        FragmentManager fragmentManager = getChildFragmentManager();
        mPagerAdapter = new FoodVisualPagerAdapter(fragmentManager, currentNutrientIndex);
        mFoodVisualPager.setAdapter(mPagerAdapter);

        // Update the fancy dots
        mTabLayout.setupWithViewPager(mFoodVisualPager, true);

        refreshEquivalenceLabel(mFoodVisualPager.getCurrentItem());
        Log.d(TAG, "New adapter set in updateViewPager!");
    }

    private void refreshEquivalenceLabel(int pagerPosition) {
        // So this method is supposed to be called when we want to update the label at the bottom of the screen
        Food currentAdapterFood = mPagerAdapter.getFoodAtIndex(pagerPosition);

        if (currentAdapterFood == null || mFood == null) return;     // Don't try to mess with that crap

        // Acquire some info!
        String foodName = currentAdapterFood.getName();
        String serving = currentAdapterFood.getMeasure();
        double nutrientRatio = getNutrientRatio(pagerPosition);

        // Construct the label string
        StringBuilder str = new StringBuilder();

        str.append(df2.format(nutrientRatio) + " x ");
        str.append(serving + " ");
        str.append(foodName);

        // Set the label
        mNutrientEquivalentTextView.setText(str.toString());
    }

    private double getNutrientRatio(int pagerPosition) {
        // This is where we can calculate the ratios and crap
        Food currentAdapterFood = mPagerAdapter.getFoodAtIndex(pagerPosition);

        if (currentAdapterFood == null || mFood == null) return 0.0;     // Don't try to mess with that crap

        // Get some sick values
        double referenceNutrientValue = currentAdapterFood.getNutrients()[currentNutrientIndex].getValue();
        double searchedNutrientValue = mFood.getNutrients()[currentNutrientIndex].getValue();

        double nutrientRatio = 0;

        // Compute the ratio of the searched food's value to the reference food's value
        if (referenceNutrientValue != 0) {
            nutrientRatio = searchedNutrientValue / referenceNutrientValue;
        }

        return nutrientRatio;
    }

    private double trimNutrientRatio(double ratio) {
        double remainder = ratio - (int)(ratio / 1);
        double trimmed = ratio;

        if (remainder < FoodVisualFragment.TRIM_WINDOW) {  // Too small
            // Floor the ratio
            trimmed = ratio - remainder;
        }
        else if (remainder > (1 - FoodVisualFragment.TRIM_WINDOW)) {
            // Ceiling the ratio
        }

        return trimmed;
    }

    private String getUnitStringForFoodUnit(String foodUnit) {
        String unit = "grams";

        if (foodUnit.equals("kcal")) {
            unit = "calories";
        }

        return unit;
    }



    private void gray_buttons() {
        for (Button b : mButtonList) {
            b.setBackgroundColor(getResources().getColor(R.color.inactiveLight2));
        }

        // Hmm this is kind of redundant
        // I think I wanted to do color stuff anyway?
//        mCalorieButton.setBackgroundResource(R.drawable.rounded_button);
//        mSugarButton.setBackgroundResource(R.drawable.rounded_button);
//        mFatButton.setBackgroundResource(R.drawable.rounded_button);
//        mCarbButton.setBackgroundResource(R.drawable.rounded_button);
//        mProteinButton.setBackgroundResource(R.drawable.rounded_button);

    }


    private void loadNutrientInfo(int nutrientIndex) {
        if (nutrientIndex < 0 || nutrientIndex > 5) return; // Do nothign if the argument is out of range
        if (mNutrientMeasureTextView == null) return;

        StringBuilder str = new StringBuilder();

        Nutrient selectedNutrient = mFood.getNutrients()[nutrientIndex];

        str.append(selectedNutrient.getValue());
        str.append(" ");
        str.append(getUnitStringForFoodUnit(selectedNutrient.getUnit()));

        mNutrientMeasureTextView.setText(str.toString());

        // Probably will need some stuff here in a bit regarding loading the view pager and crap
    }



    /* ADAPTER FOR THE VIEW PAGER!! */
    private class FoodVisualPagerAdapter extends FragmentStatePagerAdapter {
        /* DATA MEMBERS */
        ArrayList<Food> mFoodList;


        // I'm just overriding this because I have to I think, lol
        public FoodVisualPagerAdapter(FragmentManager fragmentManager, int index) {
            super(fragmentManager);
            mFoodList = ClientModelRoot.SINGLETON.getStapleFoods().get(index);
        }

        @Override
        public Fragment getItem(int position) {
            // Grab the food we're currently on
            Food currentAdapterFood = mFoodList.get(position);

            // Compute the ratio and get the image path to use
            double nutrientRatio = getNutrientRatio(position);
            nutrientRatio = trimNutrientRatio(nutrientRatio);   // Cut it down to size, for visual purposes
            String imagePath = ClientModelRoot.SINGLETON.getPathForFoodID(mFoodList.get(position).getID());

            // Create a new fragment
            FoodVisualFragment fragment = FoodVisualFragment.newInstance(
                    nutrientRatio, imagePath, currentAdapterFood.getName(), currentAdapterFood.getMeasure());

            // Ok, now here's where we should be setting the text field and crap, based on the position, yeah?
            // We still might have to override some stuff, but that's whatever
            Log.d(TAG, "Getting item #" + position + " in the View Pager Adapter!");
            Log.d(TAG, "Fragment info: " + fragment.toString());

            // Return it?
            return fragment;
        }

        @Override
        public int getCount() {
            // return the nubmer of food items in the staple list corresponding to the selected nutrient
            return mFoodList.size();
        }

        public Food getFoodAtIndex(int index) {
            if (index < 0 || index >= mFoodList.size()) return null;
            return mFoodList.get(index);
        }
    }

    private static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    e.printStackTrace();
                }
                catch(IllegalAccessException e){
                    e.printStackTrace();
                }
                catch(IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Menu item selected in FoodDetailFragment!");

        switch (item.getItemId()) {
            case android.R.id.home:
                // Finish ourselves?
                // Remove ourselves from the stack?
                // Maybe call an interface method on the listening activity?
                Log.d(TAG, "Up Button press!");
                mListener.onFoodDetailUpPress(this);
                // Then we might have to kill ourselves?
                return true;
            default:
                Toast.makeText(getActivity().getBaseContext(), "I don't know how to handle this option!", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }


    public void update(Observable o, Object arg) {
        if (arg instanceof UpdateType) {
            UpdateType updateType = (UpdateType) arg;
            if (updateType != UpdateType.FavoriteFoodsChanged) return;
        }
        else if (arg instanceof Food) {
            if (!isFavorited) return;   // Don't do anything if we're not favorited

            // We can do cool stuff?
            // Like updating our food member, and refreshing the views?
            // Let's just display a toast for now
            Toast.makeText(getActivity().getBaseContext(), "You changed a favorite food!", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach!");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

//        try
//        {
//            Field childField = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childField.setAccessible(true);
//            childField.set(this, null);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        ClientModelRoot.SINGLETON.deleteObserver(this);     // Remove ourselves as an observer

        // Stack overflow hacky stuff
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
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

        // Put in some potentially sick methods here lol
        public void onFoodDetailUpPress(Fragment fragment);
        public void onFoodDetailEditPress(Food food);
    }
}
