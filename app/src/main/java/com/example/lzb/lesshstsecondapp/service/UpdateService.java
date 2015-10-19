package com.example.lzb.lesshstsecondapp.service;

import android.util.Log;
import android.widget.TextView;

import com.example.lzb.lesshstsecondapp.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lzb on 2015/10/19.
 */
public class UpdateService {

//    Runnable runUpdateOneSheng = new Runnable(){
//        @Override
//        public void run(){
//            String urlBase = "http://djangolesshst.sinaapp.com/startUpdating/";
//            String shengName = "beijing";
//            String httpUrl = urlBase + shengName;
//            BufferedReader reader = null;
//            String result = null;
//            StringBuffer sbf = new StringBuffer();
//
//            try {
//
//                URL url = new URL(httpUrl);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                // 填入apikey到HTTP header
//                //connection.setRequestProperty("apikey", "9efeb71f9007d397d479c9d96ad29817");
//                try{
//                    connection.connect();
//                }
//                catch (Exception e)
//                {
//                    String str = e.toString();
//                    Log.d(str, "s");
//                }
//                InputStream is = connection.getInputStream();
//                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//                String strRead = null;
//                while ((strRead = reader.readLine()) != null) {
//                    sbf.append(strRead);
//                    sbf.append("\r\n");
//                }
//                reader.close();
//                result = sbf.toString();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            _weatherInfoStr = result;
//        }
//    };
//
//    private  void updateOneShengForService(){
//        Log.d("updateOneShengForService()", "update sheng weather!");
//        try{
//            Thread thread = new Thread(runUpdateOneSheng);
//            thread.start();
//            thread.join();
//            TextView myLocationText = (TextView) findViewById(R.id.cityName);
//            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
//            String t=format.format(new Date());
//            Log.e("msg", t);
//
//            myLocationText.setText("天气更新时间：" + t + "\n" + _weatherInfoStr);
//
//            updateControls();
//        }
//        catch(Exception e){
//            Log.d(e.toString(), "");
//        }
//    }
}
