package com.uv.rpc.discovery;
/*
 * @author uv
 * @date 2018/10/11 18:42
 * 扫描注解
 */

import com.uv.rpc.annotation.RpcService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.reflections.Reflections;

public class ScanUtil {

    //暴露服务的接口
    public static List<String> interfaceList = new ArrayList<>();
    //暴露接口和实现类的映射关系（key=接口，value=接口实现类）
    public static Map<String, Class<?>> interfaceClassMap = new ConcurrentHashMap<>();

    //扫描类
    public static void scanPath(String path) {
        Reflections reflections = new Reflections(path);
        //扫描带有@RpcService注解的类
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RpcService.class);
        for (Class<?> clazz : annotated) {
            RpcService rpcService = clazz.getAnnotation(RpcService.class);
            //暴露服务的接口
            interfaceList.add(rpcService.value().getName());
            //暴露接口和实现类的映射关系
            interfaceClassMap.put(rpcService.value().getName(), clazz);
        }
    }
}
