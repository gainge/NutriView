package com.bignerdranch.android.nutriview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.ArrayList;

import model.ClientModelRoot;
import model.Food;

/**
 * Created by Gibson on 10/21/17.
 */

public class EditFoodDialog extends DialogFragment {

    public void setOldFood(Food oldFood) {
        Log.d(TAG, "Old Food Set!");
        this.oldFood = oldFood;
    }
    public void setNewFood(Food newFood) {
        this.newFood = newFood;
    }


    /* CONSTANTS */
    private static final String TAG = "EDIT_FOOD_DIALOG";

    /* DATA MEMBERS */
    private Food oldFood;
    private Food newFood;
    private boolean foodChanged;

    private EditText mEditTextFoodName;
    private EditText mEditTextServingSize;
    private EditText mEditTextCalories;
    private EditText mEditTextSugar;
    private EditText mEditTextProtein;
    private EditText mEditTextFat;
    private EditText mEditTextCarbs;

    private ArrayList<EditText> mEditTextArrayList;

    private OnFragmentInteractionListener mListener;


    /* CONSTRUCTOR */
    public EditFoodDialog() {
        // Default, pls don't use this
        mEditTextArrayList = new ArrayList<>();
        foodChanged = false;
    }


    public static EditFoodDialog newInstance(Food food) {
        EditFoodDialog dialog = new EditFoodDialog();

        // Set the ting.  The ting goes pop.  Rattatatatat.  Skeee-op!
        dialog.setOldFood(food);

        // Clone into new food!
        dialog.setNewFood(food.clone());

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Set our custom title bar
        builder.setCustomTitle(inflater.inflate(R.layout.custom_dialog_title, null));

        View dialogView = inflater.inflate(R.layout.dialog_edit_food, null);

        builder.setView(dialogView)
            .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // Probably take care of some cool stuff while we're here
                    // Update the favorite food in the model if we changed?
                    if (foodChanged) {
                        ClientModelRoot.SINGLETON.updateFavoriteWithID(newFood, oldFood.getID());
                        // Then notify our listener that the thing changed
                        mListener.onDialogFoodEdited(newFood.getID());
                    }

                    // Then cancel ourselves?
                }
            })
            .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EditFoodDialog.this.getDialog().cancel();
                }
            });
        builder.setTitle("Edit Food");

        Dialog dialog = builder.create();


        initEditTexts(dialogView);

        return dialog;
    }

    private void updateUI() {
        if (mEditTextFoodName != null) {
            mEditTextFoodName.setText(oldFood.getName());
        }
        else {
            Log.d(TAG, "Food Name Edit Text was NUL??!!!??!?");
        }

        if (mEditTextServingSize != null) {
            mEditTextServingSize.setText(oldFood.getMeasure());
        }


        // Initilize values and set a click listener
        for (int i = 0; i < mEditTextArrayList.size(); i++) {
            double value = oldFood.getNutrients()[i].getValue();
            Log.d(TAG, "i : " + value);
            mEditTextArrayList.get(i).setText(String.format("%.2f", value));
        }
    }


    // Will need like, a separate listener class to enforce valid number format

    public void initEditTexts(View v) {
        Log.d(TAG, "initEditTexts");
        // Set up our text views
        mEditTextFoodName = (EditText) v.findViewById(R.id.edit_text_food_name);
        mEditTextServingSize = (EditText) v.findViewById(R.id.edit_text_serving_size);
        mEditTextCalories = (EditText) v.findViewById(R.id.edit_text_calorie_value);
        mEditTextSugar = (EditText) v.findViewById(R.id.edit_text_sugar_value);
        mEditTextProtein = (EditText) v.findViewById(R.id.edit_text_protein_value);
        mEditTextFat = (EditText) v.findViewById(R.id.edit_text_fat_value);
        mEditTextCarbs = (EditText) v.findViewById(R.id.edit_text_carbs_value);

        // Add a text watcher for the food name thing
        mEditTextFoodName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Set the name and ID of the new food
                newFood.setName(s.toString());
                newFood.setID(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditTextServingSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newFood.setMeasure(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // Add them to a sick list of edit texts
        mEditTextArrayList.add(mEditTextCalories);
        mEditTextArrayList.add(mEditTextSugar);
        mEditTextArrayList.add(mEditTextProtein);
        mEditTextArrayList.add(mEditTextFat);
        mEditTextArrayList.add(mEditTextCarbs);

        // Set up the text listeners
        for (int i = 0; i < mEditTextArrayList.size(); i++) {
            mEditTextArrayList.get(i).addTextChangedListener(new NutrientTextWatcher(i));
        }

        // Initialize the text values
        if (oldFood != null) {
            updateUI();
        }

    }

    private class NutrientTextWatcher implements TextWatcher {
        /* DATA MEMBERS */
        private int nutrientIndex;

        public NutrientTextWatcher() {
            this(0);
        }

        public NutrientTextWatcher(int index) {
            nutrientIndex = index;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Parse the nutrient value
            try {
                double nutrientValue = Double.parseDouble(s.toString());
                mEditTextArrayList.get(this.nutrientIndex).setTextColor(Color.BLACK);

                // Set the value in our food?
                newFood.getNutrients()[nutrientIndex].setValue(nutrientValue);
                foodChanged = true;
            } catch (NumberFormatException e) {
                mEditTextArrayList.get(this.nutrientIndex).setTextColor(Color.RED);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            // Handle zero length input?
        }

    }

    public interface OnFragmentInteractionListener {
        public void onDialogFoodEdited(String newFoodID);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
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

}
