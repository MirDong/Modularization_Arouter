package com.dzk.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dzk.common.PathRecordManager;
import com.dzk.login.R;

public class Login_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity_main);

    }

    public void startPersonalModel(View view) {
        Class<?> targetClass = PathRecordManager.startTargetActivity("personal",
                "Personal_MainActivity");
        startActivity(new Intent(Login_MainActivity.this,targetClass));
    }
}
