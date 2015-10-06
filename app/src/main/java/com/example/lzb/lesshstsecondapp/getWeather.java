package com.example.lzb.lesshstsecondapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lzb on 2015/9/24.
 */
public class getWeather {

    public static String getWeather(String addressName)
    {
        String httpUrl = "http://apis.baidu.com/heweather/weather/free";
        String httpArg = "city=" + addressName;
        String jsonResult = request(httpUrl, httpArg);
        return jsonResult;
    }
    /**
     * @param urlAll
     *            :请求接口
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {

            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "b3744c6bf093093044afe853a0961c3c");
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
        return result;
    }

}
