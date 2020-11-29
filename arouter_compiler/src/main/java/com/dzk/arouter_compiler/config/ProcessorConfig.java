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

    // String全类名
    public static final String STRING_PACKAGE = "java.lang.String";

    // Activity全类名
    public static final String ACTIVITY_PACKAGE = "android.app.Activity";

    // ARouter api 包名
    String AROUTER_API_PACKAGE = "com.dzk.arouter_api";

    // ARouter api 的 ARouterGroup 高层标准
    String AROUTER_API_GROUP = AROUTER_API_PACKAGE + ".ARouterGroup";

    // ARouter api 的 ARouterPath 高层标准
    String AROUTER_API_PATH = AROUTER_API_PACKAGE + ".ARouterPath";


    // 路由组，中的 Path 里面的 方法名
    String PATH_METHOD_NAME = "getPathMap";

    // 路由组，中的 Group 里面的 方法名
    String GROUP_METHOD_NAME = "getGroupMap";

    // 路由组，中的 Path 里面 的 变量名 1
    String PATH_VAR1 = "pathMap";

    // 路由组，中的 Group 里面 的 变量名 1
    String GROUP_VAR1 = "groupMap";

    // 路由组，PATH 最终要生成的 文件名
    String PATH_FILE_NAME = "ARouter$$Path$$";

    // 路由组，GROUP 最终要生成的 文件名
    String GROUP_FILE_NAME = "ARouter$$Group$$";





    // @Parameter注解 的 包名 + 类名
    //@Parameter注解的包名+类名
    String PARAMETER_PACKAGE = "com.dzk.arouter_annotations.Parameter";

    // ARouter api 的 ParameterGet 高层标准
    // ParameterGet api
    String PARAMETER_GET_API = AROUTER_API_PACKAGE + ".parameter.ParameterGet";

    // ARouter api 的 ParameterGet 方法参数的名字
    String PARAMETER_NAME = "targetParameter";

    // ARouter api 的 ParmeterGet 方法的名字
    String PARAMETER_METHOD_NAME = "getParameter";

    // ARouter aip 的 ParmeterGet 的 生成文件名称 $$Parameter
    String PARAMETER_FILE_NAME = "$$Parameter";

    // String全类名
    public static final String STRING = "java.lang.String";
}
