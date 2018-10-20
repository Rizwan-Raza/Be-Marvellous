package com.wampinfotech.marvelCharacters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
final class CharacterUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = CharacterUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link CharacterUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name CharacterUtils (and an object instance of CharacterUtils is not needed).
     */
    private CharacterUtils() {
    }

    /**
     * Return a list of {@link Characters} objects that has been built up from
     * parsing a JSON response.
     */
    static List<Characters> extractCharacters(Context context, URL url) {
//        Log.e(LOG_TAG, "Extracting Characters - extractCharacters()");

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        if (jsonResponse == null || jsonResponse.isEmpty()) {
            String type = "character";
            if (url.getQuery().contains("group")) {
                type = "group";
            } else if (url.getQuery().contains("item")) {
                type = "item";
            } else if (url.getQuery().contains("movie")) {
                type = "movie";
            } else if (url.getQuery().contains("tv")) {
                type = "tv";
            }
            Log.e(LOG_TAG, type.toUpperCase() + " Cache Retrieved");
            jsonResponse = CacheHelper.retrieve(context, type);
        } else {
            // saving result in cache
            if (url.getQuery().contains("character")) {
                CacheHelper.save(context, "character", jsonResponse);
                Log.e(LOG_TAG, "Character Cache Added");
            } else if (url.getQuery().contains("group")) {
                CacheHelper.save(context, "group", jsonResponse);
                Log.e(LOG_TAG, "Group Cache Added");
            } else if (url.getQuery().contains("item")) {
                CacheHelper.save(context, "item", jsonResponse);
                Log.e(LOG_TAG, "Item Cache Added");
            } else if (url.getQuery().contains("movie")) {
                CacheHelper.save(context, "movie", jsonResponse);
                Log.e(LOG_TAG, "Movie Cache Added");
            } else if (url.getQuery().contains("tv")) {
                CacheHelper.save(context, "tv", jsonResponse);
                Log.e(LOG_TAG, "TV Cache Added");
            }
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        return extractCharactersFromJson(jsonResponse);
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the characters JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link List} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */
    private static List<Characters> extractCharactersFromJson(String charsJSON) {

        String baseUrl = "https://terrigen-cdn-dev.marvel.com/content/prod/1x/";
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(charsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Characters> characters = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Parse the response given by the charsJSON string and
            // build up a list of Earthquake objects with the corresponding data.

            JSONArray charactersArray = new JSONObject(charsJSON).getJSONObject("data").getJSONObject("results").getJSONArray("data");

            for (int i = 0; i < charactersArray.length(); i++) {
                JSONObject character = charactersArray.getJSONObject(i);
                String name = character.getString("headline");
                String secondary = character.getString("secondary_text");
                String desc = character.getString("description");
                String image = baseUrl + character.getJSONObject("image").getString("filename");
                Characters newOne = new Characters(name, secondary, desc, image);
                characters.add(newOne);
            }
//            for (int i = 0; i < charactersArray.length(); i++) {
//                JSONObject character = charactersArray.getJSONObject(i);
//                String name = character.getString("name");
//                int id = character.getInt("id");
//                String desc = character.getString("desc");
//                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
//                Date update = spf.parse(character.getString("update"));
//                URL image = new URL(character.getString("image"));
//                Characters newOne = new Characters(name, id, desc, update, image);
//                characters.add(newOne);
//            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("CharacterUtils", "Problem parsing the characters JSON results", e);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            Log.e("CharacterUtils", "Problem parsing Image URL", e);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            Log.e("CharacterUtils", "Problem parsing Date", e);
        }

        // Return the list of earthquakes
        return characters;
    }

}