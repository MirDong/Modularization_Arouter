package com.dzk.arouter_compiler;

import com.dzk.arouter_annotations.Parameter;
import com.dzk.arouter_compiler.config.ProcessorConfig;
import com.dzk.arouter_compiler.config.ProcessorUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * @author jackie
 * @date 2020/11/29
 */

/*
   目的 生成以下代码：
        @Override
        public void getParameter(Object targetParameter) {
              Personal_MainActivity t = (Personal_MainActivity) targetParameter;
              t.name = t.getIntent().getStringExtra("name");
              t.sex = t.getIntent().getStringExtra("sex");
        }
 */
public class ParameterFactory {
    //方法构建
    private MethodSpec.Builder methodBuilder;
    //类名，如：MainActivity  /  Personal_MainActivity
    private ClassName className;
    private ParameterSpec parameterSpec;
    // Messager用来报告错误，警告和其他提示信息
    private Messager messager;

    public ParameterFactory(Builder builder) {
        this.className = builder.className;
        this.messager = builder.messager;
        // 生成此方法
        // 通过方法参数体构建方法体：public void getParameter(Object target) {
        methodBuilder = MethodSpec.methodBuilder(ProcessorConfig.PARAMETER_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(builder.parameterSpec);
    }

    /** 只有一行
     * Personal_MainActivity t = (Personal_MainActivity) targetParameter;
     */
    public void addFirstStatement(){
        methodBuilder.addStatement("$T t = ($T) $N",className,className,ProcessorConfig.PARAMETER_NAME);
    }

    public MethodSpec build(){
        return methodBuilder.build();
    }

    /** 多行 循环 复杂
     * 构建方体内容，如：t.s = t.getIntent.getStringExtra("s");
     * @param element 被注解的属性元素
     */
    public void buildStatement(Element element){
        // 遍历注解的属性节点 生成函数体
        TypeMirror typeMirror = element.asType();
        // 获取 TypeKind 枚举类型的序列号
        int type = typeMirror.getKind().ordinal();
        //获取注解的值
        String annotationValue =element.getAnnotation(Parameter.class).name();
        //获取属性名  // 获取属性名  name  age  sex
        String fieldName = element.getSimpleName().toString();
        // 配合： t.age = t.getIntent().getBooleanExtra("age", t.age ==  9);
        // 判断注解的值为空的情况下的处理（注解中有name值就用注解值）
        annotationValue = ProcessorUtils.isEmpty(annotationValue) ? fieldName : annotationValue;
        String finalValue =  "t." + fieldName;
        // TODO t.name = t.getIntent().getStringExtra("name");
        String methodContent = finalValue + " = t.getIntent().";
        if (type == TypeKind.INT.ordinal()){
            // t.s = t.getIntent().getIntExtra("age", t.age);  有默认值
            methodContent += "getIntExtra($S,"+finalValue+")";
        }else if (type == TypeKind.BOOLEAN.ordinal()){
            // t.s = t.getIntent().getBooleanExtra("isSuccessful", t.isSuccessful);
            methodContent += "getBooleanExtra($S,"+finalValue+")";
        }else {//String类型,没有序列号
            // t.s = t.getIntent.getStringExtra("s");
            // typeMirror.toString() java.lang.String
            if (typeMirror.toString().equalsIgnoreCase(ProcessorConfig.STRING)){
                //没有默认值
                methodContent += "getStringExtra($S)";
            }
        }

        if (methodContent.endsWith(")")){
            //getBooleanExtra  getIntExtra   getStringExtra进行了赋值
            methodBuilder.addStatement(methodContent,annotationValue);
        }else {
            messager.printMessage(Diagnostic.Kind.NOTE,"目前暂支持String、int、boolean传参");
        }
    }


    public static class Builder {
        // Messager用来报告错误，警告和其他提示信息
        private Messager messager;

        // 类名，如：MainActivity
        private ClassName className;

        // 方法参数体
        private ParameterSpec parameterSpec;
        public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
        }

        public Builder setMessager(Messager messager) {
            this.messager = messager;
            return this;
        }

        public Builder setClassName(ClassName className) {
            this.className = className;
            return this;
        }

        public ParameterFactory build(){
            if (parameterSpec == null){
                throw new IllegalArgumentException("parameterSpec方法参数体为空");
            }

            if (className == null){
                throw new IllegalArgumentException("方法内容中的className为空");
            }

            if (messager == null){
                throw new IllegalArgumentException("messager为空，Messager用来报告错误、警告和其他提示信息");
            }
            return new ParameterFactory(this);
        }
    }
} 
