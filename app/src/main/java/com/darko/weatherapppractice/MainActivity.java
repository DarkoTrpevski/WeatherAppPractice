package com.darko.weatherapppractice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    final int REQUEST_CODE = 1;
    public static final String API_KEY = "e51965eac9c14d7e6743450970995885";

    public static final long MIN_TIME = 5000;
    public static final float MIN_DISTANCE = 1000;

    public static final String KEY_CITY_QUERY = "q";
    public static final String KEY_APP_QUERY = "appid";

    //----------------------------------------------------------------------------------------------
    TextView textViewCityLabel;
    ImageView imageViewWeather;
    ImageButton changeCityButton;
    TextView textViewTemperature;

    LocationManager locationManager;
    LocationListener locationListener;
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ChangeCityActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

    }

    private void bindViews() {
        textViewCityLabel = findViewById(R.id.locationTV);
        imageViewWeather = findViewById(R.id.weatherSymbolIV);
        textViewTemperature = findViewById(R.id.tempTV);
        changeCityButton = findViewById(R.id.changeCityButton);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent myIntent = getIntent();
        String city = myIntent.getStringExtra("city");
        if (city != null) {
            getCityWeather(city);
        } else {
            Log.d(TAG, "Getting weather location");
            getCurrentLocationWeather();
        }
    }


    private void getCityWeather(String city) {
        RequestParams requestParams = new RequestParams();
        requestParams.put(KEY_CITY_QUERY, city);
        requestParams.put(KEY_APP_QUERY, API_KEY);
        requestApiData(requestParams);
    }

    private void getCurrentLocationWeather() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                RequestParams requestParams = new RequestParams();
                requestParams.put("lat", latitude);
                requestParams.put("lon", longitude);
                requestParams.put(KEY_APP_QUERY, API_KEY);
                requestApiData(requestParams);
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
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;

        }
        String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult(): Permission granted");
                getCurrentLocationWeather();
            } else {
                Log.d(TAG, "onRequestPermissionsResult(): permission denied");
            }
        }

    }

    //TODO CHANGE THIS REQUEST WITH A RETROFIT CALL
    private void requestApiData(RequestParams requestParams) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_BASE_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                WeatherModel weatherData = WeatherModel.fromJson(response);
                if (weatherData != null) {
                    updateUI(weatherData);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d(TAG, "onFailure " + e.toString());
            }
        });
    }


    private void updateUI(WeatherModel weather) {
        textViewTemperature.setText(weather.getTemperature());
        textViewCityLabel.setText(weather.getCity());
        int resourceID = getResources().getIdentifier(weather.getIconName(), "drawable", getPackageName());
        imageViewWeather.setImageResource(resourceID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);
    }
}
