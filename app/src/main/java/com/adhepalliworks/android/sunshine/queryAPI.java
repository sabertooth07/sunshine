package com.adhepalliworks.android.sunshine;

import android.net.Uri;
import android.text.format.Time;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class queryAPI {

    //private final String targetURL;
    private String forecastJsonStr;
    private Uri uri;

    final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    final String QUERY_PARAM = "q";
    final String UNITS_PARAM = "units";
    final String DAYS_PARAM = "cnt";
    final String MAX_TEMP = "max";
    final String MIN_TEMP = "min";
    final String ARRAY_TEMP = "temp";
    final String ARRAY_WEATHER = "weather";
    final String DESC_WEATHER = "description";


    queryAPI(String zip, String units, String daysCount) throws MalformedURLException, IOException {
        uri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, zip)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, daysCount)
                .build();
        queryOpenWeatherAPI();
    }

    public String getforecastJsonStr() {
        return forecastJsonStr;
    }

    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    JSONObject string2Json() throws JSONException {
        JSONObject sampleJsonObject = null;
        sampleJsonObject = new JSONObject(forecastJsonStr);
        return sampleJsonObject;
    }

    String fetchTempMax(int day) throws JSONException {
        JSONObject weather = string2Json();

        JSONArray days = weather.getJSONArray("list");
        JSONObject dayInfo = days.getJSONObject(day);
        JSONObject temperatureInfo = dayInfo.getJSONObject(ARRAY_TEMP);
        return temperatureInfo.getString(MAX_TEMP);
    }

    String fetchTempMin(int day) throws JSONException {
        JSONObject weather = string2Json();

        JSONArray days = weather.getJSONArray("list");
        JSONObject dayInfo = days.getJSONObject(day);
        JSONObject temperatureInfo = dayInfo.getJSONObject(ARRAY_TEMP);
        return temperatureInfo.getString(MIN_TEMP);
    }

    String fetchWeatherDesc(int day) throws JSONException {
        JSONObject weather = string2Json();

        JSONArray days = weather.getJSONArray("list");
        JSONObject dayInfo = days.getJSONObject(day);
        JSONArray weatherInfo = dayInfo.getJSONArray(ARRAY_WEATHER);
        JSONObject weatherDesc = weatherInfo.getJSONObject(0);
        return weatherDesc.getString(DESC_WEATHER);
    }

    String fetchDateTime(int day) {
        Time dayTime = new Time();
        dayTime.setToNow();
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
        long dateTime;
        dateTime = dayTime.setJulianDay(julianStartDay + day);
        return getReadableDateString(dateTime);

    }

    String[] parseMaxMinDesc() throws JSONException {
        JSONObject weather = string2Json();

        JSONArray days = weather.getJSONArray("list");
        String[] resultStrs = new String[days.length()];
        for (int day = 0; day < days.length(); day++) {
            resultStrs[day] = fetchDateTime(day) + " - " + fetchWeatherDesc(day) + " - " + fetchTempMax(day) + "/" + fetchTempMin(day);
        }
        return resultStrs;
    }

    void queryOpenWeatherAPI() throws MalformedURLException, IOException, ConnectException {
        URL url;
        HttpURLConnection connection = null;

        url = new URL(uri.toString());
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        InputStream inputStream = connection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            forecastJsonStr = null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            forecastJsonStr = null;
        }

        forecastJsonStr = buffer.toString();
    }
}
