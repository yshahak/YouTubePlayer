package pro.myvideos.youtubeplayer.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import pro.myvideos.youtubeplayer.R;

/**
 * Created by yshah on 11/7/2016.
 */

public class DialogAbout extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.action_about)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(R.string.about)
                .setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }

}