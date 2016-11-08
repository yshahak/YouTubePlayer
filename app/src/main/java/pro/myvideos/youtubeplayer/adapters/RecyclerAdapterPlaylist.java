package pro.myvideos.youtubeplayer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.activities.MainActivity;
import pro.myvideos.youtubeplayer.data.Playlist;

/**
 * Created by B.E.L on 06/11/2016.
 */

public class RecyclerAdapterPlaylist extends RecyclerView.Adapter<RecyclerAdapterPlaylist.Holder> {

    String countFormatter;


    public RecyclerAdapterPlaylist(Context context) {
        countFormatter = context.getString(R.string.playlist_count_formatter);

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_playlist_tab, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Playlist playlist = MainActivity.playlists.get(position);
        holder.playlistCount.setText(String.format(countFormatter, playlist.getVideos().size()));
        holder.playlistTitle.setText(playlist.getPlaylistName());
    }

    @Override
    public int getItemCount() {
        return MainActivity.playlists.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        GridLayout gridLayout;
        TextView playlistTitle, playlistCount;
        View playlistMenu;

        Holder(View itemView) {
            super(itemView);
            gridLayout = (GridLayout) itemView.findViewById(R.id.grid_layout);
            playlistTitle = (TextView)itemView.findViewById(R.id.playlist_title);
            playlistCount = (TextView)itemView.findViewById(R.id.playlist_count);
            playlistMenu = itemView.findViewById(R.id.playlist_menu);
        }

        @Override
        public void onClick(View view) {

        }

    }


}
