package com.uv.rpc.annotation;
/*
 * @author uv
 * @date 2018/10/11 18:31
 * 暴露的接口
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {
    //实现类实现的暴露的接口
    Class<?> value();
}
