package org.faqrobot.demo_0927.ui.two;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ToggleButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;

import org.faqrobot.demo_0927.R;


public class TimmersionActivity extends Activity {

    private MapView mapView;
    private AMap aMap;
    private ToggleButton bt_map;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timmersion);
        /**沉浸加黑色字体*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView(savedInstanceState);
    }


    /**各种风格的activity*/
    private void initView(Bundle savedInstanceState) {
        /**初始化mapView*/
        mapView = (MapView) findViewById(R.id.new_map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        /**切换地图的按钮*/
        bt_map = (ToggleButton) findViewById(R.id.new_bt_map);
        bt_map.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                /**选中为卫星地图*/
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
            } else {
                /**普通地图*/
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 重写onSaveInstanceState方法保存数据
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

}
