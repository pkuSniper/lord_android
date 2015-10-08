package com.example.lzb.lesshstsecondapp;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.util.Log;
import android.widget.TextView;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.lzb.lesshstsecondapp.WeatherInfo.*;

/**
 * Created by lzb on 2015/9/29.
 */
public class WeatherActivity extends Activity {

    protected ImageView updateBtn;
    private InfoToShow infoToShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        infoToShow = new InfoToShow();
        updateLocationAndWeather();
        //更新位置及天气信息
        updateBtn = (ImageView)findViewById(R.id.title_update_btn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateLocationAndWeather();
            }
        });
    }
    private void updateLocationAndWeather(){
        Log.d("clickUpdate()", "update the weather!");
        getLocation();
        try{
            Thread thread = new Thread(runnable);
            thread.start();
            thread.join();
            TextView myLocationText = (TextView) findViewById(R.id.cityName);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            String t=format.format(new Date());
            Log.e("msg", t);

            myLocationText.setText("天气更新时间：" + t + "\n" + _weatherInfoStr);
            updateControls();
        }
        catch(Exception e){
            Log.d(e.toString(), "");
        }
    }
    private void updateControls() {
        TextView city_name = (TextView) findViewById(R.id.city_name);
        city_name.setText(infoToShow.locationName);

        WeatherInfo weatherInfo = infoToShow.weatherInfo;

        String show;
        TextView date_time = (TextView) findViewById(R.id.date_time);
        show = weatherInfo.hourlyWeatherList.get(0).date.split(" ")[1];
        date_time.setText("今天" + show + "发布");

        TextView humidity_value = (TextView) findViewById(R.id.humidity_value);
        show = weatherInfo.hourlyWeatherList.get(0).hum;
        humidity_value.setText("湿度：" + show + "%");

        TextView temperature = (TextView) findViewById(R.id.temperature);
        show = weatherInfo.dailyWeatherList.get(1).tmp_min + "℃~" +
                weatherInfo.dailyWeatherList.get(1).tmp_max + "℃";
        temperature.setText(show);

        TextView today_week = (TextView) findViewById(R.id.today_week);
        show = "" + getDayOfWeek();
        today_week.setText(show);

        TextView climate = (TextView) findViewById(R.id.climate);
        show = "" + weatherInfo.dailyWeatherList.get(1).txt_d + "/" +
                weatherInfo.dailyWeatherList.get(1).txt_n;
        climate.setText(show);

        TextView wind = (TextView) findViewById(R.id.wind);
        show = "" + weatherInfo.dailyWeatherList.get(1).wind_sc;
        wind.setText(show);
    }

    private String getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String today = null;
        if (day == 2) {
            today = "星期一";
        } else if (day == 3) {
            today = "星期二";
        } else if (day == 4) {
            today = "星期三";
        } else if (day == 5) {
            today = "星期四";
        } else if (day == 6) {
            today = "星期五";
        } else if (day == 7) {
            today = "星期六";
        } else if (day == 1) {
            today = "星期日";
        }

        return today;
    }

    private String _weatherInfoStr;
    Runnable runnable = new Runnable(){
        @Override
        public void run(){
            String weatherJson = GetWeather.getWeather(_addressCityName);
            String latLongString = "";
            try{
                JsonControl js = new JsonControl(weatherJson);
                WeatherInfo weatherInfo = js.weatherInfo;
                infoToShow.weatherInfo = weatherInfo;
                for(DailyForcast day : weatherInfo.dailyWeatherList){
                    System.out.println(day.date);
                    Log.d(day.date, "date from baidu");
                }

                latLongString += weatherInfo.dailyWeatherList.get(0).toString();
                for(HourlyForcast hour : weatherInfo.hourlyWeatherList){
                    System.out.println(hour.date);
                    Log.d(hour.date, "date from baidu");
                }
                latLongString += weatherInfo.hourlyWeatherList.get(0).toString();

                Log.d("tep is: " + weatherInfo.nowWeather.tmp, "date from baidu");
                latLongString += weatherInfo.nowWeather.toString();
            }
            catch (JSONException e) {
                return;
            }
            Log.d(latLongString, "\ndate from baidu");
            _weatherInfoStr = latLongString;
        }
    };

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            //updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private String _addressCityName = "";

    private String FilterStr(String str){
        String filterStr = "省市区县";

        for(int i = 0; i < filterStr.length(); i++){
            String s = filterStr.substring(i, i + 1);
            if(str.endsWith(s)) {
                str = str.substring(0, filterStr.length() - 2);
                break;
            }
        }
        return str;
    }

    private void updateWithNewLocation(Location location) {
        String latLongString;
        TextView myLocationText;
        myLocationText = (TextView) findViewById(R.id.cityName);
        myLocationText.setMovementMethod(ScrollingMovementMethod.getInstance());
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latLongString = "纬度:" + lat + "\n经度:" + lng + "\n";

            Geocoder gc = new Geocoder(WeatherActivity.this, Locale.getDefault());
            try {
                // 取得地址相关的一些信息\经度、纬度
                List<Address> addresses = gc.getFromLocation(lat, lng, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String addressName = address.getSubLocality();
                    infoToShow.locationName = FilterStr(addressName);
                    _addressCityName = cn2pinyin.converterToSpell(infoToShow.locationName);
                    sb.append(address.getLocality()).append("/").append(address.getSubLocality()).append("\n");
                    latLongString = latLongString + sb.toString();
                }
            } catch (IOException e) {
            }

        } else {
            latLongString = "无法获取地理信息";
        }
        myLocationText.setText("您当前的位置是:\n" + latLongString);
    }
    private void getLocation()
    {
        try {
            LocationManager locationManager;
            String serviceName = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) getSystemService(serviceName);
            // String provider = LocationManager.GPS_PROVIDER;
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
//                    String provider = locationManager.getBestProvider(criteria, true);
            String provider = LocationManager.NETWORK_PROVIDER;
            locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
            Location location = locationManager.getLastKnownLocation(provider);
            while (location == null) {
                location = locationManager.getLastKnownLocation(provider);
            }
            updateWithNewLocation(location);
            locationManager.removeUpdates(locationListener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
