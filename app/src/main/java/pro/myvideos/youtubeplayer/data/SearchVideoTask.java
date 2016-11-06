package pro.myvideos.youtubeplayer.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static pro.myvideos.youtubeplayer.data.DataManager.YOUTUBE_DATA_API_KEY;

public class SearchVideoTask extends AsyncTaskLoader<SearchVideoHelper[]> {

    private static final String TAG = "ZAQ-SearchVideoTask";

    public static final int TYPE_VIDEO = 0;
    public static final int TYPE_CHANNEL = 1;
    public static final int TYPE_PLAYLIST = 2;
    public static final int TYPE_VIDEOS_OF_PLAYLIST = 3;
    public static final int TYPE_PLAYLISTS_OF_CHANNEL = 4;

    private String keyword;
    private String publishedAfterValue;
    private String durationValue;
    private int page = TYPE_VIDEO, type, order, published, duration;
    private String playListId, channelId, pageToken;
    public static boolean CONNECTION_STATUS = false;



    private String orderValue, typeValue;


    public SearchVideoTask(Context context, String[] strings, int[] spinners, int page) {
        super(context);
        this.keyword = strings[0];
        this.playListId = strings[1];
        this.channelId = strings[2];
        this.pageToken = strings[3];
        this.type = spinners[0];
        this.order = spinners[1];
        this.published = spinners[2];
        this.duration = spinners[3];
        this.page = page;

        getPublsihParameterValueIfNeed();
        setDurationValueBasedOnDuration();
        orderValue = getOrderValue();
        typeValue = getTypeValue();

        Log.d(TAG, "type:" + type + " " + "order:" + order + " " + "published:" +
                published + " " + "duration:" + duration + " keyword: " + keyword
                + " Playlstid: " + playListId + " chId: " + channelId + " page: " + page);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (keyword != null) {
            forceLoad();
        }
    }

    @Override
    public SearchVideoHelper[] loadInBackground() {
        try {
           return load();
        } catch (MalformedURLException e) {
            Log.e("my", "MalformedURLException Error " + e.toString(), e);
        }
        return null;
    }

    private SearchVideoHelper[] load() throws MalformedURLException {
        URL url = new URL(getBuiltUri().toString());
        String jsonResult = Helper.getJsonResultByUrl(url);

        if (jsonResult == null || jsonResult.length() < 5) {
            return null;
        }

        try {
            SearchVideoHelper[] results;
            switch (type) {
                case TYPE_VIDEO:
                    results = getVideoDataFromJson(jsonResult);
                    results = getEachVideoDetails(results);
                    return results;
                case TYPE_CHANNEL:
                    results = getChannelDataFromJson(jsonResult);
                    results = getEachChannelDetails(results);
                    return results;
                case TYPE_PLAYLIST:
                    results = getPlayListDataFromJson(jsonResult);
                    results = getEachPlayListDetails(results);
                    return results;
                case TYPE_PLAYLISTS_OF_CHANNEL:
                    results = getChannelPlaylistsFromJson(jsonResult);
                    results = getEachPlayListDetails(results);
                    return results;
                case TYPE_VIDEOS_OF_PLAYLIST:
                    results = getPlayListVideosFromJson(jsonResult);
                    results = getEachVideoDetails(results);
                    return results;
            }
            return null;
        } catch (JSONException e) {
            Log.d("my", "JSONException::: TYPE IS:" + type + " Error:" + e);
            return null;
        }
    }

