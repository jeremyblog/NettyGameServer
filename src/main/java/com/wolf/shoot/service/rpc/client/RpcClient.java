package com.wolf.shoot.service.rpc.client;


import com.wolf.shoot.service.net.RpcRequest;
import com.wolf.shoot.service.rpc.RpcServiceDiscovery;
import com.wolf.shoot.service.rpc.client.proxy.IAsyncObjectProxy;
import com.wolf.shoot.service.rpc.client.proxy.ObjectProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RPC Client（Create RPC proxy）
 * @author luxiaoxun
 */
@Service
public class RpcClient {

    @Autowired
    private RpcRequestFactroy rpcRequestFactroy;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T>(interfaceClass)
        );
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass);
    }

    public static void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }

    public void stop() {
        threadPoolExecutor.shutdown();
//        serviceDiscovery.stop();
        ConnectManage.getInstance().stop();
    }

    public RpcRequest createRpcRequest(String className, String funcName, Object... args) {
        return rpcRequestFactroy.createRequest(className, funcName, args);
    }
}

