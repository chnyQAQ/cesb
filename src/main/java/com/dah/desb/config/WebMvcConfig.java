package com.dah.desb.config;

import com.dah.desb.infrastructure.spring.web.converter.String2DateConverter;
import com.dah.desb.infrastructure.web.error.AppErrorAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ServletContext servletContext;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new String2DateConverter());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/workers").setViewName("/view/workers/workers");
    }

    @Bean
    public ServletContextTemplateResolver servletContextTemplateResolver() {
        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver(servletContext);
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(100);
        resolver.setCacheable(false);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public ErrorAttributes errorAttributes() {
        return new AppErrorAttributes();
    }

    @Bean
    public ErrorProperties errorProperties() {
        return new ErrorProperties();
    }
}
