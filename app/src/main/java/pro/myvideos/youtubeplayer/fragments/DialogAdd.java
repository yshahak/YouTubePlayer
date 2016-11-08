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
import java.util.List;

import pro.myvideos.youtubeplayer.R;

/**
 * Created by yshah on 11/7/2016.
 */

public class DialogAdd extends DialogFragment {

    private static final int ITEM_FAVORITES = 0;
    private static final int ITEM_NEW_PLAYLIST = 1;
    private List<String> items;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        items = new ArrayList<>();
        items.add("Crete new playlist");
        items.add("playlist 1");
        ArrayList<Row> list = new ArrayList<>();
        list.add(new Row("Crete new playlist", R.drawable.createplaylist_pop_icon));
        list.add(new Row("playlist 1", R.drawable.playlist_pop_exist_icon));

        loadPlaylists();
        String[] itemsString = items.toArray(new String[items.size()]);
        ArrayAdapter<Row> simpleAdapter = new MyAdapter(getActivity(), list);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_add)
                .setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case ITEM_FAVORITES:
//                                addToFavorite();
                                break;
                            case ITEM_NEW_PLAYLIST:
                                DialogFragment newDialog = new DialogAddCustomPlaylist();
//                                 TODO Should be support fragment manager or not?
                                newDialog.show(getFragmentManager(), "");
                                break;
                            default:
//                                addVideoToExistingList(items.get(which));
                                break;
                        }
                    }
                });
//                .setItems(itemsString, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case ITEM_FAVORITES:
////                                addToFavorite();
//                                break;
//                            case ITEM_NEW_PLAYLIST:
////                                DialogFragment newDialog = new DialogAddCustomPlaylist();
//                                // TODO Should be support fragment manager or not?
////                                newDialog.show(getFragmentManager(), "");
//                                break;
//                            default:
////                                addVideoToExistingList(items.get(which));
//                                break;
//                        }
//                    }
//                });
        return builder.create();
    }

    class Row{
        String row;
        int res;

        public Row(String row, int res) {
            this.row = row;
            this.res = res;
        }
    }

    class MyAdapter extends ArrayAdapter<Row>{

        private ArrayList<Row> arrayList;

        public MyAdapter(Context context, ArrayList<Row> list) {
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


    private void loadPlaylists() {

//        SharedPreferences playlists = getActivity().getSharedPreferences(ShPref.FILE_PLAY_LISTS, Context.MODE_PRIVATE);
//        int count = playlists.getInt("count", 0);
//        String tempString;
//        if (count > 0) {
//            for (int i = 0; i < count; i++) {
//                tempString = playlists.getString("name" + i, "");
//                if (tempString.length() > 0) {
//                    items.add(tempString);
//                }
//            }
//        }
    }

//    private void addVideoToExistingList(String selectedList) {
//        int realPosition = ShPref.getRealPlaylistPositionByString(getActivity(), selectedList);
//        ShPref.addVideoToPlaylist(getActivity(), realPosition, mDataset);
//    }
}