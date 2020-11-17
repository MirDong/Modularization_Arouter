package com.dzk.modularization_arouter.app;

import android.app.Application;

import com.dzk.common.PathRecordManager;
import com.dzk.login.Login_MainActivity;
import com.dzk.modularization_arouter.MainActivity;
import com.dzk.personal.Personal_MainActivity;

/**
 * @author jackie
 * @date 2020/11/17
 */
public class ModuleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 如果项目有100个Activity， 缺点:这种加法会繁琐不易维护
        PathRecordManager.addGroupInfo("app", "MainActivity", MainActivity.class);
        PathRecordManager.addGroupInfo("login", "Order_MainActivity", Login_MainActivity.class);
        PathRecordManager.addGroupInfo("personal", "Personal_MainActivity", Personal_MainActivity.class);
    }
}