    private Uri getBuiltUri() {
        Uri builtUri;
        String PART_PARAM = "part";
        String MAX_RESULTS_PARAM = "maxResults";
        String KEY_PARAM = "key";
        String TYPE_PARAM = "type";
        String numOfResults = "30";
        String partValue = "snippet";
        if (type == TYPE_VIDEOS_OF_PLAYLIST) {
            String BASE_URL_PLAY_LIST = "https://www.googleapis.com/youtube/v3/playlistItems?";
            String PLAYLIST_ID_PARAM = "playlistId";
            builtUri = Uri.parse(BASE_URL_PLAY_LIST).buildUpon()
                    .appendQueryParameter(PART_PARAM, partValue)
                    .appendQueryParameter(MAX_RESULTS_PARAM, numOfResults)
                    .appendQueryParameter(PLAYLIST_ID_PARAM, playListId)
                    .appendQueryParameter(KEY_PARAM, YOUTUBE_DATA_API_KEY)
                    .build();
        } else if (type == TYPE_PLAYLISTS_OF_CHANNEL) {
            String BASE_URL_PLAY_LISTS_CHANNEL = "https://www.googleapis.com/youtube/v3/playlists?";
            String CHANNEL_ID_PARAM = "channelId";
            builtUri = Uri.parse(BASE_URL_PLAY_LISTS_CHANNEL).buildUpon()
                    .appendQueryParameter(PART_PARAM, partValue)
                    .appendQueryParameter(MAX_RESULTS_PARAM, numOfResults)
                    .appendQueryParameter(CHANNEL_ID_PARAM, channelId)
                    .appendQueryParameter(TYPE_PARAM, typeValue)
                    .appendQueryParameter(KEY_PARAM, YOUTUBE_DATA_API_KEY)
                    .build();
        } else {
            String BASE_URL_SEARCH = "https://www.googleapis.com/youtube/v3/search?";
            String VIDEO_DURATION_PARAM = "videoDuration";
            String QUERY_PARAM = "q";
            String ORDER_PARAM = "order";
            builtUri = Uri.parse(BASE_URL_SEARCH).buildUpon()
                    .appendQueryParameter(PART_PARAM, partValue)
                    .appendQueryParameter(MAX_RESULTS_PARAM, numOfResults)
                    .appendQueryParameter(ORDER_PARAM, orderValue)
                    .appendQueryParameter(QUERY_PARAM, keyword)
                    .appendQueryParameter(TYPE_PARAM, typeValue)
                    .appendQueryParameter(VIDEO_DURATION_PARAM, durationValue)
                    .appendQueryParameter(KEY_PARAM, YOUTUBE_DATA_API_KEY)
                    .build();
            if (published > 0) {
                String PUBLISHED_AFTER_PARAM = "publishedAfter";
                builtUri = builtUri.buildUpon().appendQueryParameter(PUBLISHED_AFTER_PARAM, publishedAfterValue).build();
            }
            if (type == TYPE_VIDEO) {
                String SAFE_SEARCH_PARAM = "safeSearch";
                String EMBEDDABLE_PARAM = "videoEmbeddable";
                String SYNDICATED_PARAM = "videoSyndicated";
                String safesearchValue = "moderate";
                String embeddableValue = "true";
                String syndicatedValue = "true";
                builtUri = builtUri.buildUpon()
                        .appendQueryParameter(SAFE_SEARCH_PARAM, safesearchValue)
                        .appendQueryParameter(EMBEDDABLE_PARAM, embeddableValue)
                        .appendQueryParameter(SYNDICATED_PARAM, syndicatedValue).build();
            }
        }
        if (page > 1 && pageToken != null) {
            String PAGE_TOKEN_PARAM = "pageToken";
            builtUri = builtUri.buildUpon().appendQueryParameter(PAGE_TOKEN_PARAM, pageToken).build();
        }
        Log.d(TAG, "builtUri: " + builtUri.toString());
        return builtUri;
    }

    private SearchVideoHelper[] getVideoDataFromJson(String jsonResult) throws JSONException {

        JSONObject mainJsonObj = new JSONObject(jsonResult);
        JSONArray itemsJsonArr = mainJsonObj.getJSONArray("items");
        int arrLength = itemsJsonArr.length();
        SearchVideoHelper[] searchVideoHelper = new SearchVideoHelper[arrLength];

        for (int i = 0; i < arrLength; i++) {
            JSONObject idJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("id");
            JSONObject snippetJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("snippet");
            searchVideoHelper[i] = new SearchVideoHelper();
            searchVideoHelper[i].setId(idJsonObj.getString("videoId"));
            searchVideoHelper[i].setPublished(snippetJsonObj.getString("publishedAt"));
            searchVideoHelper[i].setTitle(snippetJsonObj.getString("title"));
            searchVideoHelper[i].setChannelId(snippetJsonObj.getString("channelId"));
            searchVideoHelper[i].setDecsription(snippetJsonObj.getString("description"));
            searchVideoHelper[i].setTotalResults(mainJsonObj.getJSONObject("pageInfo").getString("totalResults"));
            searchVideoHelper[i].setTypeOfSearch(type);
            JSONObject thumbnailsJsonObj = snippetJsonObj.getJSONObject("thumbnails");
            JSONObject defaultJsonObj = thumbnailsJsonObj.getJSONObject("default");
            searchVideoHelper[i].setImg_src(defaultJsonObj.getString("url"));
            if (!mainJsonObj.isNull("nextPageToken")) {
                searchVideoHelper[i].setNextPageToken(mainJsonObj.getString("nextPageToken"));
            }
        }
        return searchVideoHelper;
    }

