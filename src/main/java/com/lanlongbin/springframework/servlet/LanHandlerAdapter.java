package com.lanlongbin.springframework.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/3 22:26
 * @description 适配器
 * @version: v1.0
 */
public class LanHandlerAdapter {
    //方法参数和对应的索引
    private Map<String, Integer> paramMapping;

    public LanHandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }

    LanModelAndView handle(HttpServletRequest request, HttpServletResponse response, LanHandlerExecutionChain handler) throws Exception {
        Class<?>[] parameterTypes = handler.method.getParameterTypes();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Object[] paramsValues = new Object[parameterTypes.length];

        if(parameterMap.isEmpty()){
            String reqName = HttpServletRequest.class.getName();
            if (this.paramMapping.containsKey(reqName)) {
                Integer reqIndex = this.paramMapping.get(reqName);
                paramsValues[reqIndex] = request;
            }
            String resName = HttpServletResponse.class.getName();
            if (this.paramMapping.containsKey(resName)) {
                Integer resIndex = this.paramMapping.get(resName);
                paramsValues[resIndex] = response;
            }
            handler.method.invoke(handler.controller, paramsValues);
            return null;
        }
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[ | \\]", "");
            if (!this.paramMapping.containsKey(entry.getKey())) {
                continue;
            }
            //请求参数赋值

            Integer index = this.paramMapping.get(entry.getKey());
            paramsValues[index] = castValue(value, parameterTypes[index]);
            //HttpServletRequest request, HttpServletResponse response赋值
            String reqName = HttpServletRequest.class.getName();
            if (this.paramMapping.containsKey(reqName)) {
                Integer reqIndex = this.paramMapping.get(reqName);
                paramsValues[reqIndex] = request;
            }
            String resName = HttpServletResponse.class.getName();
            if (this.paramMapping.containsKey(resName)) {
                Integer resIndex = this.paramMapping.get(resName);
                paramsValues[resIndex] = response;
            }
        }
        //调用具体方法
        handler.method.invoke(handler.controller, paramsValues);
        return null;
    }

    private Object castValue(String value, Class<?> clazz) {
        if (clazz == String.class) {
            return value;
        } else if (clazz == Integer.class) {
            return Integer.valueOf(value);
        } else if (clazz == int.class) {
            return Integer.valueOf(value).intValue();
        } else {
            return null;
        }
    }
}
