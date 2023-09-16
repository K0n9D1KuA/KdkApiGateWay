package com.qyh.gateway.core.filter;

import com.qyh.gateway.core.context.GatewayContext;


public interface Filter {

    void doFilter(GatewayContext ctx) throws Exception;

    default int getOrder() {
        FilterAspect annotation = this.getClass().getAnnotation(FilterAspect.class);
        if (annotation != null) {
            return annotation.order();
        }
        return Integer.MAX_VALUE;
    }

    default String getId() {
        FilterAspect annotation = this.getClass().getAnnotation(FilterAspect.class);
        if (annotation != null) {
            return annotation.id();
        }
        return this.getClass().getName();
    }
}
