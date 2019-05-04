# springmvcframework
手写实现springmvc框架

1、创建ioc容器
 
 步骤：定位、载入、注册、初始化、注入
	
2、实现LanDispatcherServlet

 步骤：和springmvc源码中的步骤相似，主要有：
 
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
