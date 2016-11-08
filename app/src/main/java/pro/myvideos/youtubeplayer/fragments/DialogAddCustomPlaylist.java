package pro.myvideos.youtubeplayer.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.data.VideoData;

/**
 * Created by yshah on 11/7/2016.
 */

public class DialogAddCustomPlaylist extends DialogFragment {

    private VideoData mDataset;// = ListVideoAdapter.tempDataset;
    private EditText newPlaylist;
    private View view;
    private int list_id;
    private boolean list_exist = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_new_playlist);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_add_playlist, null);
        builder.setView(view);


        builder.setPositiveButton(R.string.createAdd, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                newPlaylist = (EditText) view.findViewById(R.id.et_playlist);
                String nameOfList = newPlaylist.getText().toString();
                if (nameOfList.length() > 0) {
                    addNewVideo(nameOfList);
                } else {
                    showSnackBar("Wrong name of playlist");
                }
            }
        });


        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }

    private void addNewVideo(String name) {
        //ADD LIST
        SharedPreferences sp_playlists = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int count = sp_playlists.getInt("count", 0);
        for (int i = 0; i < count; i++) {
            String savedName = sp_playlists.getString("name" + i, "null0123");
            if (name.equals(savedName)) {
                list_exist = true;
                list_id = i;
                Log.d("my3", "The list =" + name + "= exist, id = " + list_id);
            }
        }
        if (!list_exist) {
            SharedPreferences.Editor editor = sp_playlists.edit();
            editor.putString("name" + count, name);
            list_id = count;
            Log.d("my3", "The list =" + name + "= not exist, new id = " + list_id);
            count++;
            editor.putInt("count", count);
            editor.apply();
            //MainActivity.addStringToLeftMenu(name);
        }

        //ADD VIDEO TO THE LIST
//        ShPref.addVideoToPlaylist(getActivity(), list_id, mDataset);

        if (list_exist) {
            showSnackBar("Video saved to the " + name);
        } else {
            showSnackBar("List " + name + " created and video saved");
        }
    }

    private void showSnackBar(String text) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show();
    }
}
