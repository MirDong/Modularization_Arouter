package com.dzk.arouter_api;

/**
 * @author jackie
 * @date 2020/11/19
 * group分组:app,login,personal
 */

import java.util.Map;

/**
 * key: group
 * value: ARouterPath
 */
public interface ARouterGroup {
    Map<String,Class<? extends ARouterPath>> getGroupMap();
} 
