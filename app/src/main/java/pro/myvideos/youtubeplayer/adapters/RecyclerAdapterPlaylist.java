package pro.myvideos.youtubeplayer.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.activities.MainActivity;
import pro.myvideos.youtubeplayer.data.Playlist;
import pro.myvideos.youtubeplayer.data.VideoData;
import pro.myvideos.youtubeplayer.fragments.DialogDeletePlaylist;

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
        holder.itemView.setTag(playlist);
        int count = 0;
        for (VideoData videoData : playlist.getVideos()){
            ImageView imageView = null;
            switch (count){
                case 0:
                    imageView = holder.thumb0;
                    break;
                case 1:
                    imageView = holder.thumb1;
                    break;
                case 2:
                    imageView = holder.thumb2;
                    break;
                case 3:
                    imageView = holder.thumb3;
                    break;
            }
            Picasso.
                    with(holder.itemView.getContext())
                    .load(Uri.parse(videoData.getImg_src()))
                    .fit()
                    .into(imageView);
            count++;
            if (count == 4){
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return MainActivity.playlists.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView thumb0, thumb1, thumb2, thumb3;
        TextView playlistTitle, playlistCount;
        View playlistMenu;

        Holder(View itemView) {
            super(itemView);
            thumb0 = (ImageView) itemView.findViewById(R.id.thumb0);
            thumb1 = (ImageView) itemView.findViewById(R.id.thumb1);
            thumb2 = (ImageView) itemView.findViewById(R.id.thumb2);
            thumb3 = (ImageView) itemView.findViewById(R.id.thumb3);

            playlistTitle = (TextView)itemView.findViewById(R.id.playlist_title);
            playlistCount = (TextView)itemView.findViewById(R.id.playlist_count);
            playlistMenu = itemView.findViewById(R.id.playlist_menu);
            playlistMenu.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Playlist playlist = (Playlist) itemView.getTag();
            if (playlist != null) {
                if (view.equals(playlistMenu)) {
                    DialogFragment newDialog = new DialogDeletePlaylist();
                    DialogDeletePlaylist.playlistToDelete = playlist;
                    newDialog.show(((MainActivity) view.getContext()).getSupportFragmentManager(), "");
                }else {
                    if (playlist.getVideos().size() > 0) {
                        ((MainActivity) itemView.getContext()).playList(playlist);
                    } else {
                        Toast.makeText(itemView.getContext() ,"There is no videos in this playlist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }


}
