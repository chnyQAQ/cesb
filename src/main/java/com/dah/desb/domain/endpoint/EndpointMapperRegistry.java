package com.dah.desb.domain.endpoint;

import com.dah.desb.domain.route.endpoint.RouteEndpoint;
import com.dah.desb.domain.route.exception.RouteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EndpointMapperRegistry implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EndpointMapperRegistry.class);

    private ApplicationContext applicationContext;

    private Map<String, EndpointMapper> registry;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 自动按类型注册endpointMapper
        registry = new HashMap<>();
        for (EndpointMapper bean : applicationContext.getBeansOfType(EndpointMapper.class).values()) {
            EndpointTypeSupport endpointType = AnnotationUtils.findAnnotation(bean.getClass(), EndpointTypeSupport.class);
            if (endpointType != null) {
                registry.put(endpointType.value().toString(), bean);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public int getCount(String endpointType) throws RouteException {
        EndpointMapper endpointMapper = registry.get(endpointType);
        if (endpointMapper == null) {
            throw new RouteException("路由端点查询路由信息失败，endpointMapper未注册，type=" + endpointType);
        }
        return endpointMapper.getCount();
    }

    public Endpoint determineEndpoint(RouteEndpoint routeEndpoint) throws RouteException {
        return determineEndpoint(routeEndpoint.getEndpointType(), routeEndpoint.getEndpointId());
    }

    public Endpoint determineEndpoint(String endpointType, String endpointId) throws RouteException {
        EndpointMapper endpointMapper = registry.get(endpointType);
        if (endpointMapper == null) {
            throw new RouteException("路由端点查询路由信息失败，endpointMapper未注册，type=" + endpointType);
        }
        return endpointMapper.getById(endpointId);
    }

    public Object determineEndpoints(String endpointType, String search){
        EndpointMapper endpointMapper = registry.get(endpointType);
        if (endpointMapper == null) {
            logger.error("路由端点查询路由信息失败，endpointMapper未注册，type=" + endpointType);
            return null;
        }
        return endpointMapper.getSearch(search);
    }
}
