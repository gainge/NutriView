package com.bignerdranch.android.nutriview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

import model.ClientModelRoot;
import model.Food;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoritesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = "FAVORITES_FRAGMENT";

    /* DATA MEMBERS */
    private RecyclerView mFavoritesRecyclerView;
    private FavoriteAdaper mFavoriteAdaper;
    private Toolbar mToolbar;

    private OnFragmentInteractionListener mListener;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);


        // Initialize our widgets
        initRecyclerView(view);

        initToolbar(view);

        return view;
    }

    private void initRecyclerView(View v) {
        Log.d(TAG, "Setting up the recyclerview!");
        // Set up the recyclerview
        mFavoritesRecyclerView = (RecyclerView) v.findViewById(R.id.favorite_fragment_recyclerview);
        mFavoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        // Set up the adapter
        mFavoriteAdaper = new FavoriteAdaper();

        mFavoritesRecyclerView.setAdapter(mFavoriteAdaper);
        // In the future we might want to do something for handling when favorites are empty
    }



    private void initToolbar(View v) {
        Log.d(TAG, "Setting up the toolbar!");
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar_plain_title);

        if (mToolbar != null) {
            // Set the toolbar in our activity
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

            TextView titleText = (TextView)mToolbar.findViewById(R.id.label_toolbar_plain_title);

            // Set the toolbar title
            if (titleText != null) {
                titleText.setText(getActivity().getResources().getString(R.string.favorite_fragment_title));
            }
        }
    }

    public void favoritesChanged() {
        if (mFavoriteAdaper != null) {
            mFavoriteAdaper.notifyDataSetChanged();
        }
    }



    // Custom viewholder class
    // Might want to implement swipe to remove stuff here?
    private class FavoriteHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        /* DATA MEMBERS */
        private Food mFood;

        /* WIDGETS */
        TextView mFoodNameTextView;

        public FavoriteHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            // Wire up our widgets
            mFoodNameTextView = (TextView) itemView.findViewById(R.id.label_plain_food_title);
        }

        public void bindFood(Food food) {
            mFood = food;
            mFoodNameTextView.setText(mFood.getName());
        }

        @Override
        public void onClick(View v) {
            // If we have a good food instance, set it in the model to trigger a fragment addition
            if (mFood != null) {
                ClientModelRoot.SINGLETON.setSelectedFood(mFood);
            }
            else {
                Toast.makeText(getActivity().getBaseContext(), "This is no bueno!!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    // Custom Adapter Class
    private class FavoriteAdaper extends RecyclerView.Adapter<FavoriteHolder> {

        @Override
        public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity().getBaseContext());
            View view = layoutInflater.inflate(R.layout.list_item_plain, parent, false);
            return new FavoriteHolder(view);
        }

        @Override
        public void onBindViewHolder(FavoriteHolder holder, int position) {
            holder.bindFood(ClientModelRoot.SINGLETON.getFavoriteFoods().get(position));
        }

        @Override
        public int getItemCount() {
            return ClientModelRoot.SINGLETON.getFavoriteFoods().size();
        }

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
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Favorites onPause!");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Favorites onStop!");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Favorites onResume!");
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

    }
}
