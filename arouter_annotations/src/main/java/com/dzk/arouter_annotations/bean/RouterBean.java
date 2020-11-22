package com.dzk.arouter_annotations.bean;

import javax.lang.model.element.Element;

/**
 * @author jackie
 */
public class RouterBean {
    public enum TypeEnum {
        /**
         * 便于以后扩展
         */
        ACTIVITY,
        FRAGMENT,
    }


    /**
     * 枚举类型
     */
    private TypeEnum typeEnum;

    /**
     * 类节点 JavaPoet学习的时候，可以拿到很多的信息
     */
    private Element element;

    /**
     * 被注解的 Class对象 例如： MainActivity.class  Main2Activity.class  Main3Activity.class
     */
    private Class<?> myClass;
    /**
     *  路由地址  例如：/app/MainActivity
     */
    private String path;
    /**
     * 路由组  例如：app  login  personal
     */
    private String group;

    private RouterBean(TypeEnum typeEnum, /*Element element,*/ Class<?> myClass, String path, String group) {
        this.typeEnum = typeEnum;
        // this.element = element;
        this.myClass = myClass;
        this.path = path;
        this.group = group;
    }

    public String getPath() {
        return path;
    }

    public String getGroup() {
        return group;
    }

    public TypeEnum getTypeEnum() {
        return typeEnum;
    }

    public Element getElement() {
        return element;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**对外提供简易版构造方法，主要是为了方便APT生成代码
     * @param type
     * @param clazz
     * @param path
     * @param group
     * @return
     */
    public static RouterBean create(TypeEnum type, Class<?> clazz, String path, String group) {
        return new RouterBean(type, clazz, path, group);
    }

    public void setType(TypeEnum type){
        this.typeEnum = type;
    }
    /**构造者模式相关
     * @param builder
     */
    private RouterBean(Builder builder){
        this.typeEnum = builder.type;
        this.element = builder.element;
        this.myClass = builder.clazz;
        this.group = builder.group;
        this.path = builder.path;
    }

    /**
     * 构建者模式
     */
    public static final class Builder {
        // 枚举类型：Activity
        private TypeEnum type;
        // 类节点
        private Element element;
        // 注解使用的类对象
        private Class<?> clazz;
        // 路由地址
        private String path;
        // 路由组
        private String group;


        public Builder addType(TypeEnum typeEnum){
            this.type = typeEnum;
            return this;
        }

        public Builder addElement(Element element){
            this.element = element;
            return this;
        }

        public Builder addClass(Class<?> clazz){
            this.clazz = clazz;
            return this;
        }
        public Builder addPath(String path) {
            this.path = path;
            return this;
        }

        public Builder addGroup(String group) {
            this.group = group;
            return this;
        }

        /**
         * 最后的build或者create，往往是做参数的校验或者初始化赋值工作
         */
        public RouterBean build(){
            if (path == null || path.length() == 0){
                throw new IllegalArgumentException("path必填项为空，如：/app/MainActivity");
            }
            return new RouterBean(this);
        }
    }
    @Override
    public String toString() {
        return "RouterBean{" +
                "path='" + path + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
} 
