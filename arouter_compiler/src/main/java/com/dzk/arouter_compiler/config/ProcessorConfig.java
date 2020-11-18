package com.dzk.arouter_compiler.config;

public interface ProcessorConfig {

    // @ARouter注解 的 包名 + 类名
    String AROUTER_PACKAGE =  "com.dzk.arouter_annotations.ARouter";


    /**
     * 接收参数的TAG标记,目的是接收 每个module名称
     */
    String OPTIONS = "moduleName";
    /**
     * 目的是接收 包名（APT 存放的包名）
     */
    String APT_PACKAGE = "packageNameForAPT";
}
