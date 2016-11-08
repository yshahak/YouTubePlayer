package pro.myvideos.youtubeplayer.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

@SuppressWarnings({"WeakerAccess", "unused"})
public class VideoData {

    public static final int DEL_ACTION_PLAYLIST = 0;
    public static final int DEL_ACTION_HISTORY = 1;
    public static final int DEL_ACTION_FAVORITES = 2;
    public static int deleteAction;
    private static DecimalFormat formatter = new DecimalFormat("#,###,###");

    private String id, published, countViews, countVideo, countSubscribers;
    private String title, decsription, length, img_src, channelId, channelTitle;
    private String nextPageToken, prevPageToken, totalResults, playlistName;
    private int typeOfSearch;
    private long likes, dislikes;
    private boolean showDeleteOption;

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public VideoData() {

    }

    public boolean isShowDeleteOption() {
        return showDeleteOption;
    }

    public void setShowDeleteOption(boolean showDeleteOption) {
        this.showDeleteOption = showDeleteOption;
    }

    public int getTypeOfSearch() {
        return typeOfSearch;
    }

    public void setTypeOfSearch(int typeOfSearch) {
        this.typeOfSearch = typeOfSearch;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getCountVideo() {
        return countVideo;
    }

    public void setCountVideo(String countVideo) {
        this.countVideo = formatter.format(countVideo);
    }

    public String getCountSubscribers() {
        return countSubscribers;
    }

    public void setCountSubscribers(String countSubscribers) {
        this.countSubscribers = countSubscribers;
    }

    public String getImg_src() {
        return img_src;
    }

    public void setImg_src(String img_src) {
        this.img_src = img_src;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountViews() {
        if (countViews != null) {
            return countViews;
        } else {
            return "";
        }
    }

    public void setCountViews(String countViews) {
        this.countViews = formatter.format(Integer.valueOf(countViews));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDecsription(String decsription) {
        this.decsription = decsription;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getPublished() {
        if (published != null) {
            return published;
        } else {
            return "";
        }
    }

    public void setPublished(String published) {
        this.published = published;
    }


    public void setLikes(long likes) {
        this.likes = likes;
    }

    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }

    public long getLikes() {
        return likes;
    }

    public long getDislikes() {
        return dislikes;
    }


    public static void save(final Context context, final ArrayList<Playlist> list) {
        int count = 0;
        SharedPreferences sp_playlists = PreferenceManager.
                getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp_playlists.edit();
        Iterator<Playlist> iterator = list.iterator();
        while(iterator.hasNext()){
            Playlist playlist = iterator.next();
            Gson gson = new GsonBuilder().create();
            String array = gson.toJson(playlist);
            editor.putString("json" + count, array);
            count++;
        }
        editor.putInt("jsonSize", count);
        editor.apply();
    }

    public static ArrayList<Playlist> load(Context context) {
        SharedPreferences sp_playlists = PreferenceManager.
                getDefaultSharedPreferences(context);
        ArrayList<Playlist> list = new ArrayList<>();
        try {
            int size = sp_playlists.getInt("jsonSize", 0);
            if (size == 0) {
                return null;
            }
            for (Integer i = 0; i < size; i++) {
                Gson gson = new Gson();
                String array = sp_playlists.getString("json" + i.toString(), null);
                Type type = new TypeToken<Playlist>(){}.getType();
                Playlist playlist = gson.fromJson(array, type);
                list.add(playlist);
            }
        } catch (ConcurrentModificationException | IllegalStateException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

}

