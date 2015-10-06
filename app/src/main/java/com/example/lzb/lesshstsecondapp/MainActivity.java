package com.example.lzb.lesshstsecondapp;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
//package ss.pku.weathertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import android.view.View;
import android.widget.*;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.example.lzb.lesshstsecondapp.weatherGetFromBaidu;

import static com.example.lzb.lesshstsecondapp.jsonControl.*;

public class MainActivity extends Activity {

    private Button download;
    private ProgressBar pb;
    private TextView tv;

    private Button queryButton;
    private TextView cityString;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(weatherGetFromBaidu.getLocalIpAddress(), "ip");



        String strPinyin = cn2pinyin.converterToSpell("李智博");
        Log.d(strPinyin, "cn2pinyin");

        try {
            LocationManager m_location_manager = (LocationManager) getSystemService(LOCATION_SERVICE);

            Location lm = m_location_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Log.d("log", lm.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        queryButton = (Button)findViewById(R.id.query);
        cityString = (TextView)findViewById(R.id.cityString);

        queryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        Button btn = (Button)this.findViewById(R.id.button);
        pb=(ProgressBar)findViewById(R.id.pb);
        tv=(TextView)findViewById(R.id.tv);
        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {

                new Thread(runnable).start();

//
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("确认");
//                builder.setMessage("这是一个简单消息框");
//                builder.setPositiveButton("是", null);
//                builder.show();

            }
        });
        download = (Button)findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                DownloadTask dTask=new DownloadTask();
                dTask.execute(100);
            }
        });
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
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
        myLocationText = (TextView) findViewById(R.id.cityString);
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latLongString = "纬度:" + lat + "\n经度:" + lng + "\n";

            Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                // 取得地址相关的一些信息\经度、纬度
                List<Address> addresses = gc.getFromLocation(lat, lng, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String addressName = address.getSubLocality();
                    _addressCityName = cn2pinyin.converterToSpell(FilterStr(addressName));
                    sb.append(address.getLocality()).append("\n");
                    latLongString = latLongString + sb.toString();
                }
            } catch (IOException e) {
            }

        } else {
            latLongString = "无法获取地理信息";
        }
        myLocationText.setText("您当前的位置是:\n" + latLongString);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.d("mylog", "请求结果为-->" + val);
            TextView myLocationText;
            myLocationText = (TextView) findViewById(R.id.cityString);
            myLocationText.setText(val);
        }
    };
    Runnable runnable = new Runnable(){
        @Override
        public void run(){
            //
            // TODO: http request.
            //

            String weatherToShow = getWeather.getWeather(_addressCityName);

            String latLongString = "";
            try{
                jsonControl js = new jsonControl(weatherToShow);

                for(jsonControl.DailyForcast day : js.dailyWeatherList){
                    System.out.println(day.date);
                    Log.d(day.date, "date from baidu");

                }

                latLongString += js.dailyWeatherList.get(0).toString();
                for(jsonControl.HourlyForcast hour : js.hourlyWeatherList){
                    System.out.println(hour.date);
                    Log.d(hour.date, "date from baidu");
                }
                latLongString += js.hourlyWeatherList.get(0).toString();

                Log.d("tep is: " + js.nowWeather.tmp, "date from baidu");
                latLongString += js.nowWeather.toString();
            }
            catch (JSONException e) {
                return;
            }
            Log.d(latLongString, "\ndate from baidu");
//            TextView myLocationText;
//            myLocationText = (TextView) findViewById(R.id.cityString);
//            myLocationText.setText("天气:\n" + latLongString);
            String sinaString=GetHttp("http://php.weather.sina.com.cn/iframe/index/w_cl.php?code=js&day=0&city=&dfc=1");
            String jsonString= GetHttp("http://mobile.weather.com.cn/data/forecast/101010100.html");
            String XMLString= GetHttp("http://flash.weather.com.cn/wmaps/xml/china.xml");
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  //取得DocumentBuilderFactory实例
                DocumentBuilder builder = factory.newDocumentBuilder(); //从factory获取DocumentBuilder实例
                Document doc = builder.parse(new ByteArrayInputStream(XMLString.getBytes()));
                Element rootElement = doc.getDocumentElement();
                NodeList items = rootElement.getElementsByTagName("city");
                Log.i("result",items.toString());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value",latLongString);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String GetHttp(String url)
    {
        StringBuffer result = new StringBuffer();
        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = client.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();

            String line="";
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"gbk"));
            while (null !=(line =reader.readLine()))
                result.append(line);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            return  result.toString();
        }
    }

    private void AnalysisHttp(String result)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(result).getJSONObject("weather");
        }
        catch (JSONException e)
        {

        }
    }

    class DownloadTask extends AsyncTask<Integer, Integer, String> {
        //后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型

        @Override
        protected void onPreExecute() {
            //第一个执行方法
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            //第二个执行方法,onPreExecute()执行完后执行
            for(int i=0;i<=100;i++ ){
                pb.setProgress(i);
                publishProgress(i);
                try {
                    Thread.sleep(params[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "执行完毕";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //这个函数在doInBackground调用publishProgress时触发，虽然调用时只有一个参数
            //但是这里取到的是一个数组,所以要用progesss[0]来取值
            //第n个参数就用progress[n]来取值
            tv.setText(progress[0]+"%");
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            //doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
            //这里的result就是上面doInBackground执行后的返回值，所以这里是"执行完毕"
            setTitle(result);
            super.onPostExecute(result);
        }

    }

}

