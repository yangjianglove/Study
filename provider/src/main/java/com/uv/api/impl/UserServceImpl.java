package com.uv.api.impl;
/*
 * @author uv
 * @date 2018/10/12 8:56
 * 接口实现类
 */

import com.uv.api.UserService;
import com.uv.rpc.annotation.RpcService;

@RpcService(UserService.class)
public class UserServceImpl implements UserService {

    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
