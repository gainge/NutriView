package com.bignerdranch.android.nutriview;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Gibson on 10/21/17.
 */

public class ServingSizeDialog extends DialogFragment {

    private static final String TAG = "SERVING_SIZE_DIALOG";

    /* DATA MEMBERS */
    ImageView mServingSizeImage;


    public ServingSizeDialog() {
        // Default empty constructor ftw
    }


    public static ServingSizeDialog newInstance() {
        ServingSizeDialog dialog = new ServingSizeDialog();


        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Get the view for the dialog
        View dialogView = inflater.inflate(R.layout.dialog_serving_size, null);

        builder.setView(dialogView);

        initImage(dialogView);

        return builder.create();
    }

    private void initImage(View v) {
        mServingSizeImage = (ImageView) v.findViewById(R.id.image_serving_size);

        if (mServingSizeImage != null) {
            String path = getActivity().getBaseContext().getString(R.string.dialog_serving_size_image_path);

            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getBaseContext().getAssets().open(path));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                mServingSizeImage.setImageBitmap(bitmap);
            }

        }
    }



    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();

        // Disable them buttons
        if (d != null) {
            ((Button)d.getButton(Dialog.BUTTON_POSITIVE)).setEnabled(false);
            ((Button)d.getButton(Dialog.BUTTON_NEGATIVE)).setText("Dismiss");

        }
    }

}
