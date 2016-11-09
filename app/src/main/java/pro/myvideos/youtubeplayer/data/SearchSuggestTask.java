package pro.myvideos.youtubeplayer.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by B.E.L on 09/11/2016.
 */

public class SearchSuggestTask  extends AsyncTaskLoader<String[]> {

    private String keyword;
    public SearchSuggestTask(Context context, String keyword) {
        super(context);
        this.keyword = keyword;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (keyword != null) {
            forceLoad();
        }
    }

    @Override
    public String[] loadInBackground() {
        String jsonResult;
        //http://suggestqueries.google.com/complete/search?client=youtube&ds=yt&q=Query
        //http://suggestqueries.google.com/complete/search?client=youtube&ds=yt&alt=json&q=BI2
        //http://suggestqueries.google.com/complete/search?client=youtube&ds=yt&jsonp=suggestCallBack&q=BI2

        try {
            final String BASE_URL = "http://suggestqueries.google.com/complete/search?";
            final String CLIENT_PARAM = "client";
            final String DS_PARAM = "ds";
            final String QUERY_PARAM = "q";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(CLIENT_PARAM, "firefox")
                    .appendQueryParameter(DS_PARAM, "yt")
                    .appendQueryParameter(QUERY_PARAM, keyword)
                    .build();

            URL url = new URL(builtUri.toString());
            jsonResult = Helper.getJsonResultByUrl(url);

            //Log.d("my", jsonResult.toString());

            if (jsonResult == null || jsonResult.length() < 5) {
                return null;
            }

            try {
                return getSuggestsFromJson(jsonResult);
            } catch (JSONException e) {
                Log.d("my", "JSONException: " + e);
            }
        } catch (MalformedURLException e) {
            Log.e("my", "MalformedURLException Error " + e.toString(), e);
        }
        return null;
    }

    private String[] getSuggestsFromJson(String jsonResult) throws JSONException {

        JSONArray mainJsonArr = new JSONArray(jsonResult);
        JSONArray itemsJsonArr = mainJsonArr.getJSONArray(1);
        int arrLength = itemsJsonArr.length();
        String[] suggests = new String[arrLength];

        for (int i = 0; i < arrLength; i++) {
            suggests[i] = itemsJsonArr.getString(i);
        }

        return suggests;
    }

}
