package com.bignerdranch.android.nutriview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FoodVisualFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FoodVisualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodVisualFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_RATIO = "com.bignerdranch.android.nutriview.foodvisualfragment.ratio";
    private static final String ARG_PATH = "com.bignerdranch.android.nutriview.foodvisualfragment.path";
    private static final String ARG_FOOD_NAME = "com.bignerdranch.android.nutriview.foodvisualfragment.food_name";
    private static final String ARG_SERVING_SIZE = "com.bignerdranch.android.nutriview.foodvisualfragment.serving_size";

    private static final double PAD_FACTOR = 1.1;
    public static final double TRIM_WINDOW = 0.10;

    private static final String TAG = "FOOD_VISUAL_FRAGMENT";
    private static final String INSIGNIFICANT_PATH = "insignificant.png";

    /* DATA MEMBERS */
    private double mRatio;
    private int mImageDimensions;
    private int mNumColumns;
    private int mColumnWidth;
    private int mVerticalSpacing;
    private String mImagePath;
    private String mFoodName;
    private String mServingSize;

    private Bitmap mBitmap;
    private Bitmap mInsignificantBitmap;

    private RelativeLayout mSmallNutrientLayout;



    /* WIDGETS */
    private ImageView mImageView;

    private GridView mGridView;
    private ImageAdapter mImageAdapter;

    private OnFragmentInteractionListener mListener;

    public FoodVisualFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FoodVisualFragment.
     */
    // TODO: give a parameter that specifies the image resource to use
    public static FoodVisualFragment newInstance(double ratio, String path, String foodName, String servingSize) {
        FoodVisualFragment fragment = new FoodVisualFragment();

        Bundle args = new Bundle();
        Log.d(TAG, "Bundling up ratio: " + ratio);
        args.putDouble(ARG_RATIO, ratio);
        args.putString(ARG_PATH, path);
        args.putString(ARG_FOOD_NAME, foodName);
        args.putString(ARG_SERVING_SIZE, servingSize);


        // Try to set things here and now?
        fragment.mRatio = ratio;
        fragment.mImagePath = path;
        fragment.mFoodName = foodName;
        fragment.mServingSize = servingSize;

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRatio = 0.34; // Dummy value

        if (getArguments() != null) {
            Log.d(TAG, "Loading Arguments!");
            mRatio = getArguments().getDouble(ARG_RATIO);
            mImagePath = getArguments().getString(ARG_PATH);
            mFoodName = getArguments().getString(ARG_FOOD_NAME);
//            mRatio *= 10; // Scale it up for testing purposes
        }

        if (mRatio == 0.0) {
            mImagePath = "s_" + mImagePath;
            // Instead, load up the backup image as well
            loadBackupBitmap();
        }
        loadBitmap();

        // Do some calculations now that we're here
        int parentWidth = this.getParentFragment().getView().getWidth();
        mNumColumns = (int) Math.sqrt(mRatio) + 1;
        mColumnWidth = (int) (parentWidth / (mNumColumns * PAD_FACTOR));

        // Handle the case of very few images
        if (mRatio <= 2) {
            mNumColumns = 1;
        }

        mImageDimensions = mColumnWidth;

        // Compute the vertical spacing
//        double maxFit = getParentFragment().getView().getHeight() / mImageDimensions;
//        double remainder = getParentFragment().getView().getHeight() / maxFit * mImageDimensions;
//        mVerticalSpacing = (int) (remainder / (mNumColumns - 1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_food_visual, container, false);

        Log.d(TAG, "Create View container width: " + container.getWidth());
        Log.d(TAG, "Create View container height: " + container.getHeight());

        if (container.getWidth() > container.getHeight()) {
            mNumColumns = (int) Math.sqrt(mRatio) + 1;
            mColumnWidth = (int) (container.getHeight() / (mNumColumns * PAD_FACTOR));

            // Handle the case of very few images
//            if (mRatio <= 2) {
//                mNumColumns = 1;
//            }

            mImageDimensions = mColumnWidth;
        }

        initGridView(view);


        return view;
    }

    private void loadBackupBitmap() {
        try {
            Log.d(TAG, "Attempting to open resource as bitmap: " + INSIGNIFICANT_PATH);
            mInsignificantBitmap = BitmapFactory.decodeStream(getActivity().getBaseContext().getAssets().open(INSIGNIFICANT_PATH));
        } catch (IOException e) {
            // Crap
            Log.e(TAG, "Error thrown in initImageView!!");
            e.printStackTrace();
        }
    }

    private void loadBitmap() {
        try {
            Log.d(TAG, "Attempting to open resource as bitmap: " + mImagePath);
            mBitmap = BitmapFactory.decodeStream(getActivity().getBaseContext().getAssets().open(mImagePath));
        } catch (IOException e) {
            // Crap
            Log.e(TAG, "Error thrown in initImageView!!");
            e.printStackTrace();
        }
    }

    private void initGridView(View v) {
        Log.d(TAG, "Initializing GridView!");
        // Initialize the gridview member
        mGridView = (GridView) v.findViewById(R.id.gridview_food_visual);

        // handle the case where we have relatively few items
//        if (mNumColumns == 1) {
//            int pad = mColumnWidth / 2;
//            if ((int) mRatio == 1) {
//                mGridView.setPadding(pad, pad, pad, pad);   // Pad all edges
//            }
//            else if ((int) mRatio == 2) {
//                mGridView.setPadding(pad, 0, pad, 0);       // Pad only sides
//            }
//        }



        mGridView.setNumColumns(mNumColumns);
        mGridView.setColumnWidth(mColumnWidth);
        mGridView.setVerticalSpacing(mVerticalSpacing);

        // Init the adapter
        mImageAdapter = new ImageAdapter(getContext());

        // Join the two worlds together!!!!
        mGridView.setAdapter(mImageAdapter);
    }

    public void updateAdapter() {
        mImageAdapter = new ImageAdapter(getContext());

        if (mGridView != null) {
            mGridView.setAdapter(mImageAdapter);
        }
    }


    private void initImageView(View v) {
        // Set the image based on our index
        Bitmap bitmap = null;
        String path = "blueEfron.jpg";

        if (mRatio % 2 == 0) {
            path = "redEfron.jpg";
        }

        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getBaseContext().getAssets().open(path));
        } catch (IOException e) {
            // Crap
            Log.e(TAG, "Error thrown in initImageView!!");
            e.printStackTrace();
        }

        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        }
    }


    // Ok, we need to extend the base adapter class, I guess?
    private class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return (int) (mRatio + 1); // Round up one to cover fractional ratios
        }

        public Object getItem(int position) {
            return null;    // Not sure what this is for either
        }

        public long getItemId(int position) {
            return 0;   // Not sure what this is for
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView in adapter for position " + position);

            View result = null;

            // Maybe do something here about setting a ratio?
            if (mRatio == 0.0) {
                Log.d(TAG, "Ratio is 0.0, time to load a custom view");
                // Build a custom thing-aroo
                // Let's define our views!
                ImageView tooSmallImage = null;
                ImageView foodImage = null;
                TextView tooSmallText = null;



                LayoutInflater inflater = getActivity().getLayoutInflater();
                mSmallNutrientLayout = (RelativeLayout) inflater.inflate(R.layout.view_small_nutrient, parent, false);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mImageDimensions, mImageDimensions);
                mSmallNutrientLayout.setLayoutParams(params);

                // Get the microscope!
                tooSmallImage = (ImageView) mSmallNutrientLayout.findViewById(R.id.image_nutrient_too_small);
                if (mInsignificantBitmap != null) {
                    tooSmallImage.setImageBitmap(mInsignificantBitmap); // Hopefully this works
                }


                // Do the small food
                foodImage = (ImageView) mSmallNutrientLayout.findViewById(R.id.image_small_food);
                if (mBitmap != null) {
                    foodImage.setImageBitmap(mBitmap);
                }

                // Do the text view, finally!
                tooSmallText = (TextView) mSmallNutrientLayout.findViewById(R.id.label_nutrient_too_small);

                StringBuilder str = new StringBuilder();
                str.append("<");
                str.append(100 * TRIM_WINDOW);
                str.append("% of 1 Serving");


                tooSmallText.setText(str.toString());

                mSmallNutrientLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity().getBaseContext(), mFoodName + ", " + mServingSize, Toast.LENGTH_SHORT).show();
                    }
                });

                return mSmallNutrientLayout;

            }

            // Are we at the end?
            if (position == (int) mRatio) {
                View image = getImageView(null);
                RelativeLayout relativeLayout = new RelativeLayout(getActivity().getBaseContext());

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mImageDimensions, mImageDimensions);
                relativeLayout.setLayoutParams(params);

                params.addRule(RelativeLayout.CENTER_IN_PARENT);

                // Set params for food image
                image.setLayoutParams(params);

                // Modify params for other image
                // The division below is how we'll get the overalp we desire!
                double residual = (mRatio - (int) mRatio);
                params = new RelativeLayout.LayoutParams((int) (mImageDimensions * (1 - residual)), mImageDimensions);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);

                // If we're a half ratio
                Log.d(TAG, "This is the end!  (pos. " + position + ", ratio: " + mRatio + ")");
                View overlap = new View(getActivity().getBaseContext());
                overlap.setLayoutParams(params);
                overlap.setBackgroundColor(getActivity().getResources().getColor(R.color.inactiveSuperLight));
