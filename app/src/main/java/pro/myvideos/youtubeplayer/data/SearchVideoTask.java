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

import static android.R.attr.type;
import static pro.myvideos.youtubeplayer.data.DataManager.YOUTUBE_DATA_API_KEY;

public class SearchVideoTask extends AsyncTaskLoader<VideoData[]> {

    private static final String TAG = "ZAQ-SearchVideoTask";
    private String keyword;


    public SearchVideoTask(Context context, String searchWord) {
        super(context);
        this.keyword = searchWord;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (keyword != null) {
            forceLoad();
        }
    }

    @Override
    public VideoData[] loadInBackground() {
        try {
           return load();
        } catch (MalformedURLException e) {
            Log.e("my", "MalformedURLException Error " + e.toString(), e);
        }
        return null;
    }

    private VideoData[] load() throws MalformedURLException {
        URL url = new URL(getBuiltUri().toString());
        String jsonResult = Helper.getJsonResultByUrl(url);

        if (jsonResult == null || jsonResult.length() < 5) {
            return null;
        }
        try {
            VideoData[] results;
            results = getVideoDataFromJson(jsonResult);
            results = getEachVideoDetails(results);
            return results;
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
            String BASE_URL_SEARCH = "https://www.googleapis.com/youtube/v3/search?";
            String QUERY_PARAM = "q";
            String ORDER_PARAM = "order";
        String orderValue = "relevance";
        String typeValue = "video";
        builtUri = Uri.parse(BASE_URL_SEARCH).buildUpon()
                    .appendQueryParameter(PART_PARAM, partValue)
                    .appendQueryParameter(MAX_RESULTS_PARAM, numOfResults)
                    .appendQueryParameter(ORDER_PARAM, orderValue)
                    .appendQueryParameter(QUERY_PARAM, keyword)
                    .appendQueryParameter(TYPE_PARAM, typeValue)
                    .appendQueryParameter(KEY_PARAM, YOUTUBE_DATA_API_KEY)
                    .build();
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
        Log.d(TAG, "builtUri: " + builtUri.toString());
        return builtUri;
    }

    private VideoData[] getVideoDataFromJson(String jsonResult) throws JSONException {

        JSONObject mainJsonObj = new JSONObject(jsonResult);
        JSONArray itemsJsonArr = mainJsonObj.getJSONArray("items");
        int arrLength = itemsJsonArr.length();
        VideoData[] videoData = new VideoData[arrLength];

        for (int i = 0; i < arrLength; i++) {
            videoData[i] = new VideoData();

            JSONObject idJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("id");
            JSONObject snippetJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("snippet");

            videoData[i].setChannelTitle(snippetJsonObj.getString("channelTitle"));
            videoData[i].setId(idJsonObj.getString("videoId"));
            videoData[i].setPublished(snippetJsonObj.getString("publishedAt"));
            videoData[i].setTitle(snippetJsonObj.getString("title"));
            videoData[i].setChannelId(snippetJsonObj.getString("channelId"));
            videoData[i].setDecsription(snippetJsonObj.getString("description"));
            videoData[i].setTotalResults(mainJsonObj.getJSONObject("pageInfo").getString("totalResults"));
            videoData[i].setTypeOfSearch(type);

            JSONObject thumbnailsJsonObj = snippetJsonObj.getJSONObject("thumbnails");
            JSONObject defaultJsonObj = thumbnailsJsonObj.getJSONObject("default");
            videoData[i].setImg_src(defaultJsonObj.getString("url"));
        }
        return videoData;
    }

    private VideoData[] getEachVideoDetails(VideoData[] videoData) throws JSONException {
        String jsonResult;
        try {
            final String BASE_URL = "https://www.googleapis.com/youtube/v3/videos?";
            final String PART_PARAM = "part";
            final String ID_PARAM = "id";
            final String KEY_PARAM = "key";

            StringBuilder videos_id = new StringBuilder();
            for (int i = 0; i < videoData.length; i++) {
                if (i != videoData.length - 1) {
                    videos_id.append(videoData[i].getId() + ",");
                } else {
                    videos_id.append(videoData[i].getId());
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
                return videoData;
            }

            JSONObject mainJsonObj = new JSONObject(jsonResult);
            JSONArray itemsJsonArr = mainJsonObj.getJSONArray("items");
            int arrLength = itemsJsonArr.length();

            for (int i = 0; i < arrLength; i++) {
                JSONObject contentDetailsJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("contentDetails");
                JSONObject statisticsJsonObj = itemsJsonArr.getJSONObject(i).getJSONObject("statistics");
                videoData[i].setLength(contentDetailsJsonObj.getString("duration"));
                videoData[i].setCountViews(statisticsJsonObj.getString("viewCount"));
                videoData[i].setLikes(statisticsJsonObj.getLong("likeCount"));
                videoData[i].setDislikes(statisticsJsonObj.getLong("dislikeCount"));
            }
        } catch (MalformedURLException e) {
            Log.e("my", "MalformedURLException Error " + e.toString(), e);
        }
        return videoData;
    }
}