package com.bignerdranch.android.nutriview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import model.Food;

/**
 * Created by Gibson on 10/8/17.
 */

public class FoodDetailActivity extends AppCompatActivity {

    private static final String FOOD_ID = "com.bignerdranch.android.nutriview.food_id";


    /* DATA MEMBERS */
    private Food mFood; // What this whole view will be centered around
    private NutrientButtonClickListener mNutrientButtonClickListener;


    /* WIDGETS */
    private Button mCalorieButton;
    private Button mSugarButton;
    private Button mFatButton;
    private Button mCarbButton;
    private Button mProteinButton;

    // This is an intent for now
    // We might have to change some stuff in the future if it needs to be a fragment
    // It could need to be a fragment if the bottom navigation stuff doesn't end up working out
    public static Intent newIntent(Context packageContext, String foodID) {
        Intent intent = new Intent(packageContext, FoodDetailActivity.class);
        intent.putExtra(FOOD_ID, foodID); // I guess another option here is parcelables?
        // Pack up some data?
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        // Time to make our activity do some stuff!

        // So we need to query the server for our food
        // But I think for now that that can wait
        // Let's just grab our food and set the title first
        String foodID = getIntent().getStringExtra(FOOD_ID);

        getSupportActionBar().setTitle(foodID);

        initButtons();


    }

    private void initButtons() {
        mNutrientButtonClickListener = new NutrientButtonClickListener();

        mCalorieButton = (Button) findViewById(R.id.button_calories);
        mSugarButton = (Button) findViewById(R.id.button_sugar);
        mFatButton = (Button) findViewById(R.id.button_fat);
        mCarbButton = (Button) findViewById(R.id.button_carbs);
        mProteinButton = (Button) findViewById(R.id.button_protein);

        // Set up the listeners to just display a toast for now
        mCalorieButton.setOnClickListener(mNutrientButtonClickListener);
        mSugarButton.setOnClickListener(mNutrientButtonClickListener);
        mFatButton.setOnClickListener(mNutrientButtonClickListener);
        mCarbButton.setOnClickListener(mNutrientButtonClickListener);
        mProteinButton.setOnClickListener(mNutrientButtonClickListener);

        // This graying out is handled programmatically for now, but we might want to change that
        // We could just set all but the calorie button to gray in the xml
        // Then presses could just be handled from there
        // It would also make it easier because then we could have a default button for sure
        // There's definitely a better way to do button stuff in general, but...
        // I'm not really super duper familiar with it.
        gray_buttons();
        mCalorieButton.setBackgroundResource(R.drawable.calorie_button);
    }


    // Basic listener, could do crazier stuff eventually
    // Like resetting the backgrounds of the buttons that weren't clicked
    private class NutrientButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            gray_buttons();
            switch (v.getId()) {
                case R.id.button_calories:
                    Toast.makeText(getBaseContext(), "Calories!", Toast.LENGTH_SHORT).show();
                    mCalorieButton.setBackgroundResource(R.drawable.calorie_button);
                    break;
                case R.id.button_sugar:
                    Toast.makeText(getBaseContext(), "Sugar!", Toast.LENGTH_SHORT).show();
                    mSugarButton.setBackgroundResource(R.drawable.sugar_button);
                    break;
                case R.id.button_fat:
                    Toast.makeText(getBaseContext(), "Fat!", Toast.LENGTH_SHORT).show();
                    mFatButton.setBackgroundResource(R.drawable.fat_button);
                    break;
                case R.id.button_carbs:
                    Toast.makeText(getBaseContext(), "Carbs!", Toast.LENGTH_SHORT).show();
                    mCarbButton.setBackgroundResource(R.drawable.carbs_button);
                    break;
                case R.id.button_protein:
                    Toast.makeText(getBaseContext(), "Protein!", Toast.LENGTH_SHORT).show();
                    mProteinButton.setBackgroundResource(R.drawable.protein_button);
                    break;
                default:
                    Toast.makeText(getBaseContext(), "Uh-oh...", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void gray_buttons() {
        mCalorieButton.setBackgroundResource(R.drawable.rounded_button);
        mSugarButton.setBackgroundResource(R.drawable.rounded_button);
        mFatButton.setBackgroundResource(R.drawable.rounded_button);
        mCarbButton.setBackgroundResource(R.drawable.rounded_button);
        mProteinButton.setBackgroundResource(R.drawable.rounded_button);

    }

    // We should implement a click listener here that takes in a button as an instance?
    // That way each button can call the same listener?
    // Or just implement a new listener in each, and call the same helper method

}
