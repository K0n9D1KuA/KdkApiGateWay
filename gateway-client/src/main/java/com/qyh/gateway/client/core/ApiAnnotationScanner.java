package com.qyh.gateway.client.core;


import config.HttpServiceInvoker;
import config.ServiceDefinition;
import config.ServiceInvoker;
import constants.BasicConst;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解扫描类
 */
public class ApiAnnotationScanner {

    private ApiAnnotationScanner() {
    }

    private static class SingletonHolder {
        static final ApiAnnotationScanner INSTANCE = new ApiAnnotationScanner();
    }

    public static ApiAnnotationScanner getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 扫描传入的bean对象，最终返回一个服务定义
     *
     * @param bean
     * @param args
     * @return
     */
    public ServiceDefinition scanner(Object bean, Object... args) {
        Class<?> aClass = bean.getClass();

        //判断上面是否添加了 @ApiService注解
        if (!aClass.isAnnotationPresent(ApiService.class)) {
            return null;
        }

        ApiService apiService = aClass.getAnnotation(ApiService.class);

        //获得@ApiService上面的属性值
        String serviceId = apiService.serviceId();
        ApiProtocol protocol = apiService.protocol();
        String patternPath = apiService.patternPath();
        String version = apiService.version();

        ServiceDefinition serviceDefinition = new ServiceDefinition();

        Map<String, ServiceInvoker> invokerMap = new HashMap<>();

        //拿到所有方法
        Method[] methods = aClass.getMethods();
        if (methods.length > 0) {
            for (Method method : methods) {
                //判断是否有 @ApiInvoker 注解
                ApiInvoker apiInvoker = method.getAnnotation(ApiInvoker.class);
                if (apiInvoker == null) {
                    continue;
                }

                String path = apiInvoker.path();

                switch (protocol) {
                    case HTTP:
                        HttpServiceInvoker httpServiceInvoker = createHttpServiceInvoker(path);
                        invokerMap.put(path, httpServiceInvoker);
                        break;
                    case DUBBO:
                        //TODO 待实现DUBBO的协议
                        break;
                    default:
                        break;
                }
            }

            //构建serviceDefinition
            serviceDefinition.setUniqueId(serviceId + BasicConst.COLON_SEPARATOR + version);
            serviceDefinition.setServiceId("hdu-server");
            serviceDefinition.setVersion(version);
            serviceDefinition.setProtocol(protocol.getCode());
            serviceDefinition.setPatternPath(patternPath);
            serviceDefinition.setEnable(true);
            serviceDefinition.setInvokerMap(invokerMap);

            return serviceDefinition;
        }

        return null;
    }


    /**
     * 构建HttpServiceInvoker对象
     */
    private HttpServiceInvoker createHttpServiceInvoker(String path) {
        HttpServiceInvoker httpServiceInvoker = new HttpServiceInvoker();
        httpServiceInvoker.setInvokerPath(path);
        return httpServiceInvoker;
    }



}
