package pro.myvideos.youtubeplayer.data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Helper {

    private static HashMap<String, String> regexMap = new HashMap<String, String>();

    public static String getJsonResultByUrl(URL url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            // Create the request
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //Log.d("my", "HTTP_OK");
//                SearchVideoTask.CONNECTION_STATUS = true;
            } else {
                Log.d("my", "ResponseCode: " + urlConnection.getResponseCode());
                return null;
            }

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString();

        } catch (IOException e) {
            Log.e("my", "IOException Error " + e.toString(), e);
//            SearchVideoTask.CONNECTION_STATUS = false;
            // If the code didn't successfully get the  data.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("my", "Error closing stream", e);
                }
            }
        }
    }

    public static String formatDuration(String duration) {
        if (duration != null && duration.length() > 0) {
            regexMap.put("PT(\\d\\d)S", "00:$1");
            regexMap.put("PT(\\d\\d)M", "$1:00");
            regexMap.put("PT(\\d\\d)H", "$1:00:00");
            regexMap.put("PT(\\d\\d)M(\\d\\d)S", "$1:$2");
            regexMap.put("PT(\\d\\d)H(\\d\\d)S", "$1:00:$2");
            regexMap.put("PT(\\d\\d)H(\\d\\d)M", "$1:$2:00");
            regexMap.put("PT(\\d\\d)H(\\d\\d)M(\\d\\d)S", "$1:$2:$3");

            String regex2two = "(?<=[^\\d])(\\d)(?=[^\\d])";
            String two = "0$1";
            String d = duration.replaceAll(regex2two, two);
            String regex = getRegex(d);
            if (regex != null) {
                return d.replaceAll(regex, regexMap.get(regex));
            }
        }
        return "formatDuration - err";
    }

    private static String getRegex(String date) {
        for (String r : regexMap.keySet())
            if (Pattern.matches(r, date))
                return r;
        return null;
    }

    public static String formatViews(String views) {
        String result = views;
        if (views != null) {
            int len = views.length();
            if (len > 6) {
                result = views.substring(0, len - 6) + "M";
            } else if (len > 3) {
                result = views.substring(0, len - 3) + "K";
            }
        }

        //MyLogger.log("ZAQ-Helper", "Views: " + views + "    result: " + result);
        return result;
    }


}
