package io.github.kirillf.hashviewer.twitter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Twitter Search API response parser.
 * Parse JSON response into list of TwitterObjects
 */
public class TwitterParser {
    private static final String TAG = TwitterParser.class.getName();

    public List<TwitterObject> parse(String content) throws JSONException {
        List<TwitterObject> twitterObjects = new LinkedList<>();
        JSONObject jsonObject = new JSONObject(content);
        JSONArray statuses = jsonObject.getJSONArray("statuses");
        for (int i = 0; i < statuses.length(); i++) {
            try {
                JSONObject status = statuses.getJSONObject(i);
                long id = status.getLong("id");
                TwitterObject twitterObject = new TwitterObject(id);

                JSONObject entities = status.getJSONObject("entities");
                JSONArray tags = entities.getJSONArray("hashtags");
                List<String> tagsList = new LinkedList<>();
                for (int j = 0; j < tags.length(); j++) {
                    JSONObject tag = tags.getJSONObject(j);
                    tagsList.add(tag.getString("text"));
                }
                twitterObject.setTags(tagsList);

                String text = status.getString("text");
                twitterObject.setText(text);
                String date = status.getString("created_at");
                twitterObject.setDate(convertDate(date));

                JSONObject user = status.getJSONObject("user");
                String name = user.getString("name");
                String screenName = user.getString("screen_name");
                String imageUrl = user.getString("profile_image_url_https");
                imageUrl = imageUrl.replace("_normal", "_bigger");
                twitterObject.setUserName(name);
                twitterObject.setProfileImageUrl(imageUrl);
                twitterObject.setScreenName(screenName);
                twitterObjects.add(twitterObject);
            } catch (JSONException e) {
                Log.w(TAG, e);
            }
        }
        return twitterObjects;
    }

    private long convertDate(String date) {
        try {
            Date formattedDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(date);
            return formattedDate.getTime();
        } catch (ParseException e) {
            Log.w(TAG, "Unable to parse date: " + e);
        }
        return 0;
    }
}
