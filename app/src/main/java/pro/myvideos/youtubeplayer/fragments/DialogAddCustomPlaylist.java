package pro.myvideos.youtubeplayer.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.activities.MainActivity;
import pro.myvideos.youtubeplayer.data.Playlist;
import pro.myvideos.youtubeplayer.data.VideoData;

/**
 * Created by yshah on 11/7/2016.
 */

public class DialogAddCustomPlaylist extends DialogFragment {

    private EditText newPlaylist;
    private View view;
    public static VideoData playedVideo;

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
        Playlist playlist = new Playlist(name, playedVideo);
        MainActivity.playlists.add(playlist);
        ((MainActivity)getActivity()).saveAndUpdate();

    }

    private void showSnackBar(String text) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show();
    }
}
