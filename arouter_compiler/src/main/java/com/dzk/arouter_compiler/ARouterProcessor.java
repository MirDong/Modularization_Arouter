package com.dzk.arouter_compiler;

import com.dzk.arouter_annotations.ARouter;
import com.dzk.arouter_annotations.bean.RouterBean;
import com.dzk.arouter_compiler.config.ProcessorConfig;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
// 先JavaPoet 写一个简单示例，方法--->类--> 包，是倒序写的思路哦
/**
 * package com.example.helloworld;
 *
 * public final class HelloWorld {
 *   public static void main(String[] args) {
 *     System.out.println("Hello, JavaPoet!");
 *   }
 * }
 */

/**
 * bellow is realizable of HelloWorld class File with javapoet tool
 *
 *    //1.方法
 *             MethodSpec methodSpec = MethodSpec.methodBuilder("main")
 *                     .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
 *                     .returns(void.class)
 *                     .addParameter(String[].class,"args")
 *                     .addStatement("$T.out.println($S)",System.class,"Hello, JavaPoet!")
 *                     .build();
 *
 *             //2.类
 *             TypeSpec typeSpec = TypeSpec.classBuilder("HelloWorld")
 *                     .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
 *                     .addMethod(methodSpec)
 *                     .build();
 *             //3.包
 *             JavaFile javaFile = JavaFile.builder("com.example.helloworld", typeSpec).build();
 *             try {
 *                 javaFile.writeTo(filer);
 *             } catch (IOException e) {
 *                 e.printStackTrace();
 *                 messager.printMessage(Diagnostic.Kind.NOTE, "生成Test文件时失败，异常:" + e.getMessage());
 *             }
 *
 */



// 通过auto-service中的@AutoService可以自动生成AutoService注解处理器，用来注册
// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
// 允许/支持的注解类型，让注解处理器处理,这里是@ARouter
@SupportedAnnotationTypes({ProcessorConfig.AROUTER_PACKAGE})
// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 注解处理器接收的参数
@SupportedOptions({ProcessorConfig.OPTIONS,ProcessorConfig.APT_PACKAGE})
public class ARouterProcessor extends AbstractProcessor {
    /**
     * 操作Element的工具类(类，函数，属性，都是Element)
     */
    private Elements elementsTool;
    /**
     * type(类信息)的工具类，包含用于操作TypeMirror的工具方法
     */
    private Types typeTool;
    /**
     * Message用来打印日志相关信息
     */
    private Messager messager;
    /**
     * 文件生成器，类、资源等，就是最终要生成的文件 是需要Filer来完成的
     */
    private Filer filer;
    private String options;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementsTool = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        typeTool = processingEnvironment.getTypeUtils();
        //只有接收到app壳传递过来的数据，才能证明我们的APT环境搭建完成
        options = processingEnvironment.getOptions().get(ProcessorConfig.OPTIONS);
        String aptPackage = processingEnvironment.getOptions().get(ProcessorConfig.APT_PACKAGE);
        messager.printMessage(Diagnostic.Kind.NOTE,">>>>>>>>>>>>>>>>>>>>>> options:" + options);
        messager.printMessage(Diagnostic.Kind.NOTE,">>>>>>>>>>>>>>>>>>>>>> aptPackage:" + aptPackage);
        if (options != null && aptPackage != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, "APT Environment create finished....");
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "APT Environment appear problem，please check options and aptPackage is null or not...");
        }
    }
    /**
     * 相当于main函数，开始处理注解
     * 注解处理器的核心方法，处理具体的注解，生成Java文件
     *
     * @param set              使用了支持处理注解的节点集合
     * @param roundEnvironment 当前或是之前的运行环境,可以通过该对象查找的注解。
     * @return true 表示后续处理器不会再处理（已经处理完成）
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE,"process.....");
        if (set.isEmpty()){
            messager.printMessage(Diagnostic.Kind.NOTE, "can not find the class marked by @ARouter");
            return false;
        }
        //获取所有被@ARouter注解的元素集合
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
        TypeElement activityType = elementsTool.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE);
        TypeMirror activityMirror = activityType.asType();
        //遍历所有的类节点
        for (Element element : elements) {
            String packageName = elementsTool.getPackageOf(element).getQualifiedName().toString();
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>>>>> the class marked by @ARetuer is：" + className);

            ARouter aRouter = element.getAnnotation(ARouter.class);
            RouterBean routerBean = new RouterBean.Builder()
                    .addGroup(aRouter.group())
                    .addPath(aRouter.path())
                    .addElement(element)
                    .build();
        }
        return false;
    }
}