package com.dah.desb.config;

import com.dah.desb.infrastructure.camel.component.general.GeneralComponent;
import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessType;
import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessorRegistry;
import com.dah.desb.infrastructure.camel.component.general.process.impl.HttpParamsToMapProcessor;
import com.dah.desb.infrastructure.camel.component.general.process.impl.JsonToXmlProcessor;
import com.dah.desb.infrastructure.camel.component.general.process.impl.MapToJsonProcessor;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class CamelConfig {

    @Autowired
    CamelContext camelContext;

    @Bean
    @PostConstruct
    public GeneralComponent camelGeneralComponent() {
        GeneralComponent generalComponent = new GeneralComponent(camelGeneralProcessorRegistry());
        camelContext.addComponent("general", generalComponent);
        return generalComponent;
    }

    @Bean
    @PostConstruct
    public GeneralProcessorRegistry camelGeneralProcessorRegistry() {
        GeneralProcessorRegistry registry = new GeneralProcessorRegistry();
        registry.add(GeneralProcessType.jsonToXml, camelGeneralJsonToXmlProcessor());
        registry.add(GeneralProcessType.httpParamsToMap, camelGeneralHttpParamsToJsonProcessor());
        registry.add(GeneralProcessType.mapToJson, camelGeneralMapToJsonProcessor());
        return registry;
    }

    @Bean
    @PostConstruct
    public JsonToXmlProcessor camelGeneralJsonToXmlProcessor() {
        return new JsonToXmlProcessor();
    }

    @Bean
    @PostConstruct
    public HttpParamsToMapProcessor camelGeneralHttpParamsToJsonProcessor() {
        return new HttpParamsToMapProcessor();
    }

    @Bean
    @PostConstruct
    public MapToJsonProcessor camelGeneralMapToJsonProcessor() {
        return new MapToJsonProcessor();
    }

}
