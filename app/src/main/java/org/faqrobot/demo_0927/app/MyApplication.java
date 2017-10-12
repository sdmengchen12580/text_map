package org.faqrobot.demo_0927.app;

import android.app.Application;

/**
 * Created by 孟晨 on 2017/9/29.
 */

public class MyApplication extends Application {


    private String city_name = null;

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
