package com.qyh.gateway.client.core.autoconfigure;


import com.qyh.gateway.client.core.ApiProperties;
import com.qyh.gateway.client.support.springmvc.SpringMVCClientRegisterAndConfigManagerAndConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;

@Configuration
@EnableConfigurationProperties(ApiProperties.class)
@ConditionalOnProperty(prefix = "api", name = {"registerAddress"})
public class ApiClientAutoConfiguration {

    @Autowired
    private ApiProperties apiProperties;

    @Bean
    @ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
    @ConditionalOnMissingBean(SpringMVCClientRegisterAndConfigManagerAndConfigManager.class)
    public SpringMVCClientRegisterAndConfigManagerAndConfigManager springMVCClientRegisterManager() {
        return new SpringMVCClientRegisterAndConfigManagerAndConfigManager(apiProperties);
    }


}
