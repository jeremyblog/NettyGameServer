package com.wolf.shoot.manager;

/**
 * Created by jwp on 2017/2/4.
 */

import com.wolf.shoot.common.constant.Loggers;
import com.wolf.shoot.manager.spring.LocalSpringBeanManager;
import com.wolf.shoot.manager.spring.LocalSpringServiceManager;
import com.wolf.shoot.service.IService;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author b053-mac
 *	本地服务管理
 */
public class LocalMananger implements ILocalManager{

    protected static final Logger log = Loggers.serverLogger;

    public static LocalMananger instance = new LocalMananger();

    protected static Map<Class,Object> services;

    public LocalMananger() {
        services = new LinkedHashMap<Class,Object>(40,0.5f);
    }

    public static LocalMananger getInstance(){
        return instance;
    }

    private LocalSpringServiceManager localSpringServiceManager;

    private LocalSpringBeanManager localSpringBeanManager;

    public LocalSpringBeanManager getLocalSpringBeanManager() {
        return localSpringBeanManager;
    }

    public void setLocalSpringBeanManager(LocalSpringBeanManager localSpringBeanManager) {
        this.localSpringBeanManager = localSpringBeanManager;
    }

    public LocalSpringServiceManager getLocalSpringServiceManager() {
        return localSpringServiceManager;
    }

    public void setLocalSpringServiceManager(LocalSpringServiceManager localSpringServiceManager) {
        this.localSpringServiceManager = localSpringServiceManager;
    }

    @Override
    public <X,Y extends X> void create(Class<Y> clazz,Class<X> inter) throws Exception{
        log.info(clazz.getSimpleName() + " is create");
        Object newObject = clazz.newInstance();
        if(newObject instanceof IService){
            ((IService)newObject).startup();
        }
        add(newObject,inter);
    }

    @Override
    public <T> void add(Object service, Class<T> inter) {
        log.info(service.getClass().getSimpleName() + " is add");
        if(service.getClass()!=inter&&!inter.isAssignableFrom(service.getClass())) //接口和实现类必须相等或者继承关系
            throw new IllegalArgumentException();
        services.put(inter, service);
    }


    @Override
    public <T> T get(Class<T> clazz) {
        return (T)services.get(clazz);
    }

    @Override
    public void shutdown() {
//        Object[] ss = new Object[services.size()];
//        services.values().toArray(ss);
        Object[] ss = services.values().toArray(new Object[0]);
        for(int i=ss.length-1;i>0;i--){
            if(ss[i] instanceof IService) {
                try {
                    ((IService)ss[i]).shutdown();
                } catch (Exception e) {
                    Loggers.errorLogger.error(e.toString(),e);
                }
            }
        }
    }


}
