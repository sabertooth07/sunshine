package com.adhepalliworks.android.sunshine;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adhepalliworks.android.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;
    int a;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }


    /**
     * Prepare the weather high/lows for presentation.
     */
//    private String formatHighLows(Context context, double high, double low) {
//        boolean isMetric = Utility.isMetric(mContext);
//        String highLowStr =
//                Utility.formatTemperature(context, high, isMetric) + "/" +
//                Utility.formatTemperature(context, low, isMetric);
//        return highLowStr;
//    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    // Example string: <date> - <description> - <high temp>/<low temp>
//    private String convertCursorRowToUXFormat(Cursor cursor) {
//        String highAndLow = formatHighLows(
//                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
//                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));
//
//        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
//                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
//                " - " + highAndLow;
//    }


    private class RecycleViewHolder {
        private final ImageView iconView;
        private final TextView dateView;
        private final TextView highTempView;
        private final TextView lowTempView;
        private final TextView descView;

        RecycleViewHolder (View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
            highTempView = (TextView)view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView)view.findViewById(R.id.list_item_low_textview);
            descView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
        }
    }
    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_forcast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(new RecycleViewHolder(view));
        return view;

    }


    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        //TextView tv = (TextView)view;
        //tv.setText(convertCursorRowToUXFormat(cursor));

        RecycleViewHolder viewHolder = (RecycleViewHolder) view.getTag();


        // Read weather icon ID from cursor
        long weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);

        // Use placeholder image for now
        ImageView iconView = viewHolder.iconView;
        //ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        iconView.setImageResource(R.drawable.ic_launcher);

        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, date));

        Boolean isMetric = Utility.isMetric(context);

        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        TextView highTempView = viewHolder.highTempView;
        highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        TextView lowTempView = viewHolder.lowTempView;
        lowTempView.setText(Utility.formatTemperature(context, low, isMetric));

        String desc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        TextView descView = viewHolder.descView;
        descView.setText(desc);
    }
}
