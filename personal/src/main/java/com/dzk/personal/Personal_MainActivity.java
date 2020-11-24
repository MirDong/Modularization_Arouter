package com.dzk.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dzk.arouter_annotations.ARouter;

@ARouter(path = "/personal/Personal_MainActivity")
public class Personal_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main);
    }
}
