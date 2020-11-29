package com.dzk.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.dzk.arouter_annotations.ARouter;
import com.dzk.arouter_annotations.Parameter;
import com.dzk.arouter_api.ParameterManager;

@ARouter(path = "/personal/Personal_MainActivity")
public class Personal_MainActivity extends AppCompatActivity {
    private static final String TAG = "Personal_MainActivity";
    @Parameter
    String name;

    @Parameter
    String sex;

    @Parameter
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main);
        ParameterManager.getInstance().loadParameters(this);
        Log.d(TAG, "name = "+ name +",age = " + age + ",sex = " + sex);
    }
}
