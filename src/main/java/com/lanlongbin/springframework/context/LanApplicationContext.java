package com.lanlongbin.springframework.context;

import com.lanlongbin.springframework.annotation.LanAutowired;
import com.lanlongbin.springframework.annotation.LanController;
import com.lanlongbin.springframework.annotation.LanService;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/3 21:15
 * @description
 * @version: v1.0
 */
public class LanApplicationContext {
    private static final Logger logger = Logger.getLogger(LanApplicationContext.class);
    //ioc容器
    private static Map<String, Object> instanceMapping = new ConcurrentHashMap<String, Object>();
    //存放class,类型源码中的配置信息，外部是看不到的，相当于BeanFactroy
    //只能访问ioc容器，间接调用gebean()
    private List<String> cacheClass = new LinkedList<String>();

    //测试容器启动
//    public static void main(String[] args) {
//        new LanApplicationContext("application.properties");
//        System.out.println("instanceMapping: " + instanceMapping);
//    }

    public LanApplicationContext(String location) {
        //定位、载入、注册、初始化、注入
        InputStream is = null;
        try {
            //1、定位
            is = this.getClass().getClassLoader().getResourceAsStream(location.substring(location.indexOf("classpath:")+"classpath:".length()));
            //2、载入
            Properties properties = new Properties();
            properties.load(is);
            //3、注册，将扫描包下的所有class找出，放到缓存中
            String packageName = properties.getProperty("scanPackage");
            doRegister(packageName);
            //4、初始化,将所有符合注解的类初始化
            doCreate();
            //5、注入
            populate();
            logger.info("ioc容器初始化完成..");
        } catch (Exception e) {
            logger.error("LanApplicationContext初始化失败");
        }
    }
    private void populate() throws IllegalAccessException {
        if(instanceMapping.isEmpty()){return; }
        //循环遍历
        for(Map.Entry<String,Object> entry : instanceMapping.entrySet()){
            //把该对象下的所有属性取出，包括私有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field : fields){
                if(!field.isAnnotationPresent(LanAutowired.class)){ continue;}
                //LanAutowired默认是不使用名字，所以使用类型注入
                String id = field.getType().getName();
                //开放私有属性的访问权限
                field.setAccessible(true);
                //这里的id没有首字母小写，因为默认LanAutowired使用在接口上，而在doCreate方法上没做处理（instanceMapping.put(i.getName(),clazz.newInstance());）
                field.set(entry.getValue(),instanceMapping.get(id));
            }
        }
    }
    private void doCreate() throws Exception {
        if (cacheClass.isEmpty()) {
            return;
        }
        for (String name : cacheClass) {
            Class<?> clazz = Class.forName(name);
            logger.info(clazz);
            if (clazz.isAnnotationPresent(LanController.class)) {
                String id = lowerFirstChar(clazz.getSimpleName());
                instanceMapping.put(id, clazz.newInstance());

            } else if (clazz.isAnnotationPresent(LanService.class)) {
                LanService service = clazz.getAnnotation(LanService.class);
                String id = service.value();
                //如果设置了名字，优先使用自己定义的名字，这里未考虑到是接口的情况
                if (id != null && !"".equals(id)) {
                    instanceMapping.put(id, clazz.newInstance());
                    continue;
                }
                //如果未设置名字，则使用默认规则
                //1、类名首字母小写
//                String id2 = lowerFirstChar(clazz.getSimpleName());
//                instanceMapping.put(id2, clazz.newInstance());
                //2、如果该类实现了接口，就用接口的类型作为id
                Class<?>[] interfaces = clazz.getInterfaces();
                for(Class<?> i: interfaces){
                    instanceMapping.put(i.getName(),clazz.newInstance());
                }

            }
        }
    }

    //将类名首字母转为小写
    private String lowerFirstChar(String className) {
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doRegister(String packageName) {
        logger.info("packageName " + packageName);
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        if (url == null) {
            return;
        }
        File dir = new File(url.getFile());
        //返回该目录下的所有文件
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                //递归查找文件夹
                doRegister(packageName + "." + file.getName());
            } else {
                //如果是类文件则将全路径名加入cache中
                cacheClass.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }
    }
    public Map<String,Object> getAll(){
        return instanceMapping;
    }
}
