package com.dzk.arouter_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;


/**
 * @author jackie
 * @date 2020/11/29
 */
// 该注解作用在字段上
@Target(FIELD)
// 要在编译时进行一些预处理操作，注解会在class文件中存在
@Retention(CLASS)
public @interface Parameter {
    // 不填写name的注解值表示该属性名就是key，填写了就用注解值作为key
    // 从getIntent()方法中获取传递参数值
    String name() default "";
}
