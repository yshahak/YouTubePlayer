package pro.myvideos.youtubeplayer.data;

import java.util.ArrayList;

/**
 * Created by yshah on 11/8/2016.
 */

public class Playlist {

    private String playlistName;
    private ArrayList<VideoData> videos;

    public Playlist(String playlistName) {
        this.playlistName = playlistName;
        this.videos = new ArrayList<>();
    }

    public void addVideo(VideoData videoData){
        videos.add(videoData);
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public ArrayList<VideoData> getVideos() {
        return videos;
    }
}
