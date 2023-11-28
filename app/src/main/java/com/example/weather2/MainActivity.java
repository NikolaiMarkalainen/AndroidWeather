package com.example.weather2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.Manifest;
import android.content.Context;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String API_KEY = "";
    private LocationManager locManager;
    private Button locateUserButton;
    private Button searchForNewArea;
    private TextView temperature;
    private TextView windspeed;
    private TextView cityName;
    private TextView cloudiness;

    private int counter;
    private ProgressBar pb;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locateUserButton = findViewById(R.id.locateUser);
        cityName = findViewById(R.id.cityName);
        windspeed = findViewById(R.id.windspeed);
        temperature = findViewById(R.id.temperature);
        cloudiness = findViewById(R.id.cloudiness);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mQueue = Volley.newRequestQueue(this);
            locateUserButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    progress();
                    startLocationUpdates();
                }
            });
        }
    }

    public void customSearchActivity(View v) {
        Intent intent = new Intent(this, CustomSearchActivity.class);
        startActivity(intent);
    }
    private void progress() {
        pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);
        final Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                counter++;
                pb.setProgress(counter);
                if(cityName.getText() != "" && windspeed.getText() != "" && temperature.getText() != "" && cloudiness.getText() != "") {
                    t.cancel();
                    pb.setVisibility(View.INVISIBLE);
                }
            }
        };
        t.schedule(tt, 0, 100);
    }
    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            locManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            // Called when the location is updated
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();


                            jsonParse(latitude, longitude);

                            locManager.removeUpdates(this);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    }
            );
        }
    }
    private void jsonParse(double latitude, double longitude){
        Log.d("TRASH", "WE ARE IN JSONPARSE");
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;
        JsonObjectRequest request =  new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.d("WE ARE TRYING TO LOG SHIT", "D");
                            String city = response.getString("name");
                            double ws = response.getJSONObject("wind").getDouble("speed");
                            double temp = response.getJSONObject("main").getDouble("temp");
                            int cloud = response.getJSONObject("clouds").getInt("all");

                            cityName.setText(city);
                            windspeed.setText(getResources().getString(R.string.wind_label) + ": " + String.valueOf(ws));
                            temperature.setText(getResources().getString(R.string.temp_label) +": " + String.valueOf(Math.round(temp - 272.15)));
                            cloudiness.setText(getResources().getString(R.string.cloud_label) + ": " + String.valueOf(cloud) + "%");

                        } catch(JSONException e){
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
