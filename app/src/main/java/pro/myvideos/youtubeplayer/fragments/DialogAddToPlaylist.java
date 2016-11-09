package pro.myvideos.youtubeplayer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.activities.MainActivity;
import pro.myvideos.youtubeplayer.data.Playlist;
import pro.myvideos.youtubeplayer.data.VideoData;

/**
 * Created by yshah on 11/7/2016.
 */

public class DialogAddToPlaylist extends DialogFragment {

    private static final int ITEM_NEW_PLAYLIST = 0;
    private static final int ITEM_ELSE = 1;
    public static VideoData playedVideo;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ArrayList<Row> list = new ArrayList<>();
        list.add(new Row("Crete new playlist", R.drawable.createplaylist_pop_icon));
        for (Playlist playlist : MainActivity.playlists){
            list.add(new Row(playlist.getPlaylistName(), R.drawable.playlist_pop_exist_icon));
        }

        ArrayAdapter<Row> simpleAdapter = new MyAdapter(getActivity(), list);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_add)
                .setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case ITEM_NEW_PLAYLIST:
                                DialogFragment newDialog = new DialogCreatePlaylist();
                                DialogCreatePlaylist.playedVideo = playedVideo;
                                newDialog.show(getFragmentManager(), "");
                                break;
                            default:
                                addVideoToExistingList(which);
                                break;
                        }
                    }
                });
        return builder.create();
    }

    class Row{
        String row;
        int res;

        Row(String row, int res) {
            this.row = row;
            this.res = res;
        }
    }

    class MyAdapter extends ArrayAdapter<Row>{

        private ArrayList<Row> arrayList;

        MyAdapter(Context context, ArrayList<Row> list) {
            super(context, R.layout.row_playlist_popup, list);
            this.arrayList = list;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Row row = arrayList.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_playlist_popup, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.row_text)).setText(row.row);
            ((ImageView)convertView.findViewById(R.id.row_icon)).setImageResource(row.res);
            return convertView;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }
    }



    private void addVideoToExistingList(int selectedList) {
        Playlist playlist = MainActivity.playlists.get(--selectedList);
        playlist.getVideos().add(playedVideo);
        ((MainActivity)getActivity()).saveAndUpdate();
    }
}