package com.dzk.arouter_compiler;

import com.dzk.arouter_annotations.Parameter;
import com.dzk.arouter_compiler.config.ProcessorConfig;
import com.dzk.arouter_compiler.config.ProcessorUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author jackie
 * @date 2020/11/29
 */
// 通过auto-service中的@AutoService可以自动生成AutoService注解处理器，用来注册
// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
// 允许/支持的注解类型，让注解处理器处理,这里是@ARouter
@SupportedAnnotationTypes({ProcessorConfig.PARAMETER_PACKAGE})
// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ParameterProcessor extends AbstractProcessor {
    //类信息
    private Elements elementsTools;
    //具体类型
    private Types typesUtils;
    //日志
    private Messager messager;
    //文件生成器
    private Filer filer;
    //临时map存储，用来存放被@Parameter注解的属性集合,生成文件时遍历
    //key类节点(包含@Parameter注解的类)，value 被@Parameter注解的属性集合
    private Map<TypeElement, List<Element>> tempParameterMap = new HashMap<>();
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementsTools = processingEnvironment.getElementUtils();
        typesUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //扫描的时候，看那些地方使用到了@Parameter注解
        if(!ProcessorUtils.isEmpty(set)){
            // 获取所有被 @Parameter 注解的 元素（属性）集合
            Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(Parameter.class);
            //element == name ,sex, age
            for (Element element : annotatedElements) {
                //字段节点的上一个节点，也就是类节点
                //注解在属性的上面，属性节点父节点是类节点
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                //key就是类似 Personal_MainActivity
                if (tempParameterMap.containsKey(enclosingElement)){
                    tempParameterMap.get(enclosingElement).add(element);
                }else {
                    List<Element> parameterList = new ArrayList<>();
                    parameterList.add(element);
                    tempParameterMap.put(enclosingElement,parameterList);
                }
            }//end for
            TypeElement  activityElement = elementsTools.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE);
            TypeElement  parameterElement = elementsTools.getTypeElement(ProcessorConfig.PARAMETER_GET_API);
            //Object targetParameter
            ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.OBJECT, ProcessorConfig.PARAMETER_NAME).build();
            // 循环遍历 缓存tempParameterMap
            // 可能很多地方都使用了 @Parameter注解，那么就需要去遍历 仓库
            for (Map.Entry<TypeElement, List<Element>> entry : tempParameterMap.entrySet()) {
                // key：   Personal_MainActivity
                // value： [name,sex,age]
                TypeElement typeElement = entry.getKey();

                // 非Activity直接报错
                // 如果类名的类型和Activity类型不匹配
                if (!typesUtils.isSubtype(typeElement.asType(),activityElement.asType())){
                    throw new RuntimeException("@Parameter注解目前仅限用于Activity类之上");
                }
                // 获取类名 == Personal_MainActivity
                ClassName className = ClassName.get(typeElement);
                //方法生成
                ParameterFactory factory = new ParameterFactory
                        .Builder(parameterSpec)
                        .setClassName(className)
                        .setMessager(messager)
                        .build();
                // Personal_MainActivity t = (Personal_MainActivity) targetParameter;
                factory.addFirstStatement();
                //多个element，即name,age,sex
                for (Element element : entry.getValue()) {
                    factory.buildStatement(element);
                }

                //最终生成的类文件名(类名$$Parameter)例如：Personal_MainActivity$$Parameter
                String finalClassName = typeElement.getSimpleName() + ProcessorConfig.PARAMETER_FILE_NAME;
                messager.printMessage(Diagnostic.Kind.NOTE, "APT生成获取参数类文件：" +
                        className.packageName() + "." + finalClassName);
                try {
                    JavaFile.builder(
                            //包名
                            className.packageName(),
                            //类名
                            TypeSpec.classBuilder(finalClassName)
                            //  implements ParameterGet 实现ParameterGet接口
                            .addSuperinterface(ClassName.get(parameterElement))
                            .addModifiers(Modifier.PUBLIC)
                             // 方法的构建（方法参数 + 方法体）
                            .addMethod(factory.build())
                            .build())
                            .build()
                            .writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
