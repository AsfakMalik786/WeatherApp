package com.example.myweatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView cityName;
    Button search;
    TextView show;
    String url;

    // AsyncTask to get weather data
    class getWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls){
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (result == null) {
                show.setText("Failed to retrieve weather data.");
                return;
            }

            try {
                // Parse the JSON result
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");

                // Fetch temperature values from the JSON response and convert to Celsius
                double temperature = main.getDouble("temp") - 273.15;        // Convert from Kelvin to Celsius
                double feelsLike = main.getDouble("feels_like") - 273.15;    // Convert from Kelvin to Celsius
                double tempMax = main.getDouble("temp_max") - 273.15;        // Convert from Kelvin to Celsius
                double tempMin = main.getDouble("temp_min") - 273.15;        // Convert from Kelvin to Celsius
                int pressure = main.getInt("pressure");
                int humidity = main.getInt("humidity");

                // Format the information nicely
                String weatherInfo = "Temperature : " + String.format("%.2f", temperature) + " °C\n"
                        + "Feels Like : " + String.format("%.2f", feelsLike) + " °C\n"
                        + "Temperature Max : " + String.format("%.2f", tempMax) + " °C\n"
                        + "Temperature Min : " + String.format("%.2f", tempMin) + " °C\n"
                        + "Pressure : " + pressure + " hPa\n"
                        + "Humidity : " + humidity + " %";

                // Set the formatted data in the TextView
                show.setText(weatherInfo);

            } catch(Exception e){
                e.printStackTrace();
                show.setText("Error parsing weather data.");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        // Set click listener for the search button
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Searching Weather For.. ", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();

                // Check if the city name is provided
                if (!city.isEmpty()) {
                    // Construct the API URL with the city name
                    url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=64442d12643541d2a8535b276fd49641";
                    getWeather task = new getWeather();
                    try {
                        task.execute(url).get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Show a message if no city name is provided
                    Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
