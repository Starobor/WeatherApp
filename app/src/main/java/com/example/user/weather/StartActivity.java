package com.example.user.weather;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class of start activity. Shows activity with the choice of the city.
 * At the push of a button makes a request for open weather map api. Data is received in JSON.
 * In AutoCompleteTextView the query to google autocomplete place api is made each
 * time the text in it is changed.
 *
 * @author Igor Starobor
 * @version 1.0
 */
public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String OPEN_WEATHER_MAP_API = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private static final String GOOGLE_PLACES_API = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
    private static final String OPEN_WEATHER_MAPS_APP_ID = "d5600acbe92e85ff63bc06fa04297350";
    private static final String GOOGLE_PLACES_APP_ID = "AIzaSyDJYCvnfAHuYQOdy5WqnnJko6P5PeCYwRk";
    private AutoCompleteTextView autoCompleteTextView;
    private ImageView startActivityBackground;
    private TextView enterCityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        findViews();
        setTimeOfDayBackground();
        setAutocompleteTextChangeListener();
    }

    /**
     * Goes to the main activity (the forecast time list) passes to the main activity and
     * transfers JSON with the weather.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("cityName", autoCompleteTextView.getText().toString());
        String cityName = autoCompleteTextView.getText().toString();
        AsyncTaskWeather asyncTaskAutoComplete = new AsyncTaskWeather();
        asyncTaskAutoComplete.execute(cityName);
        try {
            JSONObject jsonWeather = asyncTaskAutoComplete.get();
            if (jsonWeather != null) {
                intent.putExtra("jsonData", jsonWeather.toString());
                startActivity(intent);
            } else Toast.makeText(getApplicationContext(), "Bad Input", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes request for open weather map api and returns a forecast in json format.
     */
    private class AsyncTaskWeather extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... cityName) {
            final String urlWeather = OPEN_WEATHER_MAP_API + cityName[0] + "&APPID=" + OPEN_WEATHER_MAPS_APP_ID + "&units=metric";
            return getJsonWeather(urlWeather);
        }

        private JSONObject getJsonWeather(final String urlWeatherStr) {
            try {
                URL url = new URL(urlWeatherStr);
                Log.d("Url request", String.valueOf(url));
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) json.append(line).append("\n");
                reader.close();
                return new JSONObject(json.toString());
            } catch (Exception e) {
                System.out.println("Exception " + e.getMessage());
                return null;
            }
        }

    }

    /**
     * The thread that makes the request to google and changes the selection in the
     * AutoCompleteTextView.
     */
    private class AsyncTaskAutoComplete extends AsyncTask<String, Object, ArrayAdapter<String>> {

        @Override
        protected ArrayAdapter<String> doInBackground(String... cityName) {
            return new ArrayAdapter<>(StartActivity.this, android.R.layout.simple_dropdown_item_1line, getPlaces(cityName[0]));
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> adapter) {
            autoCompleteTextView.setAdapter(adapter);
        }

        ArrayList<String> getPlaces(String cityName) {
            final String urlGoogle = GOOGLE_PLACES_API + cityName + "&key=" + GOOGLE_PLACES_APP_ID + "&types=%28cities%29";
            JSONObject jsonGooglePlace = getJsonGooglePlace(urlGoogle);
            if (jsonGooglePlace != null) {
                try {
                    ArrayList<String> places = new ArrayList<>();
                    for (int i = 0; i < jsonGooglePlace.getJSONArray("predictions").length(); i++) {
                        places.add(jsonGooglePlace.getJSONArray("predictions").getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"));
                    }
                    return places;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private JSONObject getJsonGooglePlace(final String urlWeatherStr) {
            try {
                URL url = new URL(urlWeatherStr);
                Log.d("Url request", String.valueOf(url));
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) json.append(line).append("\n");
                reader.close();
                return new JSONObject(json.toString());
            } catch (Exception e) {
                System.out.println("Exception " + e.getMessage());
                return null;
            }
        }

    }

    /**
     * Sends a request to Google every time the text AutoCompleteTextView is changes.
     */
    private void setAutocompleteTextChangeListener() {
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AsyncTaskAutoComplete asyncTaskAutoComplete = new AsyncTaskAutoComplete();
                asyncTaskAutoComplete.execute(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Sets a theme for activity depending on the time of day.
     */
    private void setTimeOfDayBackground() {
        int startDay = 4;
        int startNight = 21;
        int currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentTime < startNight && currentTime > startDay)
            startActivityBackground.setImageResource(R.drawable.landscape);
        else {
            startActivityBackground.setImageResource(R.drawable.landscape_night);
            enterCityTextView.setTextColor(Color.WHITE);
            autoCompleteTextView.setTextColor(Color.WHITE);
        }
    }

    /**
     * Find views by id.
     */
    private void findViews() {
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.cityNameAutocomplete);
        Button cityConfirmation = (Button) findViewById(R.id.countryConfirmationButton);
        startActivityBackground = (ImageView) findViewById(R.id.startActivityBackground);
        enterCityTextView = (TextView) findViewById(R.id.enterCityTextView);
        cityConfirmation.setOnClickListener(this);
    }
}