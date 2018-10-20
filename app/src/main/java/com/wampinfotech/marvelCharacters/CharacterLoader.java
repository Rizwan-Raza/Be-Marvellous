package com.wampinfotech.marvelCharacters;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CharacterLoader extends AsyncTaskLoader<List<Characters>> {
    /*
      Tag for log messages
     */

    // private static final String LOG_TAG = CharacterLoader.class.getName();

    /**
     * Query URL
     */
    private URL _Url;
    private Context _Context;

    /**
     * Constructs a new {@link CharacterLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    CharacterLoader(Context context, String url) {
        super(context);
        _Context = context;
//        Toast.makeText(context, "URL: " + url, Toast.LENGTH_SHORT).show();
        try {
            _Url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStartLoading() {
        // Log.e(LOG_TAG, "Loader Started - onStartLoading()");
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Characters> loadInBackground() {
        // Log.e(LOG_TAG, "Loader Running - loadInBackground()");

        if (_Url == null) {
            return null;
        }
        return CharacterUtils.extractCharacters(_Context, _Url);
    }
}
