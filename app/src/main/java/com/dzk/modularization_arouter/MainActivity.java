package com.dzk.modularization_arouter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dzk.arouter_annotations.ARouter;
import com.dzk.login.Login_MainActivity;
import com.dzk.personal.Personal_MainActivity;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 以前
        String serverURL = NetworkConfig.DEBUG;

        // 现在
        serverURL = BuildConfig.debug;
        Toast.makeText(this, "serverURL:" + serverURL, Toast.LENGTH_SHORT).show();

        if (BuildConfig.isRelease) {
            Log.d(TAG, "onCreate: 当前是：集成化 线上环境，以app壳为主导运行的方式");
            Toast.makeText(this, "当前是：集成化 线上环境，以app壳为主导运行的方式", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "onCreate: 当前是：组件化 测试环境，所有的子模块都可以独立运行");
            Toast.makeText(this, "当前是：组件化 测试环境，所有的子模块都可以独立运行", Toast.LENGTH_SHORT).show();
        }
    }


    // 从App壳 到  Login登录
    public void startLoginModel(View view) {
        Intent intent = new Intent(this, Login_MainActivity.class);
        intent.putExtra("name", "Derry");
        startActivity(intent);
    }

    // 从App壳 到  Personal我的
    public void startPersonalModel(View view) {
        Intent intent = new Intent(this, Personal_MainActivity.class);
        intent.putExtra("name", "Derry");
        startActivity(intent);
    }
}