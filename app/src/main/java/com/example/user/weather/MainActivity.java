package com.example.user.weather;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class of main activity. Displays a list of weather for 5 days every 3 hours.
 *
 * @author Igor Starobor
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private JSONObject jsonWeather;
    private ListView daysListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        daysListView = (ListView) findViewById(R.id.daysList);
        jsonWeather = getIntentJSON();
        setListItems();
        itemAction();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeCityItemMenu:
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
                break;
            case R.id.aboutItemMenu:
                showDialogAbout();
        }
        return true;
    }

    /**
     * Action after selected item. Transmits jsonWeather and selected item index to
     * next open activity.
     */
    private void itemAction() {
        daysListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra("jsonForecast", jsonWeather.toString());
                intent.putExtra("numItem", index);
                startActivity(intent);
            }
        });
    }

    /**
     * Find dates and times in JSON.
     *
     * @param data - JSON with weather.
     * @return array list with dates and times.
     */
    private ArrayList<String> findDaysAndTimes(JSONObject data) {
        ArrayList<String> daysAndTimes = new ArrayList<>();
        try {
            for (int i = 0; i < data.getJSONArray("list").length(); i++) {
                String time = data.getJSONArray("list").getJSONObject(i).getString("dt_txt");
                daysAndTimes.add(time);
            }
            return daysAndTimes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get JSON (open weather map api) from intent.
     *
     * @return JSON
     */
    public JSONObject getIntentJSON() {
        try {
            return new JSONObject(getIntent().getStringExtra("jsonData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Show dialog window about program.
     */
    private void showDialogAbout() {
        AlertDialog.Builder aboutDialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
     * Set items (Days and Times) for ListVew. Data get from JSON file.
     */
    public void setListItems() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, findDaysAndTimes(jsonWeather));
        daysListView.setAdapter(adapter);
    }
}