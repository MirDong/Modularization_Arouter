package com.dzk.arouter_compiler;

import com.dzk.arouter_annotations.ARouter;
import com.dzk.arouter_annotations.bean.RouterBean;
import com.dzk.arouter_compiler.config.ProcessorConfig;
import com.dzk.arouter_compiler.config.ProcessorUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
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
    /**path仓库**/
    private Map<String, List<RouterBean>> mPathMaps = new HashMap<>();
    /**group仓库**/
    private Map<String,String> mGroupMaps = new HashMap<>();
    private String aptPackage;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementsTool = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        typeTool = processingEnvironment.getTypeUtils();
        //只有接收到app壳传递过来的数据，才能证明我们的APT环境搭建完成
        options = processingEnvironment.getOptions().get(ProcessorConfig.OPTIONS);
        aptPackage = processingEnvironment.getOptions().get(ProcessorConfig.APT_PACKAGE);
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
            // 在循环里面，对 “路由对象” 进行封装
            ARouter aRouter = element.getAnnotation(ARouter.class);
            RouterBean routerBean = new RouterBean.Builder()
                    .addGroup(aRouter.group())
                    .addPath(aRouter.path())
                    .addElement(element)
                    .build();
            // ARouter注解的类 必须继承 Activity
            TypeMirror elementMirror = element.asType();
            if (typeTool.isSubtype(elementMirror,activityMirror)){
                routerBean.setType(RouterBean.TypeEnum.ACTIVITY);
            }else {
                throw new RuntimeException("the class by @ARouter need to TypeNum value");
            }

            if (checkRouterPath(routerBean)){
                messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean Check Success:" + routerBean.toString());
                List<RouterBean> beans = mPathMaps.get(routerBean.getGroup());
                // 如果从Map中找不到key为：bean.getGroup()的数据，就新建List集合再添加进Map
                if(ProcessorUtils.isEmpty(beans)){
                    beans = new ArrayList<>();
                    beans.add(routerBean);
                    mPathMaps.put(routerBean.getGroup(),beans);
                }else {
                    beans.add(routerBean);
                }

            }else {
                messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
            }
        }//end for

        //定义（生成类文件实现的接口）
        TypeElement groupType = elementsTool.getTypeElement(ProcessorConfig.AROUTER_API_GROUP);
        TypeElement pathType = elementsTool.getTypeElement(ProcessorConfig.AROUTER_API_PATH);
        //第一步:Path系列
        try {
            // 生成 Path类
            makePathFile(pathType);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "在生成PATH模板时，异常了 e:" + e.getMessage());
        }
        //第二步:Group系列
        try {
            //生成group类
            makeGroupFile(groupType, pathType);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "在生成GROUP模板时，异常了 e:" + e.getMessage());
        }
        return false;
    }

    private void makeGroupFile(TypeElement groupType, TypeElement pathType) throws IOException{

    }

    private void makePathFile(TypeElement pathType) throws IOException{
        //当Map中没有元素时,无需生成文件
        if(ProcessorUtils.isEmpty(mPathMaps)){
            return;
        }
        // 任何的class类型，必须包装
        // Map<String, RouterBean>
        TypeName methodReturn = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class)
        );
        //遍历仓库
        for (Map.Entry<String, List<RouterBean>> entry : mPathMaps.entrySet()) {
            //1.方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(ProcessorConfig.PATH_METHOD_NAME)
                    // 给方法上添加注解  @Override
                    .addAnnotation(Override.class)
                    // public修饰符
                    .addModifiers(Modifier.PUBLIC)
                    // 把Map<String, RouterBean> 加入方法返回
                    .returns(methodReturn);
            // Map<String, RouterBean> pathMap = new HashMap<>(); // $N == 变量
            methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                    // Map
                    ClassName.get(Map.class),
                    // Map<String,
                    ClassName.get(String.class),
                    // Map<String, RouterBean>
                    ClassName.get(RouterBean.class),
                    // Map<String, RouterBean> pathMap
                    ProcessorConfig.PATH_VAR1,
                    // Map<String, RouterBean> pathMap = new HashMap<>();
                    ClassName.get(HashMap.class));

            List<RouterBean>pathList = entry.getValue();
            /**
             $N == 变量 变量有引用 所以 N
             $L == TypeEnum.ACTIVITY
             */
            for (RouterBean bean : pathList){
                methodBuilder.addStatement("$N.put($S,$T.create($T.$L,$T.class,$S,$S))",
                        // pathMap.put
                        ProcessorConfig.PATH_VAR1,
                        // "/personal/Personal_Main2Activity"
                        bean.getPath(),
                        // RouterBean
                        ClassName.get(RouterBean.class),
                        // RouterBean.Type
                        ClassName.get(RouterBean.TypeEnum.class),
                        // 枚举类型：ACTIVITY
                        bean.getTypeEnum(),
                        // MainActivity.class
                        ClassName.get((TypeElement) bean.getElement()),
                        // 路径名
                        bean.getPath(),
                        // 组名
                        bean.getGroup()
                );
            }//end for
            // return pathMap;
            methodBuilder.addStatement("return $N",ProcessorConfig.PATH_VAR1);
            //注意：不能像以前一样，1.方法，2.类  3.包， 因为这里面有implements ，所以 方法和类要合为一体生成才行，这是特殊情况
            // 最终生成的类文件名  ARouter$$Path$$personal
            String finalClassName = ProcessorConfig.PATH_FILE_NAME + entry.getKey();
            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由Path类文件：" +
                    aptPackage + "." + finalClassName);
            JavaFile.builder(
                    // 包名  APT 存放的路径
                    aptPackage,
                    //类名
                    TypeSpec.classBuilder(finalClassName)
                    // 实现ARouterPath接口  implements ARouterPath==pathType
                    .addSuperinterface(ClassName.get(pathType))
                    .addModifiers(Modifier.PUBLIC)
                     // 方法的构建（方法参数 + 方法体）
                    .addMethod(methodBuilder.build())
                    .build())
                    .build()
            .writeTo(filer);
            //注意：PATH 路径文件生成出来了，才能赋值路由组mGroupMapsupMap
            mGroupMaps.put(entry.getKey(),finalClassName);
        }
    }

    /**
     * 校验@ARouter注解的值，如果group未填写就从必填项path中截取数据
     * @param routerBean  路由详细信息，最终实体封装类
     * @return
     */
    private boolean checkRouterPath(RouterBean routerBean) {
        //"app"   "login"   "personal"
        String group = routerBean.getGroup();
        // path : "/app/MainActivity" , ...
        String path = routerBean.getPath();
        // @ARouter注解中的path值，必须要以 / 开头（模仿阿里Arouter规范）
        if (ProcessorUtils.isEmpty(path) || !path.startsWith("/")){
            messager.printMessage(Diagnostic.Kind.ERROR,"path can not be null or start with /");
            return false;
        }

        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            // 架构师定义规范，让开发者遵循
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
            return false;
        }
        if (!ProcessorUtils.isEmpty(group) && !options.equals(group)){
            // 架构师定义规范，让开发者遵循
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的group值必须和子模块名一致！");
            return false;
        }else {
            //从中间截取group如：/app/MainActivity 截取出 app
            String finalGroup = path.substring(1,path.indexOf("/",1));
            routerBean.setGroup(finalGroup);
        }


        return true;
    }
}