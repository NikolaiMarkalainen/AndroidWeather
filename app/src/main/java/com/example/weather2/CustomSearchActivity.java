package com.example.weather2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class CustomSearchActivity extends AppCompatActivity {
    private EditText searchField;
    private static final String API_KEY = "";
    private TextView temperature;
    private RequestQueue mQueue;
    private TextView windspeed;
    private TextView cityName;
    private Button searchExecute;
    private TextView cloudiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.another_location);
        cityName = findViewById(R.id.city2);
        searchField = findViewById(R.id.searchField);
        temperature = findViewById(R.id.temp2);
        windspeed = findViewById(R.id.windspeed2);
        cloudiness = findViewById(R.id.cloud2);
        searchExecute = findViewById(R.id.search);

        mQueue = Volley.newRequestQueue(this);
        searchExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFetch();
            }
        });



    }
    public void backToMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startFetch() {

        String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + searchField.getText().toString() +"&appid=" + API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String city = response.getString("name");
                            double ws = response.getJSONObject("wind").getDouble("speed");
                            double temp = response.getJSONObject("main").getDouble("temp");
                            int cloud = response.getJSONObject("clouds").getInt("all");
                            cityName.setText(city);
                            windspeed.setText(getResources().getString(R.string.wind_label) + ": " + String.valueOf(ws));
                            temperature.setText(getResources().getString(R.string.temp_label) +": " + String.valueOf(Math.round(temp - 272.15)));
                            cloudiness.setText(getResources().getString(R.string.cloud_label) + ": " + String.valueOf(cloud) + "%");

                        } catch(JSONException e){
                            cityName.setText("Unknown");
                            windspeed.setText("");
                            temperature.setText("");
                            cloudiness.setText("");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}
