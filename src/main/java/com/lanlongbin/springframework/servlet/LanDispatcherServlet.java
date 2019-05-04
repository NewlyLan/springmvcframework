package com.lanlongbin.springframework.servlet;

import com.lanlongbin.springframework.context.LanApplicationContext;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private Map<String, LanHandlerExecutionChain> handlerMapping = new HashMap<String, LanHandlerExecutionChain>();

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
        LanHandlerExecutionChain handler = getHandler(req);
        if (handler == null) {
            resp.getWriter().write("404 NOT FOUND");
            return;
        }
        //2、获取适配器
        LanHandlerAdapter adapter = getHandlerAdapter(handler);
        //3、由适配器去调用具体的方法
        LanModelAndView mv = adapter.handle(req, resp, handler);

    }

    protected LanHandlerExecutionChain getHandler(HttpServletRequest request) {
        //循环获取handlerMapping
        return null;
    }

    protected LanHandlerAdapter getHandlerAdapter(Object handler) {
        return null;
    }

    //初始化步骤
    protected void initStrategies(LanApplicationContext context) {
        //解析请求
        initMultipartResolver(context);
        //多语言、国际化
        initLocaleResolver(context);
        //主题view层
        initThemeResolver(context);
        //解析url和方法的关系
        initHandlerMappings(context);
        //适配器(匹配的过程)
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
    }

    private void initHandlerAdapters(LanApplicationContext context) {
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
