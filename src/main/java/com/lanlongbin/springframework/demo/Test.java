package com.lanlongbin.springframework.demo;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 兰龙斌
 * @date Created in 2019/5/3 23:14
 * @description
 * @version: v1.0
 */
public class Test {
    public static void main(String[] args) {
        Map<String,Object> map = new ConcurrentHashMap<String, Object>();
        map.put("serviceImp",new ServiceImp());
        for(Map.Entry<String,Object> entry : map.entrySet()){
            System.out.println(entry.getValue());
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields){
                System.out.println("field:"+field);
            }
            System.out.println(fields);
        }
    }
}
