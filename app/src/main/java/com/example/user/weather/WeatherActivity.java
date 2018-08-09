package com.example.user.weather;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Shows the weather at a specific time. Reflects the weather condition in the form of a picture.
 * Depending on the time of day of the chosen forecast, the topic of activity.
 *
 * @author Igor Starobor
 * @version 1.0
 */
public class WeatherActivity extends AppCompatActivity {
    private JSONObject json;
    private TextView tempTextView;
    private TextView minTempTextView;
    private TextView maxTempTextView;
    private TextView humidityTextView;
    private TextView windSpeedTextView;
    private TextView tempValue;
    private TextView minTempValue;
    private TextView maxTempValue;
    private TextView humidityValue;
    private TextView windSpeedValue;
    private TextView nameCityAndCountry;
    private TextView dateWeather;
    private TextView timeWeather;
    private ImageView weatherImg;
    private RelativeLayout weatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        findViews();
        json = getIntentJSON();
        int position = getIntent().getIntExtra("numItem", 0);
        setValues(position);
        setImg(position);
        setBackgroundTimeOfDay(position);
        weatherImgClickAction(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeCityItemMenu:
                Intent intent = new Intent(WeatherActivity.this, StartActivity.class);
                startActivity(intent);
                break;
            case R.id.aboutItemMenu:
                showDialogAbout();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Set img for weather description.
     *
     * @param position - index selected item on ListView in activity_main (date and time).
     */
    private void setImg(int position) {
        try {
            switch (json.getJSONArray("list").getJSONObject(position)
                    .getJSONArray("weather").getJSONObject(0).getString("icon")) {
                case "01d":
                    weatherImg.setImageResource(R.drawable.one_d);
                    break;
                case "02d":
                    weatherImg.setImageResource(R.drawable.two_d);
                    break;
                case "03d":
                    weatherImg.setImageResource(R.drawable.three_d);
                    break;
                case "04d":
                    weatherImg.setImageResource(R.drawable.four_d);
                    break;
                case "09d":
                    weatherImg.setImageResource(R.drawable.nine_d);
                    break;
                case "10d":
                    weatherImg.setImageResource(R.drawable.ten_d);
                    break;
                case "11d":
                    weatherImg.setImageResource(R.drawable.eleven_d);
                    break;
                case "13d":
                    weatherImg.setImageResource(R.drawable.thirteen_d);
                    break;
                case "50d":
                    weatherImg.setImageResource(R.drawable.fifty_d);
                    break;
                case "01n":
                    weatherImg.setImageResource(R.drawable.one_n);
                    break;
                case "02n":
                    weatherImg.setImageResource(R.drawable.two_n);
                    break;
                case "03n":
                    weatherImg.setImageResource(R.drawable.three_n);
                    break;
                case "04n":
                    weatherImg.setImageResource(R.drawable.four_n);
                    break;
                case "09n":
                    weatherImg.setImageResource(R.drawable.nine_n);
                    break;
                case "10n":
                    weatherImg.setImageResource(R.drawable.ten_n);
                    break;
                case "11n":
                    weatherImg.setImageResource(R.drawable.eleven_n);
                    break;
                case "13n":
                    weatherImg.setImageResource(R.drawable.thirteen_n);
                    break;
                case "50n":
                    weatherImg.setImageResource(R.drawable.fifty_n);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a theme for activity depending on the time of day.
     *
     * @param position - index selected item on ListView in activity_main (date and time).
     */
    private void setBackgroundTimeOfDay(int position) {
        try {
            String timesOfDay = json.getJSONArray("list")
                    .getJSONObject(position).getJSONArray("weather")
                    .getJSONObject(0).getString("icon").substring(2);
            switch (timesOfDay) {
                case "d":
                    weatherActivity.setBackgroundColor(getResources().getColor(R.color.dayBackground));
                    break;
                case "n":
                    weatherActivity.setBackgroundColor(getResources().getColor(R.color.nightBackground));
                    tempValue.setTextColor(Color.WHITE);
                    minTempValue.setTextColor(Color.WHITE);
                    maxTempValue.setTextColor(Color.WHITE);
                    humidityValue.setTextColor(Color.WHITE);
                    windSpeedValue.setTextColor(Color.WHITE);
                    nameCityAndCountry.setTextColor(Color.WHITE);
                    dateWeather.setTextColor(Color.WHITE);
                    timeWeather.setTextColor(Color.WHITE);
                    tempTextView.setTextColor(Color.WHITE);
                    minTempTextView.setTextColor(Color.WHITE);
                    maxTempTextView.setTextColor(Color.WHITE);
                    humidityTextView.setTextColor(Color.WHITE);
                    windSpeedTextView.setTextColor(Color.WHITE);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets values for TextViews weather data.
     *
     * @param position - index selected item on ListView in activity_main (date and time).
     */
    private void setValues(int position) {
        try {
            JSONObject selectedItem = json.getJSONArray("list").getJSONObject(position);
            nameCityAndCountry.setText(json.getJSONObject("city").getString("name") + " " + json.getJSONObject("city").getString("country"));
            tempValue.setText(selectedItem.getJSONObject("main").getString("temp") + "\u00B0C");
            minTempValue.setText(selectedItem.getJSONObject("main").getString("temp_min") + "\u00B0C");
            maxTempValue.setText(selectedItem.getJSONObject("main").getString("temp_max") + "\u00B0C");
            humidityValue.setText(selectedItem.getJSONObject("main").getString("humidity") + "\u0025");
            windSpeedValue.setText(selectedItem.getJSONObject("wind").getString("speed") + "m/sec");
            dateWeather.setText(selectedItem.getString("dt_txt").substring(0, selectedItem.getString("dt_txt").indexOf(" ")));
            timeWeather.setText(selectedItem.getString("dt_txt").substring(selectedItem.getString("dt_txt").indexOf(" ")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find views by id.
     */
    private void findViews() {
        tempValue = (TextView) findViewById(R.id.tempValue);
        minTempValue = (TextView) findViewById(R.id.minTempValue);
        maxTempValue = (TextView) findViewById(R.id.maxTempValue);
        humidityValue = (TextView) findViewById(R.id.humidityValue);
        windSpeedValue = (TextView) findViewById(R.id.windSpeedValue);
        nameCityAndCountry = (TextView) findViewById(R.id.nameCityAndCountry);
        timeWeather = (TextView) findViewById(R.id.timeWeather);
        dateWeather = (TextView) findViewById(R.id.dateWeather);
        weatherImg = (ImageView) findViewById(R.id.imgWeather);
        weatherActivity = (RelativeLayout) findViewById(R.id.weatherActivity);
        tempTextView = (TextView) findViewById(R.id.temperatureTextView);
        minTempTextView = (TextView) findViewById(R.id.minTemperatureTextView);
        maxTempTextView = (TextView) findViewById(R.id.maxTempTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        windSpeedTextView = (TextView) findViewById(R.id.windSpeedTextView);
    }

    /**
     * Show dialog window about program.
     */
    private void showDialogAbout() {
        AlertDialog.Builder aboutDialogBuilder = new AlertDialog.Builder(WeatherActivity.this);
        aboutDialogBuilder.setTitle("About program");
        aboutDialogBuilder.setMessage("Programmer - Igor Starobor\n" + "Designer - Alexander Makarevich\n" + "Version - 1.0");
        aboutDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        aboutDialogBuilder.create().show();
    }

    /**
     * Get JSON with weather from intent.
     *
     * @return JSON with weather.
     */
    public JSONObject getIntentJSON() {
        try {
            return new JSONObject(getIntent().getStringExtra("jsonForecast"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Show message after click on img for weather description.
     *
     * @param position - index selected item on ListView in activity_main (date and time).
     */
    private void weatherImgClickAction(final int position) {
        weatherImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String description = json.getJSONArray("list").getJSONObject(position)
                            .getJSONArray("weather").getJSONObject(0).getString("description");
                    Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}