    private SearchVideoHelper[] getEachVideoDetails(SearchVideoHelper[] searchVideoHelper) throws JSONException {
        String jsonResult;
        try {
            final String BASE_URL = "https://www.googleapis.com/youtube/v3/videos?";
            final String PART_PARAM = "part";
            final String ID_PARAM = "id";
            final String KEY_PARAM = "key";

            StringBuilder videos_id = new StringBuilder();
            for (int i = 0; i < searchVideoHelper.length; i++) {
                if (i != searchVideoHelper.length - 1) {
                    videos_id.append(searchVideoHelper[i].getId() + ",");
                } else {
                    videos_id.append(searchVideoHelper[i].getId());
                }
            }

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PART_PARAM, "statistics,contentDetails")
                    .appendQueryParameter(ID_PARAM, videos_id.toString())
                    .appendQueryParameter(KEY_PARAM, YOUTUBE_DATA_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            jsonResult = Helper.getJsonResultByUrl(url);
            if (jsonResult == null || jsonResult.length() < 1) {
                return searchVideoHelper;
            }

            JSONObject mainJsonObj = new JSONObject(jsonResult);
            JSONArray itemsJsonArr = mainJsonObj.getJSONArray("items");
            int arrLength = itemsJsonArr.length();

            for (int i = 0; i < arrLength; i++) {
                JSONObject contentDetailsJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("contentDetails");
                JSONObject statisticsJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("statistics");
                searchVideoHelper[i].setLength(contentDetailsJsonObj.getString("duration"));
                searchVideoHelper[i].setCountViews(statisticsJsonObj.getString("viewCount"));
                searchVideoHelper[i].setLikes(statisticsJsonObj.getLong("likeCount"));
                searchVideoHelper[i].setDislikes(statisticsJsonObj.getLong("dislikeCount"));
            }
        } catch (MalformedURLException e) {
            Log.e("my", "MalformedURLException Error " + e.toString(), e);
        }
        return searchVideoHelper;
    }

    private SearchVideoHelper[] getChannelDataFromJson(String jsonResult) throws JSONException {

        JSONObject mainJsonObj = new JSONObject(jsonResult);
        JSONArray itemsJsonArr = mainJsonObj.getJSONArray("items");
        int arrLength = itemsJsonArr.length();
        SearchVideoHelper[] searchVideoHelper = new SearchVideoHelper[arrLength];

        for (int i = 0; i < arrLength; i++) {
            JSONObject idJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("id");
            JSONObject snippetJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("snippet");
            searchVideoHelper[i] = new SearchVideoHelper();
            if (!mainJsonObj.isNull("nextPageToken")) {
                searchVideoHelper[i].setNextPageToken(mainJsonObj.getString("nextPageToken"));
            }
            searchVideoHelper[i].setId(idJsonObj.getString("channelId"));
            searchVideoHelper[i].setPublished(snippetJsonObj.getString("publishedAt"));
            searchVideoHelper[i].setTitle(snippetJsonObj.getString("title"));
            searchVideoHelper[i].setDecsription(snippetJsonObj.getString("description"));
            searchVideoHelper[i].setTotalResults(mainJsonObj.getJSONObject("pageInfo").getString("totalResults"));
            searchVideoHelper[i].setTypeOfSearch(type);

        }
        return searchVideoHelper;
    }

    private SearchVideoHelper[] getEachChannelDetails(SearchVideoHelper[] searchVideoHelper) throws JSONException {
        String jsonResult;
        try {

            final String BASE_URL = "https://www.googleapis.com/youtube/v3/channels?";
            final String PART_PARAM = "part";
            final String ID_PARAM = "id";
            final String KEY_PARAM = "key";

            StringBuilder channels_id = new StringBuilder();
            for (int i = 0; i < searchVideoHelper.length; i++) {
                if (i != searchVideoHelper.length - 1) {
                    channels_id.append(searchVideoHelper[i].getId() + ",");
                } else {
                    channels_id.append(searchVideoHelper[i].getId());
                }
            }

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PART_PARAM, "statistics, snippet")
                    .appendQueryParameter(ID_PARAM, channels_id.toString())
                    .appendQueryParameter(KEY_PARAM, YOUTUBE_DATA_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            jsonResult = Helper.getJsonResultByUrl(url);
            if (jsonResult == null || jsonResult.length() < 1) {
                return searchVideoHelper;
            }

            JSONObject mainJsonObj = new JSONObject(jsonResult);
            JSONArray itemsJsonArr = mainJsonObj.getJSONArray("items");
            int arrLength = itemsJsonArr.length();

            for (int i = 0; i < arrLength; i++) {
                JSONObject snippetJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("snippet");
                searchVideoHelper[i].setChannelTitle(snippetJsonObj.getString("channelTitle"));
                JSONObject thumbnailsJsonObj = snippetJsonObj.getJSONObject("thumbnails");
                JSONObject defaultJsonObj = thumbnailsJsonObj.getJSONObject("default");
                searchVideoHelper[i].setImg_src(defaultJsonObj.getString("url"));
                JSONObject statisticsJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("statistics");
                searchVideoHelper[i].setCountViews(statisticsJsonObj.getString("viewCount"));
                searchVideoHelper[i].setCountVideo(statisticsJsonObj.getString("videoCount"));
                searchVideoHelper[i].setCountSubscribers(statisticsJsonObj.getString("subscriberCount"));
            }
        } catch (MalformedURLException e) {
            Log.e("my", "MalformedURLException Error " + e.toString(), e);
        }
        return searchVideoHelper;
    }

