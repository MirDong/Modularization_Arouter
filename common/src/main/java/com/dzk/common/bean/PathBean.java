package com.dzk.common.bean;

/**
 * @author jackie
 * @date 2020/11/17
 */
public class PathBean {
    /**
     * personal/Personal_MainActivity
     */
    private String path;
    /**
     * Personal_MainActivity.class
     */
    private Class clazz;

    public PathBean() {
    }

    public PathBean(String path, Class clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
} 
