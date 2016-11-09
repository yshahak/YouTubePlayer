package pro.myvideos.youtubeplayer.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.activities.MainActivity;
import pro.myvideos.youtubeplayer.data.Playlist;

/**
 * Created by yshah on 11/7/2016.
 */

public class DialogDeletePlaylist extends DialogFragment {

    public static Playlist playlistToDelete;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(playlistToDelete.getPlaylistName())
                .setIcon(R.drawable.playlist_pop_exist_icon)
                .setPositiveButton(R.string.delete_playlist, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.playlists.remove(playlistToDelete);
                        ((MainActivity)getActivity()).saveAndUpdate();
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

}