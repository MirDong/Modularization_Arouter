package com.dzk.arouter_api;

/**
 * @author jackie
 * @date 2020/11/19
 */

import com.dzk.arouter_annotations.bean.RouterBean;

import java.util.Map;

/**
 * 详细path("/app/MainActivity")映射集合接口，例如:映射到对应的MainActivity
 * key:/app/MainActivity"
 * value:RouterBean(MainActivity.class)
 */
public interface ARouterPath {
    Map<String, RouterBean> getPathMap();
}
