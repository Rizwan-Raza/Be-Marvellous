package com.wampinfotech.marvelCharacters;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Characters>>, FilterableFragment {

    /**
     * URL for earthquake data from the USGS dataset
     */
    private static final String REQUEST_URL =
            "https://www.marvel.com/v1/pagination/grid_cards";
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int GROUPS_LOADER_ID = 2;
    /**
     * Adapter for the list of earthquakes
     */
    private CharacterAdapter _Adapter;
    private TextView _EmptyStateTextView;

    // ?char_movie=Ant-Man%2CAnt-Man%20and%20The%20Wasp%2CAvengers%3A%20Age%20of%20Ultron%2CAvengers%3A%20Infinity%20War%2CBlack%20Panther%2CCaptain%20America%3A%20Civil%20War%2CCaptain%20America%3A%20The%20First%20Avenger%2CCaptain%20America%3A%20The%20Winter%20Soldier%2CCaptain%20Marvel%2CDoctor%20Strange%2CGuardians%20of%20the%20Galaxy%2CGuardians%20of%20the%20Galaxy%20Vol.%202%2CIron%20Man%2CIron%20Man%202%2CIron%20Man%203%2CSpider-Man%3A%20Homecoming%2CThe%20Avengers%2CThe%20Incredible%20Hulk%2CThor%2CThor%3A%20Ragnarok%2CThor%3A%20The%20Dark%20World&limit=60&entityType=character&sortDirection=asc&sortField=title
    private View _RootView;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _RootView = inflater.inflate(R.layout.list_frame, container, false);
        // Find a reference to the {@link ListView} in the layout
        GridView charactersListView = _RootView.findViewById(R.id.list);

        _EmptyStateTextView = _RootView.findViewById(R.id.empty_view);
        charactersListView.setEmptyView(_EmptyStateTextView);

        // Create a new {@link ArrayAdapter} of earthquakes
        _Adapter = new CharacterAdapter(_RootView.getContext(), new ArrayList<Characters>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        charactersListView.setAdapter(_Adapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = Objects.requireNonNull(getActivity()).getSupportLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(GROUPS_LOADER_ID, null, this);


        // Otherwise, display error
        // First, hide loading indicator so error message will be visible
//            View loadingIndicator = _RootView.findViewById(R.id.loading_indicator);
//            loadingIndicator.setVisibility(View.GONE);
//
//            // Update empty state with no connection error message
//            _EmptyStateTextView.setText(R.string.no_internet_connection);
        return _RootView;
    }

    @NonNull
    @Override
    public Loader<List<Characters>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String limit = sharedPrefs.getString(
                getString(R.string.settings_limit_key),
                getString(R.string.settings_limit_default));

        String charType = sharedPrefs.getString(
                getString(R.string.settings_char_type_key),
                getString(R.string.settings_char_type_default)
        );


        String order = sharedPrefs.getString(
                getString(R.string.settings_order_key),
                getString(R.string.settings_order_default));
        String orderDir = "asc", orderField = "title";
        if (order != null) {
            if (order.equalsIgnoreCase(getString(R.string.settings_order_date_desc_label))) {
                orderDir = getString(R.string.settings_desc_value);
                orderField = getString(R.string.settings_order_date_value);
            } else if (order.equalsIgnoreCase(getString(R.string.settings_order_date_asc_label))) {
                orderDir = getString(R.string.settings_asc_value);
                orderField = getString(R.string.settings_order_date_value);
            } else if (order.equalsIgnoreCase(getString(R.string.settings_order_alp_desc_label))) {
                orderDir = getString(R.string.settings_desc_value);
                orderField = getString(R.string.settings_alp_value);
            } else {
                orderDir = getString(R.string.settings_asc_value);
                orderField = getString(R.string.settings_alp_value);
            }
        }

        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("limit", limit);
        uriBuilder.appendQueryParameter("entityType", "group");
        uriBuilder.appendQueryParameter("sortDirection", orderDir);
        uriBuilder.appendQueryParameter("sortField", orderField);

        if (!(charType != null && charType.equalsIgnoreCase(getString(R.string.settings_char_type_all_label)))) {
            uriBuilder.appendQueryParameter("group_type", charType);
        }
        // Create a new loader for the given URL
        // Log.e(LOG_TAG, "Loader Created - onCreateLoader()");
        return new CharacterLoader(getContext(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Characters>> loader, List<Characters> chars) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = _RootView.findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        _EmptyStateTextView.setText(R.string.no_groups);

        // Log.e(LOG_TAG, "Loader Finished - onLoadFinished()");
        // Clear the adapter of previous earthquake data
        _Adapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (chars != null && !chars.isEmpty()) {
            _Adapter.addAll(chars);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Characters>> loader) {
        // Log.e(LOG_TAG, "Loader Reset - onLoadReset()");
        // Loader reset, so we can clear out our existing data.
        _Adapter.clear();
    }

    @Override
    public void filter(String s) {
        _Adapter.getFilter().filter(s);
    }
}
