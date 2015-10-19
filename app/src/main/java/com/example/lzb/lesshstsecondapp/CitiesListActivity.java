package com.example.lzb.lesshstsecondapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.lzb.lesshstsecondapp.bean.City;
import com.example.lzb.lesshstsecondapp.bean.CityDB;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lzb on 2015/10/19.
 */
public class CitiesListActivity extends Activity {
    public CitiesListActivity instance;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.province_list);

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> provinces = bundle.getStringArrayList("cities");
        //绑定XML中的ListView，作为Item的容器
        list = (ListView) findViewById(R.id.province_list_view);

        //生成动态数组，并且转载数据
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        for(String province : provinces)
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", province);
            mylist.add(map);
        }
        //生成适配器，数组===》ListItem
        SimpleAdapter mSchedule = new SimpleAdapter(this, //没什么解释
                mylist,//数据来源
                R.layout.my_listview_item,//ListItem的XML实现

                //动态数组与ListItem对应的子项
                new String[] {"ItemTitle"},

                //ListItem的XML文件里面的两个TextView ID
                new int[] {R.id.ItemTitle});
        //添加并且显示
        list.setAdapter(mSchedule);
        instance = this;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) list.getItemAtPosition(position);
                String cityName = map.get("ItemTitle");

                Intent intent = getIntent();
                intent.putExtra("city", cityName);
                setResult(RESULT_OK, intent);
                instance.finish();
//                Intent intent = new Intent(this, ProvinceListActivity.class);
//                Bundle bundle = new Bundle();
//                ArrayList<String> cities = getProvinceAllCities(provinceName);
//                bundle.putStringArrayList("provinces", cities);
//                intent.putExtras(bundle);
//                startActivityForResult(intent, 1);
            }
        });
    }
    private ArrayList<String> getProvinceAllCities(String provinceName){
        ArrayList<String> ret = new ArrayList<String>();
        CityDB db = RefreshActivity.cityDB;
        ArrayList<City> cities = db.getProvinceAllCities(provinceName);
        for(City city : cities){
            ret.add(city.getCity());
        }
        return ret;
    }
}
