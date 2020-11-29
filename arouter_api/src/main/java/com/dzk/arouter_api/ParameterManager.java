package com.dzk.arouter_api;

import android.app.Activity;
import android.util.LruCache;

import com.dzk.arouter_api.parameter.ParameterGet;

/**
 * @author jackie
 * @date 2020/11/29
 */
public class ParameterManager {
    private static final int MAX_NUM = 100;
    private static ParameterManager instance;
    // LRU缓存 key=类名      value=参数加载接口
    private LruCache<String, ParameterGet> mCache;
    public static ParameterManager getInstance() {
        if (instance == null) {
            synchronized (ParameterManager.class) {
                if (instance == null) {
                    instance = new ParameterManager();
                }
            }
        }
        return instance;
    }
    // 为了这个效果：Order_MainActivity + $$Parameter
    static final String FILE_SUFFIX_NAME = "$$Parameter";
    private ParameterManager(){
        mCache = new LruCache<>(MAX_NUM);
    }

    // 使用者 只需要调用这一个方法，就可以进行参数的接收
    public void loadParameters(Activity activity){
        // className == com.dzk.person.Personal_MainActivity
        String className = activity.getClass().getName();
        ParameterGet parameterLoad = mCache.get(className);
        if(null == parameterLoad){
            // 拼接 如：Order_MainActivity + $$Parameter
            try {
                Class<?> aClass = Class.forName(className + FILE_SUFFIX_NAME);
                // 用接口parameterLoad = 接口的实现类Personal_MainActivity
                parameterLoad = (ParameterGet) aClass.newInstance();
                mCache.put(className,parameterLoad);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

        }
        //最终执行赋值操作
        parameterLoad.getParameter(activity);
    }
} 