    private SearchVideoHelper[] getPlayListDataFromJson(String jsonResult) throws JSONException {

        JSONObject mainJsonObj = new JSONObject(jsonResult);
        JSONArray itemsJsonArr = mainJsonObj.getJSONArray("items");
        int arrLength = itemsJsonArr.length();
        SearchVideoHelper[] searchVideoHelper = new SearchVideoHelper[arrLength];

        for (int i = 0; i < arrLength; i++) {
            JSONObject idJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("id");
            JSONObject snippetJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("snippet");
            searchVideoHelper[i] = new SearchVideoHelper();
            if (!mainJsonObj.isNull("nextPageToken")) {
                searchVideoHelper[i].setNextPageToken(mainJsonObj.getString("nextPageToken"));
            }
            searchVideoHelper[i].setId(idJsonObj.getString("playlistId"));
            searchVideoHelper[i].setPublished(snippetJsonObj.getString("publishedAt"));
            searchVideoHelper[i].setTitle(snippetJsonObj.getString("title"));
            searchVideoHelper[i].setDecsription(snippetJsonObj.getString("description"));

            searchVideoHelper[i].setTotalResults(mainJsonObj.getJSONObject("pageInfo").getString("totalResults"));
            searchVideoHelper[i].setTypeOfSearch(type);

            searchVideoHelper[i].setChannelId(snippetJsonObj.getString("channelId"));
            JSONObject thumbnailsJsonObj = snippetJsonObj.getJSONObject("thumbnails");
            JSONObject defaultJsonObj = thumbnailsJsonObj.getJSONObject("default");
            searchVideoHelper[i].setImg_src(defaultJsonObj.getString("url"));
        }
        return searchVideoHelper;
    }

