package com.dzk.arouter_api;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.dzk.arouter_annotations.bean.RouterBean;

/**
 * @author jackie
 * @date 2020/11/29
 * 第一步：查找 ARouter$$Group$$personal ---> ARouter$$Path$$personal
 * 第二步：使用 ARouter$$Group$$personal ---> ARouter$$Path$$personal
 */
public class RouterManager {
    private static final String TAG = "RouterManager";
    private static final int MAX_NUM = 100;
    // 路由的组名 app，login，personal ...
    private String group;
    // 路由的路径  例如：/personal/Personal_MainActivity
    private static RouterManager instance;
    private String path;

    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    private LruCache<String, ARouterGroup> mGroupCache;
    private LruCache<String, ARouterPath> mPathCache;

    // 为了拼接，例如:ARouter$$Group$$personal
    private static final String GROUP_NAME = "ARouter$$Group$$";

    public RouterManager() {
        mGroupCache = new LruCache<>(MAX_NUM);
        mPathCache = new LruCache<>(MAX_NUM);
    }

    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("invalid value,path should like this, eg: /personal/Personal_MainActivity");
        }

        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("invalid value,path should like this, eg: /personal/Personal_MainActivity");
        }

        //截取组名/personal/Personal_MainActivity  finalGroup=personal
        String group = path.substring(1, path.indexOf("/", 1));
        if (TextUtils.isEmpty(group)) {
            throw new IllegalArgumentException("invalid value,path should like this, eg: /personal/Personal_MainActivity");
        }

        this.path = path;
        this.group = group;

        return new BundleManager();
    }

    public Object navigation(Context context, BundleManager bundleManager) {
        // 例如：寻找 ARouter$$Group$$personal  寻址
        String groupName = context.getPackageName() + "." + GROUP_NAME + group;
        Log.d(TAG, "navigation: groupClassName=" + groupName);
        try {
            // TODO 第一步 读取路由组Group信息
            ARouterGroup loadGroup = mGroupCache.get(group);
            if (null == loadGroup) {
                // 加载APT路由组Group类文件 例如：ARouter$$Group$$order
                Class<?> aClass = Class.forName(groupName);
                loadGroup = (ARouterGroup) aClass.newInstance();
                mGroupCache.put(group,loadGroup);
            }

            if (loadGroup.getGroupMap().isEmpty()){
                // Group这个类 加载失败
                throw new RuntimeException("路由表Group报废了...");
            }
            // TODO 第二步 读取路由Path类信息
            ARouterPath loadPath = mPathCache.get(path);
            if (null == loadPath){
                // 1.invoke loadGroup
                // 2.Map<String, Class<? extends ARouterPath>>
                Class<? extends ARouterPath> clazz = loadGroup.getGroupMap().get(group);
                loadPath = clazz.newInstance();
                // 保存到缓存
                mPathCache.put(path,loadPath);
            }
            //第三步跳转
            if (loadPath != null) {
                // pathMap.get("key") == null
                if (loadPath.getPathMap().isEmpty()) {
                    throw new RuntimeException("路由表Path报废了...");
                }

                RouterBean routerBean = loadPath.getPathMap().get(path);
                if (routerBean != null){
                    switch (routerBean.getTypeEnum()){
                        case ACTIVITY:
                            // 例如：getClazz == Personal_MainActivity.class
                            Intent intent = new Intent(context,routerBean.getMyClass());
                            intent.putExtras(bundleManager.getBundle());
                            context.startActivity(intent);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
