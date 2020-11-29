package com.dzk.arouter_api;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * @author jackie
 * @date 2020/11/29
 */
public class BundleManager {
    //Intent传输  携带的值，保存到这里
    private Bundle bundle = new Bundle();

    public Bundle getBundle() {
        return bundle;
    }

    public BundleManager withString(@NonNull String key,@NonNull String value){
        bundle.putString(key, value);
        return this;
    }

    public BundleManager withInt(@NonNull String key,@NonNull int value){
        bundle.putInt(key, value);
        return this;
    }
    public BundleManager withBoolean(@NonNull String key,@NonNull boolean value){
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withBundle(Bundle bundle){
        if (bundle.isEmpty()){
            this.bundle = bundle;
        }else {
            throw new IllegalArgumentException("bundle will override the value before set");
        }
        return this;
    }

    // 直接完成跳转
    public Object navigation(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            return RouterManager.getInstance().navigation(context,this);
        }
        return null;
    }
}
