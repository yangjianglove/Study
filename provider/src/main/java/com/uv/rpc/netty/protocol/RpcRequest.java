package com.uv.rpc.netty.protocol;
/*
 * @author uv
 * @date 2018/10/13 18:10
 * 传输请求对象
 */

import java.io.Serializable;

public class RpcRequest implements Serializable{

    private static final long serialVersionUID = -4558182507809817096L;

    private String id; //消息ID
    private String className; //远程调用的类
    private String methodName; //远程调用的方法
    private Class<?>[] parameterTypes; //方法参数类型列表
    private Object[] parameters; //方法参数

    public String getId() {
        return id;
    }

    public RpcRequest setId(String id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public RpcRequest setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public RpcRequest setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public RpcRequest setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public RpcRequest setParameters(Object[] parameters) {
        this.parameters = parameters;
        return this;
    }
}