//                overlap.setPadding(mImageDimensions / 2, 0, 0, 0);

                relativeLayout.addView(image);
                relativeLayout.addView(overlap);

                result = relativeLayout;
            }
            else {
                // Handle the case where we try to convert a relative layout :P
                View convert = null;
                if (convertView instanceof ImageView) convert = convertView;
                View image = getImageView(convert);

                result = image;
            }

            // Display food title on click
            if (result != null) {
                result.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity().getBaseContext(), mFoodName + ", " + mServingSize, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return result;

        }


        private View getImageView(View convertView) {
            ImageView imageView;

            if (convertView == null) {
                imageView = new ImageView(mContext);
                // Not really sure what anything below does...
                imageView.setLayoutParams(new GridView.LayoutParams(mImageDimensions, mImageDimensions));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            if (mBitmap != null) {
                imageView.setImageBitmap(mBitmap);
                Log.d(TAG, "Image set sucessfully!");
            }
            else {
                // Probably will never happen
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.nutrient_sugar));
                Log.d(TAG, "Image load failed!  Resorting to backup!");
            }

            return imageView;
        }

    }





    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("FoodVisualFragmentToString\n");
        str.append("Ratio: " + mRatio + '\n');
        str.append("Path: " + mImagePath + '\n');

        return str.toString();
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
//        void onFragmentInteraction(Uri uri);
    }
}
