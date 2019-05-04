package com.lanlongbin.springframework.servlet;

import com.lanlongbin.springframework.annotation.LanController;
import com.lanlongbin.springframework.annotation.LanRequsetMapping;
import com.lanlongbin.springframework.annotation.LanRequsetParam;
import com.lanlongbin.springframework.context.LanApplicationContext;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/3 21:16
 * @description
 * @version: v1.0
 */
public class LanDispatcherServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LanDispatcherServlet.class);
    //web.xml配置的属性
    private static final String LOCATION = "contextConfigLocation";
    //key为url,value为handler
    private Map<String, LanHandlerExecutionChain> handlerMapping = new HashMap<String, LanHandlerExecutionChain>();
    private Map<LanHandlerExecutionChain,LanHandlerAdapter> adapterMapping = new HashMap<LanHandlerExecutionChain, LanHandlerAdapter>();

    //1、初始化
    @Override
    public void init(ServletConfig config) throws ServletException {
        logger.info("LanDispatcherServlet is init..");
        //1、初始化ioc容器
        LanApplicationContext context = new LanApplicationContext(config.getInitParameter(LOCATION));
        //2、接下来按照springmvc的初始化方法顺序执行
        initStrategies(context);
    }
    //3、这里不重写service方法，直接调用get、post方法
//    @Override
//    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        logger.info("service..");
//    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //这里就是调用具体的方法
        try {
            doDispatcher(req, resp);
        } catch (Exception e) {
            logger.info("500 系统异常");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        logger.info("doDispatcher..");
        //1、获取handler，拦截器,HandlerMapping的定义，实际源码就是循环获取
        //通过req中的url找出对应的handler
        LanHandlerExecutionChain handler = getHandler(req);
        if (handler == null) {
            resp.getWriter().write("404 NOT FOUND");
            return;
        }
        //2、获取适配器
        LanHandlerAdapter ha = getHandlerAdapter(handler);
        //3、由适配器去调用具体的方法，通过反射将找出req中的url
        LanModelAndView mv = ha.handle(req, resp, handler);

    }

    protected LanHandlerExecutionChain getHandler(HttpServletRequest request) {
        //循环获取handlerMapping
        if (handlerMapping.isEmpty()) {
            return null;
        }
        String requestURI = request.getRequestURI();
        logger.info("requestURI " + requestURI);
        String contextPath = request.getContextPath();
        String url = requestURI.replace(contextPath, "").replaceAll("/+", "/");
        return handlerMapping.get(url);
    }

    protected LanHandlerAdapter getHandlerAdapter(LanHandlerExecutionChain handler) {
        if(adapterMapping.isEmpty()){
            return null;
        }
        return adapterMapping.get(handler);
    }

    //初始化步骤
    protected void initStrategies(LanApplicationContext context) {
        //解析请求
        initMultipartResolver(context);
        //多语言、国际化
        initLocaleResolver(context);
        //主题view层
        initThemeResolver(context);
        //解析url和方法的关系,将有LanRequsetMapping修饰的所有方法放入handlerMapping中
        initHandlerMappings(context);
        //适配器(匹配的过程),主要是用来动态的匹配方法上的参数
        initHandlerAdapters(context);
        //解析异常
        initHandlerExceptionResolvers(context);
        //视图转发
        initRequestToViewNameTranslator(context);
        //解析模板中的内容（拿到服务器的数据，生成html）
        initViewResolvers(context);
        initFlashMapManager(context);
    }

    private void initMultipartResolver(LanApplicationContext context) {
    }

    private void initLocaleResolver(LanApplicationContext context) {
    }

    private void initThemeResolver(LanApplicationContext context) {
    }

    private void initHandlerMappings(LanApplicationContext context) {
        //找出所有controller修饰的类
        Map<String, Object> ioc = context.getAll();
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(LanController.class)) {
                return;
            }
            String url = "";
            //类上是有LanRequsetMapping修饰
            if (clazz.isAnnotationPresent(LanRequsetMapping.class)) {
                LanRequsetMapping mapping = clazz.getAnnotation(LanRequsetMapping.class);
                url = mapping.value();
            }
            Method[] methods = clazz.getMethods();
            //遍历该类下的所有方法
            for (Method method : methods) {
                if (!method.isAnnotationPresent(LanRequsetMapping.class)) {
                    continue;
                }
                LanRequsetMapping mapping = method.getAnnotation(LanRequsetMapping.class);
                String mappringUrl = url + mapping.value();
                handlerMapping.put(mappringUrl, new LanHandlerExecutionChain(entry.getValue(), method));
                logger.info("Mapping: " + mappringUrl+" " +method.toString());
            }

        }
    }

    private void initHandlerAdapters(LanApplicationContext context) {
        if (handlerMapping.isEmpty()) {
            return;
        }
        //参数类型作为key,参数索引作为值
        Map<String, Integer> paramMapping = new HashMap<String, Integer>();
        for (Map.Entry<String, LanHandlerExecutionChain> entry : handlerMapping.entrySet()) {
            //获取该方法的所有参数
            Class<?>[] parameters = entry.getValue().method.getParameterTypes();
            //如果是HttpServletRequest request, HttpServletResponse response就不需要拿到参数
            //有顺序，但是通过反射没法拿到参数的名字
            for (int i = 0; i < parameters.length; i++) {
                Class<?> parameter = parameters[i];

                if (parameter == HttpServletRequest.class
                        || parameter == HttpServletResponse.class) {
                    paramMapping.put(parameter.getName(), i);
                }
            }
            //每个参数可能有多个注解
            Annotation[][] parameterAnnotations = entry.getValue().method.getParameterAnnotations();
            for (int j = 0;j<parameterAnnotations.length;j++) {
                for(Annotation annotation : parameterAnnotations[j]){
                    if(annotation instanceof LanRequsetParam){
                        String paramName = ((LanRequsetParam) annotation).value();
                        paramMapping.put(paramName,j);
                        continue;
                    }
                }
            }
            adapterMapping.put(entry.getValue(),new LanHandlerAdapter(paramMapping));
        }

    }

    private void initHandlerExceptionResolvers(LanApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(LanApplicationContext context) {
    }

    private void initViewResolvers(LanApplicationContext context) {
    }

    private void initFlashMapManager(LanApplicationContext context) {
    }
}
