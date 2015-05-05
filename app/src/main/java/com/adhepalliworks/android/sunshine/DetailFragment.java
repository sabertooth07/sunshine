package com.adhepalliworks.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;


import com.adhepalliworks.android.sunshine.R;
import com.adhepalliworks.android.sunshine.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private ShareActionProvider mShareActionProvider;
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecastStr;
    private static final int DETAIL_LOADER = 0;


    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    private TextView friendlyDateView;
    private TextView dateView;
    private TextView highTempView;
    private ImageView weatherIconView;
    private TextView lowTempView;
    private TextView descView;
    private TextView humidityView;
    private TextView windView;
    private TextView pressureView;




    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        friendlyDateView = (TextView) rootView.findViewById(R.id.detail_friendlydate_textview);
        dateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        highTempView = (TextView) rootView.findViewById(R.id.detail_high_temp);
        weatherIconView = (ImageView) rootView.findViewById(R.id.detail_weather_image);
        lowTempView = (TextView) rootView.findViewById(R.id.detail_low_temp);
        descView = (TextView) rootView.findViewById(R.id.detail_desc);
        humidityView = (TextView) rootView.findViewById(R.id.detail_humidity);
        windView = (TextView) rootView.findViewById(R.id.detail_wind);
        pressureView = (TextView) rootView.findViewById(R.id.detail_pressure);


//        Intent intent = getActivity().getIntent();
//        if (intent != null) {
//            mForecastStr = intent.getDataString();
//        }
//
//        if (null != mForecastStr) {
//            ((TextView) rootView.findViewById(R.id.detail_text))
//                    .setText(mForecastStr);
//        }

        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mForecastStr != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }

    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getActivity(),
                getActivity().getIntent().getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!cursor.moveToFirst()) {
            Log.v(LOG_TAG, "empty cursor");
        } else {
            Log.v(LOG_TAG, "cursor not empty");
        }

        int weatherConditionId = cursor.getInt(COL_WEATHER_CONDITION_ID);

        String dateString = Utility.formatDate(cursor.getLong(COL_WEATHER_DATE));
        String weatherDescription = cursor.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());
        String weatherMin = Utility.formatTemperature(getActivity(), cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        String weatherMax = Utility.formatTemperature(getActivity(), cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);


        mForecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, weatherMax, weatherMin);

        //TextView detailTextView = (TextView)getView().findViewById(R.id.detail_text);
        //detailTextView.setText(mForecastStr);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

        highTempView.setText(weatherMax);
        lowTempView.setText(weatherMin);
        descView.setText(weatherDescription);
        dateView.setText(dateString);
        friendlyDateView.setText(Utility.getFriendlyDayString(getActivity(), cursor.getLong(COL_WEATHER_DATE)));
        weatherIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherConditionId));

        // Read humidity from cursor and update view
        float humidity = cursor.getFloat(COL_WEATHER_HUMIDITY);
        humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        // Read wind speed and direction from cursor and update view
        float windSpeedStr = cursor.getFloat(COL_WEATHER_WIND_SPEED);
        float windDirStr = cursor.getFloat(COL_WEATHER_DEGREES);
        windView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

        // Read pressure from cursor and update view
        float pressure = cursor.getFloat(COL_WEATHER_PRESSURE);
        pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        cursor.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }


}
