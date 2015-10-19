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
import android.widget.Button;
import android.widget.ImageView;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.lzb.lesshstsecondapp.WeatherInfo.*;
import com.example.lzb.lesshstsecondapp.utils.GetLocation;
import com.example.lzb.lesshstsecondapp.utils.GetWeather;
import com.example.lzb.lesshstsecondapp.utils.CN2Pinyin;
import com.example.lzb.lesshstsecondapp.utils.ParseJson;

/**
 * Created by lzb on 2015/9/29.
 */
public class WeatherActivity extends Activity {

    protected ImageView updateBtn;
    protected Button updateOneShengBtn;
    private InfoToShow infoToShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.weather_info);
        infoToShow = new InfoToShow();
        updateLocationAndWeather();
        //更新位置及天气信息
        updateBtn = (ImageView)findViewById(R.id.title_update_btn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateLocationAndWeather();
            }
        });

        updateOneShengBtn = (Button)findViewById(R.id.update_one_sheng);
        updateOneShengBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateOneShengForService();
            }
        });

    }

    private  void updateOneShengForService(){
        Log.d("updateOneShengForService()", "update sheng weather!");
        try{
            Thread thread = new Thread(runUpdateOneSheng);
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
    private String _quname = null;
    Runnable runGetLocationInfo = new Runnable(){
        @Override
        public void run(){

            try{
                Location location = _location;
                GetLocation loc = new GetLocation();
                _quname = loc.GetLocationFromLatLng(location.getLatitude(), location.getLongitude());
                infoToShow.locationName = FilterStr(_quname);
                _addressCityName = CN2Pinyin.converterToSpell(infoToShow.locationName);
                getWeatherInfo();
            }
            catch (Exception e) {
                return;
            }

        }
    };
    private String _weatherInfoStr;
    Runnable runnable = new Runnable(){
        @Override
        public void run(){
            getWeatherInfo();
        }
    };
    private void getWeatherInfo(){
        String weatherJson = GetWeather.getWeather(_addressCityName);
        String latLongString = "";
        try{
            ParseJson js = new ParseJson(weatherJson);
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
    Runnable runUpdateOneSheng = new Runnable(){
        @Override
        public void run(){
            String urlBase = "http://djangolesshst.sinaapp.com/startUpdating/";
            String shengName = "beijing";
            String httpUrl = urlBase + shengName;
            BufferedReader reader = null;
            String result = null;
            StringBuffer sbf = new StringBuffer();

            try {

                URL url = new URL(httpUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // 填入apikey到HTTP header
                //connection.setRequestProperty("apikey", "9efeb71f9007d397d479c9d96ad29817");
                try{
                    connection.connect();
                }
                catch (Exception e)
                {
                    String str = e.toString();
                    Log.d(str, "s");
                }
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                reader.close();
                result = sbf.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            _weatherInfoStr = result;
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
    private Location _location;
    private void getLocationInfo(Location location){
        try{
            _location = location;
            Thread thread = new Thread(runGetLocationInfo);
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
                    _addressCityName = CN2Pinyin.converterToSpell(infoToShow.locationName);
                    sb.append(address.getLocality()).append("/").append(address.getSubLocality()).append("\n");
                    latLongString = latLongString + sb.toString();
                }
            }
            catch (Exception e)
            {
                Log.d(e.toString(), "error!");
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
            //updateWithNewLocation(location);
            getLocationInfo(location);
            locationManager.removeUpdates(locationListener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
