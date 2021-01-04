package com.dah.desb.config;

import com.dah.desb.domain.route.log.worker.RouteLogWorker;
import com.dah.desb.domain.route.worker.RouteWorker;
import com.dah.desb.infrastructure.worker.Worker;
import com.dah.desb.infrastructure.worker.WorkerEngineFactoryBean;
import com.dah.desb.infrastructure.worker.WorkerWebsocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WorkerConfig {

    @Autowired
    @Qualifier("redisConnectionFactory")
    RedisConnectionFactory redisConnectionFactory;

    @Autowired
    @Qualifier("redisMessageListenerContainer")
    RedisMessageListenerContainer redisMessageListenerContainer;

    @Autowired
    @Qualifier("workerWebsocket")
    WorkerWebsocket workerWebsocket;

    @Bean
    public List<Worker> workers() {
        List<Worker> workers = new ArrayList<>();
        workers.add(routeLogWorker10Minutes());
        workers.add(routeLogWorker1Hour());
        workers.add(routeLogWorkerAll());
        workers.add(routeWorker());
        return workers;
    }

    @Bean
    public WorkerEngineFactoryBean workerEngineFactoryBean() {
        return new WorkerEngineFactoryBean("desb", redisConnectionFactory, redisMessageListenerContainer, workerWebsocket, workers());
    }

    @Bean
    public RouteLogWorker routeLogWorker10Minutes() {
        RouteLogWorker routeLogWorker = new RouteLogWorker(10L);
        routeLogWorker.setKey("updateRouteLog10Minutes");
        routeLogWorker.setName("路由日志更新 - 10分钟");
        routeLogWorker.setCron("0 0/10 * * * ?");
        return routeLogWorker;
    }

    @Bean
    public RouteLogWorker routeLogWorker1Hour() {
        RouteLogWorker routeLogWorker = new RouteLogWorker(60L);
        routeLogWorker.setKey("updateRouteLog1Hour");
        routeLogWorker.setName("路由日志更新 - 1小时");
        routeLogWorker.setCron("0 0 0/1 * * ?");
        return routeLogWorker;
    }

    @Bean
    public RouteLogWorker routeLogWorkerAll() {
        RouteLogWorker routeLogWorker = new RouteLogWorker(0L);
        routeLogWorker.setKey("updateRouteLogAll");
        routeLogWorker.setName("路由日志更新 - 周一");
        //每周一中午12:00:00执行
        routeLogWorker.setCron("0 0 12 ? * MON");
        return routeLogWorker;
    }

    @Bean
    public RouteWorker routeWorker() {
        RouteWorker routeWorker = new RouteWorker();
        routeWorker.setKey("updateRouteExecuteInfo");
        routeWorker.setName("路由执行情况更新 - 1小时");
        routeWorker.setCron("0 0 0/1 * * ?");
        return routeWorker;
    }

}