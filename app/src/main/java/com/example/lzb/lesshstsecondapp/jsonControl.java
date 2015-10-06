package com.example.lzb.lesshstsecondapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lzb on 2015/9/24.
 */
public class jsonControl {

    public jsonControl(String jsonStr) throws JSONException{
        try{
            getWeather(jsonStr);
        }
        catch (JSONException e) {
            return;
        }
    }
    public class DailyForcast{
        public String date = "";
        public String txt_d = "";
        public String txt_n = "";
        public String wind_dir = "";
        public String wind_sc = "";
        public String wind_spd = "";
        public String tmp_max = "";
        public String tmp_min = "";
        public String pres = "";

        public String toString(){
            String strShow = "今天天气：\n";
            strShow += "日期：" + date + "\n";
            strShow += "白天：" + txt_d + "\n";
            strShow += "夜间：" + txt_n + "\n";
            strShow += "最低温度：" + tmp_min + "\n";
            strShow += "最高温度：" + tmp_max + "\n";
            strShow += "风向：" + wind_dir + "\n";
            strShow += "风类型：" + wind_sc + "\n";
            strShow += "风速：" + wind_spd + "\n";
            strShow += "压强：" + pres + "\n\n";
            return strShow;
        }
    }

    public class HourlyForcast{
        public String date = "";
        public String txt_d = "";
        public String txt_n = "";
        public String wind_dir = "";
        public String wind_sc = "";
        public String wind_spd = "";
        public String pres = "";
        public String tmp = "";

        public String toString(){
            String strShow = "此小时天气：\n";
            strShow += "时间：" + date + "\n";
            strShow += "白天：" + txt_d + "\n";
            strShow += "夜间：" + txt_n + "\n";
            strShow += "温度：" + tmp + "\n";
            strShow += "风向：" + wind_dir + "\n";
            strShow += "风类型：" + wind_sc + "\n";
            strShow += "风速：" + wind_spd + "\n";
            strShow += "压强：" + pres + "\n\n";
            return strShow;
        }
    }

    public class NowWeather{
        public String tmp;
        public String txt;
        public String wind_dir;
        public String wind_sc;
        public String wind_spd;
        public String pres;

        public String toString(){
            String strShow = "此刻天气：\n";
            strShow += "天气：" + txt + "\n";
            strShow += "温度：" + tmp + "\n";
            strShow += "风向：" + wind_dir + "\n";
            strShow += "风类型：" + wind_sc + "\n";
            strShow += "风速：" + wind_spd + "\n";
            strShow += "压强：" + pres + "\n\n";
            return strShow;
        }
    }

    public NowWeather nowWeather;
    public ArrayList<DailyForcast> dailyWeatherList;
    public ArrayList<HourlyForcast> hourlyWeatherList;

    public void getWeather(String jsonWeather) throws JSONException{
        JSONObject json = new JSONObject(jsonWeather);
        JSONArray jsonArray = json.getJSONArray("HeWeather data service 3.0");
        JSONObject firstNode = (JSONObject) jsonArray.get(0);

        //"now"
        JSONObject jaNow = (JSONObject) firstNode.get("now");

        nowWeather = new NowWeather();

        nowWeather.txt = (String) ((JSONObject)(jaNow.get("cond"))).get("txt").toString();
        nowWeather.pres = (String) jaNow.get("pres").toString();
        nowWeather.tmp = (String) jaNow.get("tmp").toString();
        nowWeather.wind_dir = (String) ((JSONObject)(jaNow.get("wind"))).get("dir").toString();
        nowWeather.wind_sc = (String) ((JSONObject)(jaNow.get("wind"))).get("sc").toString();
        nowWeather.wind_spd = (String) ((JSONObject)(jaNow.get("wind"))).get("spd").toString();

        //"daily_forcast"
        JSONArray jaDaily = (JSONArray)firstNode.getJSONArray("daily_forecast");
        dailyWeatherList = new ArrayList<DailyForcast>();

        for(int i = 0;i < jaDaily.length(); i++){
            JSONObject day = (JSONObject) jaDaily.get(i);

            DailyForcast dailyTemp = new DailyForcast();

            dailyTemp.date = (String) day.get("date").toString();
            dailyTemp.pres = (String) day.get("pres").toString();
            dailyTemp.tmp_min = (String) ((JSONObject)(day.get("tmp"))).get("min").toString();
            dailyTemp.tmp_max = (String) ((JSONObject)(day.get("tmp"))).get("max").toString();
            dailyTemp.txt_d = (String) ((JSONObject)(day.get("cond"))).get("txt_d").toString();
            dailyTemp.txt_n = (String) ((JSONObject)(day.get("cond"))).get("txt_n").toString();
            dailyTemp.wind_dir = (String) ((JSONObject)(day.get("wind"))).get("dir").toString();
            dailyTemp.wind_sc = (String) ((JSONObject)(day.get("wind"))).get("sc").toString();
            dailyTemp.wind_spd = (String) ((JSONObject)(day.get("wind"))).get("spd").toString();
            dailyWeatherList.add(dailyTemp);
        }

        //"hourly_forcast"
        JSONArray jaHourly = (JSONArray)firstNode.get("hourly_forecast");
        hourlyWeatherList = new ArrayList<HourlyForcast>();

        for(int i = 0;i < jaHourly.length(); i++){
            JSONObject hour = (JSONObject) jaHourly.get(i);

            HourlyForcast hourTemp = new HourlyForcast();

            hourTemp.date = (String) hour.get("date").toString();
            hourTemp.pres = (String) hour.get("pres").toString();
            hourTemp.tmp = (String) hour.get("tmp").toString();
            hourTemp.wind_dir = (String) ((JSONObject)(hour.get("wind"))).get("dir").toString();
            hourTemp.wind_sc = (String) ((JSONObject)(hour.get("wind"))).get("sc").toString();
            hourTemp.wind_spd = (String) ((JSONObject)(hour.get("wind"))).get("spd").toString();
            hourlyWeatherList.add(hourTemp);
        }

    }

}
