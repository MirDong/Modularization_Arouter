package com.dzk.arouter_api.parameter;

/**
 * @author jackie
 * @date 2020/11/29
 */
public interface ParameterGet {
    /**
     * 目标对象.属性名 = getIntent().属性类型   完成赋值操作
     * @param targetParamter 目标对象：例如MainActivity
     */
    void getParameter(Object targetParamter);
}
