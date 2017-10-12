package org.faqrobot.demo_0927.ui.first;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;

import org.faqrobot.demo_0927.R;
import org.faqrobot.demo_0927.config.Config;
import org.faqrobot.demo_0927.ui.three.SoCoolActivity;
import org.faqrobot.demo_0927.ui.two.TimmersionActivity;
import org.faqrobot.demo_0927.view.CommomDialog;

/**SHA1: C8:C8:95:D2:A8:53:19:A0:29:8B:E0:27:A9:F2:94:EA:3B:2C:B1:CC*/
/**keyname：gaodeMap_goout*/

/**
 * Key：18fb8e91ee16b2436b3c749bdecb8a1c
 */
public class BlackTitleActivity extends BaseActivity implements View.OnClickListener {

    private MapView mapView;
    private AMap aMap;
    private ToggleButton bt_map;
    private ImageButton img_bt_title;
    private Dialog dialog;

    private TextView textView_location;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        /**初始化地图的apikey*/
//        try {
//            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
//                    PackageManager.GET_META_DATA);
//            MapsInitializer.setApiKey(appInfo.metaData.getString("com.amap.api.v2.apikey"));
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
        initView(savedInstanceState);
    }

    /**各种风格的activity*/
    private void initView(Bundle savedInstanceState)  {
        /**更新城市*/
        textView_location = findViewById(R.id.your_location);
        /**切换activity风格*/
        img_bt_title = findViewById(R.id.bt_change_style);
        img_bt_title.setOnClickListener(this);
        /**初始化mapView*/
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        /**切换地图的按钮*/
        bt_map = findViewById(R.id.bt_map);
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

    /**改变activity的方法*/
    private void change_activity_with_selected_number(int which_one_selected) {
        switch (which_one_selected){
            case Config.NUMBER_ZERO:
                startActivity(new Intent(this,BlackTitleActivity.class));
                finish();
                break;
            case Config.NUMBER_ONE:
                startActivity(new Intent(this,TimmersionActivity.class));
                finish();
                break;
            case Config.NUMBER_TWO:
                startActivity(new Intent(this,SoCoolActivity.class));
                finish();
                break;
        }
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
        if( dialog!=null){
            dialog = null;
        }
        img_bt_title = null;
    }

    /**
     * 重写onSaveInstanceState方法保存数据
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }


    /**点击事件*/
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_change_style:
                if(dialog==null){
                    dialog=new CommomDialog(BlackTitleActivity.this, R.style.dialog,null, (dialog1, confirm, which) -> {
                        Log.e("onClick_____此时的which为: ",""+which );
                        if(confirm&&which!=-1){
                            dialog1.dismiss();
                            change_activity_with_selected_number(which-1);
                        }
                    });
                }
                dialog.show();
                break;
        }
    }
}
