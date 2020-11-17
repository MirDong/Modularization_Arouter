package com.dzk.common;

/**
 * @author jackie
 * @date 2020/11/17
 */



import android.text.TextUtils;

import androidx.collection.ArrayMap;

import com.dzk.common.bean.PathBean;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全局路径记录器（根据子模块进行分组）
 *
 * 组名：app，login，personal
 *       详情login=[Login_MainActivity,Login_MainActivity2,Login_MainActivity3]
 *
 */
public class PathRecordManager {

    /**
     * 先理解成 仓库
     * group: app,order,personal
     *
     * login:
     *      Login_MainActivity
     *      Login_MainActivity2
     *      Login_MainActivity3
     */
    private static Map<String, List<PathBean>> maps = new ArrayMap<>();
    /**
     * 将路径信息加入全局Map
     *
     * @param groupName 组名，如："personal"
     * @param pathName  路劲名，如："Personal_MainActivity"
     * @param clazz     类对象，如：Personal_MainActivity.class
     */
    public static void addGroupInfo(String groupName,String pathName,Class<?> clazz){
        List<PathBean> pathBeanList = maps.get(groupName);
        if (pathBeanList == null){
            pathBeanList = new ArrayList<>();
        }
        pathBeanList.add(new PathBean(pathName,clazz));
        maps.put(groupName,pathBeanList);
    }


    /**
     * 只需要告诉我，组名 ，路径名，  就能返回 "要跳转的Class"
     * @param groupName 组名 oder
     * @param pathName 路径名  OrderMainActivity1
     * @return 跳转目标的class类对象
     */
    public static Class<?> startTargetActivity(String groupName,String pathName){
        if (TextUtils.isEmpty(groupName)){
            throw new IllegalArgumentException("groupName can not be empty or null");
        }
        List<PathBean> pathBeans = maps.get(groupName);
        if (pathBeans == null){
            throw new IllegalStateException("Path Map has no any elements");
        }
        for (PathBean pathBean : pathBeans) {
            if (pathBean.getPath().equals(pathName)){
                return pathBean.getClazz();
            }
        }
        return null;
    }
} 
