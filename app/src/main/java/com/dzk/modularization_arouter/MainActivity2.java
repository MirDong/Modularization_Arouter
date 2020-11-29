package com.dzk.modularization_arouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dzk.arouter_annotations.ARouter;

@ARouter(path = "/app/MainActivity2")
public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}