    private SearchVideoHelper[] getEachPlayListDetails(SearchVideoHelper[] searchVideoHelper) throws JSONException {
        String jsonResult;
        try {
            final String BASE_URL = "https://www.googleapis.com/youtube/v3/playlists?";
            final String PART_PARAM = "part";
            final String ID_PARAM = "id";
            final String KEY_PARAM = "key";

            StringBuilder playlists_id = new StringBuilder();
            for (int i = 0; i < searchVideoHelper.length; i++) {
                if (i != searchVideoHelper.length - 1) {
                    playlists_id.append(searchVideoHelper[i].getId() + ",");
                } else {
                    playlists_id.append(searchVideoHelper[i].getId());
                }
            }

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PART_PARAM, "contentDetails")
                    .appendQueryParameter(ID_PARAM, playlists_id.toString())
                    .appendQueryParameter(KEY_PARAM, YOUTUBE_DATA_API_KEY)
                    .build();
            Log.d("my2", builtUri.toString());
            URL url = new URL(builtUri.toString());
            jsonResult = Helper.getJsonResultByUrl(url);

            if (jsonResult == null || jsonResult.length() < 1) {
                return searchVideoHelper;
            }

            JSONObject mainJsonObj = new JSONObject(jsonResult);
            JSONArray itemsJsonArr = mainJsonObj.getJSONArray("items");
            int arrLength = itemsJsonArr.length();

            for (int i = 0; i < arrLength; i++) {
                JSONObject contentDetailsJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("contentDetails");
                searchVideoHelper[i].setCountVideo(contentDetailsJsonObj.getString("itemCount"));
            }
        } catch (MalformedURLException e) {
            Log.e("my", "MalformedURLException Error " + e.toString(), e);
        }
        return searchVideoHelper;
    }

    private SearchVideoHelper[] getPlayListVideosFromJson(String jsonResult) throws JSONException {

        JSONObject mainJsonObj = new JSONObject(jsonResult);
        JSONArray itemsJsonArr = mainJsonObj.getJSONArray("items");
        int arrLength = itemsJsonArr.length();
        SearchVideoHelper[] searchVideoHelper = new SearchVideoHelper[arrLength];

        for (int i = 0; i < arrLength; i++) {
            JSONObject snippetJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("snippet");
            searchVideoHelper[i] = new SearchVideoHelper();
            searchVideoHelper[i].setPublished(snippetJsonObj.getString("publishedAt"));
            searchVideoHelper[i].setTitle(snippetJsonObj.getString("title"));
            searchVideoHelper[i].setDecsription(snippetJsonObj.getString("description"));
            searchVideoHelper[i].setTotalResults(mainJsonObj.getJSONObject("pageInfo").getString("totalResults"));
            searchVideoHelper[i].setTypeOfSearch(type);


            if (!mainJsonObj.isNull("nextPageToken")) {
                searchVideoHelper[i].setNextPageToken(mainJsonObj.getString("nextPageToken"));
            }
            JSONObject resourceIdJsonObj = snippetJsonObj.getJSONObject("resourceId");
            searchVideoHelper[i].setId(resourceIdJsonObj.getString("videoId"));
        }
        return searchVideoHelper;
    }

    private SearchVideoHelper[] getChannelPlaylistsFromJson(String jsonResult) throws JSONException {

        JSONObject mainJsonObj = new JSONObject(jsonResult);
        JSONArray itemsJsonArr = mainJsonObj.getJSONArray("items");
        int arrLength = itemsJsonArr.length();
        SearchVideoHelper[] searchVideoHelper = new SearchVideoHelper[arrLength];

        for (int i = 0; i < arrLength; i++) {
            searchVideoHelper[i] = new SearchVideoHelper();
            if (!mainJsonObj.isNull("nextPageToken")) {
                searchVideoHelper[i].setNextPageToken(mainJsonObj.getString("nextPageToken"));
            }
            searchVideoHelper[i].setId(itemsJsonArr.getJSONObject(i).getString("id"));
            JSONObject snippetJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("snippet");
            searchVideoHelper[i].setPublished(snippetJsonObj.getString("publishedAt"));
            searchVideoHelper[i].setTitle(snippetJsonObj.getString("title"));
            searchVideoHelper[i].setDecsription(snippetJsonObj.getString("description"));
            searchVideoHelper[i].setTotalResults(mainJsonObj.getJSONObject("pageInfo").getString("totalResults"));
            searchVideoHelper[i].setTypeOfSearch(type);

            searchVideoHelper[i].setChannelId(snippetJsonObj.getString("channelId"));
            searchVideoHelper[i].setChannelTitle(snippetJsonObj.getString("channelTitle"));
            JSONObject thumbnailsJsonObj = snippetJsonObj.getJSONObject("thumbnails");
            JSONObject defaultJsonObj = thumbnailsJsonObj.getJSONObject("default");
            searchVideoHelper[i].setImg_src(defaultJsonObj.getString("url"));
        }

        return searchVideoHelper;
    }


    //-------------------------------

    private void getPublsihParameterValueIfNeed() {
        if (published > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            GregorianCalendar gCal = new GregorianCalendar();
            switch (published) {
                case 1:
                    gCal.add(Calendar.DATE, -7);
                    break;
                case 2:
                    gCal.add(Calendar.DATE, -30);
                    break;
                case 3:
                    gCal.add(Calendar.DATE, -365);
                    break;
            }
            publishedAfterValue = dateFormat.format(gCal.getTime());
        }
    }

    private void setDurationValueBasedOnDuration() {
        switch (duration) {
            case 0:
                durationValue = "any";
                break;
            case 1:
                durationValue = "short";
                break;
            case 2:
                durationValue = "medium";
                break;
            case 3:
                durationValue = "long";
                break;
        }
    }

    private String getOrderValue() {
        switch (order) {
            case 1:
                return "date";
            case 2:
                return "viewCount";
            case 3:
                return "rating";
            case 4:
                if (type == TYPE_CHANNEL) return "videoCount";

                break;
        }
        return "relevance";
    }

    private String getTypeValue() {
        switch (type) {
            case TYPE_VIDEO:
                if (playListId != null) {
                    type = TYPE_VIDEOS_OF_PLAYLIST;
                }
                break;
            case TYPE_CHANNEL:
                return "channel";
            case TYPE_PLAYLIST:
                if (channelId != null) {
                    type = TYPE_PLAYLISTS_OF_CHANNEL;
                }
                return "playlist";
        }
        return "video";
    }
}