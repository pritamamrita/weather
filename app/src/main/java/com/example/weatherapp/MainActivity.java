package com.example.weatherapp;

import static android.location.LocationManager.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainActivity extends AppCompatActivity {
    public  class down extends AsyncTask<String, Void ,String>
    {

        @Override
        protected String doInBackground(String... z) {
            Log.d("TAG", "TAG"+z[0]);
            String s="http://api.weatherapi.com/v1/forecast.json?key=4fe492dfc0224b478f3160537212306&q=";
            String l="&aqi=no";
            s+=z[0];
            s+=l;
            Log.d("TAG", "TAG"+s);
            String r="";
            try {
                URL u = new URL(s);
                Log.d("tag", "onCreate:0 ");
                HttpURLConnection h = (HttpURLConnection) u.openConnection();
                Log.d("tag", "onCreate:1 ");
                InputStream i = h.getInputStream();
                Log.d("tag", "onCreate:2 ");
                int d;
                InputStreamReader x = new InputStreamReader(i);
                d = x.read();
                Log.d("tag", "onCreate: ");
                while (d != -1) {
                    r += (char) d;
                    d = x.read();
                    //Log.d("Tag", ""+d);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Tag", "doInBackground: "+r);
            return r;
        }
    }

    private RelativeLayout homeRL ;
    private ProgressBar loadingPB ;
    private TextView cityNameTV , temperatureTV , conditionTV;
    private ImageView backIV , iconIV , searchIV ;
    private RecyclerView weatherRV ;
    private TextInputEditText cityEdt ;
    private WeatherRVAdapter weatherRVAdapter ;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList ;
    private LocationManager locationManager ;
    private int PERMISSION_CODE =1 ;
    private String cityName ;
    JSONObject location=null ,j=null, forecastday = null  ,current = null , condition = null , forecast = null ,day = null ,
            date = null ;
    JSONArray  hour = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //FOR FULL SCREEN
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        backIV = findViewById(R.id.idTVBlack);
        iconIV = findViewById(R.id.idTVIcon);
        searchIV = findViewById(R.id.idTVSearch);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        weatherRVModelArrayList = new ArrayList<>();

        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModelArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ;
        {
            ActivityCompat.requestPermissions(MainActivity.this ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION} , PERMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation(NETWORK_PROVIDER);
        if(location != null){cityName = getCityName(location.getLongitude(),location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            cityName = "London";
            getWeatherInfo(cityName);
        }
//        cityName = getCityName(location.getLongitude() ,location.getLatitude()) ;
//       getWeatherInfo(cityName);
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString() ;
                if(city.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "ENTER CITY", Toast.LENGTH_SHORT).show();
                }
                else
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
            }
        });
    }
    public static Bitmap get(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Please provide permission", Toast.LENGTH_SHORT).show();
                 finish() ;
            }
        }
    }
    private void getWeatherInfo(String city) {
        try{
            loadingPB.setVisibility(View.GONE);
            homeRL.setVisibility(View.VISIBLE);
            weatherRVModelArrayList.clear();
            String r = new down().execute(city).get() ;
            j = new JSONObject(r);
            location = j.getJSONObject("location") ;
            current = j.getJSONObject("current") ;
            //int day = j.getJSONObject("current").getInt("is_day");
            int day = 1;
            if(day==1)
            {
                backIV.setImageBitmap(get("http://wallpaperaccess.com/full/3161112.png"));
                //Picasso.with(MainActivity.this).load("http://wallpaperaccess.com/full/3161112.png").into(backIV) ;
            }
            else
            {
                backIV.setImageBitmap(get("http://wallpaperaccess.com/full/3161112.png"));
                Picasso.with(MainActivity.this).load("http://cdn.cbeditz.com/cbeditz/preview/dark-blue-gradient-background-wallpaper-hd-11614501684pwgjugrt7g.png").into(backIV) ;
            }
            condition = current.getJSONObject("condition") ;
            forecast = j.getJSONObject("forecast") ;
            cityNameTV.setText(location.getString("name") + "," + location.getString("country"));
            temperatureTV.setText(current.getString("temp_c") +"°C");
            conditionTV.setText(condition.getString("text"));
            iconIV.setImageBitmap(get("http:"+condition.getString("icon")));
            forecastday = forecast.getJSONObject("forecastday") ;
            hour = forecastday.getJSONArray("hour") ;

//                    JSONObject forecastObj = j.getJSONObject("forecast");
//                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
//                    JSONArray hourArray = forecastO.getJSONArray("hour") ;
            for(int i=0 ; i<hour.length() ; i++)
            {
                JSONObject hourObj = hour.getJSONObject(i) ;
                String time = hourObj.getString("time") ;
                String temper = hourObj.getString("temp_c") ;
                String img = hourObj.getJSONObject("condition").getString("icon") ;
                String wind = hourObj.getString("wind_kph") ;
                weatherRVModelArrayList.add(new WeatherRVModel(time , temper , img , wind)) ;
            }
            weatherRVAdapter.notifyDataSetChanged();

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
 private String getCityName( double latitude , double longitude)
    {
        String cityName = "not found" ;
        Geocoder gcd = new Geocoder(getBaseContext() , Locale.getDefault()) ;
        try{
            List<Address> addresses = gcd.getFromLocation(latitude , longitude , 10);
            for (Address adr : addresses)
            {
                if(adr!=null)
                {
                    String city = adr.getLocality() ;
                    if(city != null && !city.equals(""))
                    {
                        cityName = city ;
                    }
                    else
                    {
                        Log.d("TAG" , "CITY NOT FOUND") ;
                        Toast.makeText(this, "CITY NOT FOUND", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return cityName ;
    }
   /* private  void getWeatherInfo ( String cityName)
    {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=0e2ab0d795c34f6fbe2152105220605&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
         cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this) ;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModelArrayList.clear();

                try{
                    String temperature = response.getJSONObject("current").getString("temp_c") ;
                    temperatureTV.setText(temperature + "°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    String conditionCode = response.getJSONObject("current").getJSONObject("condition").getString("code");
                    Picasso.with(MainActivity.this).load("http".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);
                    if(isDay==1)
                    {
                        Picasso.with(MainActivity.this).load("http://wallpaperaccess.com/full/3161112.png").into(backIV) ;
                    }
                    else
                    {
                        Picasso.with(MainActivity.this).load("http://cdn.cbeditz.com/cbeditz/preview/dark-blue-gradient-background-wallpaper-hd-11614501684pwgjugrt7g.png").into(backIV) ;
                    }
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = response.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = response.getJSONArray("hour") ;
                    for(int i=0 ; i<hourArray.length() ; i++)
                    {
                           JSONObject hourObj = hourArray.getJSONObject(i) ;
                           String time = hourObj.getString("time") ;
                            String temper = hourObj.getString("temp_c") ;
                            String img = hourObj.getJSONObject("condition").getString("icon") ;
                            String wind = hourObj.getString("wind_kph") ;
                            weatherRVModelArrayList.add(new WeatherRVModel(time , temper , img , wind)) ;
                    }
                    weatherRVAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "ENTER VALID CITY NAME", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }*/
}

